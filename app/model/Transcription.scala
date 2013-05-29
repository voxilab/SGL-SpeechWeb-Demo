package fr.lium
package model

case class TranscriptionFinished(
  file: AudioFile,
  system: Option[String] = None,
  transcription: List[Segment])

case class TranscriptionInProgress(
  file: AudioFile,
  progress: Int = 0,
  system: Option[String] = None)
