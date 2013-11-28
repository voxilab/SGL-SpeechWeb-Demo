package test

import fr.lium.tables.MediaFiles
import fr.lium.model.{ DiarizationStarted, MediaFile, Transcribing, Uploaded }

import acolyte.RowLists.rowList4
import acolyte.RowList4
import acolyte.Implicits._

object MediaFilesSpec
    extends org.specs2.mutable.Specification with MediaFilesTest {

  "Media file persistence" title

  val schema = rowList4(
    classOf[Int] -> "SUP_ID",
    classOf[String] -> "NAME",
    classOf[String] -> "STATUS",
    classOf[String] -> "AUDIO_NAME")

  "Media file" should {
    "be found uploaded for ID 1" in slickResult(
      schema :+ (1, "test-1", "uploaded", "audio-1")) { implicit s ⇒
        MediaFiles.findById(1) aka "file" must beSuccessfulTry.withValue(
          MediaFile(Some(1), "test-1", Uploaded))
      }

    "be in diarization for ID 2" in slickResult(
      schema :+ (2, "test-2", "diarization_started", "audio-2")) { implicit s ⇒
        MediaFiles.findById(2) aka "file" must beSuccessfulTry.withValue(
          MediaFile(Some(2), "test-2", DiarizationStarted))
      }

    "be in transcription for ID 3" in slickResult(
      schema :+ (3, "test-3", "transcribing", "audio-3")) { implicit s ⇒
        MediaFiles.findById(3) aka "file" must beSuccessfulTry.withValue(
          MediaFile(Some(3), "test-3", Transcribing))
      }

  }
}

private[test] trait MediaFilesTest {
  import scala.slick.session.{ Database, Session }
  import acolyte.QueryResult
  import acolyte.Acolyte.{ connection, handleQuery }

  def slickResult[A](r: ⇒ QueryResult)(f: Session ⇒ A) = new Database {
    override lazy val createConnection = connection { handleQuery(_ ⇒ r) }
  } withSession f
}
