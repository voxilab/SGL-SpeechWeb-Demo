package fr.lium
package actor

import fr.lium.model.{Converted, FailedConversion, MediaFile}
import fr.lium.tables.MediaFiles

import java.io.File

import akka.actor.Actor
import akka.event.Logging

import sys.process._

import scala.slick.session.Database
import scala.slick.driver.SQLiteDriver.simple._
import Database.threadLocalSession

object Convertor {
  case object Convert
  case object Done
}

case class SoundConvertor(inputFile: MediaFile, path: String)

class SoundConvertorActor(ffmpegBin: File, database: Database) extends Actor {
  val log = Logging(context.system, this)

  def receive = {
    case Convertor.Convert =>
      log.info("I'm converting baby!")
    case SoundConvertor(mediaFile, path) =>
    {
      val commandLine: String = ffmpegBin.getPath() +" -y -i "+ path +" -vn -acodec pcm_s16le -ac 1 -ar 16000 "+ path +".wav"
      log.info("Converting file: " + path)
      log.info("Full command: " + commandLine)

      //wave 16bits PCM (pcm_s16le), 16kHz (-ar 16000), mono (-ac 1)
      val result: Int = commandLine.!

      log.info("result: " + result)

      database.withSession {
        //Update status in database

        mediaFile.id map { id =>
          if(result == 0) {
              MediaFiles.updateStatus(id, Converted.toString)
          } else {
              MediaFiles.updateStatus(id, FailedConversion.toString)
          }
        }

      }

    }

  }
}
