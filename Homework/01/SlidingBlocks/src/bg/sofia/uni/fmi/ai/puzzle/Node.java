package bg.sofia.uni.fmi.ai.puzzle;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;

public class Node {

    private Node parent;
    private Board currentBoard;
    private Board goalBoard;
    private int g;
    private int manhattanDistance;
    private String direction;

    public Node(Node parent, Board currentBoard, Board goalBoard, int g, String direction) {
        this.parent = parent;
        this.currentBoard = currentBoard;
        this.goalBoard = goalBoard;
        this.g = g;
        this.manhattanDistance =this.getTotalManhattanDistance();
        this.direction = direction;
    }

    public Board getCurrentBoard() {
        return currentBoard;
    }

    public Node getParent() {
        return parent;
    }

    private int getTotalManhattanDistance() {
        int totalManhattanDistance = 0;

        for (int row = 0; row < this.currentBoard.size(); row++) {
            for (int col = 0; col < this.currentBoard.size(); col++) {
                totalManhattanDistance += this.getManhattanDistance(this.currentBoard.tileAt(row, col), row, col);
            }
        }
        return totalManhattanDistance;
    }

    private int getManhattanDistance(int numberOfTile, int row, int col) {
        if (numberOfTile == 0) {
            return 0;
        }
        numberOfTile -= 1;

        if (this.goalBoard.getZero() != -1 && numberOfTile >= this.goalBoard.getZero()) {
            numberOfTile += 1;
        }

        int goalRow = numberOfTile / this.currentBoard.size();
        int goalCol = numberOfTile % this.currentBoard.size();

        return Math.abs(row - goalRow) + Math.abs(col - goalCol);
    }

    public int f() {
        return this.g + this.manhattanDistance;
    }

    public int getG() {
        return g;
    }

    public String getDirection() {
        return direction;
    }

    public List<Node> neighbours() {
        List<Node> neighbours = new ArrayList<>();

        Map<String, Board> neighbourBoards = this.currentBoard.neighbours();

        for (Map.Entry<String, Board> currentEntry: neighbourBoards.entrySet()) {
            String direction = currentEntry.getKey();
            Board currentBoard = currentEntry.getValue();

            Node node = new Node(this, currentBoard, this.goalBoard, this.g + 1, direction);
            neighbours.add(node);
        }

        return neighbours;
    }

    public Deque<Node> getPathToRoot() {
        Deque<Node> path = new ArrayDeque<>();
        Node node = this;
        path.add(node);

        while (node.parent != null) {
            path.add(node.parent);
            node = node.parent;
        }

        return path;
    }
}
