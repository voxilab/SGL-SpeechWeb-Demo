package fr.lium
package actor

import akka.actor.Actor
import akka.event.Logging


object Diarization {
  case object FirstPass
  case object SecondPass
}

class DiarizationActor extends Actor {

  val log = Logging(context.system, this)

  def receive = {
    case Diarization.FirstPass => log.info("First pass")
    case Diarization.SecondPass => log.info("Second pass")
  }
}
