package fr.lium

import akka.actor.{ActorRef, ActorSystem, Props}
import com.typesafe.config.Config
import play.api.{ Application, Play }
import java.io.File

import fr.lium.api.{AudioFileApi, MediaFileApi, TranscriptionApi, WordApi}
import fr.lium.actor.{SoundConvertorActor}

import scala.slick.session.Database

final class Env(
    config: Config,
    actorSystem: ActorSystem) {

  lazy val ffmpegBin = new File(config.getString("lium.ffmpegBin"))


  lazy val mediaFileApi = new MediaFileApi(
    baseDirectory = new File(config.getString("lium.baseDir")),
    audioFileBasename = config.getString("lium.audioFileBasename"),
    database,
    Some(soundConvertorActor)
  )

  lazy val audioFileApi = new AudioFileApi(
    baseDirectory = new File(config.getString("lium.baseDir")),
    audioFileBasename = config.getString("lium.audioFileBasename"),
    database
  )

  lazy val wordApi = new WordApi(config.getString("lium.fileEncoding"))

  lazy val transcriptionApi = new TranscriptionApi(wordApi, database)

  lazy val databaseName = config.getString("lium.databaseName")
  lazy val database: Database = Database.forURL(
    config.getString("lium.databaseUrl") format databaseName,
    driver = config.getString("lium.databaseDriver"))

  lazy val soundConvertorActor: ActorRef = actorSystem.actorOf(Props(new SoundConvertorActor(ffmpegBin)), name = "soundConvertorActor")
}

object Env {

  lazy val current = new Env(
    config = appConfig,
    actorSystem = appSystem)

  private def appConfig: Config = withApp(_.configuration.underlying)

  private def appSystem = withApp { implicit app ⇒
    play.api.libs.concurrent.Akka.system
  }
  private def withApp[A](op: Application ⇒ A): A =
    Play.maybeApplication.map(op).get
}
