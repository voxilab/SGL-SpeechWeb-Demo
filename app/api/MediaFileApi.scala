package fr.lium
package api

import fr.lium.util.FileUtils
import fr.lium.model.{MediaFile, Uploaded, Status}
import fr.lium.tables.MediaFiles
import fr.lium.model.Conversions._
import fr.lium.actor._
import org.apache.commons.io.{FileUtils => ApacheFileUtils}

import java.io.File

import scala.util.{ Try, Success, Failure }

import scala.slick.session.Database
import scala.slick.driver.SQLiteDriver.simple._
import Database.threadLocalSession


import akka.actor.{ActorRef}

case class MediaFileApi(
    baseDirectory: File,
    audioFileBasename: String,
    database: Database,
    soundConvertorActor: Option[ActorRef] = None) {

  /** Take care of registering a new file into the system
    *
    * @param sourceFile The source file to register into the system
    * @param newFileName Rename the source file using this extension
    * @param move If the sourceFile should be moved or only copied
    */
  def createFile(sourceFile: File, fileExtension: Option[String], move: Boolean = true): Try[MediaFile] = {

    val fileName = audioFileBasename + fileExtension.getOrElse("")

    def registerFile(sourceFile: File, toFile: File, move: Boolean): Try[File] = {
      if(move) {
        FileUtils.moveFileToFile(sourceFile, toFile)
      } else {
        Try{
          ApacheFileUtils.copyFile(sourceFile, toFile)
          toFile
        }
      }
    }

    database.withSession {
      val mediaFile = for {
        //TODO: give a better audio file name than just appending .wav
        audioFile <- Try(MediaFiles.autoInc.insert((fileName, Uploaded)))
        id <- audioFile.id asTry badArg("Fail to get autoinc id from DB")
        dir = new File(getMediaDir(id)).mkdir();
        moved <- registerFile(sourceFile, new File(getMediaPath(id,fileName)), move)
      } yield MediaFile(audioFile.id, fileName)

      soundConvertorActor.map { actor =>
        mediaFile.map { mFile =>
          mFile.id.map { id =>
            actor ! SoundConvertor(mFile, getMediaPath(id,mFile.fileName))
          }
        }
      }
      mediaFile
    }

  }
  def getFileById(id: Int): Try[MediaFile] = {

    database.withSession {

      val dir = new File(getMediaDir(id))

      if (dir.exists && dir.isDirectory) {
        MediaFiles.findById(id)
      } else {
        Failure(new Exception("MediaFile directory doesn't exist"))
      }
    }

  }

  def getMediaDir(id: Int): String = {
    baseDirectory + File.separator + id + File.separator
  }
  def getMediaPath(id: Int, filename: String): String = {
   getMediaDir(id) + filename
  }

}


