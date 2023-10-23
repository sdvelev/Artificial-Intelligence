package bg.sofia.uni.fmi.ai.puzzle;

import java.util.HashMap;
import java.util.Map;

public class Board {

    private final static String LEFT_DIRECTION = "L";
    private final static String RIGHT_DIRECTION = "R";
    private final static String UP_DIRECTION = "U";
    private final static String DOWN_DIRECTION = "D";

    private int size;
    private int[][] tiles;
    private int zeroPosition;

    public Board(int[][] tiles, int size) {
        this.tiles = tiles;
        this.size = size;
    }

    public Board(int size, int zeroPosition, int [][] tiles) {
        this.size = size;
        this.tiles = tiles;
        this.zeroPosition = zeroPosition;
    }

    public int tileAt(int row, int col) {
        return this.tiles[row][col];
    }

    private void setTileAt(int row, int col, int tile) {
        this.tiles[row][col] = tile;
    }

    public int size() {
        return this.size;
    }

    public int getZero() {
        return this.zeroPosition;
    }

    public int[][] getTiles() {
        return tiles;
    }

    public Pair getZeroPosition() {
        Pair zeroPosition = new Pair();

        for (int row = 0; row < this.size; row++) {
            for (int col = 0; col < this.size; col++) {
                if (this.tileAt(row, col) == 0) {
                    zeroPosition.setRow(row);
                    zeroPosition.setCol(col);
                    return zeroPosition;
                }
            }
        }

        return zeroPosition;
    }

    public HashMap<String, Board> neighbours() {

        HashMap<String, Board> neighbours = new HashMap<>();

        Pair zeroPosition = this.getZeroPosition();
        int currentZeroRowPosition = zeroPosition.getRow();
        int currentZeroColPosition = zeroPosition.getCol();

        Map<String, Pair> directions = new HashMap<>();
        directions.put(LEFT_DIRECTION, new Pair(0, 1));
        directions.put(RIGHT_DIRECTION, new Pair(0, -1));
        directions.put(UP_DIRECTION, new Pair(1, 0));
        directions.put(DOWN_DIRECTION, new Pair(-1, 0));

        for (Map.Entry<String, Pair> currentDirection: directions.entrySet()) {
            String direction = currentDirection.getKey();
            Pair coordinates = currentDirection.getValue();
            this.addNeighbour(neighbours, currentZeroRowPosition, currentZeroColPosition, direction, coordinates);
        }

        return neighbours;
    }

    private void addNeighbour(HashMap<String, Board> neighbours, int currentZeroRowPosition, int currentZeroColPosition,
                              String direction, Pair directionCoordinates) {
        int newZeroRowPosition = currentZeroRowPosition + directionCoordinates.getRow();
        int newZeroColPosition = currentZeroColPosition + directionCoordinates.getCol();

        if (this.isZeroPositionValid(newZeroRowPosition, newZeroColPosition)) {
            Board neighbourBoard = this.constructNeighbourBoard(currentZeroRowPosition, currentZeroColPosition,
                newZeroRowPosition, newZeroColPosition);
            neighbours.put(direction, neighbourBoard);
        }
    }

    private Board constructNeighbourBoard(int currentZeroRowPosition, int currentZeroColPosition, int newZeroRowPosition,
                                          int newZeroColPosition) {
        int tileToMove = this.tileAt(newZeroRowPosition, newZeroColPosition);
        int[][] copyOfTiles = new int[this.size][this.size];

        for (int i = 0; i < this.size; i++) {
            System.arraycopy(this.tiles[i], 0, copyOfTiles[i], 0, this.size);
        }

        Board neighbourBoard = new Board(copyOfTiles, this.size);
        neighbourBoard.setTileAt(newZeroRowPosition, newZeroColPosition, 0);
        neighbourBoard.setTileAt(currentZeroRowPosition, currentZeroColPosition, tileToMove);

        return neighbourBoard;
    }

    private boolean isZeroPositionValid(int row, int col) {
        return row >= 0 && row < this.size() && col >= 0 && col < this.size();
    }

    public boolean isEqual(Board board) {
        for (int row = 0; row < this.size(); row++) {
            for (int col = 0; col < this.size(); col++) {
                if (this.tileAt(row, col) != board.tileAt(row, col)) return false;
            }
        }

        return true;
    }
}
