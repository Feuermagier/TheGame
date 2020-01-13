# Advanced game simulation

### Usage
(\* marks required options)

java -jar TheGame.jar [options]
  Options:
  + \* -file
      Name of the game configuration file
      
  +   -parallel
      Run in parallel mode for faster execution
      Default: false
      
  +   -cores
      Cores to use in parallel mode. Default is one less than available 
      virtual cores
      Default: 11
      
  +   -predict-win
      Decides whether the algorithm should try to predict the win of a player 
      in its next turn. May speed up the analysis.
      Default: false
      

### Configuration file description

The config file should contain one line of the following pattern (players should be marked 'w'/'white' or 
'b'/'black' (case insensitive)):
<starting player>,<x-Dimension>,<y-Dimension>,<field data>

The field data is a comma-separated list of all field positions. A positions is marked either with '0' (empty), 
'b' (black pawn) or 'w' (white pawn) (case insensitive). The black players target row is the row with the highest x index,
the white players target row is the row with x = 0.


Example:
w,5,4,b,b,b,b,b,b,b,b,0,0,0,0,w,w,w,w,w,w,w,w
