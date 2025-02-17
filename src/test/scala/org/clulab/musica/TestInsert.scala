package org.clulab.musica

import org.clulab.musica.MusicaTestObjects.AtomicObjects._
import org.clulab.musica.MusicaTestObjects.ComplexEvents.Insert
import org.clulab.musica.MusicaTestObjects.SimpleEvents._
import org.clulab.musica.MusicaTestObjects.IntermediateEvents._
import org.clulab.musica.MusicaTestObjects.ConversionUtils
import org.clulab.musica.TestUtils._
import org.clulab.utils.DisplayUtils


class TestInsert extends ExtractionTest {

  //  // needs chord + 2 notes?
  //  // needs absolute location
  //  // todo: finish this
  //  val t1 = "This is simply adding a simple chord at the end by adding an e at the same time with the c."
  //
  //  failingTest should s"extract correctly from $t1" in {
  //    val mentions = extractMentions(t1)
  //    val insertEvents = mentions.filter(_ matches "Insert")
  //
  //    insertEvents should have length(1)
  //    val found = insertEvents.head
  //    val note = Note(Some(Duration("quarter")), Some(Pitch("B")), Some(Specifier("the")))
  //    val desired = Insert(
  //      note = Some(note)
  //    )
  //
  //    testInsertEvent(found, desired)
  //  }

    val t2 = "Insert an A quarter note after the D in measure 3"

    passingTest should s"extract correctly from $t2" in {
      val mentions = extractMentions(t2)
      val insertEvents = mentions.filter(_ matches "Insert")

      insertEvents should have length(1)
      val found = insertEvents.head
      val musicalEntity = Note(Some(Duration("quarter")), Some(Pitch("A")), Some(Specifier("an")))
      val location = Location(Some(LocationTerm("after")),
        Some(Note(None, Some(Pitch("D")), Some(Specifier("the")))), Some(Measure("measure 3")),
        None, None, None)
      val desired = Insert(
        Some(musicalEntity),
        Some(location)
      )

      testInsertEvent(found, desired)
    }

    val t3 = "Insert a major fourth before the minor seventh"

    passingTest should s"extract correctly from $t3" in {
      val mentions = extractMentions(t3)
      val insertEvents = mentions.filter(_ matches "Insert")

      insertEvents should have length(1)
      val found = insertEvents.head
      val musicalEntity = Chord(Some("fourth"), Some(ChordType("major")), Some(Specifier("a")))
      val location = Location(Some(LocationTerm("before")), None, None, None, Some(Chord(Some("seventh"), Some(ChordType("minor")),
        Some(Specifier("the")))), None)
      val desired = Insert(
        Some(musicalEntity),
        Some(location)
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
      val sourceEntity = Note(Some(Duration("eighth")), None, Some(Specifier("another")))
      val location = Location(Some(LocationTerm("end of")), Some(Note(None, None, Some(Specifier("the run")))),
        Some(Measure("measure 5")), None, None, None)
      val desired = Insert(
        Some(sourceEntity),
        Some(location)
      )

      testInsertEvent(found, desired)
    }

  //  // todo: needs 2 notes in location
  //  val t5 = "Add a C quarter note between the G half note and the A half note"
  //
  //  failingTest should s"extract correctly from $t5" in {
  //    val mentions = extractMentions(t5)
  //    val insertEvents = mentions.filter(_ matches "Insert")
  //
  //    insertEvents should have length(1)
  //    val found = insertEvents.head
  //    val note = Note(Some(Duration("quarter")), Some(Pitch("C")), Some(Specifier("a")))
  //    val loc_rel = LocationRel("between")
  //    val note_prec = Note(Some(Duration("quarter")), Some(Pitch("G")), Some(Specifier("the")))
  ////    val note_final = Note(Some(Duration("half")), Some(Pitch("A")), Some(Specifier("the")))
  //    val desired = Insert(
  //      note = Some(note),
  //      loc_rel = Some(loc_rel),
  //      note_prec = Some(note_prec)
  //    )
  //
  //    testInsertEvent(found, desired)
  //  }
  //
  ////  // todo: needs 3-4 notes + rest
  ////  val t6 = "Go to the end of the score and add a measure with three quarter notes: A, B, and A. Then add a quarter rest."
  ////
  ////  failingTest should s"extract correctly from $t4" in {
  ////    val mentions = extractMentions(t4)
  ////    val insertEvents = mentions.filter(_ matches "Insert")
  ////
  ////    insertEvents should have length(1)
  ////    val found = insertEvents.head
  ////    val note = Note(Some(Duration("quarter")), Some(Pitch("A")), Some(Specifier("an")))
  ////    val loc_rel = LocationRel("after")
  ////    val note_prec = Note(None, Some(Pitch("D")), Some(Specifier("the")))
  ////    val onset = Onset(Some(Measure("3")), None)
  ////    val desired = Insert(
  ////      note = Some(note),
  ////      onset = Some(onset),
  ////      loc_rel = Some(loc_rel),
  ////      note_prec = Some(note_prec)
  ////    )
  ////
  ////    testInsertEvent(found, desired)
  ////  }
  //
    // todo: add 'measure' as option
    val t7 = "Add a new bar to the start of the score."

    failingTest should s"extract correctly from $t7" in {
      val mentions = extractMentions(t7)
      val insertEvents = mentions.filter(_ matches "Insert")

      insertEvents should have length(1)
      val found = insertEvents.head

      val musicalEntity = Measure("new")
      val location = Location(Some(LocationTerm("the start")), None, None, None, None, None)
      val desired = Insert(
        Some(musicalEntity),
        Some(location)
      )

      testInsertEvent(found, desired)
    }
  //
  ////  // todo: how to deal with this? should this actually just be a 'replace' event?
  ////  val t8 = "Add an eighth rest at the start of the space where you just removed a quarter note."
  ////
  ////  failingTest should s"extract correctly from $t4" in {
  ////    val mentions = extractMentions(t4)
  ////    val insertEvents = mentions.filter(_ matches "Insert")
  ////
  ////    insertEvents should have length(1)
  ////    val found = insertEvents.head
  ////    val note = Note(Some(Duration("quarter")), Some(Pitch("A")), Some(Specifier("an")))
  ////    val loc_rel = LocationRel("after")
  ////    val note_prec = Note(None, Some(Pitch("D")), Some(Specifier("the")))
  ////    val onset = Onset(Some(Measure("3")), None)
  ////    val desired = Insert(
  ////      note = Some(note),
  ////      onset = Some(onset),
  ////      loc_rel = Some(loc_rel),
  ////      note_prec = Some(note_prec)
  ////    )
  ////
  ////    testInsertEvent(found, desired)
  ////  }
  //
  //  // todo: add 'last' somewhere?
  //  val t9 = "Insert a quarter note and a quarter rest after the last eighth note"
  //
  //  passingTest should s"extract correctly from $t9" in {
  //    val mentions = extractMentions(t9)
  //    val insertEvents = mentions.filter(_ matches "Insert")
  //
  //    insertEvents should have length(1)
  //    val found = insertEvents.head
  //    val note = Note(Some(Duration("quarter")), None, Some(Specifier("a")))
  //    val rest = Rest(Some(Specifier("a")), Some(Duration("quarter")))
  //    val loc_rel = LocationRel("after")
  //    val note_prec = Note(Some(Duration("eighth")), None, Some(Specifier("the")))
  //    val desired = Insert(
  //      note = Some(note),
  //      rest = Some(rest),
  //      loc_rel = Some(loc_rel),
  //      note_prec = Some(note_prec)
  //    )
  //
  //    testInsertEvent(found, desired)
  //  }
  //
  // todo: 'two' currently handled as CD and not specifier
    val t10 = "Insert two eighth notes at the end of the second measure"

    failingTest should s"extract correctly from $t10" in {
      val mentions = extractMentions(t10)
      val insertEvents = mentions.filter(_ matches "Insert")

      insertEvents should have length(1)
      val found = insertEvents.head
      val musicalEntity = Note(Some(Duration("eighth")), None, Some(Specifier("two")))
      val location = Location(Some(LocationTerm("the end")), None, Some(Measure("the second measure")), None, None, None)
      val desired = Insert(
        Some(musicalEntity),
        Some(location)
      )

      testInsertEvent(found, desired)
    }

  val t11 = "Add a minor fifth"

  passingTest should s"extract correctly from $t11" in {
    val mentions = extractMentions(t11)

    mentions.foreach(DisplayUtils.displayMention)
    val insertEvents = mentions.filter(_ matches "Insert")

    insertEvents should have length (1)
    val found = insertEvents.head
    val musicalEntity = Chord(Some("fifth"), Some(ChordType("minor")), Some(Specifier("a")))
    val desired = Insert(
      Some(musicalEntity)
    )

    testInsertEvent(found, desired)
  }


  val t12 = "Add a sixteenth note"

  passingTest should s"extract correctly from $t12" in {
    val mentions = extractMentions(t12)
    val insertEvents = mentions.filter(_ matches "Insert")

    insertEvents should have length(1)
    val found = insertEvents.head
    val musicalEntity = Note(Some(Duration("sixteenth")), None, Some(Specifier("a")))
    val desired = Insert(
      Some(musicalEntity)
    )

    testInsertEvent(found, desired)
  }

  val t13 = "Add an eighth note after the C whole note"

  passingTest should s"extract correctly from $t13" in {
    val mentions = extractMentions(t13)
    val insertEvents = mentions.filter(_ matches "Insert")

    insertEvents should have length(1)
    val found = insertEvents.head
    val musicalEntity = Note(Some(Duration("eighth")), None, Some(Specifier("an")))
    val location = Location(Some(LocationTerm("after")), Some(Note(Some(Duration("whole")), Some(Pitch("C")),
      Some(Specifier("the")))), None, None, None, None)
    val desired = Insert(
      Some(musicalEntity),
      Some(location)
    )

    testInsertEvent(found, desired)
  }

  val t14 = "An augmented second should be added"

  passingTest should s"extract correctly from $t14" in {
    val mentions = extractMentions(t14)
    val insertEvents = mentions.filter(_ matches "Insert")

    insertEvents should have length(1)
    val found = insertEvents.head
    val musicalEntity = Chord(Some("second"), Some(ChordType("augmented")), Some(Specifier("An")))
    val desired = Insert(
      Some(musicalEntity)
    )

    testInsertEvent(found, desired)
  }

  // todo: 'the end' no longer found as location
  val t15 = "Add an eighth note to the end"

  failingTest should s"extract correctly from $t15" in {
    val mentions = extractMentions(t15)
    val insertEvents = mentions.filter(_ matches "Insert")

    insertEvents should have length(1)
    val found = insertEvents.head
    val musicalEntity = Note(Some(Duration("eighth")), None, Some(Specifier("an")))
    val location = Location(Some(LocationTerm("the end")), None, None, None, None, None)
    val desired = Insert(
      Some(musicalEntity),
      Some(location)
    )

    testInsertEvent(found, desired)
  }


  val t16 = "A C sharp eighth note should be added to the end"

  failingTest should s"extract correctly from $t16" in {
    val mentions = extractMentions(t16)
    val insertEvents = mentions.filter(_ matches "Insert")

    insertEvents should have length(1)
    val found = insertEvents.head
    val musicalEntity = Note(Some(Duration("eighth")), Some(Pitch("C sharp")), Some(Specifier("A")))
    val location = Location(Some(LocationTerm("the end")), None, None, None, None, None)
    val desired = Insert(
      Some(musicalEntity),
      Some(location)
    )

    testInsertEvent(found, desired)
  }

//  val t17 = "Insert another measure"
//
//  failingTest should s"extract correctly from $t17" in {
//    val mentions = extractMentions(t17)
//    val insertEvents = mentions.filter(_ matches "Insert")
//
//    insertEvents should have length(1)
//    val found = insertEvents.head
//    val measure = Measure("another")
//    val desired = Insert(
//      measure = Some(measure)
//    )
//
//    testInsertEvent(found, desired)
//  }
//

  val t18 = "A B half note should be added"

  passingTest should s"extract correctly from $t18" in {
    val mentions = extractMentions(t18)
    val insertEvents = mentions.filter(_ matches "Insert")

    insertEvents should have length(1)
    val found = insertEvents.head
    val musicalEntity = Note(Some(Duration("half")), Some(Pitch("B")), Some(Specifier("A")))
    val desired = Insert(
      Some(musicalEntity)
    )

    testInsertEvent(found, desired)
  }

  val t19 = "An eighth note should be added after the D whole note"

  passingTest should s"extract correctly from $t19" in {
    val mentions = extractMentions(t19)
    val insertEvents = mentions.filter(_ matches "Insert")

    insertEvents should have length(1)
    val found = insertEvents.head
    val musicalEntity = Note(Some(Duration("eighth")), None, Some(Specifier("An")))
    val location = Location(Some(LocationTerm("after")),
      Some(Note(Some(Duration("whole")), Some(Pitch("D")), Some(Specifier("the")))), None, None, None, None)
    val desired = Insert(
      Some(musicalEntity),
      Some(location)
    )

    testInsertEvent(found, desired)
  }

  // todo: 'AN A' identified as specifier; the A should be pitch
  val t20 = "An A quarter note should be inserted after the D in measure 3"

  failingTest should s"extract correctly from $t20" in {
    val mentions = extractMentions(t20)
    val insertEvents = mentions.filter(_ matches "Insert")

    insertEvents should have length(1)
    val found = insertEvents.head
    val musicalEntity = Note(Some(Duration("quarter")), Some(Pitch("A")), Some(Specifier("An")))
    val location = Location(Some(LocationTerm("after")),
      Some(Note(None, Some(Pitch("D")), Some(Specifier("the")))), Some(Measure("measure 3")), None, None, None)
    val desired = Insert(
      Some(musicalEntity),
      Some(location)
    )

    testInsertEvent(found, desired)
  }


  val t21 = "At the start of the second measure, insert a quarter note"

  passingTest should s"extract correctly from $t21" in {
    val mentions = extractMentions(t21)
    val insertEvents = mentions.filter(_ matches "Insert")

    insertEvents should have length(1)
    val found = insertEvents.head
    val musicalEntity = Note(Some(Duration("quarter")), None, Some(Specifier("a")))
    val location = Location(Some(LocationTerm("start")), None, Some(Measure("the second measure")), None, None, None)
    val desired = Insert(
      Some(musicalEntity),
      Some(location)
    )

    testInsertEvent(found, desired)
  }

  val t22 = "At the start of the second measure, a quarter note should be inserted"

  passingTest should s"extract correctly from $t22" in {
    val mentions = extractMentions(t22)
    val insertEvents = mentions.filter(_ matches "Insert")

    insertEvents should have length(1)
    val found = insertEvents.head
    val musicalEntity = Note(Some(Duration("quarter")), None, Some(Specifier("a")))
    val location = Location(Some(LocationTerm("start")), None, Some(Measure("the second measure")), None, None, None)
    val desired = Insert(
      Some(musicalEntity),
      Some(location)
    )

    testInsertEvent(found, desired)
  }
//
//  // todo: add sequence to testing framework
//  val t23 = "At the end of the run in measure 5 another eighth note should be added"
//
//  failingTest should s"extract correctly from $t23" in {
//    val mentions = extractMentions(t23)
//    val insertEvents = mentions.filter(_ matches "Insert")
//
//    insertEvents should have length(1)
//    val found = insertEvents.head
//    val note = Note(Some(Duration("eighth")), None, Some(Specifier("another")))
//    val loc_abs = LocationAbs("the end")
////    val sequence = Sequence("run")
//    val onset = Onset(Some(Measure("5")), None)
//    val desired = Insert(
//      note = Some(note),
//      loc_abs = Some(loc_abs),
//      onset = Some(onset)
//    )
//
//    testInsertEvent(found, desired)
//  }
//
//  // todo: add sequence to testing framework
//  val t24 = "Add an eighth note to the run of notes in the first bar"
//
//  failingTest should s"extract correctly from $t24" in {
//    val mentions = extractMentions(t24)
//    val insertEvents = mentions.filter(_ matches "Insert")
//
//    insertEvents should have length(1)
//    val found = insertEvents.head
//    val note = Note(Some(Duration("eighth")), None, Some(Specifier("another")))
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
//
//  // todo: run of notes isn't extracting as expected.
  val t25 = "Add a run of sixteenth notes"

  failingTest should s"extract correctly from $t25" in {
    val mentions = extractMentions(t25)
    val insertEvents = mentions.filter(_ matches "Insert")

    insertEvents should have length(1)
    val found = insertEvents.head
    val musicalEntity = Note(Some(Duration("sixteenth")), None, Some(Specifier("a run of")))
    val desired = Insert(
      Some(musicalEntity)
    )

    testInsertEvent(found, desired)
  }

////  // todo: same time? chord components?
////  val t26 = "A simple chord is added at the end by adding an e at the same time as the c"
////
////  failingTest should s"extract correctly from $t26" in {
////    val mentions = extractMentions(t26)
////    val insertEvents = mentions.filter(_ matches "Insert")
////
////    insertEvents should have length(1)
////    val found = insertEvents.head
////    val note = Note(None, Some(Pitch("e")), Some(Specifier("an")))
////    val loc_abs = LocationAbs("the end")
////    //    val sequence = Sequence("run")
////    val onset = Onset(Some(Measure("5")), None)
////    val desired = Insert(
////      note = Some(note),
////      loc_abs = Some(loc_abs),
////      onset = Some(onset)
////    )
////
////    testInsertEvent(found, desired)
////  }

  val t27 = "A major fourth is added before the minor seventh"

  passingTest should s"extract correctly from $t27" in {
    val mentions = extractMentions(t27)
    val insertEvents = mentions.filter(_ matches "Insert")

    insertEvents should have length(1)
    val found = insertEvents.head
    val musicalEntity = Chord(Some("fourth"), Some(ChordType("major")), Some(Specifier("A")))
    val location = Location(Some(LocationTerm("before")), None, None, None, Some(Chord(Some("seventh"),
      Some(ChordType("minor")), Some(Specifier("the")))), None)
    val desired = Insert(
      Some(musicalEntity),
      Some(location)
    )

    testInsertEvent(found, desired)
  }

//  val t28 = "Another measure should be inserted"
//
//  failingTest should s"extract correctly from $t28" in {
//    val mentions = extractMentions(t28)
//    val insertEvents = mentions.filter(_ matches "Insert")
//
//    insertEvents should have length(1)
//    val found = insertEvents.head
//    val measure = Measure("Another")
//    val desired = Insert(
//      measure = Some(measure)
//    )
//
//    testInsertEvent(found, desired)
//  }
//
//  val t29 = "A new bar should be added to the start of the score"
//  val t30 = "A measure with three quarter notes (A, B, A) should be added to the end of the score."
//  val t31 = "A quarter note and a quarter rest should be added after the last eighth note"
//  val t32 = "Two eighth notes should be inserted at the end of the second measure"
//  val t33 = "Another eighth note should be added to the end of the run in measure 5"
//  val t34 = "A C quarter note should be added between the G half note and the A half note"
//  val t35 = "After the whole note, add an eighth note"
//  val t36 = "Between the G half note and the A half note you should add a C quarter note"
//  val t37 = "After the whole note, an eighth note should be added"
//  val t38 = "An eighth rest should be added to the start of the space where you just removed a quarter note"
//
//  val t39 = "Repeat the G half note in measure 1"
  //todo: repeat events seem to have gotten messed up somehow
  val t39 = "The G half note in measure 1 should be repeated"

  failingTest should s"extract correctly from $t39" in {
    val mentions = extractMentions(t39)
    val repeatEvents = mentions.filter(_ matches "Repeat")

    repeatEvents should have length(1)
    val found = repeatEvents.head

    val musicalEntity = Note(Some(Duration("half")), Some(Pitch("G")), Some(Specifier("the")))
    val location = Location(Some(LocationTerm("in")), None, Some(Measure("measure 1")), None, None, None)
    val desired = Insert(
      Some(musicalEntity),
      Some(location)
    )

    testInsertEvent(found, desired)
  }
//
//  val t40 = "In the second bar, the eighth notes repeat traveling up. instead of down."
//
//  failingTest should s"extract correctly from $t40" in {
//    val mentions = extractMentions(t40)
//    val repeatEvents = mentions.filter(_ matches "Repeat")
//
//    repeatEvents should have length(1)
//    val found = repeatEvents.head
//
//    val note = Note(Some(Duration("eighth")), None, Some(Specifier("the")))
//    val onset = Onset(Some(Measure("the second")), None)
//    val desired = Repeat(
//      note = Some(note),
//      onset = Some(onset)
//    )
//
//    testRepeatEvent(found, desired)
//  }
//
//  val t41 = "Repeat everything in bar 3"
//
//  passingTest should s"extract correctly from $t41" in {
//    val mentions = extractMentions(t41)
//    val repeatEvents = mentions.filter(_ matches "Repeat")
//
//    repeatEvents should have length(1)
//    val found = repeatEvents.head
//
//    val onset = Onset(Some(Measure("3")), None)
//    val everything = Everything("everything")
//    val desired = Repeat(
//      onset = Some(onset),
//      everything = Some(everything)
//    )
//
//    testRepeatEvent(found, desired)
//  }
//
//  // todo: needs a way to deal with number or repetitions
//  val t42 = "Repeat everything three times"
//
//  failingTest should s"extract correctly from $t42" in {
//    val mentions = extractMentions(t42)
//    val repeatEvents = mentions.filter(_ matches "Repeat")
//
//    repeatEvents should have length(1)
//    val found = repeatEvents.head
//
//    val everything = Everything("everything")
//    val desired = Repeat(
//      everything = Some(everything)
//    )
//
//    testRepeatEvent(found, desired)
//  }
//
//  // todo: the second bar, offset?
//  val t43 = "Repeat all the half notes before the end of the second bar"
//
//  failingTest should s"extract correctly from $t43" in {
//    val mentions = extractMentions(t43)
//    val repeatEvents = mentions.filter(_ matches "Repeat")
//
//    repeatEvents should have length(1)
//    val found = repeatEvents.head
//
//    val note = Note(Some(Duration("half")), None, Some(Specifier("all the")))
//    val onset = Onset(Some(Measure("the second")), None)
//    val loc_rel = LocationRel("before")
//    val loc_abs = LocationAbs("end")
//    val desired = Repeat(
//      note = Some(note),
//      onset = Some(onset),
//      loc_rel = Some(loc_rel),
//      loc_abs = Some(loc_abs)
//    )
//
//    testRepeatEvent(found, desired)
//  }
//
//  val t44 = "Everything in the second measure should be repeated"
//
//  failingTest should s"extract correctly from $t44" in {
//    val mentions = extractMentions(t44)
//    val repeatEvents = mentions.filter(_ matches "Repeat")
//
//    repeatEvents should have length(1)
//    val found = repeatEvents.head
//
//    val onset = Onset(Some(Measure("the second")), None)
//    val everything = Everything("everything")
//    val desired = Repeat(
//      onset = Some(onset),
//      everything = Some(everything)
//    )
//
//    testRepeatEvent(found, desired)
//  }
//
//  // todo: handle number of repetitions
//  val t45 = "Everything should be repeated twice"
//
//  failingTest should s"extract correctly from $t45" in {
//    val mentions = extractMentions(t45)
//    val repeatEvents = mentions.filter(_ matches "Repeat")
//
//    repeatEvents should have length(1)
//    val found = repeatEvents.head
//
//    val everything = Everything("everything")
//    val desired = Repeat(
//      everything = Some(everything)
//    )
//
//    testRepeatEvent(found, desired)
//  }
//
//  // todo: onset as duration of validity?
//  val t46 = "All the quarter notes in the first two measures should be repeated"
//
//  failingTest should s"extract correctly from $t46" in {
//    val mentions = extractMentions(t46)
//    val repeatEvents = mentions.filter(_ matches "Repeat")
//
//    repeatEvents should have length(1)
//    val found = repeatEvents.head
//
//    val note = Note(Some(Duration("quarter")), None, Some(Specifier("All the")))
//    val onset = Onset(Some(Measure("the first two")), None)
//    val desired = Repeat(
//      note = Some(note),
//      onset = Some(onset)
//    )
//
//    testRepeatEvent(found, desired)
//  }
//
//  // todo: deal with two notes in one
//  val t47 = "Repeat the first and second quarter notes in the first bar"
//
//  failingTest should s"extract correctly from $t47" in {
//    val mentions = extractMentions(t47)
//    val repeatEvents = mentions.filter(_ matches "Repeat")
//
//    repeatEvents should have length(1)
//    val found = repeatEvents.head
//
//    val note = Note(Some(Duration("quarter")), None, Some(Specifier("the first and second")))
//    val onset = Onset(Some(Measure("the first")), None)
//    val desired = Repeat(
//      note = Some(note),
//      onset = Some(onset)
//    )
//
//    testRepeatEvent(found, desired)
//  }
//
//  // todo: needs 2 notes
//  // this SHOULD fail but it passes! issue with test framework
//  val t48 = "Repeat the first quarter note and the first eighth note in measure 2"
//
//  passingTest should s"extract correctly from $t48" in {
//    val mentions = extractMentions(t48)
//    val repeatEvents = mentions.filter(_ matches "Repeat")
//
//    repeatEvents should have length(1)
//    val found = repeatEvents.head
//
//    val note = Note(Some(Duration("quarter")), None, Some(Specifier("the first")))
//    val note_two = Note(Some(Duration("eigth")), None, Some(Specifier("the first")))
//    val onset = Onset(Some(Measure("2")), None)
//    val desired = Repeat(
//      note = Some(note),
//      onset = Some(onset)
//    )
//
//    testRepeatEvent(found, desired)
//  }
//
//  // todo: add location of repetition (if replacement for another note?) as argument
//  val t49 = "The last quarter note in the first measure should be repeated as the first note in the third measure"
//
//  failingTest should s"extract correctly from $t49" in {
//    val mentions = extractMentions(t49)
//    val repeatEvents = mentions.filter(_ matches "Repeat")
//
//    repeatEvents should have length(1)
//    val found = repeatEvents.head
//
//    val note = Note(Some(Duration("quarter")), None, Some(Specifier("the last")))
//    val note_two = Note(Some(Duration("eigth")), None, Some(Specifier("the first")))
//    val onset = Onset(Some(Measure("the first")), None)
//    val desired = Repeat(
//      note = Some(note),
//      onset = Some(onset)
//    )
//
//    testRepeatEvent(found, desired)
//  }
//
//  // todo: add measure as arg; add location of repetition as arg
//  val t50 = "Repeat the first measure as the second measure"
//
//  failingTest should s"extract correctly from $t50" in {
//    val mentions = extractMentions(t50)
//    val repeatEvents = mentions.filter(_ matches "Repeat")
//
//    repeatEvents should have length(1)
//    val found = repeatEvents.head
//
//    val onset = Onset(Some(Measure("the first")), None)
//    val desired = Repeat(
//      onset = Some(onset)
//    )
//
//    testRepeatEvent(found, desired)
//  }
//
//  // todo: add measure as arg; location of repetition
//  val t51 = "Make the second measure a repetition of the first"
//
//  failingTest should s"extract correctly from $t51" in {
//    val mentions = extractMentions(t51)
//    val repeatEvents = mentions.filter(_ matches "Repeat")
//
//    repeatEvents should have length(1)
//    val found = repeatEvents.head
//
//    val onset = Onset(Some(Measure("the second")), None)
//    val desired = Repeat(
//      onset = Some(onset)
//    )
//
//    testRepeatEvent(found, desired)
//  }
//
//  // todo: add measure as arg
//  val t52 = "Repeat the first three measures"
//
//  failingTest should s"extract correctly from $t52" in {
//    val mentions = extractMentions(t52)
//    val repeatEvents = mentions.filter(_ matches "Repeat")
//
//    repeatEvents should have length(1)
//    val found = repeatEvents.head
//
//    val onset = Onset(Some(Measure("the first")), None)
//    val desired = Repeat(
//      onset = Some(onset)
//    )
//
//    testRepeatEvent(found, desired)
//  }
//
}
