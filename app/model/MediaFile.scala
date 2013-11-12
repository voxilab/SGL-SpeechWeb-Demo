package fr.lium
package model

import java.io.File

//Input
case class MediaFile(
  id: Option[Int],
  fileName: String,
  status: Status = Uploaded)

case object MediaFile {

  def status(status: String): Status =
    status match {
      case Uploaded.value     => Uploaded
      case Diarization.value  => Diarization
      case Transcribing.value => Transcribing
      case Finished.value     => Finished
      case _                  => Unknown
    }

}

//Output
case class MediaFileTranscriptions(
  mediaFile: MediaFile,
  transcriptions: List[Transcription])

