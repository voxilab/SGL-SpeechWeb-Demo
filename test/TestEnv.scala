package test

import scala.slick.driver.SQLiteDriver.simple._
import Database.threadLocalSession
import java.io.File

import play.api.test.FakeApplication

import fr.lium.api.{AudioFileApi, MediaFileApi, SegApi}
import tasks.{DropCreateSchema, LoadFixtures}

final class TestEnv() {

  lazy val database: Database = Database.forURL(
    "jdbc:sqlite:liumtest.db",
    driver = "org.sqlite.JDBC")

  lazy val baseDir = new File("/tmp/testaudio/")
  lazy val basename = "audio"
  lazy val dropCreateSchema = new DropCreateSchema
  lazy val loadFixtures = new LoadFixtures
  lazy val spkPublicSegFile = "audio.iv.seg"

  def mediaFileApi() = new MediaFileApi(
    baseDirectory = baseDir,
    audioFileBasename = basename,
    database,
    None
  )

  def audioFileApi()(implicit app: FakeApplication) = {
    new AudioFileApi(baseDir, basename, database)
  }


  def segApi() = {
    new SegApi(
      spkPublicSegFile,
      mediaFileApi
    )
  }
}

object Env {
  lazy val current = new TestEnv
}
