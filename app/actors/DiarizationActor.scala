package fr.lium
package actor

import akka.actor.Actor
import akka.event.Logging

import java.io.File

import fr.lium.model.MediaFile

import fr.lium.model.{DiarizationPhase1, DiarizationPhase2, DiarizationPhase3, DiarizationPhase4, DiarizationPhase5, DiarizationPhase6, DiarizationPhase7, DiarizationFinished}
import fr.lium.tables.MediaFiles

import org.apache.commons.io.{FileUtils, FilenameUtils};

import sys.process._

import scala.util.{ Try, Success, Failure }

import scala.slick.session.Database
import scala.slick.driver.SQLiteDriver.simple._
import Database.threadLocalSession

case class ComputeDiarization(inputFile: MediaFile, absolutePath: String, absoluteWorkingDir: String)

class DiarizationActor(
  database: Database,
  spkDiarizationJar: String,
  glpsolBin: String,
  pmsGmm: String,
  silenceGmm: String,
  genderGmm: String,
  ubmIv:String,
  efrIv:String,
  covIv:String,
  tvIv:String,
  javaMemory: String = "-Xmx6G -Xms2G",
  javaBin: String = "/usr/bin/java",
  options: String = "--logger=CONFIG --help",
  fDescStart: String = "audio16kHz2sphinx,1:1:0:0:0:0,13,0:0:0",
  fDesc: String = "sphinx,1:1:0:0:0:0,13,0:0:0",
  fDescFilter: String = "sphinx,1:3:2:0:0:0,13,0:0:0:0",
  fDescIV: String ="sphinx,1:3:2:0:0:0,13,1:1:0:0",
  thrL: Int = 2,
  thrH: Int = 3,
  thrIv: Int = 100
) extends Actor {

  val log = Logging(context.system, this)

  def receive = {
    case ComputeDiarization(inputFile, path, workingSegDir) => {

      val workingDir = workingSegDir + "tmpSeg/"
      //Create tmp dir
      val absoluteWorkingSegDir = new File(workingDir)
      absoluteWorkingSegDir.mkdirs()

      val features = s"$workingDir%s.mfcc"
      val show = FilenameUtils.getBaseName(inputFile.fileName)
      val prog = s"$javaBin $javaMemory -cp $spkDiarizationJar"



      database.withSession {
        inputFile.id.foreach { id =>
          MediaFiles.updateStatus(id, DiarizationPhase1.toString)
        }
      }

      //ComputeMFCC
      executeCommand(s"$prog fr.lium.spkDiarization.tools.Wave2FeatureSet $options --fInputMask=$path --fInputDesc=$fDescStart --fOutputMask=$features --fOutputDesc=$fDesc --sOutputMask=$workingDir%s.uem.seg $show")

      //Clean MFCCs
      executeCommand(s"$prog fr.lium.spkDiarization.programs.MSegInit $options --fInputMask=$features --fInputDesc=$fDesc --sInputMask=$workingDir%s.uem.seg --sOutputMask=$workingDir%s.i.seg $show")

      //GLR based segmentation
      executeCommand(s"$prog fr.lium.spkDiarization.programs.MSeg $options --kind=FULL --sMethod=GLR --fInputMask=$features --fInputDesc=$fDesc --sInputMask=$workingDir%s.i.seg --sOutputMask=$workingDir%s.s.seg $show")


      database.withSession {
        inputFile.id.foreach { id =>
          MediaFiles.updateStatus(id, DiarizationPhase2.toString)
        }
      }

      //linear clustering / second segmentation pass
      executeCommand(s"$prog fr.lium.spkDiarization.programs.MClust $options --fInputMask=$features --fInputDesc=$fDesc --sInputMask=$workingDir%s.s.seg --sOutputMask=$workingDir%s.l.seg --cMethod=l --cThr=$thrL $show")

      // hierarchical clustering
      executeCommand(s"$prog fr.lium.spkDiarization.programs.MClust $options --fInputMask=$features --fInputDesc=$fDesc --sInputMask=$workingDir%s.l.seg --sOutputMask=$workingDir%s.h.seg --cMethod=h --cThr=$thrH $show")



      database.withSession {
        inputFile.id.foreach { id =>
          MediaFiles.updateStatus(id, DiarizationPhase3.toString)
        }
      }

      //initialize GMM
      executeCommand(s"$prog fr.lium.spkDiarization.programs.MTrainInit $options --nbComp=8 --kind=DIAG --fInputMask=$features --fInputDesc=$fDesc --sInputMask=$workingDir%s.h.seg --tOutputMask=$workingDir%s.init.gmms $show")


      //EM computation of the GMM
      executeCommand(s"$prog fr.lium.spkDiarization.programs.MTrainEM $options --nbComp=8 --kind=DIAG --fInputMask=$features --fInputDesc=$fDesc --sInputMask=$workingDir%s.h.seg --tInputMask=$workingDir%s.init.gmms --tOutputMask=$workingDir%s.gmms $show")

      //Viterbi decoding using GMM
      executeCommand(s"$prog fr.lium.spkDiarization.programs.MDecode $options --fInputMask=$features --fInputDesc=$fDesc --sInputMask=$workingDir%s.h.seg --sOutputMask=$workingDir%s.d.seg --dPenality=250  --tInputMask=$workingDir%s.gmms $show")


      //Adjust segment boundaries
      executeCommand(s"$prog fr.lium.spkDiarization.tools.SAdjSeg $options --fInputMask=$features --fInputDesc=$fDesc --sInputMask=$workingDir%s.d.seg --sOutputMask=$workingDir$show.adj.seg $show")


      database.withSession {
        inputFile.id.foreach { id =>
          MediaFiles.updateStatus(id, DiarizationPhase5.toString)
        }
      }

      //Speech/Music/Silence segmentation
      executeCommand(s"$prog fr.lium.spkDiarization.programs.MDecode $options --fInputDesc=$fDescFilter --fInputMask=$features --sInputMask=$workingDir%s.i.seg --sOutputMask=$workingDir%s.pms.seg --dPenality=10,10,50 --tInputMask=$pmsGmm $show")

      //filter spk segmentation according pms segmentation
      executeCommand(s"$prog fr.lium.spkDiarization.tools.SFilter $options  --fInputDesc=$fDescFilter --fInputMask=$features --fltSegMinLenSpeech=150 --fltSegMinLenSil=25 --sFilterClusterName=j --fltSegPadding=25 --sFilterMask=$workingDir%s.pms.seg --sInputMask=$workingDir%s.adj.seg --sOutputMask=$workingDir%s.flt.seg $show")


      //Split segment longer than 20s
      executeCommand(s"$prog fr.lium.spkDiarization.tools.SSplitSeg $options --sFilterMask=$workingDir%s.pms.seg --sFilterClusterName=iS,iT,j --sInputMask=$workingDir%s.flt.seg --sOutputMask=$workingDir%s.spl.seg --fInputMask=$features --fInputDesc=$fDescFilter --tInputMask=$silenceGmm $show")


      database.withSession {
        inputFile.id.foreach { id =>
          MediaFiles.updateStatus(id, DiarizationPhase5.toString)
        }
      }
      //Set gender and bandwith, %s.g.seg segmentation file for ASR
      executeCommand(s"$prog fr.lium.spkDiarization.programs.MScore $options --sGender --sByCluster --fInputDesc=$fDescIV --fInputMask=$features --sInputMask=$workingDir%s.spl.seg --sOutputMask=$workingDir%s.g.seg --tInputMask=$genderGmm $show")


      database.withSession {
        inputFile.id.foreach { id =>
          MediaFiles.updateStatus(id, DiarizationPhase7.toString)
        }
      }
      //I-vector speaker based clustering, for screen
      executeCommand(s"$prog fr.lium.spkDiarization.programs.ivector.ILPClustering $options --cMethod=es_iv --ilpThr=$thrIv --sInputMask=$workingDir$show.g.seg --sOutputMask=$workingDir%s.iv.seg --fInputMask=$features --fInputDesc=$fDescIV --ilpGLPSolProgram=$glpsolBin --tInputMask=$ubmIv --nEFRMask=$efrIv --nMahanalobisCovarianceMask=$covIv --tvTotalVariabilityMatrixMask=$tvIv --ilpOutputProblemMask=$workingDir%s.ilp.problem.txt --ilpOutputSolutionMask=$workingDir%s.ilp.solution.txt $show")

      //Use a try
      Try {
        FileUtils.moveFileToDirectory(new File(s"$workingDir$show.g.seg"), new File(workingSegDir), false)
        FileUtils.moveFileToDirectory(new File(s"$workingDir$show.iv.seg"), new File(workingSegDir), false)
      } match {
        case Success(v) => log.info(s"Seg files successfully moved to $workingSegDir")
        case Failure(e) => log.error(s"Unable to move seg files to $workingSegDir")

      }

    }
  }

  def executeCommand(command: String): Int = {
      log.info(command)

      val result: Int = command.!

      log.info("result: " + result)

      result
  }
}
