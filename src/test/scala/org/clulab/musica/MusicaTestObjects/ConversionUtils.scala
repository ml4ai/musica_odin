package org.clulab.musica.MusicaTestObjects

import org.clulab.musica.MusicaTestObjects.AtomicObjects._
import org.clulab.musica.MusicaTestObjects.ComplexEvents._
import org.clulab.musica.MusicaTestObjects.SimpleEvents._
import org.clulab.odin.Mention

object ConversionUtils {

  def mentionToString(m: Mention): String = {
    m.label match {
      case "Direction" => Direction(m.text).toMentionString // TBM
      case "Note" => noteMentionToNote(m).toMentionString
      case "Onset" => onsetMentionToOnset(m).toMentionString
      case "Transpose" => transposeMentionToTranspose(m).toMentionString
      case "Step" => stepMentionToStep(m).toMentionString
      case _ => ???
    }
  }

  def beatMentionToBeat(m: Mention): Beat = {
    val cardinality = headText("cardinality", m)
    Beat(cardinality.get)
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

  def stepMentionToStep(step: Mention): Step = {
    val cardinality = step.arguments.get("cardinality").map(_.head.text)
    val prop = step.arguments.get("proportion").map(_.head.text)
    Step(cardinality, prop)
  }

  def transposeMentionToTranspose(t: Mention): Transpose = {
    val note = t.arguments.get("note").map(notes => noteMentionToNote(notes.head))
    val direction = t.arguments.get("direction").map(ds => Direction(ds.head.text))
    val onset = t.arguments.get("onset").map(os => onsetMentionToOnset(os.head))
    val step = t.arguments.get("step").map(steps => stepMentionToStep(steps.head))
    Transpose(note, direction, onset, step)
  }

  // Helper Methods
  def headMention(role: String, m: Mention): Option[Mention] = m.arguments.get(role).map(_.head)
  def headText(role: String, m: Mention): Option[String] = headMention(role, m).map(_.text)
}
