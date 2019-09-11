package org.clulab.musica.MusicaTestObjects

import com.google.protobuf.Field.Cardinality
import org.clulab.musica.MusicaTestObjects.AtomicObjects._
import org.clulab.musica.MusicaTestObjects.ComplexEvents._
import org.clulab.musica.MusicaTestObjects.SimpleEvents._
import org.clulab.odin.Mention

object ConversionUtils {

  def mentionToString(m: Mention): String = {
    m.label match {
//      case "Cardinality" => Cardinality(m.text).toMentionString
      case "Chord" => chordMentionToChord(m).toMentionString
      case "Direction" => Direction(m.text).toMentionString // TBM
      case "Everything" => Everything(m.text).toMentionString
      case "LocationAbs" => locationabsMentionToLocationAbs(m).toMentionString
      case "LocationRel" => locationrelMentionToLocationRel(m).toMentionString
      case "Measure" => measureMentionToMeasure(m).toMentionString
      case "Note" => noteMentionToNote(m).toMentionString
      case "Onset" => onsetMentionToOnset(m).toMentionString
      case "Pitch" => pitchMentionToPitch(m).toMentionString
      case "Rest" => restMentionToRest(m).toMentionString
      case "Step" => stepMentionToStep(m).toMentionString
      case "Transpose" => transposeMentionToTranspose(m).toMentionString
      case _ => ???
    }
  }

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

  def directionMentionToDirection(m:Mention): Direction = {
    val direction = headText("direction", m)
    Direction(direction.get)
  }

  def locationabsMentionToLocationAbs(m: Mention): LocationAbs = {
    val location = headText("locationabs", m)
    LocationAbs(location.get)
  }

  def locationrelMentionToLocationRel(m: Mention): LocationRel = {
    val location = headText("locationrel", m)
    LocationRel(location.get)
  }

  def measureMentionToMeasure(m: Mention): Measure = {
    val cardinality = headText("cardinality", m)
    Measure(cardinality.get)
  }

  def noteMentionToNote(note: Mention): Note = {
    val duration = headText("duration", note).map(Duration)
    val pitch = headText("pitch", note).map(Pitch)
    val specifier = headText("specifier", note).map(Specifier)
    Note(duration, pitch, specifier)
  }

  def onsetMentionToOnset(onset: Mention): Onset = {
    val measure = headMention("measure", onset).map(measureMentionToMeasure)
    val beat = headMention("beat", onset).map(beatMentionToBeat)
    Onset(measure, beat)
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

  def stepMentionToStep(step: Mention): Step = {
    val cardinality = step.arguments.get("cardinality").map(_.head.text)
    val prop = step.arguments.get("proportion").map(_.head.text)
    Step(cardinality, prop)
  }

  def transposeMentionToTranspose(t: Mention): Transpose = {
    val note = t.arguments.get("note").map(notes => noteMentionToNote(notes.head))
//    val note2 = t.arguments.get("note2").map(notes => noteMentionToNote(notes.head))
    val direction = t.arguments.get("direction").map(ds => Direction(ds.head.text))
    val onset = t.arguments.get("onset").map(os => onsetMentionToOnset(os.head))
    val step = t.arguments.get("step").map(steps => stepMentionToStep(steps.head))
    val chord = t.arguments.get("chord").map(chords => chordMentionToChord(chords.head))
    Transpose(note, direction, onset, step, chord)
//    Transpose(note, note2, direction, onset, step)
  }

  // Helper Methods
  def headMention(role: String, m: Mention): Option[Mention] = m.arguments.get(role).map(_.head)
  def headText(role: String, m: Mention): Option[String] = headMention(role, m).map(_.text)
}
