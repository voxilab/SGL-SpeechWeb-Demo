package fr.lium
package model

import java.io.File

case class Segment(
  start: Float,
  duration: Float,
  speaker: Option[Speaker] = None)
