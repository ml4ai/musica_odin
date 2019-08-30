package org.clulab.musica.MusicaTestObjects

import org.clulab.musica.MusicaTestObjects.AtomicObjects._
import org.clulab.musica.MusicaTestObjects.SimpleEvents._

object ComplexEvents {

  case class Transpose(note: Option[Note], direction: Option[Direction], onset: Option[Onset], step: Option[Step]) extends MusicaObj
  {
    def toMentionString: String = toString
  }
}
