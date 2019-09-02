package org.clulab.musica

import org.clulab.musica.MusicaTestObjects.AtomicObjects._
import org.clulab.musica.MusicaTestObjects.ComplexEvents.{Convert}
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
    val desired = Convert(
      note = Some(note),
      onset = Some(onset)
    )

    testConvertEvent(found, desired)
  }

  val t2 = "The eighth note should be deleted"

  passingTest should s"extract correctly from $t2" in {
    val mentions = extractMentions(t2)
    val deleteEvents = mentions.filter(_ matches "Delete")
    //    println(ConversionUtils.mentionToString(convertEvents.head))

    deleteEvents should have length(1)
    val found = deleteEvents.head

    val note = Note(Some(Duration("eighth")), None, Some(Specifier("The")))
    val onset = Onset(None, None)
    val desired = Convert(
      note = Some(note),
      onset = Some(onset)
    )

    testConvertEvent(found, desired)
  }
}
