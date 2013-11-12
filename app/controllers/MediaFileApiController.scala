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

object MediaFileApiController extends BaseApiController {

  private lazy val env = Env.current


  def getOptions(path: String) = Action { implicit request ⇒ JsonResponse(Ok(Json.obj("message" -> "Ok"))) }

  def addFile() = Action(parse.multipartFormData) { request ⇒
    request.body.file("file").map { file ⇒
      val f = env.fileApi.createFile(file.ref.file, FileUtils.getFileExtension(file.filename))
      f match {
        case Success(mediaFile) => JsonResponse(Ok(Json.toJson(mediaFile)))
        case Failure(e) => JsonResponse(InternalServerError(
          Json.obj("message" -> ("Ooops! It seems we had a problem storing the file." + e.getMessage()))
        ))
      }

    }.getOrElse {
      JsonResponse(Status(405)(Json.obj("message" -> "Invalid input")))
    }
  }


}
