package fr.lium
package tables

import fr.lium.model._

import scala.util.{ Try, Success, Failure }

import java.io.File
import scala.slick.driver.SQLiteDriver.simple._

object Transcriptions extends Table[(Option[Int], String, Option[String], String, Int)]("transcriptions") {
  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc) // This is the primary key column

  val autoInc = filename ~ system ~ status ~ mediaFileId returning id

  def status = column[String]("STATUS")

  def filename = column[String]("FILENAME")

  def system = column[Option[String]]("SYSTEM", O.Nullable)

  def mediaFileId = column[Int]("MEDIA_FILE_ID")


  // Every table needs a * projection with the same type as the table's type parameter
  def * = id.? ~ filename ~ system ~ status ~ mediaFileId

  // A reified foreign key relation that can be navigated to create a join
  def mediaFile = foreignKey("MEDIA_FILE_PK", mediaFileId, MediaFiles)(_.id)

  def findById(id: Int)(implicit session: scala.slick.session.Session): Try[DbTranscription] = {

    val query = for {
      t <- Transcriptions if t.id === id
      a <- MediaFiles if a.id === t.mediaFileId
    } yield (t, a)

    val maybeTranscription: Option[DbTranscription] =
      query.firstOption map {
        case ((id, filename, system, status, mediaFileId), (aId, aName, aStatus, audioFileName)) => new DbTranscription(
          new AudioFile(id, audioFileName, filename, MediaFile.status(aStatus)),
          system,
          DbTranscription.status(status))
      }

    maybeTranscription asTry badArg("Transcription not found.")
  }


  def findByMediaFile(mediaFile: MediaFile)(implicit session: scala.slick.session.Session): List[DbTranscription] = {

    val query = for {
      t <- Transcriptions if t.mediaFileId === mediaFile.id
      a <- MediaFiles if a.id === t.mediaFileId
    } yield (t, a)

    query.list map {
      case ((id, filename, system, status, mediaFileId), ((aId, aName, aStatus, audioFileName))) => new DbTranscription(new
        AudioFile(id, audioFileName, filename, MediaFile.status(aStatus)), system, DbTranscription.status(status), filename = Some(new File(filename)))
    }

  }

}

