package controllers

import play.api._
import play.api.mvc._

import fr.lium.Env
import fr.lium.json.ReadsWrites._
import fr.lium.util.FileUtils

import java.io.File
import scala.concurrent.ExecutionContext.Implicits._

import scala.util.{ Try, Success, Failure }
import play.api.libs.json._

object AudioFileApiController extends BaseApiController {

  private lazy val env = Env.current

  /**
  def startTranscriptions(id: Int) = Action { implicit request =>
    env.audioFileApi.getAudioFileById(id).map { audioFile =>
      val progress = env.transcriptionApi.startTranscription(audioFile)
      JsonResponse(Ok(Json.obj(
        "transcription" -> Json.toJson(progress),
        "urlStatus" -> routes.AudioFileApiController.getTranscriptionProgress(id).absoluteURL())))
    }.getOrElse {
      JsonResponse(NotFound(Json.obj("message" -> "AudioFile not found")))
    }
  }

  def getTranscriptionProgress(id: Int) = Action { implicit request =>
    env.audioFileApi.getAudioFileById(id).map { audioFile =>
      val progress = env.transcriptionApi.getTranscriptionProgress(audioFile)
      JsonResponse(Ok(Json.toJson(progress)))
    }.getOrElse {
      JsonResponse(NotFound(Json.obj("message" -> "AudioFile not found")))
    }
  }
  **/

  def getTranscriptions(id: Int) = Action { implicit request =>

    env.mediaFileApi.getFileById(id) match {
      case Success(mediaFile) => {
        val transcriptions = env.transcriptionApi.getTranscriptions(mediaFile)
        JsonResponse(Ok(Json.toJson(transcriptions)))
      }
      case Failure(e) => JsonResponse(NotFound(Json.obj("message" -> ("AudioFile not found. " + e.getMessage()))))
    }
  }
}
