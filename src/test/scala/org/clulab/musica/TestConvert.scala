package org.clulab.musica

import org.clulab.musica.MusicaTestObjects.AtomicObjects._
import org.clulab.musica.MusicaTestObjects.ComplexEvents.{Convert}
import org.clulab.musica.MusicaTestObjects.SimpleEvents._
import org.clulab.musica.MusicaTestObjects.ConversionUtils
import org.clulab.musica.TestUtils._

class TestConvert extends ExtractionTest {
  val t1 = "Convert the quarter note in measure 1 into a quarter rest"

  passingTest should s"extract correctly from $t1" in {
    val mentions = extractMentions(t1)
    val convertEvents = mentions.filter(_ matches "Convert")
//    println(ConversionUtils.mentionToString(convertEvents.head))

    convertEvents should have length(1)
    val found = convertEvents.head

    val note = Note(Some(Duration("quarter")), None, Some(Specifier("the")))
    val onset = Onset(Some(Measure("1")), None)
    val rest = Rest(Some(Specifier("a")), Some(Duration("quarter")))
    val desired = Convert(
      note = Some(note),
      onset = Some(onset),
      rest = Some(rest)
    )

    testConvertEvent(found, desired)
  }

  // only dealing with the conversion event
  // this one will be very hard to do
  // todo: relative location needed
  val t2 = "In the first measure, after the C quarter note, take the D eighth note out and change it to an eighth note rest."

  passingTest should s"extract correctly from $t2" in {
    val mentions = extractMentions(t2)
    val convertEvents = mentions.filter(_ matches "Convert")

    convertEvents should have length(1)
    val found = convertEvents.head

    val note = Note(Some(Duration("eighth")), Some(Pitch("D")), Some(Specifier("the")))
    val onset = Onset(Some(Measure("the first")), None)
    val rest = Rest(Some(Specifier("an")), Some(Duration("eighth")))
    val desired = Convert(
      note = Some(note),
      onset = Some(onset),
      rest = Some(rest)
    )

    testConvertEvent(found, desired)
  }

  val t3 = "Change the D quarter note into a minor fifth"

  passingTest should s"extract correctly from $t3" in {
    val mentions = extractMentions(t3)
    val convertEvents = mentions.filter(_ matches "Convert")

    convertEvents should have length(1)
    val found = convertEvents.head

    val note = Note(Some(Duration("quarter")), Some(Pitch("D")), Some(Specifier("the")))
    val chord = Chord(cardinality = Some("fifth"), Some(ChordType("minor")), Some(Specifier("a")))
    val desired = Convert(
      note = Some(note),
      chord = Some(chord)
    )

  }
}
