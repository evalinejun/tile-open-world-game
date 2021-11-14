package byow.lab12;

import org.junit.Test;
import static org.junit.Assert.*;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {
    private static final int WIDTH = 50;
    private static final int HEIGHT = 80;

    private static final long SEED = 2873129;
    private static final Random RANDOM = new Random(SEED);

    /**
     * draws a row of tiles on the board based on given anchor position
     */
    public static void drawRow(TETile[][] tiles, Position p, TETile tile, int length) {
        for (int dx = 0; dx < length; dx++) {
            tiles[p.x + dx][p.y] = tile;
        }
    }

    /**
     * add a hexagon to the world at position P of size SIZE
     */
    public static void addHexagon(TETile[][] tiles, Position p, TETile t, int size) {
        if (size < 2) {
            return;
        }
        addHexagonHelper(tiles, p, t, size - 1, size);
    }

    /**
     * adds a column of NUM hexagons, each with random biomes
     * to the world at Position p. Each of the hexagons are of size SIZE
     */
    public static void addHexColumn(TETile[][] tiles, Position p, int size, int num) {
        if (num < 1) {
            return;
        }
        /* draw this hexagon */
        addHexagon(tiles, p, randomTile(), size);
        /* draw n-1 hexagon below it */
        if (num > 1) {
            Position bottomNeighbor = getBottomNeighbor(p, size);
            addHexColumn(tiles, bottomNeighbor, size, num - 1);
        }
    }

    /**
     * recursive helper method for addHexagon
     */
    public static void addHexagonHelper(TETile[][] tiles, Position p, TETile tile, int b, int t) {
        /* draw this row */
        Position startOfRow = p.shift(b, 0);
        drawRow(tiles, startOfRow, tile, t);

        /* draw remaining rows recursively */
        if (b > 0) {
            Position nextP = p.shift(0, -1);
            addHexagonHelper(tiles, nextP, tile,b - 1, t + 2);
        }

        /* draw this row again to be the reflection */
        Position startOfReflectiveRow = startOfRow.shift(0, -(2*b + 1));
        drawRow(tiles, startOfReflectiveRow, tile, t);
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

    /**
     * Picks a random biome tile
     */
    private static TETile randomTile() {
        int tileNum = RANDOM.nextInt(3);
        switch (tileNum) {
            case 0: return Tileset.GRASS;
            case 1: return Tileset.FLOWER;
            case 2: return Tileset.SAND;
            case 3: return Tileset.MOUNTAIN;
            case 4: return Tileset.TREE;
            default: return Tileset.NOTHING;
        }
    }

    /**
     * gets the position of the top right neighbor of a hexagon at Position p
     * N = size of the hexagon we are tessellating
     */
    public static Position getTopRightNeighbor(Position p, int n) {
        return p.shift(2*n - 1, n);
    }

    /**
     * gets the position of the top right neighbor of a hexagon at Position p
     * N = size of the hexagon we are tessellating
     */
    public static Position getBottomRightNeighbor(Position p, int n) {
        return p.shift(2*n - 1, -n);
    }

    /**
     *  gets the position of the bottom neighbor of a hexagon at Position P
     *  n = size of the hexagons we are tessellating
     */
    public static Position getBottomNeighbor(Position p, int n) {
        return p.shift(0, -2*n);
    }

    /**
     * private helper class to deal with anchor positions
     */
    private static class Position {
        int x;
        int y;

        Position(int x, int y) {
            this.x = x;
            this.y = y;
        }
        public Position shift(int dx, int dy) {
            return new Position(this.x + dx, this.y + dy);
        }
    }

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        TETile[][] world = new TETile[WIDTH][HEIGHT];
        fillBoardWithNothing(world);
        Position anchor = new Position(12, 34);
        drawWorld(world, anchor, 3, 4);

        ter.renderFrame(world);
    }

    /**
     * draws hexagonal world (another main)
     */
    public static void drawWorld(TETile[][] tiles, Position p, int hexSize, int tessSize) {
        /* draw the first hexagon */
        addHexColumn(tiles, p, hexSize, tessSize);

        /* expand up and to the right */
        for (int i = 1; i < tessSize; i++) {
            p = getTopRightNeighbor(p, hexSize);
            addHexColumn(tiles, p, hexSize, tessSize + i);
        }
        /* expand down and to the right */
        for (int i = tessSize - 2; i >= 0; i--) {
            p = getBottomRightNeighbor(p, hexSize);
            addHexColumn(tiles, p, hexSize, tessSize + i);
        }
    }
}

