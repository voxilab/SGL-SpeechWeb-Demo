package fr.lium
package actor

import fr.lium.model.{Converted, FailedConversion, MediaFile}
import fr.lium.tables.MediaFiles

import java.io.File

import akka.actor.{Actor, ActorRef}
import akka.event.Logging

import sys.process._

import scala.slick.session.Database
import scala.slick.driver.SQLiteDriver.simple._
import Database.threadLocalSession

object Convertor {
  case object Convert
  case object Done
}

case class SoundConvertor(inputFile: MediaFile, fullPath: String, workingDir: String)

class SoundConvertorActor(ffmpegBin: File, database: Database, diarizationActor: ActorRef) extends Actor {
  val log = Logging(context.system, this)

  def receive = {
    case Convertor.Convert => log.info("I'm converting baby!")
    case SoundConvertor(mediaFile, fullPath, workingDir) =>
    {
      val ffmpegBinPath = ffmpegBin.getPath()
      val commandLine: String = s"$ffmpegBinPath -y -i $fullPath -vn -acodec pcm_s16le -ac 1 -ar 16000 $fullPath.wav"
      log.info("Converting file: " + fullPath)
      log.info("Full command: " + commandLine)

      //wave 16bits PCM (pcm_s16le), 16kHz (-ar 16000), mono (-ac 1)
      val result: Int = commandLine.!

      log.info("result: " + result)

      database.withSession {
        //Update status in database

        mediaFile.id.foreach { id =>
          MediaFiles.updateStatus(id, (if (result == 0) Converted else FailedConversion).toString)
          diarizationActor ! ComputeDiarization(mediaFile, fullPath + ".wav", workingDir)
        }

      }

    }

  }
}
