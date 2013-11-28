package fr.lium.api

import fr.lium.model.{ Female, Male, MediaFile, Segment, Speaker, UnknownGender, Word }
import fr.lium.util.conversion.parseFloatOption

import java.io.File
import scala.util.{ Failure, Success, Try }
import scala.io.Source

import play.api.Logger

case class SegApi(spkPublicSegFile: String, mediaFileApi: MediaFileApi) {

  def getDiarization(mediaFile: MediaFile, enc: String = "ISO-8859-1"): Try[Map[Speaker,List[Segment]]] =
    getSpeakers(new File(mediaFileApi.getMediaPath(mediaFile)), enc)

  def getSpeakers(f: File, enc: String = "ISO-8859-1"): Try[Map[Speaker,List[Segment]]] =
    Try(Source.fromFile(f, enc).getLines).transform(
      ls ⇒ Success(getSpeakersFromLines(ls)), e ⇒ {
        Logger.error(s"Problem with file ${f.getAbsolutePath}: ${e.getMessage}")
        Failure(e)
      })

  def getSpeakersFromLines(lines: String): Map[Speaker, List[Segment]] =
    getSpeakersFromLines(lines split "\n")

  def getSpeakersFromLines(lines: TraversableOnce[String]): Map[Speaker, List[Segment]] =
    lines.foldLeft(Map[Speaker, List[Segment]]())((m,l) => getSpeakerFromLine(l) match {
      case Some((speaker, segment)) => m + (speaker -> (m.get(speaker) match {
        case Some(segments) => (segment :: segments)
        case None => List(segment)
      }))
      case None => m
    })



  def getSpeakerFromLine(line: String): Option[(Speaker, Segment)] = line.split(" ") match {

    case Array(show, "1", start, duration, gender, chan, _, spkId) =>
            Some(
              (Speaker(spkId, chan, gender match {
              case "M" ⇒ Male
              case "F" ⇒ Female
              case _   ⇒ UnknownGender // Option[Gender] ?
            }),
              Segment(parseFloatOption(start).getOrElse(0), parseFloatOption(duration).getOrElse(0)))
            )
    case _ => None

  }
}
