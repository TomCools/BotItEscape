import java.util.*;
import java.io.*;
import java.math.*;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int w = in.nextInt(); // width of the board
        int h = in.nextInt(); // height of the board
        int playerCount = in.nextInt(); // number of players (2 or 3)
        int myId = in.nextInt(); // id of my player (0 = 1st player, 1 = 2nd player, ...)

        Dragon me = null;
        List<Wall> walls;
        // game loop
        while (true) {
            for (int i = 0; i < playerCount; i++) {
                int x = in.nextInt(); // x-coordinate of the player
                int y = in.nextInt(); // y-coordinate of the player
                int wallsLeft = in.nextInt(); // number of walls available for the player

                if(i == myId) {
                    me = new Dragon(i,x,y,wallsLeft);
                }
            }
            int wallCount = in.nextInt(); // number of walls on the board
            walls = new ArrayList<Wall>();
            for (int i = 0; i < wallCount; i++) {
                int wallX = in.nextInt(); // x-coordinate of the wall
                int wallY = in.nextInt(); // y-coordinate of the wall
                String wallOrientation = in.next(); // wall orientation ('H' or 'V')
                walls.add(new Wall(wallX,wallY,wallOrientation.equalsIgnoreCase("H")));
            }

            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");

            String nextMove = nextMove(me, walls);

            // action: LEFT, RIGHT, UP, DOWN or "putX putY putOrientation" to place a wall
            System.out.println(nextMove);
        }
    }

    private static String nextMove(Dragon me, List<Wall> walls) {
        int x = me.getX();
        int y = me.getY();

        //Move to the right
        if(walls.stream().noneMatch(w -> w.blocks(x, y, x + 1, y))) {
            return "RIGHT";
        } else {
            return "DOWN";
        }
    }

    static class Dragon {
        int id;
        int x;
        int y;
        int wallsLeft;

        public Dragon(int id, int x, int y, int wallsLeft) {
            this.id = id;
            this.x = x;
            this.y = y;
            this.wallsLeft = wallsLeft;
        }

        public int getId() {
            return id;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getWallsLeft() {
            return wallsLeft;
        }
    }

    static class Wall {
        static final int LENGTH = 2;
        int x;
        int y;
        boolean isHorizontal;

        public Wall(int x, int y, boolean isHorizontal) {
            this.x = x;
            this.y = y;
            this.isHorizontal = isHorizontal;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public boolean isHorizontal() {
            return isHorizontal;
        }

        public boolean blocks(int x1, int y1, int x2, int y2) {
            System.err.println("Blocks? : " + x +y+isHorizontal);
            if(!isHorizontal) {
                if(x1 == x-1 && x2 == x && y1 >= y && y1 < y+2) {
                    return true;
                } else if(x1 == x && x2 == x+1 && y1 > y && y1 < y+2) {
                    return true;
                }
                return false;
            }
            return false;
        }
    }
}