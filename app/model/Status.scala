package fr.lium
package model

sealed trait Status {
  val value: String

  override def toString = value
}

case object Uploaded extends Status {
  val value = "uploaded"
}
case object Diarization extends Status {
  val value = "diarization"
}

case object Transcribing extends Status {
  val value = "transcribing"
}

case object Finished extends Status {
  val value =  "finished"
}

case object InProgress extends Status {
  val value = "inprogress"
}


case object Converting extends Status {
  val value = "converting"
}


case object Converted extends Status {
  val value = "converted"
}


case object FailedConversion extends Status {
  val value = "failed_conversion"
}

case object Unknown extends Status {
  val value = "unknown"
}
