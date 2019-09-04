package org.clulab.musica

import org.clulab.musica.MusicaTestObjects.AtomicObjects._
import org.clulab.musica.MusicaTestObjects.ComplexEvents.{Delete}
import org.clulab.musica.MusicaTestObjects.SimpleEvents._
import org.clulab.musica.MusicaTestObjects.ConversionUtils
import org.clulab.musica.TestUtils._

class TestDelete extends ExtractionTest {

  val t1 = "Delete the B quarter note at measure 1 beat 1"

  passingTest should s"extract correctly from $t1" in {
    val mentions = extractMentions(t1)
    val deleteEvents = mentions.filter(_ matches "Delete")
    //    println(ConversionUtils.mentionToString(convertEvents.head))

    deleteEvents should have length(1)
    val found = deleteEvents.head

    val note = Note(Some(Duration("quarter")), Some(Pitch("B")), Some(Specifier("the")))
    val onset = Onset(Some(Measure("1")), Some(Beat("1")))
    val desired = Delete(
      note = Some(note),
      onset = Some(onset)
    )

    testDeleteEvent(found, desired)
  }

  val t2 = "The eighth note should be deleted"

  passingTest should s"extract correctly from $t2" in {
    val mentions = extractMentions(t2)
//    for (mention <- mentions) {
//      println(ConversionUtils.mentionToString(mention))
//    }
    val deleteEvents = mentions.filter(_ matches "Delete")
//      println(ConversionUtils.mentionToString(deleteEvents.head))

    deleteEvents should have length(1)
    val found = deleteEvents.head

    val note = Note(Some(Duration("eighth")), None, Some(Specifier("The")))
    val onset = Onset(None, None)
    val desired = Delete(
      note = Some(note)
    )

    testDeleteEvent(found, desired)
  }

  val t3 = "Delete all the whole notes"

  passingTest should s"extract correctly from $t3" in {
    val mentions = extractMentions(t3)
    val deleteEvents = mentions.filter(_ matches "Delete")

    deleteEvents should have length(1)
    val found = deleteEvents.head

    val note = Note(Some(Duration("whole")), None, Some(Specifier("all the")))
    val onset = Onset(None, None)
    val desired = Delete(
      note = Some(note)
    )

    testDeleteEvent(found, desired)
  }

  // needs 'everything' handling
  val t4 = "Delete everything in the first two bars"

  failingTest should s"extract correctly from $t4" in {
    val mentions = extractMentions(t4)
    val deleteEvents = mentions.filter(_ matches "Delete")
    //    println(ConversionUtils.mentionToString(convertEvents.head))

    deleteEvents should have length(1)
    val found = deleteEvents.head

    val note = Note(Some(Duration("quarter")), Some(Pitch("B")), Some(Specifier("the")))
    val onset = Onset(Some(Measure("1")), Some(Beat("1")))
    val desired = Delete(
      note = Some(note),
      onset = Some(onset)
    )

    testDeleteEvent(found, desired)
  }

  // needs 'everything' AND relative location handling
  val t5 = "Delete everything before the first half note"

  failingTest should s"extract correctly from $t5" in {
    val mentions = extractMentions(t5)
    val deleteEvents = mentions.filter(_ matches "Delete")
    //    println(ConversionUtils.mentionToString(convertEvents.head))

    deleteEvents should have length(1)
    val found = deleteEvents.head

    val note = Note(Some(Duration("quarter")), Some(Pitch("B")), Some(Specifier("the")))
    val onset = Onset(Some(Measure("1")), Some(Beat("1")))
    val desired = Delete(
      note = Some(note),
      onset = Some(onset)
    )

    testDeleteEvent(found, desired)
  }
}
