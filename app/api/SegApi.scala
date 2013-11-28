package fr.lium.api

import fr.lium.model.{Segment, Speaker}

import java.io.File
import scala.util.{ Failure, Success, Try }
import scala.io.Source

import play.api.Logger

object SegApi {

  def getSpeakers(f: File, enc: String = "ISO-8859-1"): Try[Map[Speaker,List[Segment]]] =
    Try(Source.fromFile(f, enc).getLines).transform(
      ls ⇒ Success(getSpeakersFromLines(ls)), e ⇒ {
        Logger.error(s"Problem with file ${f.getAbsolutePath}: ${e.getMessage}")
        Failure(e)
      })

  def getSpeakersFromLines(lines: TraversableOnce[String]): Map[Speaker, List[Segment]] = {
    Map()
  }



  def getSpeakerFromLine(line: String): Option[(Speaker, Segment)] = {
    None
  }
}
