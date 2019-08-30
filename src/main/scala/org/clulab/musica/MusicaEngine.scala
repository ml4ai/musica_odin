package org.clulab.musica

import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.config.ConfigFactory
import org.clulab.odin.{ExtractorEngine, Mention, State}
import org.clulab.processors.{Document, Processor}
import org.clulab.processors.fastnlp.FastNLPProcessor
import org.clulab.utils.{Configured, FileUtils}
import org.slf4j.LoggerFactory

class MusicaEngine (val config: Config = ConfigFactory.load("musica")) extends Configured {

  val proc: Processor = new FastNLPProcessor() // TODO: Get from configuration file soon

  override def getConf: Config = config

  protected def getFullName(name: String) = MusicaEngine.PREFIX + "." + name

  protected def getPath(name: String, defaultValue: String): String = {
    val path = getArgString(getFullName(name), Option(defaultValue))

    MusicaEngine.logger.info(name + ": " + path)
    path
  }

  class LoadableAttributes(
    // These are the values which can be reloaded.  Query them for current assignments.
    val actions: MusicaActions,
    val engine: ExtractorEngine,
  )

  object LoadableAttributes {
    val   masterRulesPath: String = getPath(  "masterRulesPath", "/org/clulab/musica/grammars/master.yml")
    val      taxonomyPath: String = getPath(     "taxonomyPath", "/org/clulab/musica/grammars/taxonomy.yml")

    def apply(): LoadableAttributes = {
      // Reread these values from their files/resources each time based on paths in the config file.
      val masterRules = FileUtils.getTextFromResource(masterRulesPath)
      val actions = MusicaActions(taxonomyPath)

      new LoadableAttributes(
        actions,
        ExtractorEngine(masterRules, actions, actions.keepLongest), // ODIN component
      )
    }
  }

  var loadableAttributes = LoadableAttributes()

  // These public variables are accessed directly by clients which
  // don't know they are loadable and which had better not keep copies.
  def engine = loadableAttributes.engine

  def reload() = loadableAttributes = LoadableAttributes()


  // MAIN PIPELINE METHOD
  def extractFromText(text: String, keepText: Boolean = false): Seq[Mention] = {
    val doc = annotate(text, keepText)   // CTM: processors runs (sentence splitting, tokenization, POS, dependency parse, NER, chunking)
    val odinMentions = extractFrom(doc)  // CTM: runs the Odin grammar
    //println(s"\nodinMentions() -- entities : \n\t${odinMentions.map(m => m.text).sorted.mkString("\n\t")}")

    odinMentions  // CTM: collection of mentions ; to be converted to some form (json)
  }


  def extractFrom(doc: Document): Vector[Mention] = {
    val events =  engine.extractFrom(doc, new State()).toVector
    //println(s"In extractFrom() -- res : ${res.map(m => m.text).mkString(",\t")}")

    // todo: some appropriate version of "keepMostComplete"
    loadableAttributes.actions.keepLongest(events).toVector
  }

  // ---------- Helper Methods -----------


  // Annotate the text using a Processor and then populate lexicon labels
  def annotate(text: String, keepText: Boolean = false): Document = {
    val doc = proc.annotate(text, keepText)
    //    doc.sentences.foreach(addLexiconNER)
    doc
  }

}

object MusicaEngine {

  val logger = LoggerFactory.getLogger(this.getClass())
  val PREFIX: String = "MusicaEngine"

}

