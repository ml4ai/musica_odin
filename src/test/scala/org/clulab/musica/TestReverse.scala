package org.clulab.musica

import org.clulab.musica.MusicaTestObjects.AtomicObjects._
import org.clulab.musica.MusicaTestObjects.ComplexEvents.{Reverse}
import org.clulab.musica.MusicaTestObjects.SimpleEvents._
import org.clulab.musica.MusicaTestObjects.ConversionUtils
import org.clulab.musica.TestUtils._

class TestReverse extends ExtractionTest {

  val t1 = "Reverse all the notes in measure 1"

  passingTest should s"extract correctly from $t1" in {
    val mentions = extractMentions(t1)
    val reverseEvents = mentions.filter(_ matches "Reverse")

    reverseEvents should have length(1)
    val found = reverseEvents.head

    val note = Note(None, None, Some(Specifier("all the")))
    val onset = Onset(Some(Measure("1")), None)
    val desired = Reverse(
      note = Some(note),
      onset = Some(onset)
    )

    testReverseEvent(found, desired)
  }
}
