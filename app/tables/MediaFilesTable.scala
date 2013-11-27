package fr.lium
package tables

import fr.lium.model.{AudioFile, MediaFile, Diarization, Status, Transcribing, Uploaded}

import scala.util.{ Try, Success, Failure }

import java.io.File
import scala.slick.driver.SQLiteDriver.simple._

object MediaFiles extends Table[(Option[Int], String, String, Option[String])]("media_files") {
  def id = column[Int]("SUP_ID", O.PrimaryKey, O.AutoInc) // This is the primary key column

  val autoInc = name ~ status returning id into { case ((name, status), id) => MediaFile(Some(id), name, Status.status(status)) }

  def name = column[String]("NAME")
  def status = column[String]("STATUS")
  def audioName = column[Option[String]]("AUDIO_NAME")
  // Every table needs a * projection with the same type as the table's type parameter
  def * = id.? ~ name ~ status ~ audioName

  def findById(id: Int)(implicit session: scala.slick.session.Session): Try[MediaFile] = {

    val query = for {
      a <- MediaFiles if a.id === id
    } yield (a.id, a.name, a.status)

    val maybeMediaFile: Option[MediaFile] = query.firstOption.map{ t => new MediaFile(Some(t._1), t._2, Status.status(t._3)) }

    maybeMediaFile asTry badArg("MediaFile not found.")
  }

  def updateStatus(id: Int, status: String)(implicit session: scala.slick.session.Session) = {

    val q = for {
      a <- MediaFiles if a.id === id
    } yield a.status

    q.update(status)
  }
}
