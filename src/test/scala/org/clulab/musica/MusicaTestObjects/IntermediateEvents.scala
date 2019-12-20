package org.clulab.musica.MusicaTestObjects

import org.clulab.musica.MusicaTestObjects.AtomicObjects._
import org.clulab.musica.MusicaTestObjects.SimpleEvents._

object IntermediateEvents {

//  case class Axis(pitch: Option[Pitch] = None, note: Option[Note] = None) extends MusicaObj
//  {
//    def toMentionString: String = toString
//  }

  // this is going to need the location term PLUS a measure or note or chord or interval
  // what about 'at the end of the score?'
  // 'after the end of the score'? --> is 'after' or 'end' marked as location term?
  case class Location(locationTerm: Option[LocationTerm] = None, note: Option[Note] = None,
                      measure: Option[Measure] = None, beat: Option[Beat] = None, chord: Option[Chord] = None,
                      rest: Option[Rest]) extends MusicaObj
  {
    def toMentionString: String = toString
  }



//  case class MusicalEntity(note: Option[Note] = None, chord: Option[Chord] = None,
//                           rest: Option[Rest] = None) extends MusicaObj
//  {
//    def toMentionString: String = toString
//  }

}
