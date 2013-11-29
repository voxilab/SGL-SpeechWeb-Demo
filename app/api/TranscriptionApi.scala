package fr.lium
package api

import java.io.File

import fr.lium.model.{ AudioFile, MediaFile, Transcription, Finished }
import fr.lium.tables.Transcriptions

import scala.util.Try
import scala.slick.session.Database
import scala.slick.driver.SQLiteDriver.simple._
import Database.threadLocalSession

final class TranscriptionApi(database: Database) {

  def startTranscription(file: MediaFile): Transcription = {

    //TODO
    //We should for sure do something here, like starting the transcription ;)

    new Transcription(file)
  }

  def getTranscriptionProgress(file: MediaFile): Transcription = {

    //TODO
    //We should for sure do something here

    new Transcription(file)
  }

  def getTranscriptions(file: MediaFile): List[Transcription] = {

    database.withSession {
      val dbTranscription = Transcriptions.findByMediaFile(file)

      dbTranscription.map { d =>
        Transcription(file, Finished, d.system, d.filename.flatMap { f => WordApi.getWordsFromFile(f).toOption })
      }
    }

  }

}
