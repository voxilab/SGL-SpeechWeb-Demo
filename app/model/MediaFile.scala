package fr.lium
package model

import java.io.File

//Input
case class MediaFile(
  id: Option[Int],
  fileName: String,
  status: Status = Uploaded)

//Output
case class MediaFileTranscriptions(
  mediaFile: MediaFile,
  transcriptions: List[Transcription])

