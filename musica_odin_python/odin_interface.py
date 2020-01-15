import re
import sys
import json
import pprint
import requests
import MusECI
import ExtraTypes
import PyECI


# ------------------------------------------------------------------------
# File contains interface with Odin parser implementing musica_odin domain
# Includes
#   odin_request: communicate with Odin server
#   odin_music mention handlers and parsers
#   odin_sentence_to_pyeci_spec
#
# Current top-level entry point:
# (1) odin_sentence_to_pyeci_spec()


# ------------------------------------------------------------------------
# NOTE on terminology:
# 'PyECI_spec' refers to a recursive dictionary specification for
# the values needed for *actual* PyECI syntax
# This is a placeholder until we finalize best way to map to PyECI


# ------------------------------------------------------------------------
# TODO (does not include TODOs for action types...
#       See sentence_parse_dev and Google doc: Sentence Corpus with PyECI)
# (1) Create top-level main
# (2) Create translator from PyECI_spec to *actual* PyECI (help from Donya)


class OdinInterfaceException(Exception):
    pass


# ------------------------------------------------------------------------
# Single Odin processing request functionality
# ------------------------------------------------------------------------

def odin_request(sentence: str,
                 host='http://localhost:9000',
                 fn='/getMentions') -> requests.Response:
    """
    Send an http request to Odin host, using function fn with argument
    The Odin request fn getMentions extracts all of the mentions
    :param sentence: [str] sentence
    :param host: URL for odin host
    :param fn: odin request function
    :return: either a request.Request or str
    """
    try:
        r = requests.get(host + fn, params={'text': sentence})
        # print('RESPONSE: {0}'.format(r.status_code))
        return r
    except requests.exceptions.ConnectionError as ce:
        print('ERROR odin_request(): Could not connect to Odin: ', str(ce))
        sys.exit()
    except Exception as e:
        print('ERROR odin_request(): Something went wrong: ', str(e))
        sys.exit()


# ------------------------------------------------------------------------
# Filter Actions from mentions
# ------------------------------------------------------------------------

def filter_actions(mentions: dict):
    """
    Extract any mentions that are Actions
    Actions are intended to correspond to PyECI/MusECI operations
    :param mentions:
    :return:
    """
    actions = list()
    for m in mentions['mentions']:
        if 'labels' in m:
            if 'Action' in m['labels']:
                actions.append(m)
    return actions


# ------------------------------------------------------------------------
# Process Action mentions
# ------------------------------------------------------------------------

def process_action_mention(action_mention: dict):
    """
    Top-level dispatch for currently handled Action types
    :param action_mention:
    :return:
    """
    op = None
    args = None
    if 'Insert' in action_mention['labels']:
        op = 'Insert'
        args = handle_insert(action_mention)
    elif 'Delete' in action_mention['labels']:
        op = 'Delete'
        args = handle_delete(action_mention)
    elif 'Invert' in action_mention['labels']:
        op = 'Invert'
        # NOTE: delete handler covers many cases, except...
        # TODO: add handling of axis of Inversion.
        args = handle_invert(action_mention)
    elif 'Reverse' in action_mention['labels']:
        op = 'Reverse'
        # TODO: add handling of axis of Retrograde
        args = handle_reverse(action_mention)
    elif 'Transpose' in action_mention['labels']:
        op = 'Transpose'
        args = handle_transpose(action_mention)

    return {'action': op, 'arguments': args}


def action_spec_to_ecito(spec):
    ecito = None
    if spec['action'] == 'Transpose':
        ecito = eci_transpose(spec)
    return ecito


# ------------------------------------------------------------------------
# PyECI_spec Action handlers
# These are the top-level entry to map between Odin mentions and PyECI_spec
# TODO:
#   There are repeated/redundant patterns within handlers that could
#       likely be resolved/combined.
# ------------------------------------------------------------------------


def handle_insert(mention: dict):
    loc = get_location(mention)
    # onset = get_onset(mention)
    musical_entity = get_musicalEntity(mention)
    # freq = get_frequency(mention)

    if musical_entity['onset'] is None and loc is not None:
        musical_entity['onset'] = resolve_onset(musical_entity, loc)
    else:
        if musical_entity['onset'] is None and loc is None:
            # onset not required
            pass
        else:
            print('UNHANDLED CASE: handle_transpose() onset:', mention)
            sys.exit()

    # if musical_entity['onset'] is None and loc is not None:
    #     musical_entity['onset'] = resolve_onset(musical_entity, loc)
    # else:
    #     # Appears to require an onset ?
    #     print('UNHANDLED CASE: handle_insert() onset:', mention)
    #     sys.exit()

    specifier = None
    if musical_entity['specifier'] is not None:
        specifier = musical_entity['specifier']
        # resolve_location(specifier, loc)

    #fixme
    mus_ent_type = mention['arguments']['musicalEntity'][0]['labels'][0]

    if mus_ent_type == 'Note':
        return {'MusicEntity': {'Specifier': specifier,
                                'Note': {'Pitch': musical_entity['pitch'],
                                         'Onset': musical_entity['onset'],
                                         'Duration': musical_entity['duration']}}}
    elif mus_ent_type == 'Chord':
        return {'MusicEntity': {'Specifier': specifier,
                                'Chord': {'chord_type': musical_entity['chord_type']}}}
    elif mus_ent_type == 'Rest':
        return {'MusicEntity': {'Specifier': specifier,
                                'Rest': {'Onset': musical_entity['onset'],
                                         'Duration': musical_entity['duration']}}}


def handle_delete(mention: dict):
    onset = get_onset(mention)
    note = get_note(mention)

    if note['onset'] is None and onset is not None:
        note['onset'] = onset
    else:
        if note['onset'] is None and onset is None:
            # onset not required
            pass
        else:
            print('UNHANDLED CASE: handle_delete() onset:', mention)
            sys.exit()

    specifier = None
    if note['specifier'] is not None:
        specifier = note['specifier']

    return {'MusicEntity': {'Specifier': specifier,
                            'Note': {'Pitch': note['pitch'],
                                     'Onset': note['onset'],
                                     'Duration': note['duration']}}}

def handle_invert(mention: dict):
    # musEnt, location, axis
    loc = get_location(mention)
    musicalEntity, m_type = get_musicalEntity(mention)
    # todo: Axis is not yet handled in MusECI but we have it from Odin
    axis = get_axis(mention)

    resolve_music_ent(musicalEntity, loc, mention)
    specifier = mk_specifier(musicalEntity, loc)
    mus_ent_dict = mk_music_entity_dict(musicalEntity, m_type)

    return {'MusicEntity': {'Specifier': specifier,
                            m_type: mus_ent_dict}}


def handle_reverse(mention: dict):
    onset = get_onset(mention)
    note = get_note(mention)

    if note['onset'] is None and onset is not None:
        note['onset'] = onset
    else:
        if note['onset'] is None and onset is None:
            # onset not required
            pass
        else:
            print('UNHANDLED CASE: handle_reverse() onset:', mention)
            sys.exit()

    specifier = None
    if note['specifier'] is not None:
        specifier = note['specifier']

    return {'MusicEntity': {'Specifier': specifier,
                            'Note': {'Pitch': note['pitch'],
                                     'Onset': note['onset'],
                                     'Duration': note['duration']}}}


# ------------------------------------------------------------------------
# Handle Transpose

def resolve_music_ent(musicalEntity, loc, mention):
    if musicalEntity['onset'] is None and loc is not None:
        musicalEntity['onset'] = resolve_onset(musicalEntity, loc)
    else:
        if musicalEntity['onset'] is None and loc is None:
            # onset not required
            pass
        else:
            print('UNHANDLED CASE: handle_transpose() onset:', mention)
            sys.exit()

def mk_specifier(musicalEntity, loc):
    specifier = None
    if musicalEntity['specifier'] is not None:
        specifier = musicalEntity['specifier']
        resolve_location(specifier, loc)
    return specifier

<<<<<<< HEAD
=======
def mk_music_entity_dict(musicalEntity, m_type):
    if m_type == 'Note':
        return {'Pitch': musicalEntity['pitch'],
                'Onset': musicalEntity['onset'],
                'Duration': musicalEntity['duration']}
    elif m_type == 'Chord':
        return  {'chord_type': musicalEntity['chord_type']}

def handle_transpose(mention: dict):
    loc = get_location(mention)
    musicalEntity, m_type = get_musicalEntity(mention)
    direction = get_direction(mention)
    step = get_step(mention)

    resolve_music_ent(musicalEntity, loc, mention)
    specifier = mk_specifier(musicalEntity, loc)

    mus_ent_dict = mk_music_entity_dict(musicalEntity, m_type)

>>>>>>> 0dba63050a9367e85bf24c0e954d55d973c30c13
    #fixme
    # mus_ent_type = mention['arguments']['musicalEnti ty'][0]['labels'][0]

    return {'MusicEntity': {'Specifier': specifier,
                            m_type: mus_ent_dict},
            'Direction': direction,
            'Step': step}
            


def resolve_location(specifier, loc):
    if 'relativePos' in loc:
        specifier['relative_location'] = loc['relativePos']


# ------------------------------------------------------------------------

def eci_direction(spec):
    step = None
    if spec == 'up':
        step = PyECI.Relations.Up()
    elif spec == 'down':
        step = PyECI.Relations.Down()
    return step


def string_to_int(string):
    string_to_int_map = {'one':1, 'two':2, 'three':3, 'four':4, 'five':5,
                         'six':6, 'seven':7, 'eight':8, 'nine':9, 'ten':10}
    # TODO: make this test whether can be converted and
    #       generate useful error on failure
    try:
        return int(string)
    except:
        try:
            return string_to_int_map[string]
        except:
            return int(string) #temporary to ensure it still crashes


def eci_amount(spec):
    unit = None
    if spec['proportion'] == 'whole':
        unit = ExtraTypes.WholeStep()
    elif spec['proportion'] == 'half':
        unit = ExtraTypes.HalfStep()

    value = string_to_int(spec['cardinality'])

    return ExtraTypes.PitchInterval(value=PyECI.Abstractions.Integer(value=value), unit=unit)


def handle_specifier(specifier_spec):
    # TODO: Donya's working example of parsing "transpose all notes up 1 whole step"
    #       returns
    #       Transpose(target=Note(specifier=Specifier(determiner=All())),
    #                 direction=Up(),
    #                 amount=PitchInterval(value=Integer(value=1), unit=WholeStep()))
    #       Note that
    if specifier_spec['quantifier'] == 'all' and specifier_spec['cardinality'] is None and specifier_spec['set_choice'] is None:
        return PyECI.Abstractions.All()
    else:
        determiner = None
        if 'Determiner' in specifier_spec:
            determiner_spec = specifier_spec['Determiner']
            if determiner_spec == 'All':
                determiner = PyECI.Abstractions.All()
        cardinality = None
        if 'quantifier' in specifier_spec:
            determiner = specifier_spec['quantifier']
        if 'Cardinality' in specifier_spec:
            cardinality = specifier_spec['Cardinality']
        set_choice = None
        # if 'chord_type' in specifier_spec:
        #     chord_type = specifier_spec['chord_type']
        if 'SetChoice' in specifier_spec:
            set_choice = specifier_spec['SetChoice']
        return PyECI.Abstractions.Specifier(determiner=determiner,
                                            cardinality=cardinality,
                                            setChoice=set_choice)
    # TODO: add proper error handling...
    # raise OdinInterfaceException(f"ERROR: Unknown Specifier \'{specifier}\'")


def eci_music_entity(spec):

    # print('eci_music_entity()')

    specifier = None
    if 'Specifier' in spec and spec['Specifier'] is not None:
        specifier_spec = spec['Specifier']
        specifier = handle_specifier(specifier_spec)

    music_entity = None
    if 'Note' in spec:

        # print('eci_music_entity(): Found Note')

        note_spec = spec['Note']
        duration = note_spec['Duration']
        onset = note_spec['Onset']
        pitch = note_spec['Pitch']

        # Note <- MusicEntity <- Entity <- ECI
        # ECIs have: context, specifier, attributes, words
        music_entity = ExtraTypes.Note(pitch=pitch, dur=duration, onset=onset)

        # This is done b/c Note constructor does not have arg for specifier
        # but Note <- MusicEntity <- Entity <- ECI
        # and ECIs have: context, specifier, attributes, words
        music_entity.specifier = specifier

    # todo: we can get chords, but there needs to be an interface in MusECI created for it
    # if 'Chord' in spec:
    #
    #     chord_spec = spec['Chord']
    #     duration = chord_spec['Duration']
    #     chord_type = chord_spec['chord_type']



    return music_entity


def eci_transpose(spec):
    args = spec['arguments']
    direction = eci_direction(args['Direction'])
    target = eci_music_entity(args['MusicEntity'])
    amount = eci_amount(args['Step'])
    return ExtraTypes.Transpose(target=target, direction=direction, amount=amount)


# ------------------------------------------------------------------------
# Parsers to extract various info from musica_odin mentions
# ------------------------------------------------------------------------

def get_property_value(arguments: dict, property_name: str, value_key='words'):
    """
    Helper for extracting named-key values from property in a dictionary of properties
        { '<property_name>': [ { '<key>': [ value ] ... } ... ] ... }
    :param arguments: property (arguments) dictionary
    :param property_name: property name string
    :param value_key: key name string
    :return:
    """
    property_value = None
    if property_name in arguments:
        property_args = arguments[property_name][0]
        if value_key in property_args:
            # fixme: prob make a list
            property_value = ", ".join(property_args[value_key])
    return property_value


def get_onset(mention: dict):
    """
    Extract onset info from a musica_odin mention
    Includes (optional): beat, measure
    :param mention: musica_odin mention
    :return:
    """
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
        # print('NO Onset')
        return None


def get_location(mention: dict, arg_name: str = "location"):
    """
    Extract location info from a musica_odin mention
    Includes (optional): beat, measure, location term, Note, Rest, Chord
    :param mention: musica_odin mention
    :return:
    """
    if arg_name in mention['arguments']:
        lm = mention['arguments'][arg_name][0]
        lm_args = lm['arguments']
        # get the trigger if there is one
        location_term = None
        if 'trigger' in lm:
            location_term = lm['trigger']['words']

        # find beat
        beat = None
        # TODO: add verbose failure conditions
        if 'beat' in lm_args:
            om_beat_args = lm_args['beat'][0]['arguments']
            beat = get_property_value(om_beat_args, 'cardinality')

        # find measure
        measure = None
        # TODO: add verbose failure conditions
        if 'measure' in lm_args:
            om_measure_args = lm_args['measure'][0]['arguments']
            measure = get_property_value(om_measure_args, 'cardinality')

        rel_pos = {'relation': location_term}

        # find note, if any
        note = None
        if 'note' in lm_args:
            om_note = lm_args['note'][0]
            rel_pos['music_ent'] = get_note(om_note)

        # find chord, if any
        chord = None
        if 'chord' in lm_args:
            om_chord = lm_args['chord'][0]
            rel_pos['music_ent'] = get_chord(om_chord)

        # find chord, if any
        rest = None
        if 'rest' in lm_args:
            om_rest = lm_args['rest'][0]
            rel_pos['music_ent'] = get_rest(om_rest)

        return {'beat': beat, 'measure': measure, 'relativePos': rel_pos}
    else:
        # print('NO Onset')
        return None


def resolve_onset(mus_ent, loc):
    # fixme: currently this method does essentially nothing, but we're thinking that we may need to reason a little here
    # ... if not, delete
    beat = loc['beat']
    measure = loc['measure']

    return {'beat': beat, 'measure': measure}


def get_musicalEntity(mention: dict, arg_name: str = "musicalEntity"):
    """
    Extract musicalEntity info from a musica_odin mention
    musicalEntity may be a note, rest, chord, measure
    Initial assumption is NOTE -- needs updating later
    :param mention: musica_odin mention
    :param arg_name: string name of the musical entity's argument role
    :return:
    """
    if arg_name in mention['arguments']:
        assert(len(mention['arguments'][arg_name]) == 1)
        mus_ent = mention['arguments'][arg_name][0]
        # nm = mention['arguments']['musicalEntity'][0]['arguments']

        mlabel = mus_ent['labels'][0]
        print(mlabel)

        #try using label to run different musicalEntities
        if mlabel == 'Note':
            return get_note(mus_ent), mlabel
        elif mlabel == 'Rest':
            return get_rest(mus_ent), mlabel
        elif mlabel == 'Chord':
            return get_chord(mus_ent), mlabel
        elif mlabel == 'Measure':
            return get_measure(mention), mlabel
        else:
            pass

def get_note(mention: dict):
    """
    Extract note info from a musica_odin mention
    Includes (optional): pitch, onset, duration, specifier (associated with note)
    :param mention: musica_odin mention
    :return:
    """

    note_args = mention['arguments']

    pitch_info = None
    # find pitch
    if 'pitch' in note_args:
        pitch_info = get_property_value(note_args, 'pitch')
        pitch_info = parse_pitch(pitch_info)

    # onset_info = None
    # # I don't think this will ever happen
    # if 'onset' in nm:
    #     onset_info = get_property_value(nm, 'onset')

    duration_info = None
    if 'duration' in note_args:
        duration_info = get_property_value(note_args, 'duration')
        duration_info = parse_duration(duration_info)

    specifier_info = None
    if 'specifier' in note_args:
        specifier_info = get_specifier(note_args)

    return {'pitch': pitch_info,
            'onset': None,  # todo: revisit
            'duration': duration_info,
            'specifier': specifier_info}


def get_rest(mention: dict):
    """
    Extract note info from a musica_odin mention
    Includes (optional): pitch, onset, duration, specifier (associated with note)
    :param mention: musica_odin mention
    :return:
    """

    args = mention['arguments']

    # onset_info = None
    # # I don't think this will ever happen
    # if 'onset' in nm:
    #     onset_info = get_property_value(nm, 'onset')

    duration_info = None
    if 'duration' in args:
        duration_info = get_property_value(args, 'duration')
        duration_info = parse_duration(duration_info)

    specifier_info = None
    if 'specifier' in args:
        specifier_info = get_specifier(args)

    return {'onset': None,  # todo: revisit
            'duration': duration_info,
            'specifier': specifier_info}

def get_chord(mention: dict):
    """
    Extract note info from a musica_odin mention
    Includes (optional): chordtype, specifier (associated with note)
    :param mention: musica_odin mention
    :return:
    """
    args = mention['arguments']

    # need trigger bc in instances where 'chord' not stated expicitly
    # trigger gives info on the chord type
    trigger_info = None
    if "chord" not in mention['trigger']['words']:
        trigger_info = " ".join(mention['trigger']['words'])

    chordtype_info = None
    if 'chordType' in args:
        # todo: change from join to list representation?
        chordtype_info = " ".join(args['chordType'][0]['words'])

    if trigger_info != None:
        chordtype_info = chordtype_info + " " + trigger_info

    specifier_info = None
    if 'specifier' in args:
        specifier_info = get_specifier(args)

    return {'onset': None,  # todo: revisit
            'chord_type': chordtype_info,
            'specifier': specifier_info}


def get_measure(mention: dict):
    """
    Extract measure when used as a musical entity
    E.g. 'Insert two measures'
    :param mention: musica_odin mention
    :return:
    """
    args = mention['arguments']

    specifier_info = None
    if 'specifier' in args:
        specifier_info = get_specifier(args)

    return {'specifier': specifier_info}

def get_frequency(mention: dict):
    #todo: add get frequency
    return None

def get_axis(mention: dict):
    """
    Get the axis of inversion
    :param mention:
    :return: axis dict
    """
    args = mention['arguments']

    pitch_info = None
    # find pitch
    if 'pitch' in args:
        pitch_info = get_property_value(args, 'pitch')
        pitch_info = parse_pitch(pitch_info)
    elif 'note' in args:
        note_info = get_note(args['note'])
        pitch_info = note_info['pitch']
    # either a pitch or note, if it's a note, dig in and get the note's pitch, return a string
    return {'axis': pitch_info}

def parse_duration(duration_info: str) -> dict:
    """
    Map common duration text terms to MusECI duration values
    :param duration_info:
    :return:
    """
    duration = {'measure': None, 'beat': None}
    if duration_info == 'eighth':
        duration['measure'] = 0
        duration['beat'] = 0.5
        return duration
    if duration_info == 'quarter':
        duration['measure'] = 0
        duration['beat'] = 1
        return duration
    if duration_info == 'half':
        duration['measure'] = 0
        duration['beat'] = 2
        return duration
    if duration_info == 'whole':
        duration['measure'] = 0
        duration['beat'] = 4
        return duration


def parse_pitch(raw_pitch_info: str) -> dict:
    """
    Parse pitch text to pitch_class and (optional) octave
    :param raw_pitch_info: raw text string
    :return:
    """
    pitch_info = raw_pitch_info.strip('s')  # strip 's' for plural
    pitch_info = re.split('(\d+)', pitch_info)

    pitch_class = pitch_info[0].strip('.')  # strip '.' in token for "initials": 'G.' short for 'George'

    octave = None
    if len(pitch_info) > 1:
        octave = pitch_info[1]

    return {'pitch_class': pitch_class, 'octave': octave}


def test_parse_pitch():
    assert parse_pitch('C') == {'pitch_class': 'C', 'octave': None}
    assert parse_pitch('C#') == {'pitch_class': 'C#', 'octave': None}
    assert parse_pitch('Cb') == {'pitch_class': 'Cb', 'octave': None}
    assert parse_pitch('C.') == {'pitch_class': 'C', 'octave': None}
    assert parse_pitch('Cs') == {'pitch_class': 'C', 'octave': None}
    assert parse_pitch('C#4') == {'pitch_class': 'C#', 'octave': '4'}
    assert parse_pitch('C#43') == {'pitch_class': 'C#', 'octave': '43'}
    assert parse_pitch('C#43s') == {'pitch_class': 'C#', 'octave': '43'}
    assert parse_pitch('Cb43') == {'pitch_class': 'Cb', 'octave': '43'}
    assert parse_pitch('C#.43') == {'pitch_class': 'C#', 'octave': '43'}


# test_parse_pitch()


def get_specifier(mention: dict) -> dict:
    """
    Extract Specifier info from a musica_odin mention
    Includes (optional): quantifier, cardinality, set_choice
    :param mention: musica_odin mention
    :return:
    """
    specifier_args = mention['specifier'][0]['arguments']

    quantifier = None
    if 'quantifier' in specifier_args:
        quantifier = get_property_value(specifier_args, 'quantifier')

    cardinality = None
    if 'cardinality' in specifier_args:
        cardinality = get_property_value(specifier_args, 'cardinality')
        cardinality = attempt_cardinality_to_int(cardinality)

    set_choice = None
    # todo: does this exist in the Odin/scala side?
    if 'set_choice' in specifier_args:
        set_choice = get_property_value(specifier_args, 'set_choice')

    if quantifier == 'a' or quantifier == 'an':
        quantifier = 'a'
        cardinality = 1

    return {'quantifier': quantifier, 'cardinality': cardinality, 'set_choice': set_choice}


# TODO: bring in nth algorithm from last spring 2018
NTHTEXT_TO_INT_MAP = {'first': 1, 'second': 2, 'third': 3, 'fourth': 4, 'fifth': 5,
                      'sixth': 6, 'seventh': 7, 'eighth': 8, 'ninth': 9, 'tenth': 10}


def attempt_cardinality_to_int(cardinality_value):
    if cardinality_value in NTHTEXT_TO_INT_MAP:
        return NTHTEXT_TO_INT_MAP[cardinality_value]
    else:
        return cardinality_value


def get_direction(mention: dict):
    # NOTE: assumes direction is at top-level, as is currently the case with transpose
    return get_property_value(mention['arguments'], 'direction')


def get_step(mention: dict):
    if 'step' in mention['arguments']:
        sm = mention['arguments']['step'][0]['arguments']

        cardinality = None
        if 'cardinality' in sm:
            cardinality = get_property_value(sm, 'cardinality')

        proportion = None
        if 'proportion' in sm:
            proportion = get_property_value(sm, 'proportion')

        return {'proportion': proportion, 'cardinality': cardinality}
    else:
        return None


# ------------------------------------------------------------------------
# Perform single Odin parse to PyECI
# ------------------------------------------------------------------------

def odin_sentence_to_pyeci_spec(sentence: str,
                                return_sentence=False,
                                return_mentions=False,
                                verbose=False):
    """
    Given a sentence, request Odin to process the sentence, extract the musica_odin mentions.
    :param sentence: str representing sentence to be parsed
    :param return_sentence: whether fn returns the original sentence
                            This is a helper for auto-generating target unit test targets
    :param return_mentions: whether fn includes *all* mentions in return
                            This is a helper for auto-generating target unit test targets
    :param verbose: flag controlling whether to print intermediate values (e.g., mentions)
    :return: parsed PyECI_spec (dict) and optionally sentence and/or mentions (parsed json in dict form)
    """

    if verbose:
        print('Running odin_sentence_to_pyeci_spec()')
        print('sentence: \"{0}\"'.format(sentence))

    r = odin_request(sentence)
    if verbose:
        print(r)
        pprint.pprint(r.json())

    mentions = json.loads(r.text)

    filtered_actions = filter_actions(mentions)

    actions = list()
    for filtered_action in filtered_actions:
        if verbose:
            print('\n================== Filtered Action')
            pprint.pprint(filtered_action)
            print('\n================== Actions')
        action = process_action_mention(filtered_action)
        actions.append(action)
        if verbose:
            pprint.pprint(action)

    if verbose:
        print('\n==================')
        print('Finished: \"{0}\"'.format(sentence))
        print('DONE')

    ret = [actions]

    if return_sentence:
        ret.append(sentence)

    if return_mentions:
        ret.append(mentions)

    return tuple(ret)


'''
ret = odin_sentence_to_pyeci_spec("Move the A up three steps",
                            return_sentence=True,
                            return_mentions=True,
                            verbose=True)
print(ret)
'''
