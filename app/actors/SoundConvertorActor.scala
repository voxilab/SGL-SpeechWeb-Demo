package fr.lium
package actor

import java.io.File

import akka.actor.Actor
import akka.event.Logging

import sys.process._

object Convertor {
  case object Convert
  case object Done
}

case class SoundConvertor(inputFile: File)

class SoundConvertorActor(ffmpegBin: File) extends Actor {
  val log = Logging(context.system, this)

  def receive = {
    case Convertor.Convert =>
      log.info("I'm converting baby!")
    case SoundConvertor(f) =>
    {
      log.info("Converting file: " + f.getPath())
      val result: String = (ffmpegBin.getPath() +" -y -i "+ f.getPath() +" -vn -acodec pcm_s16le -ac 1 -ar 16000 "+f.getPath()+".wav").!!
      log.info("result: " + result)
    }

  }
}
