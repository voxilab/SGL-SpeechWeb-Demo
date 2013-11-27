package fr.lium
package model

sealed trait Status {
  val value: String

  override def toString = value
}


case object Status {

  def status(status: String): Status =
    status match {
      case Uploaded.value           => Uploaded
      case DiarizationStarted.value => DiarizationStarted
      case DiarizationPhase1.value  => DiarizationPhase1
      case DiarizationPhase2.value  => DiarizationPhase2
      case DiarizationPhase3.value  => DiarizationPhase3
      case DiarizationPhase4.value  => DiarizationPhase4
      case DiarizationPhase5.value  => DiarizationPhase5
      case DiarizationPhase6.value  => DiarizationPhase6
      case DiarizationPhase7.value  => DiarizationPhase7
      case Transcribing.value       => Transcribing
      case Finished.value           => Finished
      case Converting.value         => Converting
      case _                        => Unknown
    }

}


case object Uploaded extends Status {
  val value = "uploaded"
}
case object DiarizationStarted extends Status {
  val value = "diarization_started"
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
