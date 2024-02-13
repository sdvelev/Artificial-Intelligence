package bg.sofia.uni.fmi.ai.puzzle;

import java.util.HashMap;
import java.util.Map;

public class Board {
    private final static String LEFT_DIRECTION = "left";
    private final static String RIGHT_DIRECTION = "right";
    private final static String UP_DIRECTION = "up";
    private final static String DOWN_DIRECTION = "down";

    private final int[][] tiles;
    private final int size;
    private final int zeroPositionIndexInSolution;

    public Board(int [][] tiles,int size, int zeroPositionIndexInSolution) {
        this.size = size;
        this.tiles = tiles;
        this.zeroPositionIndexInSolution = zeroPositionIndexInSolution;
    }

    public int getZeroPositionIndexInSolution() {
        return zeroPositionIndexInSolution;
    }

    public int getSize() {
        return size;
    }

    public Pair getZeroPosition() {
        Pair zeroPosition = new Pair();

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (getTileAt(row, col) == 0) {
                    zeroPosition.setRow(row);
                    zeroPosition.setCol(col);
                    return zeroPosition;
                }
            }
        }

        return zeroPosition;
    }

    public int getTileAt(int row, int col) {
        if (row < 0 || row >= size || col < 0 || col >= size) {
            return -1;
        }

        return tiles[row][col];
    }

    public boolean isEqual(Board board) {
        for (int row = 0; row < getSize(); row++) {
            for (int col = 0; col < getSize(); col++) {
                if (getTileAt(row, col) != board.getTileAt(row, col)) {
                    return false;
                }
            }
        }

        return true;
    }

    public Map<String, Board> neighbours() {
        Map<String, Board> neighboursCollection = new HashMap<>();

        Pair zeroPosition = getZeroPosition();
        int currentZeroRowPosition = zeroPosition.getRow();
        int currentZeroColPosition = zeroPosition.getCol();

        Map<String, Pair> directions = new HashMap<>();
        directions.put(LEFT_DIRECTION, new Pair(0, 1));
        directions.put(RIGHT_DIRECTION, new Pair(0, -1));
        directions.put(UP_DIRECTION, new Pair(1, 0));
        directions.put(DOWN_DIRECTION, new Pair(-1, 0));

        for (Map.Entry<String, Pair> currentDirection : directions.entrySet()) {
            String directionForNeighbour = currentDirection.getKey();
            Pair directionCoordinatesForZero = currentDirection.getValue();

            addNeighbour(neighboursCollection, currentZeroRowPosition, currentZeroColPosition,
                directionForNeighbour, directionCoordinatesForZero);
        }

        return neighboursCollection;
    }

    private void addNeighbour(Map<String, Board> neighboursCollection, int currentZeroRowPosition,
                              int currentZeroColPosition, String directionForNeighbour,
                              Pair directionCoordinatesForZero) {
        int newZeroRowPosition = currentZeroRowPosition + directionCoordinatesForZero.getRow();
        int newZeroColPosition = currentZeroColPosition + directionCoordinatesForZero.getCol();

        if (isNewZeroPositionValid(newZeroRowPosition, newZeroColPosition)) {
            Board neighbourBoard = constructNeighbourBoard(currentZeroRowPosition, currentZeroColPosition,
                newZeroRowPosition, newZeroColPosition);
            neighboursCollection.put(directionForNeighbour, neighbourBoard);
        }
    }

    private boolean isNewZeroPositionValid(int row, int col) {
        return row >= 0 && row < size && col >= 0 && col < size;
    }

    private Board constructNeighbourBoard(int currentZeroRowPosition, int currentZeroColPosition,
                                          int newZeroRowPosition, int newZeroColPosition) {
        int tileToMove = getTileAt(newZeroRowPosition, newZeroColPosition);
        int[][] copyOfTiles = new int[size][size];

        for (int i = 0; i < size; i++) {
            System.arraycopy(tiles[i], 0, copyOfTiles[i], 0, size);
        }

        Board neighbourBoard = new Board(copyOfTiles, size, zeroPositionIndexInSolution);
        neighbourBoard.setTileAt(newZeroRowPosition, newZeroColPosition, 0);
        neighbourBoard.setTileAt(currentZeroRowPosition, currentZeroColPosition, tileToMove);

        return neighbourBoard;
    }

    private void setTileAt(int row, int col, int tile) {
        tiles[row][col] = tile;
    }
}