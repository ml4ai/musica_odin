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
    val convertEvents = mentions.filter(_ matches "ChangeDuration")
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
}
