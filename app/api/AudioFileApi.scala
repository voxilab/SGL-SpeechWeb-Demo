package fr.lium
package api

import fr.lium.util.FileUtils
import org.apache.commons.io.{FileUtils => ApacheFileUtils}

import java.io.File

import scala.util.{ Try, Success, Failure }

import scala.slick.session.Database
import scala.slick.driver.SQLiteDriver.simple._
import Database.threadLocalSession

case class AudioFileApi(
    baseDirectory: File,
    audioFileBasename: String,
    database: Database) {

}
