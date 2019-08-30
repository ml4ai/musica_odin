package org.clulab.musica.MusicaTestObjects

import org.clulab.musica.MusicaTestObjects.AtomicObjects._
import org.clulab.musica.MusicaTestObjects.SimpleEvents._

object ComplexEvents {

  case class Transpose(note: Option[Note] = None, direction: Option[Direction] = None, onset: Option[Onset] = None, step: Option[Step] = None) extends MusicaObj
  {
    def toMentionString: String = toString
  }
}
