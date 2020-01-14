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
    pprint.pprint(mentions)
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
    SENTENCES = ["transpose all notes after the first B up 1 whole step"]
    # SENTENCES = [
    #              "transpose all the notes up 1 whole step",
    #              "transpose the first quarter note down 1 half step",
    #              "move the A up three steps",
    #              "transpose the minor second up 1 whole step"]


    for SENTENCE in SENTENCES:
        print(f'\nSentence: \"{SENTENCE}\"')
        get_and_print_actions(SENTENCE)
        antlr_sentence_parse(SENTENCE)
