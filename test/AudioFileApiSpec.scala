package test

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._
import play.api.libs.Files

import java.io.File

import scala.util.Try

import fr.lium.api.AudioFileApi
import fr.lium.util.FileUtils
import fr.lium.model.AudioFile

import org.apache.commons.io.{FileUtils => ApacheFileUtils}

class AudioFileApiSpec extends Specification
  with CreateSampleDirectories
  with SampleDirectories {

  val env = Env.current

  // TODO
}

