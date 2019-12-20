package org.clulab.musica

import ai.lum.common.ConfigUtils._
import com.typesafe.config.{Config, ConfigFactory}
import org.clulab.musica.MusicaTestObjects.ConversionUtils._
import org.clulab.musica.MusicaTestObjects.AtomicObjects._
import org.clulab.musica.MusicaTestObjects.SimpleEvents._
import org.clulab.musica.MusicaTestObjects.ComplexEvents._
import org.clulab.musica.MusicaTestObjects.MusicaObj
import org.clulab.odin.Mention
import org.clulab.processors.Document
import org.clulab.serialization.json.JSONSerializer
import org.clulab.utils.DisplayUtils
import org.scalatest.{FlatSpec, Matchers}
import org.json4s.jackson.JsonMethods._


object TestUtils {

  // From Processors -- I couldn't import it for some reason
  def jsonStringToDocument(jsonstr: String): Document = JSONSerializer.toDocument(parse(jsonstr))

  val successful = Seq()

  protected var mostRecentOdinEngine: Option[MusicaEngine] = None
  protected var mostRecentConfig: Option[Config] = None

  // This is the standard way to extract mentions for testing
  def extractMentions(ieSystem: MusicaEngine, text: String): Seq[Mention] = {
    ieSystem.extractFromText(text, true)
  }

  def newOdinSystem(config: Config): MusicaEngine = this.synchronized {
    val readingSystem =
      if (mostRecentOdinEngine.isEmpty) new MusicaEngine(config)
      else if (mostRecentConfig.get == config) mostRecentOdinEngine.get
      else new MusicaEngine(config)

    mostRecentOdinEngine = Some(readingSystem)
    mostRecentConfig = Some(config)
    readingSystem
  }

  class Test extends FlatSpec with Matchers {
    val passingTest = it
    val failingTest = ignore
    val brokenSyntaxTest = ignore
    val toDiscuss = ignore

  }


  class ExtractionTest(val ieSystem: MusicaEngine) extends Test {
    def this(config: Config = ConfigFactory.load("test")) = this(newOdinSystem(config))


    // ----------------------------------------------------
    //                  General Purpose
    // ----------------------------------------------------

    def extractMentions(text: String): Seq[Mention] = TestUtils.extractMentions(ieSystem, text)

    def shouldHaveDesired(label: String, ms: Seq[Mention], desired: MusicaObj): Unit = {
      // These should all be notes...
      println("ARGS:")
      ms.foreach(DisplayUtils.displayMention)
      ms.foreach(m => m.labels should contain (label))

//         There shouldn't be any extra notes that we didn't want...
      ms should have length 1

      // Make sure each of the desired Notes is in the list of notes
      val mentionsAsStrings = ms.map(mentionToString)
      mentionsAsStrings should contain (desired.toMentionString)
    }


    // ----------------------------------------------------
    //                  Event Specific
    // ----------------------------------------------------


    def testConvertEvent(m: Mention, desired: Convert) = {
      // Example: Convert the quarter note to a quarter rest

      DisplayUtils.displayMention(m)

      // Test the source entity
      if (desired.sourceEntity.nonEmpty) {
        val sourceEntArgs = m.arguments.getOrElse("sourceEntity", Seq())
        shouldHaveDesired("EntityMusic", sourceEntArgs, desired.sourceEntity.get)
      }

      // Test the destination entity
      if (desired.destEntity.nonEmpty) {
        val destEntArgs = m.arguments.getOrElse("destEntity", Seq())
        shouldHaveDesired("EntityMusic", destEntArgs, desired.destEntity.get)
      }

      // Test the source location
      if (desired.sourceLocation.nonEmpty) {
        val sourceLocArgs = m.arguments.getOrElse("sourceLocation", Seq())
        shouldHaveDesired("Location", sourceLocArgs, desired.sourceLocation.get)
      }

      if (desired.destLocation.nonEmpty) {
        val destLocArgs = m.arguments.getOrElse("destLocation", Seq())
        shouldHaveDesired("Location", destLocArgs, desired.destLocation.get)
      }

    }


    def testDeleteEvent(m: Mention, desired: Delete) = {
      // Example: Delete the G in measure 1

      // Test the musical entity
      if (desired.musicalEntity.nonEmpty) {
        val musicalEntArgs = m.arguments.getOrElse("musicalEntity", Seq())
        shouldHaveDesired("EntityMusic", musicalEntArgs, desired.musicalEntity.get)
      }

      // Test the location
      if (desired.location.nonEmpty) {
        val locArgs = m.arguments.getOrElse("location", Seq())
        shouldHaveDesired("Location", locArgs, desired.location.get)
      }

    }

    def testInsertEvent(m: Mention, desired: Insert) = {
      // Example: Insert a D quarter note after the quarter rest in measure 2

      // Test the musical entity
      if (desired.musicalEntity.nonEmpty) {
        val musicalEntArgs = m.arguments.getOrElse("musicalEntity", Seq())
        shouldHaveDesired("EntityMusic", musicalEntArgs, desired.musicalEntity.get)
      }

      // Test the location
      if (desired.location.nonEmpty) {
        val locArgs = m.arguments.getOrElse("location", Seq())
        shouldHaveDesired("Location", locArgs, desired.location.get)
      }

    }

    def testInvertEvent(m: Mention, desired: Invert) = {
      // Example: Invert the second measure around middle C


      // Test the musical entity
      if (desired.musicalEntity.nonEmpty) {
        val musicalEntArgs = m.arguments.getOrElse("musicalEntity", Seq())
        shouldHaveDesired("EntityMusic", musicalEntArgs, desired.musicalEntity.get)
      }

      // Test the location
      if (desired.location.nonEmpty) {
        val locArgs = m.arguments.getOrElse("location", Seq())
        shouldHaveDesired("Location", locArgs, desired.location.get)
      }

      // Test the axis
      if (desired.axis.nonEmpty) {
        val axisArgs = m.arguments.getOrElse("axis", Seq())
        shouldHaveDesired("EntityMusic", axisArgs, desired.axis.get)
      }
    }

    def testReverseEvent(m: Mention, desired: Reverse) = {
      // Example: Reverse the notes in measure 2

      // Test the musical entity
      if (desired.musicalEntity.nonEmpty) {
        val musicalEntArgs = m.arguments.getOrElse("musicalEntity", Seq())
        shouldHaveDesired("EntityMusic", musicalEntArgs, desired.musicalEntity.get)
      }

      // Test the location
      if (desired.location.nonEmpty) {
        val locArgs = m.arguments.getOrElse("location", Seq())
        shouldHaveDesired("Location", locArgs, desired.location.get)
      }
    }

    def testSwitchEvent(m: Mention, desired: Switch) = {
      // Example: Reverse the notes in measure 2

      // Test the source entity
      if (desired.sourceEntity.nonEmpty) {
        val sourceEntArgs = m.arguments.getOrElse("sourceEntity", Seq())
        shouldHaveDesired("EntityMusic", sourceEntArgs, desired.sourceEntity.get)
      }

      // Test the destination entity
      if (desired.destEntity.nonEmpty) {
        val destEntArgs = m.arguments.getOrElse("destEntity", Seq())
        shouldHaveDesired("EntityMusic", destEntArgs, desired.destEntity.get)
      }

      // Test the source location
      if (desired.sourceLocation.nonEmpty) {
        val sourceLocArgs = m.arguments.getOrElse("sourceLocation", Seq())
        shouldHaveDesired("Location", sourceLocArgs, desired.sourceLocation.get)
      }

      if (desired.destLocation.nonEmpty) {
        val destLocArgs = m.arguments.getOrElse("destLocation", Seq())
        shouldHaveDesired("Location", destLocArgs, desired.destLocation.get)
      }
    }

    def testTransposeEvent(m: Mention, desired: Transpose) = {
      // Test the musical entity
      if (desired.musicalEntity.nonEmpty) {
        val musicalEntArgs = m.arguments.getOrElse("musicalEntity", Seq())
        shouldHaveDesired("EntityMusic", musicalEntArgs, desired.musicalEntity.get)
      }

      // Test the location
      if (desired.location.nonEmpty) {
        val locArgs = m.arguments.getOrElse("location", Seq())
        shouldHaveDesired("Location", locArgs, desired.location.get)
      }

      // Test the direction
      if (desired.direction.nonEmpty) {
        val locArgs = m.arguments.getOrElse("direction", Seq())
        shouldHaveDesired("Direction", locArgs, desired.direction.get)
      }

      // Test the step
      if (desired.step.nonEmpty) {
        val locArgs = m.arguments.getOrElse("step", Seq())
        shouldHaveDesired("Step", locArgs, desired.step.get)
      }
    }

  }

}
