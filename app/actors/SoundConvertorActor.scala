package fr.lium
package actor

import fr.lium.model.MediaFile

import java.io.File

import akka.actor.Actor
import akka.event.Logging

import sys.process._

object Convertor {
  case object Convert
  case object Done
}

case class SoundConvertor(inputFile: MediaFile)

class SoundConvertorActor(ffmpegBin: File) extends Actor {
  val log = Logging(context.system, this)

  def receive = {
    case Convertor.Convert =>
      log.info("I'm converting baby!")
    case SoundConvertor(f) =>
    {
      log.info("Converting file: " + f.fileName)
      //wave 16bits PCM (pcm_s16le), 16kHz (-ar 16000), mono (-ac 1)
      val result: String = (ffmpegBin.getPath() +" -y -i "+ f.fileName +" -vn -acodec pcm_s16le -ac 1 -ar 16000 "+f.fileName+".wav").!!
      log.info("result: " + result)
    }

  }
}
