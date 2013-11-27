package fr.lium
package model

import java.io.File


case class Diarization(
  file: MediaFile,
  speakers: Map[Speaker,List[Segment]] = Map())
