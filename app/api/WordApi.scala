package fr.lium.api

import fr.lium.model.{ Female, Male, Speaker, Word }
import fr.lium.util.conversion.parseFloatOption

import java.io.File
import scala.io.Source
import scala.util.{ Failure, Success, Try }

import play.api.Logger

object WordApi {

  def getWordsFromFile(f: File, enc: String = "ISO-8859-1"): Try[List[Word]] =
    Try(Source.fromFile(f, enc).getLines).transform(
      ls ⇒ Success(getWordsFromLines(ls)), e ⇒ {
        Logger.error(s"Problem with file ${f.getAbsolutePath}: ${e.getMessage}")
        Failure(e)
      })

  def getWordsFromLines(lines: String): List[Word] =
    getWordsFromLines(lines split "\n")

  def getWordsFromLines(lines: TraversableOnce[String]): List[Word] =
    lines.foldLeft(Nil: List[Word]) { (ws, l) ⇒
      ws ++: getWordFromLine(l).fold(Nil: List[Word])(List(_))
    }

  def getWordFromLine(line: String): Option[Word] = line.split(" ") match {
    // Default CTM format with 4 fields added at the end:
    // score, gender, channel, spkId
    case Array(show, _, start, duration, word, score, gender, chan, spkId) ⇒
      Some(Word(show,
        parseFloatOption(start).getOrElse(0),
        parseFloatOption(duration).getOrElse(0),
        word,
        parseFloatOption(score),
        (gender, chan, spkId) match {
          case ("N/A", "N/A", "N/A") ⇒ None
          case (gender, chan, spkId) ⇒
            Some(Speaker(spkId, chan, gender match {
              case "M" ⇒ Some(Male)
              case "F" ⇒ Some(Female)
              case _   ⇒ None
            }))
        }))

    // Default CTM format with one more field at the end: score
    case Array(show, _, start, duration, word, score) ⇒
      Some(Word(show,
        parseFloatOption(start).getOrElse(0),
        parseFloatOption(duration).getOrElse(0),
        word,
        parseFloatOption(score)))

    // Default CTM format
    case Array(show, _, start, duration, word) ⇒
      Some(Word(show,
        parseFloatOption(start).getOrElse(0),
        parseFloatOption(duration).getOrElse(0),
        word,
        None,
        None))

    case _ ⇒ None
  }

}
