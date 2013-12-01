
package test

import fr.lium.api.SegApi
import fr.lium.model.{ Female, Male, Speaker, Segment }

object SegApiSpec
    extends org.specs2.mutable.Specification with SegFixtures {

  val env = Env.current
  val segApi = env.segApi()

  "Seg API" title

  "Speaker and segment" should {

    "be extracted from proper input" in {
      segApi getInfoFromLine {
        "audio 1 3 735 M S U S0"
      } aka "seg" must beSome((Speaker("S0", "S", Male), Segment(3f, 735f)))
    }

    "not be extracted on commment line" in {
      segApi getInfoFromLine {
        ";; cluster S10 [ score:FS = -24.007352755529855 ] [ score:FT = -25.066834491056138 ] [ score:MS = -23.156822547707108 ] [ score:MT = -23.67201956328635 ]"
      } aka "seg" must beNone
    }
  }



  "Speaker list" should {
    "be extracted from proper input" in {
      segApi.getSpeakersFromLines(sampleLines) aka "speakers" must have size 4
    }

  }


  "Speaker list" should {
    "be extracted from proper input" in {
      segApi.getSpeakersFromLines(sampleLines).get(Speaker("S0", "S", Male)) aka "segments" must beSome(
        List(Segment(3f,735f))
      )

      segApi.getSpeakersFromLines(sampleLines).get(Speaker("S10", "S", Male)) aka "segments" must beSome(
        List(Segment(5743.0f,1096.0f), Segment(3954.0f,863.0f), Segment(849.0f,1126.0f))
      )


      segApi.getSpeakersFromLines(sampleLines).get(Speaker("S13", "S", Male)) aka "segments" must beSome(
        List(Segment(4846.0f,794.0f), Segment(3095.0f,787.0f), Segment(1979.0f,1099.0f))
      )

      segApi.getSpeakersFromLines(sampleLines).get(Speaker("S15", "S", Female)) aka "segments" must beSome(
        List(Segment(7000.0f,96.00f))
      )
    }

  }

}

private[test] trait SegFixtures {
  val sampleLine = "audio 1 3 735 M S U S0"

  val sampleLines = """;; cluster S0 [ score:FS = -25.025719013854324 ] [ score:FT = -26.07859009618822 ] [ score:MS = -24.00701515330892 ] [ score:MT = -24.78770878394768 ]
audio 1 3 735 M S U S0
;; cluster S10 [ score:FS = -24.007352755529855 ] [ score:FT = -25.066834491056138 ] [ score:MS = -23.156822547707108 ] [ score:MT = -23.67201956328635 ]
audio 1 849 1126 M S U S10
audio 1 3954 863 M S U S10
audio 1 5743 1096 M S U S10
audio 1 7000 96 F S U S15
;; cluster S13 [ score:FS = -24.835755606024684 ] [ score:FT = -25.810161228919313 ] [ score:MS = -23.94930273590971 ] [ score:MT = -24.213562907457156 ]
audio 1 1979 1099 M S U S13
audio 1 3095 787 M S U S13
audio 1 4846 794 M S U S13"""

}
