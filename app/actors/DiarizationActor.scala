package fr.lium
package actor

import akka.actor.Actor
import akka.event.Logging

import fr.lium.model.MediaFile
import org.apache.commons.io.FilenameUtils;

import sys.process._

case class ComputeDiarization(inputFile: MediaFile, absolutePath: String, absoluteWorkingDir: String)

class DiarizationActor(
  spkDiarizationJar: String,
  javaMemory: String = "-Xmx6G -Xms2G",
  javaBin: String = "/usr/bin/java",
  options: String = "--logger=CONFIG --help",
  fDescStart: String = "audio16kHz2sphinx,1:1:0:0:0:0,13,0:0:0",
  fDesc: String = "sphinx,1:1:0:0:0:0,13,0:0:0"
) extends Actor {

  val log = Logging(context.system, this)

  def receive = {
    case ComputeDiarization(inputFile, path, workingDir) => {

      val features = s"$workingDir%s.mfcc"
      val show = FilenameUtils.getBaseName(inputFile.fileName)

      computeMFCC(inputFile, path, workingDir, features, show)
      cleanMFCC(inputFile, path, workingDir, features, show)
      glrSegmentation(inputFile, path, workingDir, features, show)

    }
  }

  def computeMFCC(inputFile: MediaFile, path: String, workingDir: String, features: String, show: String) = {

      val commandLine: String = s"$javaBin $javaMemory -cp $spkDiarizationJar fr.lium.spkDiarization.tools.Wave2FeatureSet $options --fInputMask=$path --fInputDesc=$fDescStart --fOutputMask=$features --fOutputDesc=$fDesc --sOutputMask=$workingDir%s.uem.seg $show"

      log.info(commandLine)

      val result: Int = commandLine.!

      log.info("result: " + result)
  }



  def cleanMFCC(inputFile: MediaFile, path: String, workingDir: String, features: String, show: String) = {

      val commandLine: String = s"$javaBin $javaMemory -cp $spkDiarizationJar fr.lium.spkDiarization.programs.MSegInit $options --fInputMask=$features --fInputDesc=$fDesc --sInputMask=$workingDir%s.uem.seg --sOutputMask=$workingDir%s.i.seg $show"

      log.info(commandLine)

      val result: Int = commandLine.!

      log.info("result: " + result)
  }



  def glrSegmentation(inputFile: MediaFile, path: String, workingDir: String, features: String, show: String) = {

      val commandLine: String = s"$javaBin $javaMemory -cp $spkDiarizationJar fr.lium.spkDiarization.programs.MSeg $options --kind=FULL --sMethod=GLR --fInputMask=$features --fInputDesc=$fDesc --sInputMask=$workingDir%s.i.seg --sOutputMask=$workingDir%s.s.seg $show"

      log.info(commandLine)

      val result: Int = commandLine.!

      log.info("result: " + result)
  }
}
