package fr.lium
package model

import java.io.File

//Input
case class AudioFile(
  fileName: String,
  mediaFile: Option[MediaFile] = None
)

