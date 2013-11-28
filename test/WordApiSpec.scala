package test

import fr.lium.api.WordApi
import fr.lium.model.{ Male, Speaker, Word }

object WordApiSpec
    extends org.specs2.mutable.Specification with WordFixtures {

  "Word API" title

  "Word" should {
    "be extracted from proper input" in {
      WordApi getWordFromLine {
        "BFMTV_BFMStory_2012-01-10_175800 1 2406.395 0.02 donc 1.00 M S S23"
      } aka "word" must beSome(Word(
        show = "BFMTV_BFMStory_2012-01-10_175800",
        start = 2406.395f,
        duration = 0.02f,
        word = "donc",
        score = Some(1f),
        speaker = Some(Speaker("S23", "S", Male))))
    }

    "be extracted without a speaker on N/A values" in {
      WordApi getWordFromLine {
        "BFMTV_BFMStory_2012-01-10_175800 1 2406.395 0.02 donc 1.00 N/A N/A N/A"
      } aka "word" must beSome(Word(
        show = "BFMTV_BFMStory_2012-01-10_175800",
        start = 2406.395f,
        duration = 0.02f,
        word = "donc",
        score = Some(1f)))
    }

    "not be extracted on missing field" in {
      WordApi getWordFromLine {
        "BFMTV_BFMStory_2012-01-10_175800 2406.395 0.02 donc 1.00 M S S23"
      } aka "word" must beNone
    }
  }

  "Word list" should {
    "be extracted from proper input" in {
      WordApi.getWordsFromLines(sampleLines) aka "words" must have size 4
    }

    "be extracted from bad input" in {
      WordApi.getWordsFromLines(badSampleLines) aka "words" must have size 3
    }
  }
}

private[test] trait WordFixtures {
  val sampleLines = """BFMTV_BFMStory_2012-01-10_175800 1 2407.27 0.02 est 0.998 M S S23
BFMTV_BFMStory_2012-01-10_175800 1 2407.43 0.02 un 1.00 M S S23
BFMTV_BFMStory_2012-01-10_175800 1 2407.725 0.02 gouvernement 1.00 M S S23
BFMTV_BFMStory_2012-01-10_175800 1 2408.33 0.02 responsable 1.00 M S S23"""

  val badSampleLines = """BFMTV_BFMStory_2012-01-10_175800 1 2407.27 0.02 est 0.998 M S S23
BFMTV_BFMStory_2012-01-10_175800 2407.43 0.02 un 1.00 M S S23
BFMTV_BFMStory_2012-01-10_175800 1 2407.725 0.02 gouvernement 1.00 N/A N/A N/A
BFMTV_BFMStory_2012-01-10_175800 1 2408.33 0.02 responsable 1.00 M S S23"""

}
