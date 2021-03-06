package tasks

import scala.slick.driver.SQLiteDriver.simple._
import Database.threadLocalSession

import fr.lium.tables.{ MediaFiles, Transcriptions }
import fr.lium.model.{ Finished, InProgress, Uploaded }
import fr.lium.model.Conversions._
import fr.lium.api.MediaFileApi
import fr.lium.util.FileUtils

import com.typesafe.config._
import scala.util.Try

import java.io.File

trait Env {

  val config = ConfigFactory.load()
  val env = new TaskEnv(config)
}

class DropCreateSchema extends Runnable with Env {

  def run {
    System.setProperty("config.file", "conf/application.conf")
    env.database.withSession {
      statements()
    }
  }

  def statements() = {
    Try((MediaFiles.ddl).drop)
    Try((Transcriptions.ddl).drop)
    (MediaFiles.ddl).create
    (Transcriptions.ddl).create
  }
}

class LoadFixtures extends Runnable with Env {

  def run {
    System.setProperty("config.file", "conf/application.conf")
    env.database.withSession {
      statements(env.database)
    }
  }

  def statements(database: Database) = {
    val mediaFile = MediaFiles.autoInc.insert(("audio.wav", Uploaded))
    mediaFile.id map { id => Transcriptions.autoInc.insert((config.getString("lium.sampleFile"), None, InProgress.toString, id)) }

    //Insert some sample data for the ASH combination project
    val sampleMediaFile = new File("data/ASH/audio/BFMTV_BFMStory_2011-03-17_175900.wav")

    //If we have some sample data to play with, let's register it using the system API
    val id: Option[Int] = if (sampleMediaFile.exists) {

      val api = new MediaFileApi(new File(config.getString("lium.baseDir")), config.getString("lium.audioFileBasename"), database)

      val audioFileAsh = api.createFile(sampleMediaFile, FileUtils.getFileExtension(sampleMediaFile), false)
      audioFileAsh.toOption.flatMap { _.id }
    } else {

      val audioFileAsh = MediaFiles.autoInc.insert(("BFMTV_BFMStory_2011-03-17_175900.wav", Uploaded))
      audioFileAsh.id
    }

    id map { id =>
      Transcriptions.autoInc.insert(("data/ASH/ctm/combination/BFMTV_BFMStory_2011-03-17_175900.ctm", Some("ASH"), Finished.toString, id))
      Transcriptions.autoInc.insert(("data/ASH/ctm/LIA/BFMTV_BFMStory_2011-03-17_175900.ctm", Some("LIA"), Finished.toString, id))
      Transcriptions.autoInc.insert(("data/ASH/ctm/RASR/BFMTV_BFMStory_2011-03-17_175900.ctm", Some("RASR"), Finished.toString, id))
      Transcriptions.autoInc.insert(("data/ASH/ctm/SPH/BFMTV_BFMStory_2011-03-17_175900.ctm", Some("SPH"), Finished.toString, id))
    }
  }
}
