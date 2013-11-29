package fr.lium
package json
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.json.Json.JsValueWrapper

import fr.lium.model.{ AudioFile, Diarization, Female, Gender, Male, MediaFile, Segment, Speaker, Status, Transcribing, Transcription, UnknownGender, Uploaded, Word }

object ReadsWrites {

  implicit val statusWrites = new Writes[Status] {
    def writes(s: Status): JsValue = {
      JsString(s.value)
    }
  }

  implicit val audioFileWrites = new Writes[AudioFile] {
    def writes(a: AudioFile): JsValue = {
      Json.obj(
        "fileName" -> a.fileName
      )
    }
  }

  implicit val mediaFileWrites = new Writes[MediaFile] {
    def writes(a: MediaFile): JsValue = {
      Json.obj(
        "id" -> a.id,
        "status" -> a.status
      )
    }
  }

  implicit val genderWrites = new Writes[Gender] {
    def writes(g: Gender): JsValue = {
      JsString(g match {
        case Male => "m"
        case Female => "f"
        case UnknownGender => "u"
      })
    }
  }

  implicit val speakerWrites = new Writes[Speaker] {
    def writes(s: Speaker): JsValue = {
      Json.obj(
        "id"     -> s.id,
        "gender" -> s.gender
      )
    }
  }

  implicit val segmentWrites = new Writes[Segment] {
    def writes(s: Segment): JsValue = {
      Json.obj(
        "start"     -> "%.2f".format(s.start),
        "duration"  -> "%.2f".format(s.duration)
      )
    }
  }

  implicit val speakerMapWrites = new Writes[Map[Speaker,List[Segment]]] {

    def writes(m: Map[Speaker,List[Segment]]): JsValue = {
      JsArray(
        m.foldLeft(Nil:List[JsObject]){case (list, (speaker, segmentList)) =>
          Json.obj(
            "id"       -> speaker.id,
            "gender"   -> speaker.gender,
            "segments" -> JsArray(segmentList.map { segmentWrites.writes(_) })
          ) :: list
        }
      )

    }
  }


  implicit val wordWrites = new Writes[Word] {
    def writes(w: Word): JsValue = {
      Json.obj(
        "start"     -> "%.2f".format(w.start),
        "word"      -> w.word,
        "spk"       -> w.speaker
      )
    }
  }


  implicit val transcriptionWrites = new Writes[Transcription] {
    def writes(t: Transcription): JsValue = {
      Json.obj(
        "system"    -> t.system,
        "audioFile" -> Json.toJson(t.file),
        "content"   -> Json.toJson(t.transcription)
      )
    }
  }
}
