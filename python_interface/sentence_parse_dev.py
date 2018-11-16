from odin_interace import odin_sentence_to_pyeci_spec
import pprint
import datetime


# ------------------------------------------------------------------------
# Utility: get_timestamp
# ------------------------------------------------------------------------


def pretty_time(the_time: datetime.datetime) -> str:
    """
    Returns a string in the following format:
        <year><month><day><24hour><minute><second>-<milliseconds>
    :param the_time: a datetime.datetime object
    :return: str
    """
    return '{y}{mon:02d}{day:02d}{hr:02d}{min:02d}{sec:02}_{msec:07d}' \
        .format(y=the_time.year, mon=the_time.month, day=the_time.day,
                hr=the_time.hour, min=the_time.minute, sec=the_time.second,
                msec=the_time.microsecond)


def pretty_timedelta(delta: datetime.timedelta):
    return 'days: {days}, seconds: {sec}, microseconds: {msec}' \
        .format(days=delta.days, sec=delta.seconds, msec=delta.microseconds)


def get_timestamp() -> str:
    """
    Get pretty_time of current time.
    :return: str
    """
    return pretty_time(datetime.datetime.now())


# ------------------------------------------------------------------------
# Corpora by action category
# ------------------------------------------------------------------------

CORPUS_INSERT = \
    ("Insert a C4 quarter note on beat 1 of measure 3.",
     "Insert a C4 quarter note on measure 1 beat 1",
     "Insert a C4 quarter note on measure 1, beat 1",
     "Insert a G4 half note on beat 3 of measure 2",
     "Insert a C4 quarter note at beat 1 of measure 1",
     "Insert a G4 half note on beat 1 of measure 1",
     "Insert an F4 whole note on beat 1 of measure 3"
    )

CORPUS_DELETE = \
    ("Delete the C in measure 1",
     "Delete the Cs in measure 1",
     "Delete all the notes in measure 2",
     "Delete the second G",
     "Delete all the notes",
     "Delete all the Fs"
    )

CORPUS_REVERSE = \
    ("Reverse all the notes",
     "Reverse all the G",
     "Reverse all the notes in measure 1"  # currently broken
    )

CORPUS_TRANSPOSE = \
    ("Transpose the C4 quarter note on beat 1 of measure 1 up 5 half steps",  # draft
     "Transpose the Eb in measure 5 down 3 half steps",                  # draft
     "Transpose the note on beat 3.5 of measure 5 down 1 half step",     # draft
     "Transpose the C# on beat 1 up 1 half step",
     "Transpose the C in measure 1 up 1 half step",
     "Transpose the C up 1 half step",
     "Transpose the C quarter note up 1 half step",
     "Transpose all the notes up 1 half step",
     "Transpose all the notes in measure 2 down 5 half steps",
     "Transpose the quarter note up 1 half step",
     "Transpose all the quarter notes up 1 half step",
     "Transpose the first C up 2 half steps",
     "Transpose the second C up 2 half steps",
     "Transpose everything up 5 half steps",
     "Transpose all the C up 5 half steps",
     "Transpose all the C in measure 1 up 5 half steps",
     "Transpose all the Cs up 5 half steps in measure 1",
     "Transpose all the notes up 5 half steps in measure 1",
     "Transpose all the notes up 5 half steps between measures 1 and 3"
     )

CORPUS_INVERSION = \
    ("Invert all the notes",
     "Invert all the G",
     "Invert all the notes in measure 1",
     "Invert everything in measure 1",
     "Invert all the notes around G4"
     )

CORPUS_BETWEEN_THROUGH = \
    ("Delete everything between C4 and E5",
     "Reverse all the notes in measures 1 through 4",
     "Transpose everything between the C in measure 1 and the E in measure 2 up 5 half steps",
     "Reverse the notes from beats 1 through 3 in measure 2"
     )

# TODO: Add: Generation, Make et al, Setting selections, Naming, Co-refs


# ------------------------------------------------------------------------
# Parse Corpus
# ------------------------------------------------------------------------

def batch_odin_parse(corpus, return_sentence=False, verbose=False):
    for sentence in corpus:
        pprint.pprint(odin_sentence_to_pyeci_spec(sentence=sentence,
                                                  return_sentence=return_sentence,
                                                  verbose=verbose))


# ------------------------------------------------------------------------
# Corpus Regression Testing
# ------------------------------------------------------------------------

# TODO: create fn to:
# (1) iterate through sentence corpus getting current (a) musica_odin mentions and (b) pyeci-spec parses
# (2) save (a) sentence, (b) musica_odin_mentions, and (c) pyeci-sped parses to snapshot file
def create_corpus_state_snapshot(corpus, snapshot_dst):
    pass


# TODO: create fn to:
# (1) read in corpus snapshot file
# (2) iterate throuch each sentence and (a) get *current* parse and (b) compare *current* against snapshot
# (3) report diffs
def regression_test(snapshot_src):
    pass


# ------------------------------------------------------------------------
# Scripts
# ------------------------------------------------------------------------

# batch_odin_parse(CORPUS_REVERSE, return_sentence=True)

batch_odin_parse(["Transpose the note on beat 3.5 of measure 5 down 1 half step"], verbose=True)
