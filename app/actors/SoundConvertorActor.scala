package fr.lium
package actor

import java.io.File

import akka.actor.Actor
import akka.event.Logging

object Convertor {
  case object Convert
  case object Done
}

class SoundConvertorActor(ffmpegBin: File) extends Actor {
  val log = Logging(context.system, this)

  def receive = {
    case Convertor.Convert =>
    log.info("I'm converting baby!")
  }
}
