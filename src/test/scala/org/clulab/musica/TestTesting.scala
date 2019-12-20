package org.clulab.musica

import org.clulab.musica.MusicaTestObjects.AtomicObjects._
import org.clulab.musica.MusicaTestObjects.ComplexEvents.Convert
import org.clulab.musica.MusicaTestObjects.SimpleEvents._
import org.clulab.musica.MusicaTestObjects.IntermediateEvents._
import org.clulab.musica.MusicaTestObjects.ConversionUtils
import org.clulab.musica.TestUtils._

class TestTesting extends ExtractionTest {

  val t1 = "Shorten all the half notes"

  passingTest should s"extract correctly from $t1" in {
    val mentions = extractMentions(t1)

    for (mention <- mentions) {
      println(mention.text + " is a " + mention.label + " found by " + mention.foundBy)
    }

    val convertEvents = mentions.filter(_ matches "Convert")

    println(convertEvents)
    convertEvents should have length(1)
    val found = convertEvents.head

    val sourceEntity = Note(Some(Duration("half")), None, Some(Specifier("all the")))

    val desired = Convert(
      Some(sourceEntity)
    )

    testConvertEvent(found, desired)

  }

  val t2 = "Shorten the C in measure 1 to a quarter note"

  passingTest should s"extract correctly from $t2" in {
    val mentions = extractMentions(t2)
    val convertEvents = mentions.filter(_ matches "Convert")

    convertEvents should have length(1)
    val found = convertEvents.head

    val sourceEntity = Note(None, Some(Pitch("C")), Some(Specifier("the")))
    val location = Location(Some(LocationTerm("in")), None, Some(Measure("measure 1")), None, None, None)
    val destEntity = Note(Some(Duration("quarter")), None, Some(Specifier("a")))
    val desired = Convert(
      Some(sourceEntity),
      Some(location),
      Some(destEntity)
    )

    testConvertEvent(found, desired)
  }

  val t3 = "Shorten the C after the half note in measure 1 to a quarter note"

  passingTest should s"extract correctly from $t3" in {
    val mentions = extractMentions(t3)
    val convertEvents = mentions.filter(_ matches "Convert")

    convertEvents should have length(1)
    val found = convertEvents.head

    val sourceEntity = Note(None, Some(Pitch("C")), Some(Specifier("the")))
    val location = Location(Some(LocationTerm("after")), Some(Note(Some(Duration("half")), None, Some(Specifier("the")))),
      Some(Measure("measure 1")), None, None, None)
    val destEntity = Note(Some(Duration("quarter")), None, Some(Specifier("a")))
    val desired = Convert(
      Some(sourceEntity),
      Some(location),
      Some(destEntity)
    )

    testConvertEvent(found, desired)
  }
}
