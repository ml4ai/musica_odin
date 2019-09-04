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
      direction = Some(direction),
      onset = Some(onset),
      step = Some(step)
    )

    testTransposeEvent(found, desired)
  }

  val t2 = "Transpose the C quarter note up 1 half step"

  passingTest should s"extract correctly from $t2" in {
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
      direction = Some(direction),
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
      direction = Some(direction),
      onset = Some(onset),
      step = Some(step)
    )

    testTransposeEvent(found, desired)
  }

//   testing with two notes; currently doesn't work and direction causes problems
  val t4 = "Change the G quarter note to an F"

  passingTest should s"extract correctly from $t4" in {
    val mentions = extractMentions(t4)
    val transposeEvents = mentions.filter(_ matches "Transpose")

    transposeEvents should have length(1)
    val found = transposeEvents.head

    val note = Note(Some(Duration("quarter")), Some(Pitch("G")), Some(Specifier("the")))
    val final_note = Note(None, Some(Pitch("F")), Some(Specifier("an")))
    val desired = Transpose(
      note = Some(note),
      final_note = Some(final_note)
    )

    testTransposeEvent(found, desired)
  }

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

//  // requires 3 notes
//  val t7 = "Change the second note in the first measure from a G to an A"
//
//  passingTest should s"extract correctly from $t7" in {
//    val mentions = extractMentions(t7)
//    val transposeEvents = mentions.filter(_ matches "Transpose")
//
//    transposeEvents should have length(1)
//    val found = transposeEvents.head
//
//    val original_note = Note(None, Some(Pitch("G")), Some(Specifier("a")))
//    val onset = Onset(Some(Measure("first")), None)
//    val step = Step(cardinality = Some("1"), None)
//    val final_note = Note(None, Some(Pitch("A")), Some(Specifier("an")))
//    val generic_note = Note(None, None, Some(Specifier("the second")))
//    val desired = Transpose(
//      original_note = Some(original_note),
//      onset = Some(onset),
//      step = Some(step),
//      final_note = Some(final_note),
//      generic_note = Some(generic_note)
//    )
//
//    testTransposeEvent(found, desired)
//  }

  // do we want to extract transpose events with no pitch OR direction of change?
  val t8 = "Move the second quarter note in measure 2 two steps"

  passingTest should s"extract correctly from $t8" in {
    val mentions = extractMentions(t8)
    val transposeEvents = mentions.filter(_ matches "Transpose")

    transposeEvents should have length(1)
    val found = transposeEvents.head

    val note = Note(Some(Duration("quarter")), None, Some(Specifier("the second")))
    val onset = Onset(Some(Measure("2")), None)
    val step = Step(cardinality = Some("two"), None)
    val desired = Transpose(
      note = Some(note),
      onset = Some(onset),
      step = Some(step)
    )

    testTransposeEvent(found, desired)
  }

  // requires 3 notes
  val t9 = "In the 3rd bar you want to change the 3rd note from a d to an a"

  failingTest should s"extract correctly from $t9" in {
    val mentions = extractMentions(t9)
    val transposeEvents = mentions.filter(_ matches "Transpose")

    transposeEvents should have length(1)
    val found = transposeEvents.head

    val note = Note(None, None, Some(Specifier("the 3rd")))
    val onset = Onset(Some(Measure("3rd")), None)
    val step = Step(cardinality = Some("two"), None)
    val desired = Transpose(
      note = Some(note),
      direction = None,
      onset = Some(onset),
      step = Some(step)
    )

    testTransposeEvent(found, desired)
  }

  val t10 = "Raise the second quarter note in measure 1 2 half steps to a D"

  passingTest should s"extract correctly from $t10" in {
    val mentions = extractMentions(t10)
    val transposeEvents = mentions.filter(_ matches "Transpose")

    transposeEvents should have length(1)
    val found = transposeEvents.head

    val note = Note(Some(Duration("quarter")), None, Some(Specifier("the second")))
    val onset = Onset(Some(Measure("1")), None)
    val step = Step(cardinality = Some("2"), proportion = Some("half"))
    val final_note = Note(None, Some(Pitch("D")), Some(Specifier("a")))
    val desired = Transpose(
      note = Some(note),
      onset = Some(onset),
      step = Some(step),
      final_note = Some(final_note)
    )

    testTransposeEvent(found, desired)
  }

  /*
  todo: Eventually we will need to deal with the entire statement at once:
  The three notes in the third bar changed. The first note switched from a half note to a
  quarter note and moved up two notes. As a result, the second not emoved further down and
  the third note switched from a quarter note to a half note and moved down four steps.
   */
  // only examining the transposition -- should we deal with 'notes' for steps?
  val t11 = "The first note switched from a half note to a quarter note and moved up two notes"

  passingTest should s"extract correctly from $t11" in {
    val mentions = extractMentions(t11)
    val transposeEvents = mentions.filter(_ matches "Transpose")

    transposeEvents should have length(1)
    val found = transposeEvents.head

    val note = Note(None, None, Some(Specifier("The first")))
    val direction = Direction("up")
    val step = Step(cardinality = Some("two"), None)
    val desired = Transpose(
      note = Some(note),
      direction = Some(direction),
      step = Some(step)
    )

    testTransposeEvent(found, desired)
  }

//  // todo: create 'TestChange' and move this there
//  val t12 = "The three notes in the third bar changed."
//
//  passingTest should s"extract correctly from $t12" in {
//    val mentions = extractMentions(t12)
//    val transposeEvents = mentions.filter(_ matches "Transpose")
//
//    transposeEvents should have length(1)
//    val found = transposeEvents.head
//
//    val note = Note(None, None, Some(Specifier("The three")))
//    val onset = Onset(Some(Measure("third")), None)
//    val desired = Transpose(
//      note = Some(note),
//      onset = Some(onset)
//    )
//
//    testTransposeEvent(found, desired)
//  }


}
