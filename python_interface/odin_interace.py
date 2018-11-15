from typing import List
import re
import os
import sys
import json
import pprint
import datetime
from collections import OrderedDict
import requests
import pygraphviz


# ------------------------------------------------------------------------
# Usage:
# The following script contains two "top-level" functions -- see docstrings
# (1) perform_single_dependency_parse()
# (2) batch_odin_parse()


# ------------------------------------------------------------------------
# TODO
# (1) Create top-level main


# ------------------------------------------------------------------------
# Optional global path to sentence corpus file


# NOTE: you'll need to update the root to your own location of MUSICA Google Drive contents
SENTENCE_CORPUS_ROOT = '/Users/claytonm/Google Drive/MUSICA/DataSets/Sentences/'
# SENTENCE_CORPUS_ROOT = '/home/elision/MUSICA/musica/sandbox/matsuura/sentences/'
SENTENCE_CORPUS_PATH = os.path.join(SENTENCE_CORPUS_ROOT, 'sentences.txt')


# ------------------------------------------------------------------------
# Utility: get_timestamp


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
# Single Odin processing request functionality

# def odin_request(sentence: str,
#                  host='localhost:9000/getMentions') -> requests.Response:
#     """
#
#     :param sentence:
#     :param host:
#     :return:
#     """
#     try:
#         r = requests.get(host, params={'sent': sentence})
#         return r
#     except requests.exceptions.ConnectionError as ce:
#         print('ERROR odin_request(): Could not connect to Odin: ', str(ce))
#         sys.exit()
#     except Exception as e:
#         print('ERROR odin_request(): Something went wrong: ', str(e))
#         sys.exit()


def odin_request(sentence: str,
                 host='http://localhost:9000',
                 fn='/getMentions') -> requests.Response:
    """
    Send an http request to Odin host, using function fn with argument
    :param sentence: [str] sentence
    :param host: URL for odin host
    :param fn: odin request function
    :return: either a request.Request or str
    """
    try:
        r = requests.get(host + fn, params={'text': sentence})
        print('RESPONSE: {0}'.format(r.status_code))
        return r
    except requests.exceptions.ConnectionError as ce:
        print('ERROR odin_request(): Could not connect to Odin: ', str(ce))
        sys.exit()
    except Exception as e:
        print('ERROR odin_request(): Something went wrong: ', str(e))
        sys.exit()


def filter_actions(mentions: dict):
    actions = list()
    for m in mentions['mentions']:
        if 'labels' in m:
            if 'Action' in m['labels']:
                actions.append(m)
    return actions


def process_action_mention(action_mention: dict):
    if 'Insert' in action_mention['labels']:
        return 'Insert', handle_insert(action_mention)


def handle_insert(mention: dict):
    onset = get_onset(mention)
    note = get_note(mention)

    if note['onset'] is None and onset is not None:
        note['onset'] = onset
    else:
        print('UNHANDLED CASE: handle_insert() onset:', mention)
        sys.exit()

    specifier = None
    if note['specifier'] is not None:
        specifier = note['specifier']

    return {'specifier': specifier,
            'note': { 'pitch': note['pitch'],
                      'onset': note['onset'],
                      'duration': note['duration']}}


# TODO: generalize - really just getting a property values down one level
def get_property_value(arguments: dict, property_name: str, value_key='words'):
    """
    assume pattern
        { '<property_name>': [ { '<key>': [ value ] ... } ... ] ... }
    :param arguments:
    :param property_name:
    :param value_key:
    :return:
    """
    property_value = None
    if property_name in arguments:
        property_args = arguments[property_name][0]
        if 'words' in property_args:
            property_value = property_args[value_key][0]
    return property_value


def get_onset(mention: dict):
    if 'onset' in mention['arguments']:
        om = mention['arguments']['onset'][0]['arguments']

        # find beat
        beat = None
        # TODO: add verbose failure conditions
        if 'beat' in om:
            om_beat_args = om['beat'][0]['arguments']
            beat = get_property_value(om_beat_args, 'cardinality')

        # find measure
        measure = None
        # TODO: add verbose failure conditions
        if 'measure' in om:
            om_measure_args = om['measure'][0]['arguments']
            measure = get_property_value(om_measure_args, 'cardinality')

        return {'beat': beat, 'measure': measure}
    else:
        print('NO Onset')
        return None


def get_note(mention: dict):
    if 'note' in mention['arguments']:
        nm = mention['arguments']['note'][0]['arguments']

        pitch_info = None
        # find pitch
        if 'pitch' in nm:
            # TODO: parsing pitch: token may include period
            # (because processors may treat as name initials, e.g., 'G.' short for George)
            # Could be handled on Odin side, but not yet, so just be aware...
            # TODO: parsing pitch: may end in 's' for plurals
            pitch_info = get_property_value(nm, 'pitch')

        onset_info = None
        if 'onset' in nm:
            onset_info = get_property_value(nm, 'onset')

        duration_info = None
        if 'duration' in nm:
            duration_info = get_property_value(nm, 'duration')

        specifier_info = None
        if 'specifier' in nm:
            specifier_info = get_specifier(nm)

        return {'pitch': pitch_info,
                'onset': onset_info,
                'duration': duration_info,
                'specifier': specifier_info}

    else:
        print('NO Note')
        return None


def get_specifier(mention: dict):
    specifier_args = mention['specifier'][0]['arguments']

    quantifier = None
    if 'quantifier' in specifier_args:
        quantifier = get_property_value(specifier_args, 'quantifier')

    cardinality = None
    if 'cardinality' in specifier_args:
        cardinality = get_property_value(specifier_args, 'cardinality')

    set_choice = None
    if 'set_choice' in specifier_args:
        set_choice = get_property_value(specifier_args, 'set_choice')

    return quantifier, cardinality, set_choice


def perform_single_dependency_parse(sentence: str):
    """
    Given a sentence, request Odin process the sentence, extract the dependency parse
    and save the graph to file (currently defaults to 'dep_graph_test.png').
    :param sentence: str representing sentence to be parsed
    :return:
    """
    r = odin_request(sentence)
    print(r)
    pprint.pprint(r.json())

    filtered_actions = filter_actions(json.loads(r.text))

    for filtered_action in filtered_actions:
        print('\n================== Filtered Action')
        pprint.pprint(filtered_action)
        print('\n================== Actions')
        action = process_action_mention(filtered_action)
        pprint.pprint(action)

    print('\n==================')


    print('DONE')


perform_single_dependency_parse(sentence="Insert a C4 quarter note on beat 1 of measure 3.")

