package org.clulab.musica

import org.clulab.musica.MusicaTestObjects.AtomicObjects._
import org.clulab.musica.MusicaTestObjects.ComplexEvents.Transpose
import org.clulab.musica.MusicaTestObjects.ConversionUtils
import org.clulab.musica.MusicaTestObjects.SimpleEvents._
import org.clulab.musica.TestUtils._

class TestTranspose extends ExtractionTest {

  val t1 = "Transpose the C4 quarter note on beat 1 of measure 1 up 5 half steps"

  passingTest should s"extract correctly from $t1" in {
    val mentions = extractMentions(t1)
    val transposeEvents = mentions.filter(_ matches "Transpose")

    transposeEvents should have length(1)
    val found = transposeEvents.head

    val note = Note(Some(Duration("quarter")), Some(Pitch("C4")), Some(Specifier("the")))
    val onset = Onset(Some(Measure("1")), Some(Beat("1")))
    val direction = Direction("up")
    val step = Step(cardinality = Some("5"), proportion = Some("half"))
    val desired = Transpose(
      note = Some(note),
//      note2 = None,
      direction = Some(direction),
      onset = Some(onset),
      step = Some(step)
    )

    testTransposeEvent(found, desired)
  }

  // this one is marked as failing in the corpus, and fails here
  // says transposeEvents length = 0, but webapp shows a transpose event
  val t2 = "Transpose the C quarter note up 1 half step"

  failingTest should s"extract correctly from $t2" in {
    val mentions = extractMentions(t2)
    val transposeEvents = mentions.filter(_ matches "Transpose")

    transposeEvents should have length(1)
    val found = transposeEvents.head

    val note = Note(Some(Duration("quarter")), Some(Pitch("C")), Some(Specifier("the")))
    val onset = Onset(None, None)
    val direction = Direction("up")
    val step = Step(cardinality = Some("1"), proportion = Some("half"))
    val desired = Transpose(
      note = Some(note),
//      note2 = None,
      direction = Some(direction),
      onset = Some(onset),
      step = Some(step)
    )

    testTransposeEvent(found, desired)
  }

  val t3 = "Transpose the C in measure 1 up 1 half step"

  passingTest should s"extract correctly from $t3" in {
    val mentions = extractMentions(t3)
    val transposeEvents = mentions.filter(_ matches "Transpose")

    transposeEvents should have length(1)
    val found = transposeEvents.head

    val note = Note(None, Some(Pitch("C")), Some(Specifier("the")))
    val onset = Onset(Some(Measure("1")), None)
    val direction = Direction("up")
    val step = Step(cardinality = Some("1"), proportion = Some("half"))
    val desired = Transpose(
      note = Some(note),
//      note2 = None,
      direction = Some(direction),
      onset = Some(onset),
      step = Some(step)
    )

    testTransposeEvent(found, desired)
  }

  // testing with two notes; currently doesn't work and direction causes problems
//  val t4 = "Change the G quarter note to an F"
//
//  passingTest should s"extract correctly from $t4" in {
//    val mentions = extractMentions(t4)
//    val transposeEvents = mentions.filter(_ matches "Transpose")
//
//    transposeEvents should have length(1)
//    val found = transposeEvents.head
//
//    val note = Note(Some(Duration("quarter")), Some(Pitch("G")), Some(Specifier("the")))
////    val note2 = Note(None, Some(Pitch("F")), Some(Specifier("an")))
//    val onset = Onset(None, None)
//    val direction = Direction("down")
//    val step = Step(cardinality = None, proportion = None)
//    val desired = Transpose(
//      note = Some(note),
////      note2 = Some(note2),
//      direction = Some(direction),
//      onset = Some(onset),
//      step = Some(step)
//    )
//
//    testTransposeEvent(found, desired)
//  }

  val t5 = "The quarter note in measure 1 is transposed down 2 steps"

  passingTest should s"extract correctly from $t5" in {
    val mentions = extractMentions(t5)
    val transposeEvents = mentions.filter(_ matches "Transpose")

    transposeEvents should have length(1)
    val found = transposeEvents.head

    val note = Note(Some(Duration("quarter")), None, Some(Specifier("The")))
    val onset = Onset(Some(Measure("1")), None)
    val direction = Direction("down")
    val step = Step(cardinality = Some("2"), None)
    val desired = Transpose(
      note = Some(note),
      direction = Some(direction),
      onset = Some(onset),
      step = Some(step)
    )

    testTransposeEvent(found, desired)
  }

  val t6 = "The second note in measure 1 should be transposed down 1 step"

  passingTest should s"extract correctly from $t6" in {
    val mentions = extractMentions(t6)
    val transposeEvents = mentions.filter(_ matches "Transpose")

    transposeEvents should have length(1)
    val found = transposeEvents.head

    val note = Note(None, None, Some(Specifier("The second")))
    val onset = Onset(Some(Measure("1")), None)
    val direction = Direction("down")
    val step = Step(cardinality = Some("1"), None)
    val desired = Transpose(
      note = Some(note),
      direction = Some(direction),
      onset = Some(onset),
      step = Some(step)
    )

    testTransposeEvent(found, desired)
  }
}
