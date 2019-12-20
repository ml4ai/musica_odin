package org.clulab.musica.MusicaTestObjects

import org.clulab.musica.MusicaTestObjects.AtomicObjects._
import org.clulab.musica.MusicaTestObjects.IntermediateEvents._
import org.clulab.musica.MusicaTestObjects.SimpleEvents._

object ComplexEvents {

  case class Convert(sourceEntity: Option[MusicalEntity] = None, sourceLocation: Option[Location] = None,
                     destEntity: Option[MusicalEntity] = None, destLocation: Option[Location] = None)
                     extends MusicaObj
  {
    def toMentionString: String = toString
  }


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


  case class Switch(sourceEntity: Option[MusicalEntity] = None, sourceLocation: Option[Location] = None,
                    destEntity: Option[MusicalEntity] = None, destLocation: Option[Location] = None)
                    extends MusicaObj
  {
    def toMentionString: String = toString
  }

  case class Transpose(musicalEntity: Option[MusicalEntity] = None, location: Option[Location] = None,
                       direction: Option[Direction] = None, step: Option[Step] = None) extends MusicaObj
  {
    def toMentionString: String = toString
  }

}
