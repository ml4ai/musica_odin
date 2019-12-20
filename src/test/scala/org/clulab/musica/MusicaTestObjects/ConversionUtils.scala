package org.clulab.musica.MusicaTestObjects

import com.google.protobuf.Field.Cardinality
import org.clulab.musica.MusicaTestObjects.AtomicObjects._
import org.clulab.musica.MusicaTestObjects.ComplexEvents._
import org.clulab.musica.MusicaTestObjects.SimpleEvents._
import org.clulab.musica.MusicaTestObjects.IntermediateEvents._
import org.clulab.odin.{EventMention, Mention}

object ConversionUtils {

  def mentionToString(m: Mention): String = {
    m.label match {
//      case "Cardinality" => Cardinality(m.text).toMentionString
//      case "Axis" => axisMentionToAxis(m).toMentionString
      case "Beat" => beatMentionToBeat(m).toMentionString
      case "Chord" => chordMentionToChord(m).toMentionString
      case "Convert" => convertMentionToConvert(m).toMentionString
      case "Delete" => deleteMentionToDelete(m).toMentionString
//      case "DestEntity" => destEntityMentionToDestEntity(m).toMentionString
//      case "DestLocation" => destLocationMentionToDestLocation(m).toMentionString
      case "Direction" => Direction(m.text).toMentionString // TBM
      case "Everything" => Everything(m.text).toMentionString
      case "Frequency" => frequencyMentionToFrequency(m).toMentionString
      case "Insert" => insertMentionToInsert(m).toMentionString
      case "Invert" => invertMentionToInvert(m).toMentionString
      case "Location" => locationMentionToLocation(m).toMentionString
      case "Measure" => measureMentionToMeasure(m).toMentionString
      case "MusicalEntity" => musicalEntityMentionToMusicalEntity(m).toMentionString
      case "Note" => noteMentionToNote(m).toMentionString
      case "Pitch" => pitchMentionToPitch(m).toMentionString
      case "Rest" => restMentionToRest(m).toMentionString
      case "Reverse" => reverseMentionToReverse(m).toMentionString
//      case "SourceEntity" => sourceEntityMentionToSourceEntity(m).toMentionString
//      case "SourceLocation" => sourceLocationMentionToSourceLocation(m).toMentionString
      case "Step" => stepMentionToStep(m).toMentionString
//      case "Switch" => switchMentionToSwitch(m).toMentionString
      case _ => ???
    }
  }

//  def axisMentionToAxis(m: Mention): Axis = {
//    m.label match {
//      case "Pitch" => pitchMentionToPitch(m)
//      case "Note" => noteMentionToNote(m)
//      case _ => ???
//    }
//
//    val pitch = headText("pitch", m).map(Pitch)
//    val note = m.arguments.get("note").map(ns => noteMentionToNote(ns.head))
//
//    Axis(pitch, note)
//  }

  def beatMentionToBeat(m: Mention): Beat = {
    val cardinality = headText("cardinality", m)
    Beat(cardinality.get)
  }

  def chordMentionToChord(chord: Mention): Chord = {
    //    val cardinality = chord.arguments.get("cardinality").map(_.head.text)
    val cardinality = Option(headText("cardinality", chord).get)
    val chordtype = headText("chordType", chord).map(ChordType)
    val specifier = headText("specifier", chord).map(Specifier)
    Chord(cardinality, chordtype, specifier)
  }

  // todo: is this just all the mentions that appear in the ComplexEvents file?

  def convertMentionToConvert(c: Mention): Convert = {
    val sourceEntity = c.arguments.get("sourceEntity").map(se => musicalEntityMentionToMusicalEntity(se.head))
    val sourceLocation = c.arguments.get("sourceLocation").map(sl => locationMentionToLocation(sl.head))
    val destEntity = c.arguments.get("destEntity").map(de => musicalEntityMentionToMusicalEntity(de.head))
    val destLocation = c.arguments.get("destLocation").map(dl => locationMentionToLocation(dl.head))

    Convert(sourceEntity, sourceLocation, destEntity, destLocation)
  }

  def deleteMentionToDelete(d: Mention): Delete = {
    val musicalEntity = d.arguments.get("musicalEntity").map(me => musicalEntityMentionToMusicalEntity(me.head))
    val location = d.arguments.get("location").map(l => locationMentionToLocation(l.head))

    Delete(musicalEntity, location)
  }

//  def destEntityMentionToDestEntity(m: Mention): DestEntity = {
//    val musEnt = musicalEntityMentionToMusicalEntity(m)
//
//    DestEntity(Option(musEnt))
//  }
//
//  def destLocationMentionToDestLocation(m: Mention): DestLocation = {
//    val location = locationMentionToLocation(m)
//
//    DestLocation(Option(location))
//  }

  def directionMentionToDirection(m:Mention): Direction = {
    val direction = headText("direction", m).get
    Direction(direction)
  }

  def frequencyMentionToFrequency(m: Mention): Frequency = {
    val frequency = headText("frequency", m).get

    Frequency(frequency)
  }

  def insertMentionToInsert(i: Mention): Insert = {
    val musicalEntity = i.arguments.get("musicalEntity").map(me => musicalEntityMentionToMusicalEntity(me.head))
    val location = i.arguments.get("location").map(l => locationMentionToLocation(l.head))
    val frequency = i.arguments.get("frequency").map(f => frequencyMentionToFrequency(f.head))

    Insert(musicalEntity, location, frequency)
  }

  def invertMentionToInvert(i: Mention): Invert = {
    val musicalEntity = i.arguments.get("musicalEntity").map(me => musicalEntityMentionToMusicalEntity(me.head))
    val location = i.arguments.get("location").map(l => locationMentionToLocation(l.head))
    val axis = i.arguments.get("axis").map(a => musicalEntityMentionToMusicalEntity(a.head)).map(a => a.asInstanceOf[Axis])

    Invert(musicalEntity, location, axis)
  }

  def locationMentionToLocation(m: Mention): Location = {
    val locationTerm = m match {
      case em: EventMention => Some(LocationTerm(em.trigger.text))
      case _ => None
    }
//    val locationTerm = headText("locationTerm", m).map(LocationTerm)
    val measure = headText("measure", m).map(Measure)

    val note = m.arguments.get("note").map(ns => noteMentionToNote(ns.head))
    val chord = m.arguments.get("chord").map(ch => chordMentionToChord(ch.head))
    val rest = m.arguments.get("rest").map(rs => restMentionToRest(rs.head))

    Location(locationTerm, note, measure, chord, rest)
  }

  def measureMentionToMeasure(m: Mention): Measure = {
    val cardinality = headText("cardinality", m)
    Measure(cardinality.get)
  }


  def musicalEntityMentionToMusicalEntity(m: Mention): MusicalEntity = {
    m.label match {
      case "Note" => noteMentionToNote(m)
      case "Chord" => chordMentionToChord(m)
      case "Rest" => restMentionToRest(m)
      case "Pitch" => pitchMentionToPitch(m)
      case _ => ???
    }
  }

  def noteMentionToNote(note: Mention): Note = {
    val duration = headText("duration", note).map(Duration)
    val pitch = headText("pitch", note).map(Pitch)
    val specifier = headText("specifier", note).map(Specifier)
    Note(duration, pitch, specifier)
  }

  def pitchMentionToPitch(m: Mention): Pitch = {
    val pitch = headText("pitch", m)
    Pitch(pitch.get)
  }

  def restMentionToRest(rest: Mention): Rest = {
    val specifier = headText("specifier", rest).map(Specifier)
    val duration = headText("duration", rest).map(Duration)
    Rest(specifier, duration)
  }


  def reverseMentionToReverse(r: Mention): Reverse = {
    val musicalEntity = r.arguments.get("musicalEntity").map(me => musicalEntityMentionToMusicalEntity(me.head))
    val location = r.arguments.get("location").map(l => locationMentionToLocation(l.head))

    Reverse(musicalEntity, location)
  }

//  // todo: do we need different ones for this or can ALL sourceEntities be musical entities?
//  def sourceEntityMentionToSourceEntity(m: Mention): SourceEntity = {
//    val musEnt = musicalEntityMentionToMusicalEntity(m)
//
//    SourceEntity(Option(musEnt))
//  }
//
//  // todo: do we need different ones for this or can ALL sourceLocations be musical entities?
//  def sourceLocationMentionToSourceLocation(m: Mention): SourceLocation = {
//    val location = locationMentionToLocation(m)
//
//    SourceLocation(Option(location))
//  }

  def stepMentionToStep(step: Mention): Step = {
    val cardinality = step.arguments.get("cardinality").map(_.head.text)
    val prop = step.arguments.get("proportion").map(_.head.text)
    Step(cardinality, prop)
  }

//  def switchMentionToSwitch(s: Mention): Switch = {
//    val sourceEntity = s.arguments.get("sourceEntity").map(se => sourceEntityMentionToSourceEntity(se.head))
//    val sourceLocation = s.arguments.get("sourceLocation").map(sl => sourceLocationMentionToSourceLocation(sl.head))
//    val destEntity = s.arguments.get("destEntity").map(de => destEntityMentionToDestEntity(de.head))
//    val destLocation = s.arguments.get("destLocation").map(dl => destLocationMentionToDestLocation(dl.head))
//
//    Switch(sourceEntity, sourceLocation, destEntity, destLocation)
//  }

  // Helper Methods
  def headMention(role: String, m: Mention): Option[Mention] = m.arguments.get(role).map(_.head)
  def headText(role: String, m: Mention): Option[String] = headMention(role, m).map(_.text)
}
