import java.util.*;
import java.util.function.Supplier;

import static java.util.stream.Collectors.toList;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {
    static final int DEPTH = 20;
    static int w;
    static int h;
    static int playerCount;

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        w = in.nextInt(); // width of the board
        h = in.nextInt(); // height of the board
        playerCount = in.nextInt(); // number of players (2 or 3)
        int myId = in.nextInt(); // id of my player (0 = 1st player, 1 = 2nd player, ...)

        Map<Integer, Dragon> dragons = new HashMap<>();


        // game loop
        while (true) {
            List<Wall> walls;
            for (int i = 0; i < playerCount; i++) {
                int x = in.nextInt(); // x-coordinate of the player
                int y = in.nextInt(); // y-coordinate of the player
                int wallsLeft = in.nextInt(); // number of walls available for the player
                Dragon d = dragons.get(i);
                if (d == null) {
                    dragons.put(i, createDragon(i, x, y, wallsLeft));
                } else {
                    d.setX(x);
                    d.setY(y);
                    d.setWallsLeft(wallsLeft);
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
            System.out.println(calculatedMove);

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

        public Round(int boardHeight, int boardWidth, List<Player.Wall> walls, int myId, Map<Integer, Dragon> players) {
            this.boardHeight = boardHeight;
            this.boardWidth = boardWidth;
            this.walls = walls;
            this.players = players;
            this.myId = myId;
        }

        public String calculateMove() {
            Map<Integer, Path> bestPathPerPlayer = new HashMap<>();
            PathCalculation normalPathCalculation = new PathCalculation(walls);
            for (Dragon dragon : players.values().stream().filter(Dragon::isAlive).collect(toList())) {
                List<Path> paths = normalPathCalculation.calculateFor(dragon.id, dragon.x, dragon.y, dragon.targetX, dragon.targetY);
                Optional<Player.Path> bestPath = paths.stream().min(Comparator.comparingInt(c -> c.getMoves().size()));
                bestPathPerPlayer.put(dragon.id, bestPath.orElseThrow((Supplier<RuntimeException>) () -> new IllegalStateException("no valid path found for: " + dragon.id)));
            }

            String expectedResult = "Unkown";
            if (playerCount == 2) {
                Map.Entry<Integer, Path> otherPlayer = bestPathPerPlayer.entrySet().stream().filter(p -> p.getKey() != myId).collect(toList()).get(0);
                Path myPath = bestPathPerPlayer.get(myId);
                if (otherPlayer.getValue().length() > myPath.length() || (otherPlayer.getValue().length() == myPath.length() && myId < otherPlayer.getKey())) {
                    expectedResult = "WINNING!";
                } else {
                    expectedResult = "LOSING";
                }
            }
            return bestPathPerPlayer.get(myId).getMoves().get(0).getDefinition() + " " + expectedResult;
        }


        static class PathCalculation {
            private List<Wall> walls;
            private int[][] heighestRemainingMovesForEachPosition;

            public PathCalculation(List<Wall> walls) {
                this.walls = walls;
            }

            public List<Path> calculateFor(int playerId, int x, int y, int tX, int tY) {
                heighestRemainingMovesForEachPosition = new int[w][h];
                List<Player.Path> paths = new ArrayList<>();

                System.err.println("Calculating path for: " + playerId + x + y + tX + tY);
                calculatePaths(playerId, x, y, tX, tY, new ArrayList<>(), paths, DEPTH);
                return paths;
            }

            private void calculatePaths(int playerId, int x, int y, int tX, int tY, List<Player.Move> moves, List<Player.Path> paths, int depth) {
                if (reachedHorizontalTarget(x, tX, playerId) || reachedVerticalTarget(y, tY, playerId)) {
                    //System.err.println("Path added");
                    paths.add(new Player.Path(moves));
                } else if (depth == 0) {
                    //end reached, don't spend more energy on trying to find the path
                } else {
                    if (isShorterThanPreviousPathAttempt(x, y, depth)) {
                        attemptRight(playerId, x, y, tX, tY, moves, paths, depth);
                        attemptLeft(playerId, x, y, tX, tY, moves, paths, depth);
                        attemptDown(playerId, x, y, tX, tY, moves, paths, depth);
                        attemptUp(playerId, x, y, tX, tY, moves, paths, depth);
                    }

                }
            }

            private void attemptUp(int playerId, int x, int y, int tX, int tY, List<Move> moves, List<Path> paths, int depth) {
                if (canGoUp(x, y, moves)) {
                    List<Move> newMoves = new ArrayList<>(moves);
                    newMoves.add(new Move(moves.size(), 0, -1, x, y));
                    calculatePaths(playerId, x, y - 1, tX, tY, newMoves, paths, depth - 1);
                }
            }

            private void attemptDown(int playerId, int x, int y, int tX, int tY, List<Move> moves, List<Path> paths, int depth) {
                if (canGoDown(x, y, moves)) {
                    List<Move> newMoves = new ArrayList<>(moves);
                    newMoves.add(new Move(moves.size(), 0, 1, x, y));
                    calculatePaths(playerId, x, y + 1, tX, tY, newMoves, paths, depth - 1);
                }
            }

            private void attemptLeft(int playerId, int x, int y, int tX, int tY, List<Move> moves, List<Path> paths, int depth) {
                if (canGoLeft(x, y, moves)) {
                    List<Move> newMoves = new ArrayList<>(moves);
                    newMoves.add(new Move(moves.size(), -1, 0, x, y));
                    calculatePaths(playerId, x - 1, y, tX, tY, newMoves, paths, depth - 1);
                }
            }

            private void attemptRight(int playerId, int x, int y, int tX, int tY, List<Move> moves, List<Path> paths, int depth) {
                if (canGoRight(x, y, moves)) {
                    List<Move> newMoves = new ArrayList<>(moves);
                    newMoves.add(new Move(moves.size(), 1, 0, x, y));
                    calculatePaths(playerId, x + 1, y, tX, tY, newMoves, paths, depth - 1);
                }
            }

            private boolean isShorterThanPreviousPathAttempt(int x, int y, int depth) {
                //System.err.println(heighestRemainingMovesForEachPosition);
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
                        && y + 1 < h
                        && hasNotBeenAtLocation(moves, x, y + 1);
            }

            private boolean canGoLeft(int x, int y, List<Player.Move> moves) {
                return walls.stream().filter(w -> !w.isHorizontal()).noneMatch(w -> w.blocks(x - 1, y, x, y))
                        && x - 1 >= 0
                        && hasNotBeenAtLocation(moves, x - 1, y);
            }

            private boolean canGoRight(int x, int y, List<Player.Move> moves) {
                return walls.stream().filter(w -> !w.isHorizontal()).noneMatch(w -> w.blocks(x, y, x + 1, y))
                        && x + 1 < w
                        && hasNotBeenAtLocation(moves, x + 1, y);
            }

            private boolean hasNotBeenAtLocation(List<Player.Move> moves, int newX, int newY) {
                return moves.stream().noneMatch(m -> m.x == newX && m.y == newY);
            }
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

        public int length() {
            return moves.size();
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

        public boolean isDead() {
            return x == -1 || y == -1;
        }

        public boolean isAlive() {
            return !isDead();
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