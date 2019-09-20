package org.clulab.musica

import org.clulab.musica.MusicaTestObjects.AtomicObjects._
import org.clulab.musica.MusicaTestObjects.ComplexEvents.{Insert}
import org.clulab.musica.MusicaTestObjects.SimpleEvents._
import org.clulab.musica.MusicaTestObjects.ConversionUtils
import org.clulab.musica.TestUtils._


class TestInsert extends ExtractionTest {

  // needs chord + 2 notes?
  // needs absolute location
  // todo: finish this
  val t1 = "This is simply adding a simple chord at the end by adding an e at the same time with the c."

  failingTest should s"extract correctly from $t1" in {
    val mentions = extractMentions(t1)
    val insertEvents = mentions.filter(_ matches "Insert")

    insertEvents should have length(1)
    val found = insertEvents.head
    val note = Note(Some(Duration("quarter")), Some(Pitch("B")), Some(Specifier("the")))
    val desired = Insert(
      note = Some(note)
    )

    testInsertEvent(found, desired)
  }

  val t2 = "Insert an A quarter note after the D in measure 3"

  failingTest should s"extract correctly from $t2" in {
    val mentions = extractMentions(t2)
    val insertEvents = mentions.filter(_ matches "Insert")

    insertEvents should have length(1)
    val found = insertEvents.head
    val note = Note(Some(Duration("quarter")), Some(Pitch("A")), Some(Specifier("an")))
    val loc_rel = LocationRel("after")
    val note_prec = Note(None, Some(Pitch("D")), Some(Specifier("the")))
    val onset = Onset(Some(Measure("3")), None)
    val desired = Insert(
      note = Some(note),
      onset = Some(onset),
      loc_rel = Some(loc_rel),
      note_prec = Some(note_prec)
    )

    testInsertEvent(found, desired)
  }

  // todo: needs a second chord
  val t3 = "Insert a major fourth before the minor seventh"

  failingTest should s"extract correctly from $t3" in {
    val mentions = extractMentions(t3)
    val insertEvents = mentions.filter(_ matches "Insert")

    insertEvents should have length(1)
    val found = insertEvents.head
    val chord = Chord(Some("fourth"), Some(ChordType("major")), Some(Specifier("a")))
    val loc_rel = LocationRel("before")
    val desired = Insert(
      chord = Some(chord),
      loc_rel = Some(loc_rel)
    )

    testInsertEvent(found, desired)
  }

  // todo: needs ability to add 'run'
  val t4 = "Add another eighth note to the end of the run in measure 5"

  failingTest should s"extract correctly from $t4" in {
    val mentions = extractMentions(t4)
    val insertEvents = mentions.filter(_ matches "Insert")

    insertEvents should have length(1)
    val found = insertEvents.head
    val note = Note(Some(Duration("quarter")), Some(Pitch("A")), Some(Specifier("an")))
    val loc_rel = LocationRel("after")
    val note_prec = Note(None, Some(Pitch("D")), Some(Specifier("the")))
    val onset = Onset(Some(Measure("3")), None)
    val desired = Insert(
      note = Some(note),
      onset = Some(onset),
      loc_rel = Some(loc_rel),
      note_prec = Some(note_prec)
    )

    testInsertEvent(found, desired)
  }

  // todo: needs 3 notes
  val t5 = "Add a C quarter note between the G half note and the A half note"

  failingTest should s"extract correctly from $t5" in {
    val mentions = extractMentions(t5)
    val insertEvents = mentions.filter(_ matches "Insert")

    insertEvents should have length(1)
    val found = insertEvents.head
    val note = Note(Some(Duration("quarter")), Some(Pitch("C")), Some(Specifier("a")))
    val loc_rel = LocationRel("between")
    val note_prec = Note(Some(Duration("quarter")), Some(Pitch("G")), Some(Specifier("the")))
//    val note_final = Note(Some(Duration("half")), Some(Pitch("A")), Some(Specifier("the")))
    val desired = Insert(
      note = Some(note),
      loc_rel = Some(loc_rel),
      note_prec = Some(note_prec)
    )

    testInsertEvent(found, desired)
  }

//  // todo: needs 3-4 notes + rest
//  val t6 = "Go to the end of the score and add a measure with three quarter notes: A, B, and A. Then add a quarter rest."
//
//  failingTest should s"extract correctly from $t4" in {
//    val mentions = extractMentions(t4)
//    val insertEvents = mentions.filter(_ matches "Insert")
//
//    insertEvents should have length(1)
//    val found = insertEvents.head
//    val note = Note(Some(Duration("quarter")), Some(Pitch("A")), Some(Specifier("an")))
//    val loc_rel = LocationRel("after")
//    val note_prec = Note(None, Some(Pitch("D")), Some(Specifier("the")))
//    val onset = Onset(Some(Measure("3")), None)
//    val desired = Insert(
//      note = Some(note),
//      onset = Some(onset),
//      loc_rel = Some(loc_rel),
//      note_prec = Some(note_prec)
//    )
//
//    testInsertEvent(found, desired)
//  }

  // todo: add 'measure' as option
  val t7 = "Add a new bar to the start of the score."

  failingTest should s"extract correctly from $t7" in {
    val mentions = extractMentions(t7)
    val insertEvents = mentions.filter(_ matches "Insert")

    insertEvents should have length(1)
    val found = insertEvents.head
    val measure = Measure("new")
    val loc_abs = LocationAbs("start")
    val desired = Insert(
      measure = Some(measure),
      loc_abs = Some(loc_abs)
    )

    testInsertEvent(found, desired)
  }

//  // todo: how to deal with this? should this actually just be a 'replace' event?
//  val t8 = "Add an eighth rest at the start of the space where you just removed a quarter note."
//
//  failingTest should s"extract correctly from $t4" in {
//    val mentions = extractMentions(t4)
//    val insertEvents = mentions.filter(_ matches "Insert")
//
//    insertEvents should have length(1)
//    val found = insertEvents.head
//    val note = Note(Some(Duration("quarter")), Some(Pitch("A")), Some(Specifier("an")))
//    val loc_rel = LocationRel("after")
//    val note_prec = Note(None, Some(Pitch("D")), Some(Specifier("the")))
//    val onset = Onset(Some(Measure("3")), None)
//    val desired = Insert(
//      note = Some(note),
//      onset = Some(onset),
//      loc_rel = Some(loc_rel),
//      note_prec = Some(note_prec)
//    )
//
//    testInsertEvent(found, desired)
//  }

  // todo: add 'last' somewhere?
  val t9 = "Insert a quarter note and a quarter rest after the last eighth note"

  passingTest should s"extract correctly from $t9" in {
    val mentions = extractMentions(t9)
    val insertEvents = mentions.filter(_ matches "Insert")

    insertEvents should have length(1)
    val found = insertEvents.head
    val note = Note(Some(Duration("quarter")), None, Some(Specifier("a")))
    val rest = Rest(Some(Specifier("a")), Some(Duration("quarter")))
    val loc_rel = LocationRel("after")
    val note_prec = Note(Some(Duration("eighth")), None, Some(Specifier("the")))
    val desired = Insert(
      note = Some(note),
      rest = Some(rest),
      loc_rel = Some(loc_rel),
      note_prec = Some(note_prec)
    )

    testInsertEvent(found, desired)
  }

  val t10 = "Insert two eighth notes at the end of the second measure"

  failingTest should s"extract correctly from $t10" in {
    val mentions = extractMentions(t10)
    val insertEvents = mentions.filter(_ matches "Insert")

    insertEvents should have length(1)
    val found = insertEvents.head
    val note = Note(Some(Duration("eighth")), None, Some(Specifier("two")))
    val loc_abs = LocationAbs("the end")
    val onset = Onset(Some(Measure("the second")), None)
    val desired = Insert(
      note = Some(note),
      onset = Some(onset),
      loc_abs = Some(loc_abs)
    )

    testInsertEvent(found, desired)
  }

  val t11 = "Add a minor fifth"

  passingTest should s"extract correctly from $t11" in {
    val mentions = extractMentions(t11)
    val insertEvents = mentions.filter(_ matches "Insert")

    insertEvents should have length(1)
    val found = insertEvents.head
    val chord = Chord(Some("fifth"), Some(ChordType("minor")), Some(Specifier("a")))
    val desired = Insert(
      chord = Some(chord)
    )

    testInsertEvent(found, desired)
  }

  val t12 = "Add a sixteenth note"

  passingTest should s"extract correctly from $t12" in {
    val mentions = extractMentions(t12)
    val insertEvents = mentions.filter(_ matches "Insert")

    insertEvents should have length(1)
    val found = insertEvents.head
    val note = Note(Some(Duration("sixteenth")), None, Some(Specifier("a")))
    val desired = Insert(
      note = Some(note)
    )

    testInsertEvent(found, desired)
  }

  val t13 = "Add an eighth note after the C whole note"

  passingTest should s"extract correctly from $t13" in {
    val mentions = extractMentions(t13)
    val insertEvents = mentions.filter(_ matches "Insert")

    insertEvents should have length(1)
    val found = insertEvents.head
    val note = Note(Some(Duration("eighth")), None, Some(Specifier("an")))
    val loc_rel = LocationRel("after")
    val note_prec = Note(Some(Duration("whole")), Some(Pitch("C")), Some(Specifier("the")))
    val desired = Insert(
      note = Some(note),
      loc_rel = Some(loc_rel),
      note_prec = Some(note_prec)
    )

    testInsertEvent(found, desired)
  }

  val t14 = "An augmented second should be added"

  passingTest should s"extract correctly from $t14" in {
    val mentions = extractMentions(t14)
    val insertEvents = mentions.filter(_ matches "Insert")

    insertEvents should have length(1)
    val found = insertEvents.head
    val chord = Chord(Some("second"), Some(ChordType("augmented")), Some(Specifier("An")))
    val desired = Insert(
      chord = Some(chord)
    )

    testInsertEvent(found, desired)
  }

  val t15 = "Add an eighth note to the end"

  passingTest should s"extract correctly from $t15" in {
    val mentions = extractMentions(t15)
    val insertEvents = mentions.filter(_ matches "Insert")

    insertEvents should have length(1)
    val found = insertEvents.head
    val note = Note(Some(Duration("eighth")), None, Some(Specifier("an")))
    val loc_abs = LocationAbs("the end")
    val desired = Insert(
      note = Some(note),
      loc_abs = Some(loc_abs)
    )

    testInsertEvent(found, desired)
  }


  val t16 = "A C sharp eighth note should be added to the end"

  passingTest should s"extract correctly from $t16" in {
    val mentions = extractMentions(t16)
    val insertEvents = mentions.filter(_ matches "Insert")

    insertEvents should have length(1)
    val found = insertEvents.head
    val note = Note(Some(Duration("eighth")), Some(Pitch("C sharp")), Some(Specifier("A")))
    val loc_abs = LocationAbs("the end")
    val desired = Insert(
      note = Some(note),
      loc_abs = Some(loc_abs)
    )

    testInsertEvent(found, desired)
  }

  val t17 = "Insert another measure"

  failingTest should s"extract correctly from $t17" in {
    val mentions = extractMentions(t17)
    val insertEvents = mentions.filter(_ matches "Insert")

    insertEvents should have length(1)
    val found = insertEvents.head
    val measure = Measure("another")
    val desired = Insert(
      measure = Some(measure)
    )

    testInsertEvent(found, desired)
  }


  val t18 = "A B half note should be added"

  passingTest should s"extract correctly from $t18" in {
    val mentions = extractMentions(t18)
    val insertEvents = mentions.filter(_ matches "Insert")

    insertEvents should have length(1)
    val found = insertEvents.head
    val note = Note(Some(Duration("half")), Some(Pitch("B")), Some(Specifier("A")))
    val desired = Insert(
      note = Some(note)
    )

    testInsertEvent(found, desired)
  }

  val t19 = "An eighth note should be added after the D whole note"

  passingTest should s"extract correctly from $t19" in {
    val mentions = extractMentions(t19)
    val insertEvents = mentions.filter(_ matches "Insert")

    insertEvents should have length(1)
    val found = insertEvents.head
    val note = Note(Some(Duration("eighth")), None, Some(Specifier("An")))
    val loc_rel = LocationRel("after")
    val note_prec = Note(Some(Duration("whole")), Some(Pitch("D")), Some(Specifier("the")))
    val desired = Insert(
      note = Some(note),
      loc_rel = Some(loc_rel),
      note_prec = Some(note_prec)
    )

    testInsertEvent(found, desired)
  }

  val t20 = "An A quarter note should be inserted after the D in measure 3"

  failingTest should s"extract correctly from $t20" in {
    val mentions = extractMentions(t20)
    val insertEvents = mentions.filter(_ matches "Insert")

    insertEvents should have length(1)
    val found = insertEvents.head
    val note = Note(Some(Duration("quarter")), Some(Pitch("A")), Some(Specifier("An")))
    val loc_rel = LocationRel("after")
    val note_prec = Note(None, Some(Pitch("D")), Some(Specifier("the")))
    val onset = Onset(Some(Measure("3")), None)
    val desired = Insert(
      note = Some(note),
      loc_rel = Some(loc_rel),
      note_prec = Some(note_prec),
      onset = Some(onset)
    )

    testInsertEvent(found, desired)
  }


  val t21 = "At the start of the second measure, insert a quarter note"

  failingTest should s"extract correctly from $t21" in {
    val mentions = extractMentions(t21)
    val insertEvents = mentions.filter(_ matches "Insert")

    insertEvents should have length(1)
    val found = insertEvents.head
    val note = Note(Some(Duration("quarter")), None, Some(Specifier("a")))
    val loc_abs = LocationAbs("the start")
    val onset = Onset(Some(Measure("the second")), None)
    val desired = Insert(
      note = Some(note),
      loc_abs = Some(loc_abs),
      onset = Some(onset)
    )

    testInsertEvent(found, desired)
  }

  val t22 = "At the start of the second measure, a quarter note should be inserted"

  failingTest should s"extract correctly from $t22" in {
    val mentions = extractMentions(t22)
    val insertEvents = mentions.filter(_ matches "Insert")

    insertEvents should have length(1)
    val found = insertEvents.head
    val note = Note(Some(Duration("quarter")), None, Some(Specifier("a")))
    val loc_abs = LocationAbs("the start")
    val onset = Onset(Some(Measure("the second")), None)
    val desired = Insert(
      note = Some(note),
      loc_abs = Some(loc_abs),
      onset = Some(onset)
    )

    testInsertEvent(found, desired)
  }

  // todo: add sequence to testing framework
  val t23 = "At the end of the run in measure 5 another eighth note should be added"

  failingTest should s"extract correctly from $t23" in {
    val mentions = extractMentions(t23)
    val insertEvents = mentions.filter(_ matches "Insert")

    insertEvents should have length(1)
    val found = insertEvents.head
    val note = Note(Some(Duration("eighth")), None, Some(Specifier("another")))
    val loc_abs = LocationAbs("the end")
//    val sequence = Sequence("run")
    val onset = Onset(Some(Measure("5")), None)
    val desired = Insert(
      note = Some(note),
      loc_abs = Some(loc_abs),
      onset = Some(onset)
    )

    testInsertEvent(found, desired)
  }

  // todo: add sequence to testing framework
  val t24 = "Add an eighth note to the run of notes in the first bar"

  failingTest should s"extract correctly from $t24" in {
    val mentions = extractMentions(t24)
    val insertEvents = mentions.filter(_ matches "Insert")

    insertEvents should have length(1)
    val found = insertEvents.head
    val note = Note(Some(Duration("eighth")), None, Some(Specifier("another")))
    val loc_abs = LocationAbs("the end")
    //    val sequence = Sequence("run")
    val onset = Onset(Some(Measure("5")), None)
    val desired = Insert(
      note = Some(note),
      loc_abs = Some(loc_abs),
      onset = Some(onset)
    )

    testInsertEvent(found, desired)
  }

  // todo: add sequence to framework
  val t25 = "Add a run of sixteenth notes"

  failingTest should s"extract correctly from $t25" in {
    val mentions = extractMentions(t25)
    val insertEvents = mentions.filter(_ matches "Insert")

    insertEvents should have length(1)
    val found = insertEvents.head
    val note = Note(Some(Duration("sixteenth")), None, None)
    //    val sequence = Sequence("run")
    val desired = Insert(
      note = Some(note)
    )

    testInsertEvent(found, desired)
  }

//  // todo: same time? chord components?
//  val t26 = "A simple chord is added at the end by adding an e at the same time as the c"
//
//  failingTest should s"extract correctly from $t26" in {
//    val mentions = extractMentions(t26)
//    val insertEvents = mentions.filter(_ matches "Insert")
//
//    insertEvents should have length(1)
//    val found = insertEvents.head
//    val note = Note(None, Some(Pitch("e")), Some(Specifier("an")))
//    val loc_abs = LocationAbs("the end")
//    //    val sequence = Sequence("run")
//    val onset = Onset(Some(Measure("5")), None)
//    val desired = Insert(
//      note = Some(note),
//      loc_abs = Some(loc_abs),
//      onset = Some(onset)
//    )
//
//    testInsertEvent(found, desired)
//  }

  val t27 = "A major fourth is added before the minor seventh"

  failingTest should s"extract correctly from $t27" in {
    val mentions = extractMentions(t27)
    val insertEvents = mentions.filter(_ matches "Insert")

    insertEvents should have length(1)
    val found = insertEvents.head
    val chord = Chord(Some("fourth"), Some(ChordType("major")), Some(Specifier("a")))
    val loc_rel = LocationRel("before")
    val desired = Insert(
      chord = Some(chord),
      loc_rel = Some(loc_rel)
    )

    testInsertEvent(found, desired)
  }

  val t28 = "Another measure should be inserted"

  failingTest should s"extract correctly from $t28" in {
    val mentions = extractMentions(t28)
    val insertEvents = mentions.filter(_ matches "Insert")

    insertEvents should have length(1)
    val found = insertEvents.head
    val measure = Measure("Another")
    val desired = Insert(
      measure = Some(measure)
    )

    testInsertEvent(found, desired)
  }

  val t29 = "A new bar should be added to the start of the score"
  val t30 = "A measure with three quarter notes (A, B, A) should be added to the end of the score."
  val t31 = "A quarter note and a quarter rest should be added after the last eighth note"
  val t32 = "Two eighth notes should be inserted at the end of the second measure"
  val t33 = "Another eighth note should be added to the end of the run in measure 5"
  val t34 = "A C quarter note should be added between the G half note and the A half note"
  val t35 = "After the whole note, add an eighth note"
  val t36 = "Between the G half note and the A half note you should add a C quarter note"
  val t37 = "After the whole note, an eighth note should be added"
  val t38 = "An eighth rest should be added to the start of the space where you just removed a quarter note"

}
