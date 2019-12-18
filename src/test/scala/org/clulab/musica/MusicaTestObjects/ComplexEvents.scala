package org.clulab.musica.MusicaTestObjects

import org.clulab.musica.MusicaTestObjects.AtomicObjects._
import org.clulab.musica.MusicaTestObjects.IntermediateEvents._
import org.clulab.musica.MusicaTestObjects.SimpleEvents._
import org.clulab.musica.MusicaTestObjects.SomewhatComplexEvents._

object ComplexEvents {

  case class Convert(sourceEntity: Option[SourceEntity] = None, sourceLocation: Option[SourceLocation] = None,
                     destEntity: Option[DestEntity] = None, destLocation: Option[DestLocation] = None)
                     extends MusicaObj
  {
    def toMentionString: String = toString
  }

//  case class Convert(sourceEntity: Option[Note]|Option[Rest]|Option[Chord] = None) extends MusicaObj{
//    def toMentionString: String = toString
//  }

  case class Delete(musicalEntity: Option[MusicalEntity] = None, location: Option[Location] = None)
                    extends MusicaObj
  {
    def toMentionString: String = toString
  }

  case class Insert(musicalEntity: Option[MusicalEntity] = None, location: Option[Location] = None,
                    frequency: Option[Frequency] = None) extends MusicaObj
  {
    def toMentionString: String = toString
  }

  case class Invert(musicalEntity: Option[MusicalEntity] = None, location: Option[Location] = None,
                    axis: Option[Axis] = None) extends MusicaObj
  {
    def toMentionString: String = toString
  }

  case class Reverse(musicalEntity: Option[MusicalEntity] = None, location: Option[Location] = None)
                     extends MusicaObj
  {
    def toMentionString: String = toString
  }

  case class Switch(sourceEntity: Option[SourceEntity] = None, sourceLocation: Option[SourceLocation] = None,
                    destEntity: Option[DestEntity] = None, destLocation: Option[DestLocation] = None)
                    extends MusicaObj
  {
    def toMentionString: String = toString
  }

}
