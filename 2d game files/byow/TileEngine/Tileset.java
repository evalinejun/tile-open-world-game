package byow.TileEngine;

import java.awt.Color;

/**
 * Contains constant tile objects, to avoid having to remake the same tiles in different parts of
 * the code.
 *
 * You are free to (and encouraged to) create and add your own tiles to this file. This file will
 * be turned in with the rest of your code.
 *
 * Ex:
 *      world[x][y] = Tileset.FLOOR;
 *
 * The style checker may crash when you try to style check this file due to use of unicode
 * characters. This is OK.
 */

public class Tileset {
    public static final Color BROWN = new Color(172, 131, 91);
    public static final Color DARK_GREEN = new Color(0, 153, 0);
    public static final Color GRASS_GREEN = new Color(102, 204, 0);
    public static final Color DARK_GRASS = new Color(0, 180, 0);
    public static final Color SEA = new Color(0, 102, 204);
    public static final Color TREE_GREEN = new Color(51, 102, 0);
    public static final Color DARK_BROWN = new Color(131, 74, 0);
    public static final Color PURPLE = new Color(153, 51, 255);

    public static final TETile AVATAR = new TETile('@', Color.white, Color.black, "you");
    public static final TETile WALL = new TETile('#', new Color(216, 128, 128), Color.darkGray,
            "wall");
    public static final TETile FLOOR = new TETile('·', new Color(128, 192, 128), Color.black,
            "floor");
    public static final TETile NOTHING = new TETile(' ', SEA, SEA, "nothing");
    public static final TETile GRASS = new TETile('"', DARK_GREEN, GRASS_GREEN, "grass");
    public static final TETile WATER = new TETile('≈', Color.blue, Color.black, "water");
    public static final TETile FLOWER = new TETile('❀', Color.magenta, Color.pink, "flower");
    public static final TETile LOCKED_DOOR = new TETile('█', PURPLE, Color.black,
            "locked door");
    public static final TETile UNLOCKED_DOOR = new TETile('∩', DARK_BROWN, DARK_GRASS,
            "unlocked door");
    public static final TETile SAND = new TETile('▒', Color.yellow, Color.black, "sand");
    public static final TETile MOUNTAIN = new TETile('▲', Color.gray, Color.black, "mountain");
    public static final TETile TREE = new TETile('♠', TREE_GREEN, BROWN, "tree");
    public static final TETile POLLEN = new TETile('*', Color.yellow, GRASS_GREEN, "pollen");
}


