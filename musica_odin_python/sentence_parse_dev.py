import odin_interface
import NewAntlrMusic
import pprint
import json


def antlr_sentence_parse(sentence, verbose=True):
    ecis, unprocessed, tokens, unknown_words = NewAntlrMusic.parseStringANTLR(sentence)
    if verbose:
        print('\n----- MusicaAntlr -----')
        print('New Tokens:  ', tokens)
        print('ECIs:        ', ecis)
        print('Unprocessed: ', unprocessed)
        print('Unknown:     ', unknown_words)
        print('-----------------------')
    return ecis


def get_and_print_actions(sentence):
    print('\n----- Musica Odin -----')
    r = odin_interface.odin_request(sentence)
    mentions = json.loads(r.text)
    # pprint.pprint(mentions)
    action_mentions = odin_interface.filter_actions(mentions)
    actions = list()
    for filtered_action in action_mentions:
        action_spec = odin_interface.process_action_mention(filtered_action)
        pprint.pprint(action_spec)

        ecito = odin_interface.action_spec_to_ecito(action_spec)
        print(f'ecito: {ecito}')

        actions.append((ecito, action_spec))
    print('-----------------------')

    '''
    ret = odin_interface.odin_sentence_to_pyeci_spec \
        (sentence,
         return_sentence=True,
         return_mentions=True,
         verbose=False)

    for action in ret:
        pprint.pprint(action)
    '''

    return actions


if __name__ == '__main__':
    SENTENCES = ["delete the note in measure 1",
                 "insert a half rest",
                 "transpose all the notes up 1 whole step",
                 "transpose the quarter note in measure 2 down 2 half steps",
                 "reverse the notes in measure 2",
                 "transpose the first quarter note down 1 half step"
                 # "transpose the minor second up 1 whole step", #works in odin, not in antlr
                 # "convert the quarter note in measure 1 to a half note", #works in odin, not in antlr
                 # "move the A up three steps" #processed as both convert and transpose, convert fails
                 # "change the C in measure 1 to a D", # assertion error, because this is runs both convert + TRANSPOSE
                 # "take the first note in measure 1 and switch it with the second note in measure 2", #works in odin
                 ]


    for SENTENCE in SENTENCES:
        print(f'\nSentence: \"{SENTENCE}\"')
        get_and_print_actions(SENTENCE)
        antlr_sentence_parse(SENTENCE)
