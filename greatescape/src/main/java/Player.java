import java.util.*;
import java.io.*;
import java.math.*;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {
    static int h;
    static int w;

    static List<Path> path;

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        w = in.nextInt(); // width of the board
        h = in.nextInt(); // height of the board
        int playerCount = in.nextInt(); // number of players (2 or 3)
        int myId = in.nextInt(); // id of my player (0 = 1st player, 1 = 2nd player, ...)

        Dragon me = null;
        List<Wall> walls;
        // game loop
        while (true) {
            path = new ArrayList<>();

            for (int i = 0; i < playerCount; i++) {
                int x = in.nextInt(); // x-coordinate of the player
                int y = in.nextInt(); // y-coordinate of the player
                int wallsLeft = in.nextInt(); // number of walls available for the player

                if (i == myId) {
                    me = new Dragon(i, x, y, wallsLeft);
                }
            }
            int wallCount = in.nextInt(); // number of walls on the board
            walls = new ArrayList<Wall>();
            for (int i = 0; i < wallCount; i++) {
                int wallX = in.nextInt(); // x-coordinate of the wall
                int wallY = in.nextInt(); // y-coordinate of the wall
                String wallOrientation = in.next(); // wall orientation ('H' or 'V')
                walls.add(new Wall(wallX, wallY, wallOrientation.equalsIgnoreCase("H")));
            }

            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");
            calculatePaths(me.x, me.y, w - 1, me.y, walls, new ArrayList<>(), 10);
            Optional<Path> bestPath = Player.path.stream().min(Comparator.comparingInt(c -> c.getMoves().size()));
            bestPath.ifPresent(p -> {
                //System.err.println("Shortest Path: ");
                //for (Move move : p.getMoves()) {
                //    System.err.println(move);
                //}
                Move move = p.getMoves().get(0);
                //System.err.println("Using path definition");
                System.out.println(move.getDefinition());
            });

            //System.out.println("No Path Found! :(");
        }
    }

    private static void calculatePaths(int x, int y, int tX, int tY, List<Wall> walls, List<Move> moves, int depth) {

        if (x == tX && y == tY) {
            System.err.println("Path added");
            path.add(new Path(moves));
        } else if (depth == 0) {
            //end
        } else {
            //Move to the right
            boolean hasNotBeenThere = moves.stream().noneMatch(m -> m.x == x + 1 && m.y == y);
            boolean xSmallerThanEdge = x + 1 < w;
            boolean doesNotHitWall = walls.stream().filter(w -> !w.isHorizontal()).noneMatch(w -> w.blocks(x, y, x + 1, y));
            //System.err.println("Does not hit wall:" + doesNotHitWall +" Has not been there: " + hasNotBeenThere + " x smaller than edge:" + xSmallerThanEdge);
            if (doesNotHitWall
                    && xSmallerThanEdge
                    && hasNotBeenThere) {
                List<Move> newMoves = new ArrayList<>(moves);
                newMoves.add(new Move(1, 0, x, y));
                calculatePaths(x + 1, y, tX, tY, walls, newMoves, depth - 1);
            } if(walls.stream().filter(w -> !w.isHorizontal()).noneMatch(w -> w.blocks(x +1, y, x, y))
                    && x-1 >= 0
                    && moves.stream().noneMatch(m -> m.x == x-1 && m.y == y)) {
                List<Move> newMoves = new ArrayList<>(moves);
                newMoves.add(new Move(-1,0,x,y));
                calculatePaths(x-1,y,tX,tY, walls, newMoves, depth-1);
            } if(walls.stream().filter(Wall::isHorizontal).noneMatch(w -> w.blocks(x, y, x, y+1))
                    && y+1 < h
                    && moves.stream().noneMatch(m -> m.x == x && m.y+1 == y)) {
                List<Move> newMoves = new ArrayList<>(moves);
                newMoves.add(new Move(0,1,x,y));
                calculatePaths(x,y+1,tX,tY, walls, newMoves, depth-1);
            } if(walls.stream().filter(Wall::isHorizontal).noneMatch(w -> w.blocks(x, y+1, x, y))
                    && y-1 >= 0
                    && moves.stream().noneMatch(m -> m.x == x && m.y-1 == y)) {
                List<Move> newMoves = new ArrayList<>(moves);
                newMoves.add(new Move(0,-1,x,y));
                calculatePaths(x,y-1,tX,tY, walls, newMoves, depth-1);
            }
        }
    }

    static class Move {
        int deltaX;
        int deltaY;
        int x;
        int y;

        public Move(int deltaX, int deltaY, int x, int y) {
            this.deltaX = deltaX;
            this.deltaY = deltaY;
            this.x = x;
            this.y = y;
        }

        public String getDefinition() {
            if (deltaX == 1) {
                return "RIGHT";
            } else if (deltaX == -1) {
                return "LEFT";
            } else if (deltaY == 1) {
                return "DOWN";
            } else if (deltaY == -1) {
                return "UP";
            }
            return "INVALID_COMBINATION_MOVE";
        }

        @Override
        public String toString() {
            return "Move{" +
                    "deltaX=" + deltaX +
                    ", deltaY=" + deltaY +
                    ", x=" + x +
                    ", y=" + y +
                    '}';
        }
    }

    static class Path {
        List<Move> moves;

        public Path(List<Move> moves) {
            this.moves = moves;
        }

        public List<Move> getMoves() {
            return moves;
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
            //System.err.println("Blocks? : " + x + y + isHorizontal);
            if (!isHorizontal) {
                return (x1 == x - 1 && x2 == x && y1 >= y && y1 < y + 2);
            } else {
                return (y1 == y - 1 && y2 == y && x1 >= x && x1 < x + 2);
            }
        }
    }
}