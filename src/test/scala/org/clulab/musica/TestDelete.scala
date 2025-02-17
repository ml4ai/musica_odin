package org.clulab.musica

import org.clulab.musica.MusicaTestObjects.AtomicObjects._
import org.clulab.musica.MusicaTestObjects.ComplexEvents.{Delete}
import org.clulab.musica.MusicaTestObjects.SimpleEvents._
import org.clulab.musica.MusicaTestObjects.IntermediateEvents._
import org.clulab.musica.MusicaTestObjects.ConversionUtils
import org.clulab.musica.TestUtils._

class TestDelete extends ExtractionTest {

  val t1 = "Delete the B quarter note at measure 1 beat 1"

  passingTest should s"extract correctly from $t1" in {
    val mentions = extractMentions(t1)
    val deleteEvents = mentions.filter(_ matches "Delete")
    //    println(ConversionUtils.mentionToString(convertEvents.head))

    deleteEvents should have length (1)
    val found = deleteEvents.head

    val musicalEntity = Note(Some(Duration("quarter")), Some(Pitch("B")), Some(Specifier("the")))
    val location = Location(Some(LocationTerm("at")), None, Some(Measure("measure 1")),
      Some(Beat("beat 1")), None, None)
    val desired = Delete(
      Some(musicalEntity),
      Some(location)
    )

    testDeleteEvent(found, desired)
  }


  val t2 = "The eighth note should be deleted"

  passingTest should s"extract correctly from $t2" in {
    val mentions = extractMentions(t2)

    val deleteEvents = mentions.filter(_ matches "Delete")
//      println(ConversionUtils.mentionToString(deleteEvents.head))

    deleteEvents should have length(1)
    val found = deleteEvents.head

    val musicalEntity = Note(Some(Duration("eighth")), None, Some(Specifier("The")))
    val desired = Delete(
      Some(musicalEntity)
    )

    testDeleteEvent(found, desired)
  }

  val t3 = "Delete all the whole notes"

  passingTest should s"extract correctly from $t3" in {
    val mentions = extractMentions(t3)
    val deleteEvents = mentions.filter(_ matches "Delete")

    deleteEvents should have length(1)
    val found = deleteEvents.head

    val musicalEntity = Note(Some(Duration("whole")), None, Some(Specifier("all the")))
    val desired = Delete(
      Some(musicalEntity)
    )

    testDeleteEvent(found, desired)
  }

  val t4 = "Delete everything in the first two bars"

  passingTest should s"extract correctly from $t4" in {
    val mentions = extractMentions(t4)
    val deleteEvents = mentions.filter(_ matches "Delete")
    //    println(ConversionUtils.mentionToString(convertEvents.head))

    deleteEvents should have length(1)
    val found = deleteEvents.head

    val musicalEntity = Everything("everything")
    val location = Location(Some(LocationTerm("in")), None, Some(Measure("the first two bars")), None, None, None)
    val desired = Delete(
      Some(musicalEntity),
      Some(location)
    )

    testDeleteEvent(found, desired)
  }

  val t5 = "Delete the notes in the first two bars"

  passingTest should s"extract correctly from $t5" in {
    val mentions = extractMentions(t5)
    val deleteEvents = mentions.filter(_ matches "Delete")
    //    println(ConversionUtils.mentionToString(convertEvents.head))

    deleteEvents should have length(1)
    val found = deleteEvents.head

    val musicalEntity = Note(None, None, Some(Specifier("the")))
    val location = Location(Some(LocationTerm("in")), None, Some(Measure("the first two bars")), None, None, None)
    val desired = Delete(
      Some(musicalEntity),
      Some(location)
    )

    testDeleteEvent(found, desired)
  }

  val t6 = "Delete everything before the first half note"

  passingTest should s"extract correctly from $t6" in {
    val mentions = extractMentions(t6)
    val deleteEvents = mentions.filter(_ matches "Delete")
    //    println(ConversionUtils.mentionToString(convertEvents.head))

    deleteEvents should have length(1)
    val found = deleteEvents.head

    val musicalEntity = Everything("everything")
    val location = Location(Some(LocationTerm("before")), Some(Note(Some(Duration("half")), None, Some(Specifier("the first")))),
      None, None, None, None)

    val desired = Delete(
      Some(musicalEntity),
      Some(location)
    )

    testDeleteEvent(found, desired)
  }

  val t7 = "Delete all the quarter notes before the first half note"

  passingTest should s"extract correctly from $t7" in {
    val mentions = extractMentions(t7)
    val deleteEvents = mentions.filter(_ matches "Delete")
    //    println(ConversionUtils.mentionToString(convertEvents.head))

    deleteEvents should have length(1)
    val found = deleteEvents.head

    val musicalEntity = Note(Some(Duration("quarter")), None, Some(Specifier("all the")))
    val location = Location(Some(LocationTerm("before")), Some(Note(Some(Duration("half")), None, Some(Specifier("the first")))),
      None, None, None, None)

    val desired = Delete(
      Some(musicalEntity),
      Some(location)
    )

    testDeleteEvent(found, desired)
  }
//
//  // only looking at 'take the D eighth note out'
//  // todo: add relative location (after the C quarter note)
//  val t6 = "In the first measure, after the C quarter note, take the D eighth note out and change it to an eighth note rest."
//
//  failingTest should s"extract correctly from $t6" in {
//    val mentions = extractMentions(t6)
//    val deleteEvents = mentions.filter(_ matches "Delete")
//    //    println(ConversionUtils.mentionToString(convertEvents.head))
//
//    deleteEvents should have length(1)
//    val found = deleteEvents.head
//
//    val note = Note(Some(Duration("eighth")), Some(Pitch("D")), Some(Specifier("the")))
//    val onset = Onset(Some(Measure("first")), None)
//    val desired = Delete(
//      note = Some(note),
//      onset = Some(onset)
//    )
//
//    testDeleteEvent(found, desired)
//  }
}
