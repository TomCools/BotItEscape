import java.util.*;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {
    static final int DEPTH = 40;
    static int w;
    static int h;

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        w = in.nextInt(); // width of the board
        h = in.nextInt(); // height of the board
        int playerCount = in.nextInt(); // number of players (2 or 3)
        int myId = in.nextInt(); // id of my player (0 = 1st player, 1 = 2nd player, ...)

        Map<Integer, Dragon> dragons = new HashMap<>();


        // game loop
        while (true) {
            List<Wall> walls;
            for (int i = 0; i < playerCount; i++) {
                int x = in.nextInt(); // x-coordinate of the player
                int y = in.nextInt(); // y-coordinate of the player
                int wallsLeft = in.nextInt(); // number of walls available for the player
                if (dragons.isEmpty()) {
                    dragons.put(i, createDragon(i, x, y, wallsLeft));
                } else {
                    Dragon dragon = dragons.get(i);
                    dragon.setX(x);
                    dragon.setY(y);
                    dragon.setWallsLeft(wallsLeft);
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
            Round round = new Round(h, w, walls, myId, dragons);
            String calculatedMove = round.calculateMove();
            System.out.println(calculatedMove + " FOR FRODO!");

        }
    }

    static Dragon createDragon(int id, int x, int y, int wallsLeft) {
        int targetX, targetY;
        if (id == 0) {
            targetX = w - 1;
            targetY = y;
        } else if (id == 1) {
            targetX = 0;
            targetY = y;
        } else if (id == 2) {
            targetX = x;
            targetY = h - 1;
        } else {
            throw new IllegalArgumentException("Invalid player id: " + id);
        }
        return new Dragon(id, x, y, wallsLeft, targetX, targetY);
    }

    static class Round {
        int boardHeight;
        int boardWidth;
        List<Player.Wall> walls;
        int myId;
        Map<Integer, Dragon> players;
        List<Player.Path> paths = new ArrayList<>();
        int[][] heighestRemainingMovesForEachPosition;

        public Round(int boardHeight, int boardWidth, List<Player.Wall> walls, int myId, Map<Integer, Dragon> players) {
            this.boardHeight = boardHeight;
            this.boardWidth = boardWidth;
            this.walls = walls;
            this.players = players;
            this.myId = myId;
            this.heighestRemainingMovesForEachPosition = new int[boardWidth][boardHeight];
        }

        public String calculateMove() {
            Dragon me = players.get(myId);
            calculatePaths(me.id, me.x, me.y, me.targetX, me.targetY, new ArrayList<>(), paths, DEPTH);
            Optional<Player.Path> bestPath = paths.stream().min(Comparator.comparingInt(c -> c.getMoves().size()));
            return bestPath.get().getMoves().get(0).getDefinition();
        }

        private void calculatePaths(int playerId, int x, int y, int tX, int tY, List<Player.Move> moves, List<Player.Path> paths, int depth) {
            if (reachedHorizontalTarget(x, tX, playerId) || reachedVerticalTarget(y, tY, playerId)) {
                //System.err.println("Path added");
                paths.add(new Player.Path(moves));
            } else if (depth == 0) {
                //end reached, don't spend more energy on trying to find the path
            } else {
                if (isShorterThanPreviousPathAttempt(x, y, depth)) {
                    attemptRight(x, y, tX, tY, moves, paths, depth);
                    attemptLeft(x, y, tX, tY, moves, paths, depth);
                    attemptDown(x, y, tX, tY, moves, paths, depth);
                    attemptUp(x, y, tX, tY, moves, paths, depth);
                }

            }
        }

        private void attemptUp(int x, int y, int tX, int tY, List<Move> moves, List<Path> paths, int depth) {
            if (canGoUp(x, y, moves)) {
                List<Move> newMoves = new ArrayList<>(moves);
                newMoves.add(new Move(moves.size(), 0, -1, x, y));
                calculatePaths(x, y - 1, tX, tY, newMoves, paths, depth - 1);
            }
        }

        private void attemptDown(int x, int y, int tX, int tY, List<Move> moves, List<Path> paths, int depth) {
            if (canGoDown(x, y, moves)) {
                List<Move> newMoves = new ArrayList<>(moves);
                newMoves.add(new Move(moves.size(), 0, 1, x, y));
                calculatePaths(x, y + 1, tX, tY, newMoves, paths, depth - 1);
            }
        }

        private void attemptLeft(int x, int y, int tX, int tY, List<Move> moves, List<Path> paths, int depth) {
            if (canGoLeft(x, y, moves)) {
                List<Move> newMoves = new ArrayList<>(moves);
                newMoves.add(new Move(moves.size(), -1, 0, x, y));
                calculatePaths(x - 1, y, tX, tY, newMoves, paths, depth - 1);
            }
        }

        private void attemptRight(int x, int y, int tX, int tY, List<Move> moves, List<Path> paths, int depth) {
            if (canGoRight(x, y, moves)) {
                List<Move> newMoves = new ArrayList<>(moves);
                newMoves.add(new Move(moves.size(), 1, 0, x, y));
                calculatePaths(x + 1, y, tX, tY, newMoves, paths, depth - 1);
            }
        }

        private boolean isShorterThanPreviousPathAttempt(int x, int y, int depth) {
            if (heighestRemainingMovesForEachPosition[x][y] < depth) {
                heighestRemainingMovesForEachPosition[x][y] = depth;
                return true;
            }
            return false;
        }

        private boolean reachedVerticalTarget(int y, int tY, int playerId) {
            return y == tY && playerId == 2;
        }

        private boolean reachedHorizontalTarget(int x, int tX, int playerId) {
            return x == tX && (playerId == 0 || playerId == 1);
        }

        private boolean canGoUp(int x, int y, List<Player.Move> moves) {
            return walls.stream().filter(Player.Wall::isHorizontal).noneMatch(w -> w.blocks(x, y - 1, x, y))
                    && y - 1 >= 0
                    && hasNotBeenAtLocation(moves, x, y - 1);
        }

        private boolean canGoDown(int x, int y, List<Player.Move> moves) {
            return walls.stream().filter(Player.Wall::isHorizontal).noneMatch(w -> w.blocks(x, y, x, y + 1))
                    && y + 1 < this.boardHeight
                    && hasNotBeenAtLocation(moves, x, y + 1);
        }

        private boolean canGoLeft(int x, int y, List<Player.Move> moves) {
            return walls.stream().filter(w -> !w.isHorizontal()).noneMatch(w -> w.blocks(x - 1, y, x, y))
                    && x - 1 >= 0
                    && hasNotBeenAtLocation(moves, x - 1, y);
        }

        private boolean canGoRight(int x, int y, List<Player.Move> moves) {
            return walls.stream().filter(w -> !w.isHorizontal()).noneMatch(w -> w.blocks(x, y, x + 1, y))
                    && x + 1 < this.boardWidth
                    && hasNotBeenAtLocation(moves, x + 1, y);
        }

        private boolean hasNotBeenAtLocation(List<Player.Move> moves, int newX, int newY) {
            return moves.stream().noneMatch(m -> m.x == newX && m.y == newY);
        }
    }

    static class Move {
        int nr;
        int deltaX;
        int deltaY;
        int x;
        int y;

        public Move(int nr, int deltaX, int deltaY, int x, int y) {
            this.nr = nr;
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
        final int id;
        int x;
        int y;
        int wallsLeft;
        final int targetX;
        final int targetY;

        public Dragon(int id, int x, int y, int wallsLeft, int targetX, int targetY) {
            this.id = id;
            this.x = x;
            this.y = y;
            this.wallsLeft = wallsLeft;
            this.targetX = targetX;
            this.targetY = targetY;
        }

        public void setX(int x) {
            this.x = x;
        }

        public void setY(int y) {
            this.y = y;
        }

        public void setWallsLeft(int wallsLeft) {
            this.wallsLeft = wallsLeft;
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
            if (!isHorizontal) {
                return (x1 == x - 1 && x2 == x && y1 >= y && y1 < y + LENGTH);
            } else {
                return (y1 == y - 1 && y2 == y && x1 >= x && x1 < x + LENGTH);
            }
        }
    }
}