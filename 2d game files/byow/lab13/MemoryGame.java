package byow.lab13;

import byow.Core.RandomUtils;
import edu.princeton.cs.algs4.ST;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.util.Random;

public class MemoryGame {
    /** The width of the window of this game. */
    private int width;
    /** The height of the window of this game. */
    private int height;
    /** The current round the user is on. */
    private int round;
    /** The Random object used to randomly generate Strings. */
    private Random rand;
    /** Whether or not the game is over. */
    private boolean gameOver;
    /** Whether or not it is the player's turn. Used in the last section of the
     * spec, 'Helpful UI'. */
    private boolean playerTurn;
    /** The characters we generate random Strings from. */
    private static final char[] CHARACTERS = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    /** Encouraging phrases. Used in the last section of the spec, 'Helpful UI'. */
    private static final String[] ENCOURAGEMENT = {"You can do this!", "I believe in you!",
            "You got this!", "You're a star!", "Go Bears!",
            "Too easy for you!", "Wow, so impressive!"};

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please enter a seed");
            return;
        }

        long seed = Long.parseLong(args[0]);
        MemoryGame game = new MemoryGame(40, 40, seed);
        game.startGame();
    }

    public MemoryGame(int width, int height, long seed) {
        /* Sets up StdDraw so that it has a width by height grid of 16 by 16 squares as its canvas
         * Also sets up the scale so the top left is (0,0) and the bottom right is (width, height)
         */
        this.width = width;
        this.height = height;
        StdDraw.setCanvasSize(this.width * 16, this.height * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, this.width);
        StdDraw.setYscale(0, this.height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();

        //TODO: Initialize random number generator
        this.rand = new Random(seed);
    }

    public String generateRandomString(int n) {
        //TODO: Generate random string of letters of length n
        String result = "";
        while (result.length() < n) {
            int randomIndex = RandomUtils.uniform(this.rand, CHARACTERS.length);
            result += CHARACTERS[randomIndex];
        }
        return result;
    }

    public void drawFrame(String s) {
        //TODO: Take the string and display it in the center of the screen
        //TODO: If game is not over, display relevant game information at the top of the screen
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 30));
        StdDraw.text(this.width / 2, this.height / 2, s);
        if (!gameOver) {
            StdDraw.line(0, this.height - 2, this.width, this.height - 2);
            StdDraw.textLeft(0, this.height - 1, "Round " + this.round);
            if (this.playerTurn) {
                //display Type
                StdDraw.text(this.width / 2, this.height - 1, "Type!");
            }
            else {
                //display Watch
                StdDraw.text(this.width / 2, this.height - 1, "Watch!");
            }
            int randomIndex = RandomUtils.uniform(this.rand, ENCOURAGEMENT.length);
            StdDraw.textRight(this.width, this.height - 1, ENCOURAGEMENT[randomIndex]);
        }
        StdDraw.show();
    }

    public void flashSequence(String letters) {
        //TODO: Display each character in letters, making sure to blank the screen between letters
        for (int i = 0; i < letters.length(); i++) {
            Character current = letters.charAt(i);
            this.drawFrame(current.toString());
            StdDraw.pause(500);
            this.drawFrame("");
            StdDraw.pause(500);
        }
    }

    public String solicitNCharsInput(int n) {
        //TODO: Read n letters of player input
        String userInput = "";
        while (userInput.length() < n) {
            if (StdDraw.hasNextKeyTyped()) {
                char current = StdDraw.nextKeyTyped();
                userInput += current;
                this.drawFrame(userInput);
            }
        }
        return userInput;
    }

    public void startGame() {
        //TODO: Set any relevant variables before the game starts
        this.round = 1;
        String randomString = "";
        String userInput = "";

        //TODO: Establish Engine loop
        while (userInput.equals(randomString)) {
            //play the game
            this.playerTurn = false;
            StdDraw.pause(1000);
            this.drawFrame("Round: " + this.round);
            StdDraw.pause(1000);
            randomString = this.generateRandomString(round);
            this.flashSequence(randomString);
            this.playerTurn = true;
            userInput = this.solicitNCharsInput(this.round);
            this.round += 1;
        }

        //quit the game
        this.round -= 1;
        this.gameOver = true;
        this.drawFrame("Game Over! You made it to round: " + this.round);
    }

}
