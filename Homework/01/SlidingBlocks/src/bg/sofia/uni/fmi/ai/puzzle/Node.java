package bg.sofia.uni.fmi.ai.puzzle;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;

public class Node {
    private final Node parentNode;
    private final Board currentBoard;
    private final Board goalBoard;
    private final String direction;
    private final int g;
    private final int manhattanDistance;

    public Node(Node parentNode, Board currentBoard, Board goalBoard, String direction, int g) {
        this.parentNode = parentNode;
        this.currentBoard = currentBoard;
        this.goalBoard = goalBoard;
        this.direction = direction;
        this.g = g;
        this.manhattanDistance = getTotalManhattanDistance();
    }

    public Board getCurrentBoard() {
        return currentBoard;
    }

    public Node getParentNode() {
        return parentNode;
    }

    public int f() {
        return g + manhattanDistance;
    }

    public int getG() {
        return g;
    }

    public String getDirection() {
        return direction;
    }

    private int getTotalManhattanDistance() {
        int totalManhattanDistance = 0;

        for (int row = 0; row < currentBoard.getSize(); row++) {
            for (int col = 0; col < currentBoard.getSize(); col++) {
                totalManhattanDistance += getManhattanDistance(currentBoard.getTileAt(row, col), row, col);
            }
        }
        return totalManhattanDistance;
    }

    private int getManhattanDistance(int numberOfTile, int row, int col) {
        // 0 (empty tile) does not have a manhattan distance
        if (numberOfTile == 0) {
            return 0;
        }

        // decrement as row and col indexes start from 0
        --numberOfTile;

        // check if the number of the tile is after the given index of the 0 tile
        if (goalBoard.getZeroPositionIndexInSolution() != -1 && numberOfTile >= goalBoard.getZeroPositionIndexInSolution()) {
            // increment as the 0 tile occupies a preceding tile
            ++numberOfTile;
        }

        int goalRow = numberOfTile / currentBoard.getSize();
        int goalCol = numberOfTile % currentBoard.getSize();

        return Math.abs(row - goalRow) + Math.abs(col - goalCol);
    }

    public Deque<Node> solutionPathToRoot() {
        Deque<Node> solutionPathToRootDeque = new ArrayDeque<>();
        Node node = this;

        solutionPathToRootDeque.push(node);
        while (node.parentNode != null) {
            solutionPathToRootDeque.push(node.parentNode);
            node = node.parentNode;
        }

        return solutionPathToRootDeque;
    }

    public List<Node> neighbours() {
        List<Node> neighboursNodesList = new ArrayList<>();
        Map<String, Board> neighbourBoards = currentBoard.neighbours();

        for (Map.Entry<String, Board> currentBoardEntry : neighbourBoards.entrySet()) {
            String directionForNeighbour = currentBoardEntry.getKey();
            Board currentBoard = currentBoardEntry.getValue();

            Node node = new Node(this, currentBoard, goalBoard, directionForNeighbour, g + 1);
            neighboursNodesList.add(node);
        }

        return neighboursNodesList;
    }
}