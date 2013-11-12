package fr.lium
package model

import java.io.File

//Input
case class AudioFile(
  id: Option[Int],
  fileName: String,
  mediaFileName: String,
  status: Status = Uploaded)
