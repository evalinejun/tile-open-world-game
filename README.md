# CS61B Tile Based 2D Open World Project 

## The Game
> POV: you are a pretty flower exploring a forest while collecting other flower pollen to find clues of your family
- By entering "N" to start new game and providing an input string, engine generates a random 2D world of rooms and hallways to explore
- Use WASD keys on keyboard to move avatar (a single tile flower)
- Project uses StdDraw to handle the user inputs (keys and cursor)

## Mini Games
Single tile portals are randomly generated throughout the map when the world is generated.
- Moving the avatar into each portal will teleport the avater into another single room to collect pollen tiles
- User will only be able to teleport back to original world after collecting all of the pollen

## Persistance
If user enters ":Q" in game, the program will quit game and save the latest rendering.
Enter "L" to load in the previously saved game in the home screen.

## User Interface Elements
Element descriptions are shown in the top left corner of the game screen based on position of the cursor.
Current data and time are shown in the top right corner of the game screen.

- User Avatar = pink flower
- Room/Hallway Floors = grass
- Room/Hallway Walls = trees
- Beyond the walls = ocean 
- Purple and Black outlined tiles = portals
