**CLASSES AND DATA STRUCTURES**

ENGINE CLASS =

Instance Variables:

SEED - keeps track of seed to use 

RANDOM - makes random object with seed

WIDTH - width of the world randomized between 50 to 80

HEIGHT - height of the world randomized between 20 to 32

WALL - which Tileset tile to use to represent the walls (TREE)

FLOOR - which Tileset tile to use to represent the floors of rooms and hallways (WATER)



**ALGORITHMS**

ENGINE CLASS =

public TETile[][] interactWithInputString(String input)
- Sets up the world and acts as the main method, as it calls makeRooms

public static void makeRooms(TETile[][] world)
- Divides the world into quarters (horizontally)
- Iterates through each quarter, filling it with randomly placed rooms of random sizes

public static void fillQuarterWRooms(int heightMin, int heightMax, TETile[][] world)
- Fills the individual quarter with the randomly placed and sized rooms from left to right
- Variable x keeps track of the last available spot to place a new room (ie width - x = how much space is still available to place a room)
- While x is less than the width (there is still space for more rooms), new rooms of random heights and widths are created and placed at a random distance from the previous room

public static void drawRoom(TETile[][] tiles, Room r):
- “Draws” the room by filling up the tiles in the room
  - Iterates through all the rows of the room
     - The first and last row of the room will always be all walls
     - The rooms in between the first and last rooms with have the pattern of wall → floors → wall
       - Iterate through all the middle tiles in the row to fill them with floor tiles, and the first and last tiles of the row will be wall tiles

public static int randomInt(int min, int max)
- Helper method for generating a random integer between the min and max bounds

public static void fillBoardWithNothing(TETile[][] tiles)
- Fills the board with Tileset.Nothing tiles after instantiating the world

private static class Room
- Instance variables:
   - w: width of the room
   - h: height of the room
   - bottomLeftX: the x coordinate of the bottom left corner of the room
   - bottomLeftY: the y coordinate of the bottom left corner of the room
   
- Room(int w, int h, int bottomLeftX, int bottomLeftY)
   - Constructs a new Room

