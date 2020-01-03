package org.clulab.musica

import org.clulab.musica.MusicaTestObjects.AtomicObjects._
import org.clulab.musica.MusicaTestObjects.ComplexEvents.{Switch}
import org.clulab.musica.MusicaTestObjects.SimpleEvents._
import org.clulab.musica.MusicaTestObjects.ConversionUtils
import org.clulab.musica.MusicaTestObjects.IntermediateEvents.Location
import org.clulab.musica.TestUtils._


class TestSwitch extends ExtractionTest {

  val t1 = "Switch the first quarter note and the last eighth note"

  passingTest should s"extract correctly from $t1" in {
    val mentions = extractMentions(t1)
    val switchMentions = mentions.filter(_ matches "Switch")

    switchMentions should have length(1)
    val found = switchMentions.head

    val sourceEntity = Note(Some(Duration("quarter")), None, Some(Specifier("the first")))
    val destEntity = Note(Some(Duration("eighth")), None, Some(Specifier("the last")))
    val desired = Switch(
      Some(sourceEntity),
      None,
      Some(destEntity),
      None
    )

    testSwitchEvent(found, desired)
  }

  val t2 = "Switch the first quarter note and the first quarter rest"

  passingTest should s"extract correctly from $t2" in {
    val mentions = extractMentions(t2)
    val switchMentions = mentions.filter(_ matches "Switch")

    switchMentions should have length(1)
    val found = switchMentions.head

    val sourceEntity = Note(Some(Duration("quarter")), None, Some(Specifier("the first")))
    val destEntity = Rest(Some(Specifier("the first")), Some(Duration("quarter")))
    val desired = Switch(
      Some(sourceEntity),
      None,
      Some(destEntity),
      None
    )

    testSwitchEvent(found, desired)
  }

}
