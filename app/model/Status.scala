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

case object DiarizationPhase1 extends Status {
  val value = "diarization_phase1"
}


case object DiarizationPhase2 extends Status {
  val value = "diarization_phase2"
}

case object DiarizationPhase3 extends Status {
  val value = "diarization_phase3"
}

case object DiarizationPhase4 extends Status {
  val value = "diarization_phase4"
}

case object DiarizationPhase5 extends Status {
  val value = "diarization_phase5"
}

case object DiarizationPhase6 extends Status {
  val value = "diarization_phase6"
}

case object DiarizationPhase7 extends Status {
  val value = "diarization_phase7"
}

case object DiarizationFinished extends Status {
  val value = "diarization_finished"
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
