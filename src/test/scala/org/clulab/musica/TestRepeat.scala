package org.clulab.musica

import org.clulab.musica.MusicaTestObjects.AtomicObjects._
import org.clulab.musica.MusicaTestObjects.ComplexEvents.{Repeat}
import org.clulab.musica.MusicaTestObjects.SimpleEvents._
import org.clulab.musica.MusicaTestObjects.ConversionUtils
import org.clulab.musica.TestUtils._

class TestRepeat extends ExtractionTest {

  val t1 = "Repeat the G half note in measure 1"

  passingTest should s"extract correctly from $t1" in {
    val mentions = extractMentions(t1)
    val repeatEvents = mentions.filter(_ matches "Repeat")

    repeatEvents should have length(1)
    val found = repeatEvents.head

    val note = Note(Some(Duration("half")), Some(Pitch("G")), Some(Specifier("the")))
    val onset = Onset(Some(Measure("1")), None)
    val desired = Repeat(
      note = Some(note),
      onset = Some(onset)
    )

    testRepeatEvent(found, desired)
  }
}
