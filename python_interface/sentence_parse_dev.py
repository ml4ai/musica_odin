from odin_interace import odin_sentence_to_pyeci_spec
from sentence_corpora import *
import pprint
import datetime
import os


# root directory home for corpus_parse_state files
SNAPSHOT_DST_ROOT = 'corpus_parse_state_snapshots'
CSTATE_FILENAME_BASE = 'odin_parse_state'


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
# Parse Corpus
# ------------------------------------------------------------------------

def batch_odin_parse(corpus, return_sentence=False, verbose=False):
    """
    Top-level helper for use when developing or testing sequence of sentences.
    This can be used
    :param corpus: sequence of sentences
    :param return_sentence: when True: include sentence in return value
    :param verbose: when True: run odin_sentence_to_pyeci_spec with verbose True
    :return:
    """
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
def create_corpus_parse_state_snapshot(corpus, filename=None):
    if not os.path.exists(SNAPSHOT_DST_ROOT):
        print('Snapshot DST_ROOT does not exist, creating:')
        os.makedirs(SNAPSHOT_DST_ROOT)
        print('    ... created:', SNAPSHOT_DST_ROOT)
    if filename is None:
        filename = CSTATE_FILENAME_BASE + '_' + get_timestamp()
    snapshot_dst = os.path.join(SNAPSHOT_DST_ROOT, filename)
    print('Saving corpus parse state:', snapshot_dst)
    with open(snapshot_dst, 'w') as fout:
        pass


create_corpus_parse_state_snapshot(ALL_SENTENCES)


# TODO: create fn to:
# (1) read in corpus snapshot file
# (2) iterate throuch each sentence and (a) get *current* parse and (b) compare *current* against snapshot
# (3) report diffs
def regression_test(snapshot_src):
    pass


# ------------------------------------------------------------------------
# Scripts calls
# ------------------------------------------------------------------------

# batch_odin_parse(CORPUS_REVERSE, return_sentence=True)

# batch_odin_parse([CORPUS_INVERSION[0]], return_sentence=True, verbose=True)

# batch_odin_parse(["Reverse all the notes in measure 1"], return_sentence=True, verbose=True)
