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
      ms.foreach(m => m.label should be (label))

      // There shouldn't be any extra notes that we didn't want...
      ms should have length 1

      // Make sure each of the desired Notes is in the list of notes
      val mentionsAsStrings = ms.map(mentionToString)
      mentionsAsStrings should contain (desired.toMentionString)
    }


    // ----------------------------------------------------
    //                  Event Specific
    // ----------------------------------------------------


    def testTransposeEvent(m: Mention, desired: Transpose) = {
      // Example: Transpose the C4 quarter note on beat 1 of measure 1 up 5 half steps

      // Test the Note
      if (desired.note.nonEmpty) {
        val noteArgs = m.arguments.getOrElse("note", Seq())
        shouldHaveDesired("Note", noteArgs, desired.note.get)
      }

      // Test the Onset
      if (desired.onset.nonEmpty) {
        val onsetArgs = m.arguments.getOrElse("onset", Seq())
        shouldHaveDesired("Onset", onsetArgs, desired.onset.get)
      }

      // Test the direction
      if (desired.direction.nonEmpty) {
        val directionArgs = m.arguments.getOrElse("direction", Seq())
        shouldHaveDesired("Direction", directionArgs, desired.direction.get)
      }

      // Test the Step
      if (desired.step.nonEmpty) {
        val stepArgs = m.arguments.getOrElse("step", Seq())
        shouldHaveDesired("Step", stepArgs, desired.step.get)
      }
    }

  }

}
