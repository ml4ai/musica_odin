from odin_interace import odin_sentence_to_pyeci_spec
import NewAntlrMusic


def antlr_sentence_parse(sentence, verbose=True):
    ecis, unprocessed, tokens, unknown_words = NewAntlrMusic.parseStringANTLR(sentence)
    if verbose:
        print('New Tokens:  ', tokens)
        print('ECIs:        ', ecis)
        print('Unprocessed: ', unprocessed)
        print('Unknown:     ', unknown_words, '\n')
    return ecis

'''
ret = odin_sentence_to_pyeci_spec \
    ("Move the A up three steps",
     return_sentence=True,
     return_mentions=True,
     verbose=True)

print(ret)
'''


antlr_sentence_parse("Move the A up three steps")
