package org.clulab.musica.MusicaTestObjects

import org.clulab.musica.MusicaTestObjects.SimpleEvents._
import org.clulab.musica.MusicaTestObjects.IntermediateEvents._

object SomewhatComplexEvents {

  // todo: this entire file might not be required

  case class SourceEntity(musicalEntity: Option[MusicalEntity] = None) extends MusicaObj
  {
    def toMentionString: String = toString
  }

  case class DestEntity(musicalEntity: Option[MusicalEntity] = None) extends MusicaObj
  {
    def toMentionString: String = toString
  }

  case class SourceLocation(location: Option[Location] = None) extends MusicaObj
  {
    def toMentionString: String = toString
  }

  case class DestLocation(location: Option[Location] = None) extends MusicaObj
  {
    def toMentionString: String = toString
  }

}
