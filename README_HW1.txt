What ran was after I did an orientation change, first came onPause, to pause the activity. Then
ran onStop, then onCreate, then onStart, and onResume. The activity was running, then it was paused and stopped
then it had to be created again, started again, and resumed with any saved/cached data.