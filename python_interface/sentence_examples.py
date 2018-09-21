from odin_interace import perform_single_dependency_parse


# perform_single_dependency_parse('Work on measures 1 to 5')
# perform_single_dependency_parse('Work on measure 1')
# perform_single_dependency_parse('Work on measure 1 through beat 3 of measure 5') ## broken!

# perform_single_dependency_parse('Measure 1')
# perform_single_dependency_parse('Beat 1 of Measure 2')

# perform_single_dependency_parse('Move the car')

"""
perform_single_dependency_parse('Delete all the notes')
# Delete(MusicEntity(Specifier(All, None, None), Note(None, None, None)))

perform_single_dependency_parse('Invert all the notes')
# Invert(MusicEntity(Specifier(All, None, None), Note(None, None, None)), None)  # second arg: axis of inversion

"""

perform_single_dependency_parse('Delete everything below C4')  # Ask Donya: what's representation for 'below' -- in specifier?
# Delete(MusicEntity(Specifier(All, None, None), Note(C4)))


"""
perform_single_dependency_parse('Invert everything in measure 1')  # new (not in original list)
# Invert(MusicEntity(Specifier(All, None, None), Note(None, None, Onset(1, None)), None)

perform_single_dependency_parse('Invert all the notes around G4')  # invert is vertical, so below & above
# Invert(MusicEntity(Specifier(All, None, None), Note(None, None, Onset(1, None)), Pitch(...stuff specifying G4...))
perform_single_dependency_parse('Insert a C4 quarter note on beat 1 of measure 3')  #
# Insert(MusicEntity(Specifier(A, 1, None),
#                    Note(C4, (0, 1) <dur: measure, beat>, (2, 0) <onset: measure, beat>)))
perform_single_dependency_parse('Transpose everything up 5 half steps')  # easier  # new (not in original list)
# Transpose(MusicEntity(Specifier(All, None, None),
#                       Note(None, None, None)),
#           <direction>: Up,
#           <extent (amount); units of HalfSteps>: 12)  # "up 3 steps" often means 3 steps in the scale - context!
perform_single_dependency_parse('Transpose all the Cs up 5 half steps')  # intermediate

perform_single_dependency_parse('Transpose all the Cs in measure 1 up 5 half steps')  # harder
perform_single_dependency_parse('Transpose all the Cs up 5 half steps in measure 1')  # harder: distributed target (in TRIPS, in measure 1 gets associated with transpose, rather than the Cs)
"""