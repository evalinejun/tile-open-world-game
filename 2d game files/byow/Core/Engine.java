package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.io.*;

import java.util.HashMap;

import java.util.ArrayList;

import java.util.Random;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;

import java.text.SimpleDateFormat;
import java.util.Date;

/** @author Evaline Jun, Sarah Zhang
 *
 * @Source : We used gitlet.Utils.java methods to save the previous input
 *          String as a file (saved.txt).
 *
 * @Source : This following source showed us how to check if a character is a digit or letter.
 * https://stackoverflow.com/questions/6117389/checking-if-a-character-is-an-integer-or-letter
 *
 * @Source : The following source showed us how to convert a character to a long.
 * https://stackoverflow.com/questions/29997665/converting-from-a-char-to-a-long
 *
 * @Source : We used the following source to establish the current time/date for our HUD.
 * https://www.javatpoint.com/java-get-current-date
 */

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */

    long SEED;
    Random RANDOM;
    int WIDTH;
    int HEIGHT;
    int xCurr;
    int yCurr;
    int xCurrMini;
    int yCurrMini;
    int count;
    String currSaved;
    TETile[][] world;
    // public static final ArrayList<Room> ROOMS = new ArrayList<Room>();

    public static final TETile WALL = Tileset.TREE;
    public static final TETile FLOOR = Tileset.GRASS;
    public static final TETile AVATAR = Tileset.FLOWER;
    public static final TETile ENCOUNTER = Tileset.LOCKED_DOOR;
    public static final TETile POLLEN = Tileset.POLLEN;

    public static final File CWD = new File(System.getProperty("user.dir"));
    private static final File SAVED = Utils.join(CWD, "saved.txt");

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard(String[] args) {
        /** display main menu with options:
         *      - start a new world ("N")
         *          - prompt user to enter a seed: press "S" after they've finished entering seed
         *      - load previously saved world ("L")
         *      - quit ("Q")
         */
        String seed;
        setUpMainMenu();
        initCommand();
        //game.startGame()
        while (true) { //this is an infinite loop silly
            if (StdDraw.hasNextKeyTyped()) {
                char menuChoice = StdDraw.nextKeyTyped(); //will be either N, L, or Q
                if (menuChoice == 'N' || menuChoice == 'n') {
                    Utils.writeContents(SAVED, "");
                    addCharToSaved(menuChoice);
                    seed = askForSeed();
                    world = helper(seed);
                    interactWithWorld(world);
                    ter.initialize(WIDTH, HEIGHT + 2);
                    ter.renderFrame(world);
                } else if (menuChoice == 'L' || menuChoice == 'l') {
                    //loadWorld(seed);
                    if (SAVED.exists()) {
                        currSaved = getSaved();
                        world = interactWithInputString(currSaved);
                        ter.initialize(WIDTH, HEIGHT + 2);
                        ter.renderFrame(world);
                        interactWithWorld(world);
                        ter.renderFrame(world);
                    } else {
                        System.exit(0);
                    }
                    Utils.writeContents(SAVED, "");
                    addCharToSaved(menuChoice);
                } else if (menuChoice == 'Q' || menuChoice == 'q') {
                    System.exit(0);
                }
            }
        }

    }

    public void hud() {
        TETile currentTile = updateMouse(world);
        String tileString = convertTileToString(currentTile);
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date date = new Date();

        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 15));
        StdDraw.textLeft(1, HEIGHT + 1, tileString);
        StdDraw.textRight(WIDTH - 1, HEIGHT + 1, formatter.format(date));
        StdDraw.show();
        StdDraw.pause(40);
    }

    public TETile updateMouse(TETile[][] world1) {
        int x = (int) StdDraw.mouseX();
        int y = (int) StdDraw.mouseY();
        if (x < WIDTH && y < HEIGHT) {
            return world1[x][y];
        }
        return null;
    }

    public String convertTileToString(TETile tile) {
        if (tile == FLOOR) {
            return "grass (floor)";
        }
        if (tile == WALL) {
            return "tree (wall)";
        }
        if (tile == ENCOUNTER) {
            return "portal (encounter)";
        }
        if (tile == AVATAR) {
            return "flower (avatar)";
        }
        if (tile == POLLEN) {
            return "pollen";
        }
        return "ocean (nothing)";
    }

    /* possibly returning a world to save */
    public void interactWithWorld(TETile[][] world1) {
        if (!checkWorld(world1)) {
            count = randomInt(4, 7);
            for (int i = 0; i < count + 1; i++) {
                spawnExtras(world1, ENCOUNTER);
            }
            spawn(world1);
        }
        ter.initialize(WIDTH, HEIGHT + 2);
        char prevChar = 'x';
        while (true) {
            hud();
            if (StdDraw.hasNextKeyTyped()) {
                char directionChoice = StdDraw.nextKeyTyped();
                addCharToSaved(directionChoice);
                if (directionChoice == 'S' || directionChoice == 's') {
                    move(xCurr, yCurr, xCurr, yCurr - 1, world1, false);
                } else if (directionChoice == 'A' || directionChoice == 'a') {
                    move(xCurr, yCurr, xCurr - 1, yCurr, world1, false);
                } else if (directionChoice == 'D' || directionChoice == 'd') {
                    move(xCurr, yCurr, xCurr + 1, yCurr, world1, false);
                } else if (directionChoice == 'W' || directionChoice == 'w') {
                    move(xCurr, yCurr, xCurr, yCurr + 1, world1, false);
                } else if (directionChoice == ':') {
                    prevChar = ':';
                } else if (prevChar == ':' && (directionChoice == 'Q' || directionChoice == 'q')) {
                    System.exit(0);
                }
            }
            ter.renderFrame(world1);
        }
    }

    public static void initCommand() {
        if (SAVED.exists()) {
            return; //potentially not enough to abort
        } else {
            setupFiles();
        }
    }

    private static void addCharToSaved(char command) {
        String stringCommand = Character.toString(command);
        String previousCommands = Utils.readContentsAsString(SAVED);
        previousCommands += stringCommand;
        Utils.writeContents(SAVED, previousCommands);
    }

    private static String getSaved() {
        String savedCommands = Utils.readContentsAsString(SAVED);
        return savedCommands;
    }

    public static void setupFiles() {
        try {
            SAVED.createNewFile();
        } catch (IOException e) {
            return;
        }
        Utils.writeContents(SAVED, "");
    }

    public void spawn(TETile[][] world1) {
        xCurr = RANDOM.nextInt(WIDTH - 1);
        yCurr = RANDOM.nextInt(HEIGHT - 1);
        while (world1[xCurr][yCurr] != FLOOR) {
            xCurr = RANDOM.nextInt(WIDTH - 1);
            yCurr = RANDOM.nextInt(HEIGHT - 1);
        }
        world1[xCurr][yCurr] = AVATAR;
    }

    public void spawnMini(TETile[][] world1) {
        xCurrMini = RANDOM.nextInt(WIDTH - 1);
        yCurrMini = RANDOM.nextInt(HEIGHT - 1);
        while (world1[xCurrMini][yCurrMini] != FLOOR) {
            xCurrMini = RANDOM.nextInt(WIDTH - 1);
            yCurrMini = RANDOM.nextInt(HEIGHT - 1);
        }
        world1[xCurrMini][yCurrMini] = AVATAR;
    }

    public void spawnExtras(TETile[][] world1, TETile tile) {
        int x = RANDOM.nextInt(WIDTH - 1);
        int y = RANDOM.nextInt(HEIGHT - 1);
        while (world1[x][y] != FLOOR) {
            x = RANDOM.nextInt(WIDTH - 1);
            y = RANDOM.nextInt(HEIGHT - 1);
        }
        world1[x][y] = tile;
    }

    public void move(int oldX, int oldY, int newX, int newY,
                     TETile[][] world1, boolean isMiniWorld) {
        if (world1[newX][newY] == WALL) {
            return;
        } else if (world1[newX][newY] == FLOOR) {
            world1[oldX][oldY] = FLOOR;
            world1[newX][newY] = AVATAR;
            if (isMiniWorld) {
                xCurrMini = newX;
                yCurrMini = newY;
            } else {
                xCurr = newX;
                yCurr = newY;
            }
        } else if (world1[newX][newY] == ENCOUNTER) {
            world1[oldX][oldY] = FLOOR;
            encounter();
            ter.renderFrame(world1);
            world1[newX][newY] = AVATAR;
            xCurr = newX;
            yCurr = newY;
        } else if (world1[newX][newY] == POLLEN) {
            world1[oldX][oldY] = FLOOR;
            world1[newX][newY] = AVATAR;
            count--;
            xCurrMini = newX;
            yCurrMini = newY;
        }
    }

    public void encounter() {
        /*screen with instructions to the encounter minigame*/
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 20));
        StdDraw.text(WIDTH / 2, (HEIGHT / 2) + 1,
                "Collect all the pollen for your flower! .・゜゜・(´・ω・｀)");
        StdDraw.show();
        StdDraw.pause(1900);

        /*the minigame*/
        TETile[][] miniWorld = new TETile[WIDTH][HEIGHT];
        fillBoardWithNothing(miniWorld);
        Room miniRoom = new Room(WIDTH / 3, HEIGHT / 4, WIDTH / 3, 0, 0);
        drawRoom(miniWorld, miniRoom);
        count = 6;
        for (int i = 0; i < count; i++) {
            spawnExtras(miniWorld, POLLEN);
        }
        spawnMini(miniWorld);
        ter.renderFrame(miniWorld);
        char prevChar = 'x';
        while (count > 0) {
            if (StdDraw.hasNextKeyTyped()) {
                char directionChoice = StdDraw.nextKeyTyped();
                addCharToSaved(directionChoice);
                if (directionChoice == 'S' || directionChoice == 's') {
                    move(xCurrMini, yCurrMini, xCurrMini, yCurrMini - 1, miniWorld, true);
                } else if (directionChoice == 'A' || directionChoice == 'a') {
                    move(xCurrMini, yCurrMini, xCurrMini - 1, yCurrMini, miniWorld, true);
                } else if (directionChoice == 'D' || directionChoice == 'd') {
                    move(xCurrMini, yCurrMini, xCurrMini + 1, yCurrMini, miniWorld, true);
                } else if (directionChoice == 'W' || directionChoice == 'w') {
                    move(xCurrMini, yCurrMini, xCurrMini, yCurrMini + 1, miniWorld, true);
                } else if (directionChoice == ':') {
                    prevChar = ':';
                } else if (prevChar == ':' && (directionChoice == 'Q' || directionChoice == 'q')) {
                    System.exit(0);
                }
            }
            ter.renderFrame(miniWorld);
        }

        /*screen after finishing minigame*/
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 20));
        StdDraw.text(WIDTH / 2, (HEIGHT / 2) + 1, "Great job you pollinating queen/king!（⋆＾－＾⋆）");
        StdDraw.show();
        StdDraw.pause(1900);
    }

    public static void setUpMainMenu() {
        /** set up main menu screen*/
        int width = 40;
        int height = 40;
        StdDraw.setCanvasSize(width * 16, height * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, width);
        StdDraw.setYscale(0, height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();

        /** main menu text*/
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 40));
        StdDraw.text(width / 2, height * .75, "CS 61B GAME");
        StdDraw.setFont(new Font("Monaco", Font.PLAIN, 20));
        StdDraw.text(width / 2, height / 2, "New Game (N)");
        StdDraw.text(width / 2, (height / 2) - 2, "Load Game (L)");
        StdDraw.text(width / 2, (height / 2) - 4, "Quit (Q)");

        StdDraw.show();
    }

    public static String askForSeed() {
        int width = 40;
        int height = 40;
        String inputString = "";

        StdDraw.setCanvasSize(width * 16, height * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, width);
        StdDraw.setYscale(0, height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();

        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 20));
        StdDraw.text(width / 2, (height / 2) + 2, "Input a seed followed by the letter S");

        StdDraw.show();

        while (inputString.length() == 0
                || !(Character.toUpperCase(inputString.charAt(inputString.length() - 1)) == 'S')) {
            if (StdDraw.hasNextKeyTyped()) {
                char current = StdDraw.nextKeyTyped();
                inputString += current;
                addCharToSaved(current);
                StdDraw.clear(Color.BLACK);
                StdDraw.setPenColor(Color.WHITE);
                StdDraw.setFont(new Font("Monaco", Font.BOLD, 20));
                StdDraw.text(width / 2, (height / 2) + 2, "Input a seed followed by the letter S");
                StdDraw.text(width / 2, height / 2, inputString);
                StdDraw.show();
            }
        }
        return inputString;
    }


    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
    //     */
//    public static void main (String[] args) {
//        interactWithInputString("hi");
//    }
    public TETile[][] interactWithInputString(String input) {
        // Fill out this method so that it run the engine using the input
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.
        // - initialize tile rendering
        // TERenderer ter = new TERenderer();
        // ter.initialize(WIDTH, HEIGHT);
        // - create world
        // TETile[][] world = new TETile[WIDTH][HEIGHT];
        // fillBoardWithNothing(world);
        // Position anchor = new Position(12, 34);
        // drawWorld(world, anchor, 3, 4);
        // - display
        // ter.renderFrame(world);
        currSaved = input;
        initCommand();
        world = null;
        char menuChoice = nextChar(); //will be either N, L, or Q
        if (menuChoice == 'N' || menuChoice == 'n') {
            Utils.writeContents(SAVED, "");
            addCharToSaved(menuChoice);
            world = helper(input);
            chopSeed();
            world = loadWorld(world);
        } else if (menuChoice == 'L' || menuChoice == 'l') {
            if (SAVED.exists()) {
                String otherCommands = input.substring(1);
                String savedCommands = getSaved();
                currSaved = savedCommands + otherCommands;
                //currSaved += otherCommands;
                //System.out.println(currSaved);
                world = interactWithInputString(currSaved);
                //System.out.println(currSaved);
            }
            Utils.writeContents(SAVED, "");
            addCharToSaved(menuChoice);
        } else if (menuChoice == 'Q' || menuChoice == 'q') {
            return world;
        }
//        ter.initialize(WIDTH, HEIGHT);
//        ter.renderFrame(world);
        return world;
    }

    public void chopSeed() {
        Character currentChar;
        int len = currSaved.length();
        for (int i = 0; i < len; i++) {
            currentChar = currSaved.charAt(i);
            addCharToSaved(currentChar);
            if (Character.toUpperCase(currentChar) == 'S') {
                currSaved = currSaved.substring(i + 1);
                break;
            }
        }
    }

    public boolean hasNextChar() {
        if (currSaved.length() > 0) {
            if (currSaved.charAt(0) == ':') {
                if (currSaved.length() > 1 && (currSaved.charAt(1) == 'Q'
                        || currSaved.charAt(1) == 'q')) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public Character nextChar() {
        if (hasNextChar()) {
            Character nextChar = currSaved.charAt(0);
            currSaved = currSaved.substring(1);
            return nextChar;
        } else {
            return null;
        }
    }

    public TETile[][] loadWorld(TETile[][] world1) {
        count = randomInt(4, 7);
        for (int i = 0; i < count + 1; i++) {
            spawnExtras(world1, ENCOUNTER);
        }
        spawn(world1);
        char prevChar = 'x';
        while (hasNextChar()) {
            char directionChoice = nextChar();
            addCharToSaved(directionChoice);
            if (directionChoice == 'S' || directionChoice == 's') {
                loadMove(xCurr, yCurr, xCurr, yCurr - 1, world1, false);
            } else if (directionChoice == 'A' || directionChoice == 'a') {
                loadMove(xCurr, yCurr, xCurr - 1, yCurr, world1, false);
            } else if (directionChoice == 'D' || directionChoice == 'd') {
                loadMove(xCurr, yCurr, xCurr + 1, yCurr, world1, false);
            } else if (directionChoice == 'W' || directionChoice == 'w') {
                loadMove(xCurr, yCurr, xCurr, yCurr + 1, world1, false);
            } else if (directionChoice == ':') {
                prevChar = ':';
            } else if (prevChar == ':' && (directionChoice == 'Q' || directionChoice == 'q')) {
                break;
            } else {
                break;
            }
        }
        return world1;
    }

    public boolean checkWorld(TETile[][] world1) {
        for (int row = 0; row < WIDTH; row++) {
            for (int col = 0; col < HEIGHT; col++) {
                if (world1[row][col] == AVATAR) {
                    xCurr = row;
                    yCurr = col;
                    return true;
                }
            }
        }
        return false;
    }

    public void loadMove(int oldX, int oldY, int newX, int newY,
                         TETile[][] world1, boolean isMiniWorld) {
        if (world1[newX][newY] == WALL) {
            return;
        } else if (world1[newX][newY] == FLOOR) {
            world1[oldX][oldY] = FLOOR;
            world1[newX][newY] = AVATAR;
            if (isMiniWorld) {
                xCurrMini = newX;
                yCurrMini = newY;
            } else {
                xCurr = newX;
                yCurr = newY;
            }
        } else if (world1[newX][newY] == ENCOUNTER) {
            world1[oldX][oldY] = FLOOR;
            loadEncounter();
            world1[newX][newY] = AVATAR;
            xCurr = newX;
            yCurr = newY;
        } else if (world1[newX][newY] == POLLEN) {
            world1[oldX][oldY] = FLOOR;
            world1[newX][newY] = AVATAR;
            count--;
            xCurrMini = newX;
            yCurrMini = newY;
        }
    }

    public void loadEncounter() {
        /*the minigame*/
        TETile[][] miniWorld = new TETile[WIDTH][HEIGHT];
        fillBoardWithNothing(miniWorld);
        Room miniRoom = new Room(WIDTH / 3, HEIGHT / 4, WIDTH / 3, 0, 0);
        drawRoom(miniWorld, miniRoom);
        count = 6;
        for (int i = 0; i < count; i++) {
            spawnExtras(miniWorld, POLLEN);
        }
        spawnMini(miniWorld);
        char prevChar = 'x';
        while (count > 0) {
            if (hasNextChar()) {
                char directionChoice = nextChar();
                addCharToSaved(directionChoice);
                if (directionChoice == 'S' || directionChoice == 's') {
                    loadMove(xCurrMini, yCurrMini, xCurrMini, yCurrMini - 1, miniWorld, true);
                } else if (directionChoice == 'A' || directionChoice == 'a') {
                    loadMove(xCurrMini, yCurrMini, xCurrMini - 1, yCurrMini, miniWorld, true);
                } else if (directionChoice == 'D' || directionChoice == 'd') {
                    loadMove(xCurrMini, yCurrMini, xCurrMini + 1, yCurrMini, miniWorld, true);
                } else if (directionChoice == 'W' || directionChoice == 'w') {
                    loadMove(xCurrMini, yCurrMini, xCurrMini, yCurrMini + 1, miniWorld, true);
                } else if (directionChoice == ':') {
                    prevChar = ':';
                } else if (prevChar == ':' && (directionChoice == 'Q' || directionChoice == 'q')) {
                    count = 0;
                    break;
                }
            } else {
                break;
            }
        }
    }

    public TETile[][] helper(String input) {
        SEED = inputToSeed(input);
        RANDOM = new Random(SEED);
        WIDTH = (5 + RANDOM.nextInt(3)) * 10; //min = 50, max = 80
        HEIGHT = (8 + RANDOM.nextInt(5)) * 4; //min = 32, max = 52

        TETile[][] finalWorldFrame = new TETile[WIDTH][HEIGHT];
        fillBoardWithNothing(finalWorldFrame);
        makeRooms(finalWorldFrame);

        return finalWorldFrame;
    }

    public long inputToSeed(String input) {
        Character currentChar;
        long seed1 = 0;
        for (int i = 0; i < input.length(); i++) {
            currentChar = input.charAt(i);
            if (Character.isDigit(currentChar)) {
                seed1 = seed1 * 10 + Character.getNumericValue(currentChar);
            }
        }
        return seed1;
    }

    public void makeRooms(TETile[][] world1) {
        int quarterMax = HEIGHT / 4; //exclusive
        int quarterMin = 0; //inclusive

        //hashmap of halls, where key = quarter # (0 indexed)
        //value = AList of hallways in that quarter
        HashMap<Integer, ArrayList<HorizHallway>> hallsPerQ
                = new HashMap<Integer, ArrayList<HorizHallway>>();
        ArrayList<Room> allRooms = new ArrayList<Room>();
        for (int i = 0; i < 4; i++) {
            ArrayList<Room> quarterRooms = fillQuarterWRooms(quarterMin, quarterMax, world1, i);
            ArrayList<HorizHallway> hallways = fillQuarterWHallways(world1, quarterRooms);
            quarterMin = quarterMax;
            quarterMax += HEIGHT / 4;
            hallsPerQ.put(i, hallways);
            allRooms.addAll(quarterRooms);
        }
        makeVertHallways(world1, hallsPerQ);
        ArrayList<Room> falseRoomies = allConnected(allRooms);
        for (Room r : falseRoomies) {
            boolean upDone = straightUpFalseRoom(r, world1);
            if (!upDone) {
                straightDownFalseRoom(r, world1);
            }
        }
    }

    public ArrayList<Room> fillQuarterWRooms(int hMin, int hMax, TETile[][] world1, int quarter) {
        ArrayList<Room> quarterRooms = new ArrayList<Room>();
        int x = randomInt(0, WIDTH / 4);
        while (x < WIDTH) {
            int roomHeight = randomInt(5, hMax - hMin);
            int roomWidth = randomInt(4, 10);
            int newX = x + randomInt(1, 10);
            int newY = randomInt(hMin, hMax - roomHeight);
            if (newX + roomWidth - 1 >= WIDTH) {
                break;
            } else {
                Room newRoom = new Room(roomWidth, roomHeight, newX, newY, quarter);
                quarterRooms.add(newRoom);
                drawRoom(world1, newRoom);
                //potentially add to a hashmap of rooms^
                x = newX + roomWidth;
            }
        }
        return quarterRooms;
    }

    public static void drawRoom(TETile[][] tiles, Room r) {
        for (int y = r.bottomLeftY; y < r.bottomLeftY + r.h; y++) { //row indices
            // top and bottom walls
            if (y == r.bottomLeftY || y == r.h + r.bottomLeftY - 1) {
                for (int x = r.bottomLeftX; x < r.w + r.bottomLeftX; x++) {
                    tiles[x][y] = WALL;
                }
            } else { //middle left to right
                tiles[r.bottomLeftX][y] = WALL;
                for (int i = r.bottomLeftX + 1; i < r.w + r.bottomLeftX; i++) {
                    tiles[i][y] = FLOOR;
                }
                tiles[r.bottomLeftX + r.w - 1][y] = WALL;
            }
        }
    }

    /** generates a random integer using RANDOM between the given bounds. */
    public int randomInt(int min, int max) {
        return min + RANDOM.nextInt(Math.abs(max - min));
    }

    private static class Room {
        int w;
        int h;
        int bottomLeftX;
        int bottomLeftY;
        boolean isConnected = false;
        int quarter; //zero-indexed

        Room(int w, int h, int bottomLeftX, int bottomLeftY, int quarter) {
            this.w = w;
            this.h = h;
            this.bottomLeftX = bottomLeftX;
            this.bottomLeftY = bottomLeftY;
            this.quarter = quarter;
        }
//        public HexWorld.Position shift(int dx, int dy) {
//            return new HexWorld.Position(this.x + dx, this.y + dy);
//        }
    }

    private static class HorizHallway {
        int length;
        //the coordinates of the floor tile in the hallway
        int startX; //where the hallway is connected to the room
        int endX;
        int y;

        HorizHallway(int length, int startX, int endX, int y) {
            this.length = length;
            this.startX = startX;
            this.endX = endX;
            this.y = y;
        }
    }

    /** use this method at the end of creating hallways to check that all the rooms are connected*/
    public static ArrayList<Room> allConnected(ArrayList<Room> rooms) {
        ArrayList<Room> falseRooms = new ArrayList<Room>();
        for (Room r : rooms) {
            if (!r.isConnected) {
                falseRooms.add(r);
            }
        }
        return falseRooms;
    }

//    public void makeHallways(TETile[][] world) {
//        int quarterMax = HEIGHT / 4; //exclusive
//        int quarterMin = 0; //inclusive
//
//        for (int i = 0; i < 4; i++) {
//            fillQuarterWHallways(quarterMin, quarterMax, world, );
//            quarterMin = quarterMax;
//            quarterMax += HEIGHT / 4;
//        }
//    }

    public ArrayList<HorizHallway> fillQuarterWHallways(TETile[][] world1, ArrayList<Room> qRooms) {
        ArrayList<HorizHallway> hallways = new ArrayList<HorizHallway>();
        for (int i = 0; i < qRooms.size() - 1; i++) {
            Room r1 = qRooms.get(i);
            Room r2 = qRooms.get(i + 1);
            ArrayList<Integer> overlap = horizontalOverlap(r1, r2);
            int r1rightWall = r1.bottomLeftX + r1.w - 1;
            int hallMiddleI;
            if (overlap == null) {
                continue;
            } else if (overlap.size() == 3) {
                hallMiddleI = overlap.get(1);
            } else {
                hallMiddleI = overlap.get(randomInt(1, overlap.size() - 2));
            }
            int distBtwnRooms = r2.bottomLeftX - (r1.bottomLeftX + r1.w);
            for (int j = r1rightWall; j < r1rightWall + distBtwnRooms + 2; j++) {
                drawHorizHall(j, hallMiddleI, world1);
            }
            r1.isConnected = true;
            int hLen = distBtwnRooms + 1;
            HorizHallway hall = new HorizHallway(hLen, r1rightWall, r2.bottomLeftX, hallMiddleI);
            hallways.add(hall);
        }
        return hallways;
    }

    public void makeVertHallways(TETile[][] world1,
                                 HashMap<Integer,
                                         ArrayList<HorizHallway>> hallwayMap) {
        /** make sure that there is atleast one hallways long enough to branch
         * if none long enough, branch from a room
         * else, find longest hallway, and branch upwards brrrrrr
         * */

        for (int i = 0; i < hallwayMap.size() - 1; i++) { //iterates through each quarter
            ArrayList<HorizHallway> longHall = longHall(hallwayMap.get(i));
            if (longHall.size() == 0) { //no hallways in this quarter are long enough to branch
                continue;
            } else if (longHall.size() == 1) {
                HorizHallway chosenHall = longHall.get(0);
                straightUpHallway(chosenHall, world1);
            } else {
                for (HorizHallway hall : longHall) {
                    boolean save = straightUpHallway(hall, world1);
                    if (save) {
                        break;
                    }
                }
            }
        }
    }

    public boolean straightUpHallway(HorizHallway hall, TETile[][] world1) {
        int x = hall.startX + 2;
        int lastX = hall.endX - 2;
        int y = hall.y + 2;
        int startY = y - 1;
        int endY = -1;
        while (x <= lastX) {
            while (y < HEIGHT) {
                if (world1[x][y] == WALL) {
                    if (world1[x - 1][y] == WALL && world1[x + 1][y] == WALL) {
                        endY = y;
                        drawWholeVertHall(x, startY, endY, world1);
                        break;
                    }
                    break;
                } else {
                    y++;
                }
            }
            x++;
            if (endY >= 0) {
                break;
            }
            y = hall.y + 2;
        }
        if (endY < 0) {
            return false; //returns whether we successfully added a hallway or not
        }
        return true;
    }

    public boolean straightUpFalseRoom(Room fRoom, TETile[][] world1) {
        int x = fRoom.bottomLeftX + 1;
        int lastX = fRoom.bottomLeftX + fRoom.w - 2;
        int y = fRoom.bottomLeftY + fRoom.h;
        int startY = y - 1;
        int endY = -1;
        while (x <= lastX) {
            while (y < HEIGHT) {
                if (world1[x][y] == WALL) {
                    if (world1[x - 1][y] == WALL && world1[x + 1][y] == WALL) {
                        endY = y;
                        drawWholeVertHall(x, startY, endY, world1);
                        break;
                    }
                    break;
                } else {
                    y++;
                }
            }
            x++;
            if (endY >= 0) {
                break;
            }
            y = fRoom.bottomLeftY + fRoom.h;
        }
        if (endY < 0) {
            return false; //returns whether we successfully added a hallway or not
        }
        return true;
    }

    public void straightDownFalseRoom(Room fRoom, TETile[][] world1) {
        int x = fRoom.bottomLeftX + 2;
        int lastX = fRoom.bottomLeftX + fRoom.w - 2;
        int y = fRoom.bottomLeftY - 1;
        int startY = y + 1;
        int endY = -1;
        while (x <= lastX) {
            while (y > 0) {
                if (world1[x][y] == FLOOR) {
                    return;
                }
                if (world1[x][y] == WALL) {
                    if (world1[x - 1][y] == WALL && world1[x + 1][y] == WALL) {
                        endY = y;
                        drawWholeVertHall(x, endY, startY, world1);
                        break;
                    }
                    break;
                } else {
                    y--;
                }
            }
            x++;
            y = fRoom.bottomLeftY - 1;
        }
    }

    /** takes a list of hallways of the quarter,
     * returns a list of hallways that are long enough to form an L**/
    public static ArrayList<HorizHallway> longHall(ArrayList<HorizHallway> hallways) {
        ArrayList<HorizHallway> longHallways
                = new ArrayList<HorizHallway>();
        for (HorizHallway hall : hallways) {
            if (hall.length >= 4) {
                longHallways.add(hall);
            }
        }
        return longHallways;
    }

    public static void drawHorizHall(int x, int y, TETile[][] tiles) {
        tiles[x][y] = FLOOR;
        tiles[x][y + 1] = WALL;
        tiles[x][y - 1] = WALL; //might be problematic when y = 0
    }

    public static void drawVertHall(int x, int y, TETile[][] world) {
        world[x][y] = FLOOR;
        world[x + 1][y] = WALL;
        world[x - 1][y] = WALL; //might be problematic when y = 0
    }

    public static void drawWholeVertHall(int x, int startY, int endY, TETile[][] world) {
        for (int i = startY; i < endY + 1; i++) {
            drawVertHall(x, i, world);
        }
    }

    public static ArrayList<Integer> horizontalOverlap(Room room1, Room room2) {
        int yMin1 = room1.bottomLeftY;
        int yMin2 = room2.bottomLeftY;
        int yMax1 = room1.bottomLeftY + room1.h;
        int yMax2 = room2.bottomLeftY + room2.h;
        int overlapCount = 0;
        ArrayList<Integer> y1 = new ArrayList<Integer>();
        ArrayList<Integer> overlapValues = new ArrayList<Integer>();
        for (int i = yMin1; i < yMax1; i++) {
            y1.add(i);
        }
        for (int j = yMin2; j < yMax2; j++) {
            if (y1.contains(j)) {
                overlapCount += 1;
                overlapValues.add(j);
            }
        }
        if (overlapCount >= 3) {
            return overlapValues;
        }
        return null;
    }

    /**
     * Fills the given 2D array of tiles with blank tiles.
     */
    public static void fillBoardWithNothing(TETile[][] tiles) {
        int height = tiles[0].length;
        int width = tiles.length;
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }
    }

    /** Assorted utilities.
     *
     * Give this file a good read as it provides several useful utility functions
     * to save you some time.
     *
     *  @author P. N. Hilfinger
     */
    private static class Utils {
        static byte[] readContents(File file) {
            if (!file.isFile()) {
                throw new IllegalArgumentException("must be a normal file");
            }
            try {
                return Files.readAllBytes(file.toPath());
            } catch (IOException excp) {
                throw new IllegalArgumentException(excp.getMessage());
            }
        }

        /** Return the entire contents of FILE as a String.  FILE must
         *  be a normal file.  Throws IllegalArgumentException
         *  in case of problems. */
        static String readContentsAsString(File file) {
            return new String(readContents(file), StandardCharsets.UTF_8);
        }

        /** Write the result of concatenating the bytes in CONTENTS to FILE,
         *  creating or overwriting it as needed.  Each object in CONTENTS may be
         *  either a String or a byte array.  Throws IllegalArgumentException
         *  in case of problems. */
        static void writeContents(File file, Object... contents) {
            try {
                if (file.isDirectory()) {
                    throw
                            new IllegalArgumentException("cannot overwrite directory");
                }
                BufferedOutputStream str =
                        new BufferedOutputStream(Files.newOutputStream(file.toPath()));
                for (Object obj : contents) {
                    if (obj instanceof byte[]) {
                        str.write((byte[]) obj);
                    } else {
                        str.write(((String) obj).getBytes(StandardCharsets.UTF_8));
                    }
                }
                str.close();
            } catch (IOException | ClassCastException excp) {
                throw new IllegalArgumentException(excp.getMessage());
            }
        }

        static File join(File first, String... others) {
            return Paths.get(first.getPath(), others).toFile();
        }

        /** Returns a byte array containing the serialized contents of OBJ. */
        static byte[] serialize(Serializable obj) {
            try {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                ObjectOutputStream objectStream = new ObjectOutputStream(stream);
                objectStream.writeObject(obj);
                objectStream.close();
                return stream.toByteArray();
            } catch (IOException excp) {
                throw new Error("Internal error serializing commit.");
            }
        }
    }
}

