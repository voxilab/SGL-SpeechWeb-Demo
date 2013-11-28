
package test

import fr.lium.api.SegApi
import fr.lium.model.{ Male, Speaker, Segment }

object SegApiSpec
    extends org.specs2.mutable.Specification with SegFixtures {

  "Seg API" title

  "Speaker and segment" should {
    "be extracted from proper input" in {
      SegApi getSpeakerFromLine {
        "audio 1 3 735 M S U S0"
      } aka "seg" must beSome((Speaker("S0", "S", Male), Segment(3.00f, 735.00f)))
    }

    "not be extracted on commment line" in {
      SegApi getSpeakerFromLine {
        ";; cluster S10 [ score:FS = -24.007352755529855 ] [ score:FT = -25.066834491056138 ] [ score:MS = -23.156822547707108 ] [ score:MT = -23.67201956328635 ]"
      } aka "seg" must beNone
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
;; cluster S13 [ score:FS = -24.835755606024684 ] [ score:FT = -25.810161228919313 ] [ score:MS = -23.94930273590971 ] [ score:MT = -24.213562907457156 ]
audio 1 1979 1099 M S U S13
audio 1 3095 787 M S U S13
audio 1 4846 794 M S U S13"""

}
