package fr.lium.api

import scala.language.postfixOps

import fr.lium.model.{ Female, Male, MediaFile, Segment, Speaker, UnknownGender, Word }
import fr.lium.util.conversion.parseFloatOption

import java.io.File
import scala.util.{ Failure, Success, Try }
import scala.io.Source

import play.api.Logger

case class SegApi(spkPublicSegFile: String, mediaFileApi: MediaFileApi) {

  def getSegments(mediaFile: MediaFile, enc: String = "ISO-8859-1"): Try[List[Segment]] =
    getSegments(new File(mediaFileApi.getMediaDir(mediaFile) + spkPublicSegFile), enc)

  def getSegments(f: File, enc: String): Try[List[Segment]] =
    Try(Source.fromFile(f, enc).getLines).transform(
      ls ⇒ Success(getSegmentsFromLines(ls)), e ⇒ {
        Logger.error(s"Problem with file ${f.getAbsolutePath}: ${e.getMessage}")
        Failure(e)
      })

  def getSegmentsFromLines(lines: String): List[Segment] =
    getSegmentsFromLines(lines split "\n")

  def getSegmentsFromLines(lines: TraversableOnce[String]): List[Segment] = {lines.flatMap { l =>
    getInfoFromLine(l).map { t => Segment(t._2.start, t._2.duration, Some(t._1)) }
  } toList}.sortBy(_.start)

  def getSpeakers(mediaFile: MediaFile, enc: String = "ISO-8859-1"): Try[Map[Speaker,List[Segment]]] =
    getSpeakers(new File(mediaFileApi.getMediaDir(mediaFile) + spkPublicSegFile), enc)

  def getSpeakers(f: File, enc: String): Try[Map[Speaker,List[Segment]]] =
    Try(Source.fromFile(f, enc).getLines).transform(
      ls ⇒ Success(getSpeakersFromLines(ls)), e ⇒ {
        Logger.error(s"Problem with file ${f.getAbsolutePath}: ${e.getMessage}")
        Failure(e)
      })

  def getSpeakersFromLines(lines: String): Map[Speaker, List[Segment]] =
    getSpeakersFromLines(lines split "\n")

  def getSpeakersFromLines(lines: TraversableOnce[String]): Map[Speaker, List[Segment]] =
    lines.foldLeft(Map[Speaker, List[Segment]]())((m,l) => getInfoFromLine(l) match {
      case Some((speaker, segment)) => m + (speaker -> (m.get(speaker) match {
        case Some(segments) => (segment :: segments)
        case None => List(segment)
      }))
      case None => m
    })


  def getInfoFromLine(line: String): Option[(Speaker, Segment)] = line.split(" ") match {

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
