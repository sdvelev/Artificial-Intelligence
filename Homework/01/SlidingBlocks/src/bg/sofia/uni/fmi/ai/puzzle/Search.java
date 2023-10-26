package bg.sofia.uni.fmi.ai.puzzle;

import java.text.DecimalFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;

public class Search {
    private static final DecimalFormat DECIMAL_FORMAT_ROUND_TWO = new DecimalFormat("0.00");

    private final Node startNode;
    private final Node goalNode;

    public Search(Node startNode, Node goalNode) {
        this.startNode = startNode;
        this.goalNode = goalNode;
    }

    public Node IDAStarAlgorithm() {
        int threshold = startNode.f();
        int potentialThreshold;

        while (true) {
            Deque<Node> alreadyVisitedDeque = new ArrayDeque<>();
            Queue<Node> candidatesQueue = new PriorityQueue<>(Comparator.comparingInt(Node::f));

            alreadyVisitedDeque.push(startNode);
            potentialThreshold = searchRecursive(alreadyVisitedDeque, candidatesQueue, threshold);

            // reached goal state
            if (potentialThreshold == 0) {
                return candidatesQueue.poll();
            }

            if (isGoalNode(alreadyVisitedDeque.peek())) {
                return alreadyVisitedDeque.peek();
            }

            threshold = potentialThreshold;
        }
    }

    private int searchRecursive(Deque<Node> alreadyVisitedDeque, Queue<Node> candidatesQueue, int threshold) {
        Node currentNode = alreadyVisitedDeque.peek();

        if (currentNode != null && currentNode.f() > threshold) {
            candidatesQueue.add(currentNode);
            return currentNode.f();
        }

        if (isGoalNode(currentNode)) {
            candidatesQueue.clear();
            candidatesQueue.add(currentNode);
            return 0;
        }

        List<Node> neighboursNodesList = new ArrayList<>();
        if (currentNode != null) {
            neighboursNodesList = currentNode.neighbours();
        }

        for (Node neighbourNode : neighboursNodesList) {
            if (!alreadyVisitedDeque.contains(neighbourNode)) {
                if (currentNode.getParentNode() != null &&
                    currentNode.getParentNode().getCurrentBoard().isEqual(neighbourNode.getCurrentBoard())) {
                    continue;
                }

                alreadyVisitedDeque.push(neighbourNode);

                int recursiveFunctionResult = searchRecursive(alreadyVisitedDeque, candidatesQueue, threshold);

                // reached goal state
                if (recursiveFunctionResult == 0) {
                    return 0;
                }

                alreadyVisitedDeque.pop();
            }
        }

        if (!candidatesQueue.isEmpty()) {
            return candidatesQueue.peek().f();
        }

        return 0;
    }

    private boolean isGoalNode(Node node) {
        for (int row = 0; row < startNode.getCurrentBoard().getSize(); row++) {
            for (int col = 0; col < startNode.getCurrentBoard().getSize(); col++) {
                if (node.getCurrentBoard().getTileAt(row, col) != goalNode.getCurrentBoard().getTileAt(row, col)) {
                    return false;
                }
            }
        }

        return true;
    }

    public boolean isPuzzleSolvable() {
        int size = startNode.getCurrentBoard().getSize();
        int numberOfInversions = inversions();

        if (size % 2 != 0) {
            return numberOfInversions % 2 == 0;
        }

        return (numberOfInversions + startNode.getCurrentBoard().getZeroPosition().getRow()) % 2 != 0;
    }

    private int inversions() {
        int inversionsCounter = 0;

        List<Integer> tilesInRowOrder = new ArrayList<>();
        for (int row = 0; row < startNode.getCurrentBoard().getSize(); row++) {
            for (int col = 0; col < startNode.getCurrentBoard().getSize(); col++) {
                int potentialTileToAdd = startNode.getCurrentBoard().getTileAt(row, col);
                if (potentialTileToAdd != 0) {
                    tilesInRowOrder.add(potentialTileToAdd);
                }
            }
        }

        for (int i = 0; i < tilesInRowOrder.size(); i++) {
            for (int j = i + 1; j < tilesInRowOrder.size(); j++) {
                if (tilesInRowOrder.get(j) < tilesInRowOrder.get(i)) {
                    ++inversionsCounter;
                }
            }
        }

        return inversionsCounter;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int zeroPositionIndexInSolution = scanner.nextInt();

        int sizeOfRow = (int) Math.sqrt(n + 1);

        int[][] startBoardArray = new int[sizeOfRow][];
        for (int i = 0; i < startBoardArray.length; i++) {
            startBoardArray[i] = new int[sizeOfRow];
        }

        for (int i = 0; i < startBoardArray.length; i++) {
            for (int j = 0; j < startBoardArray[i].length; j++) {
                startBoardArray[i][j] = scanner.nextInt();
            }
        }

        int[][] goalBoardArray = new int[sizeOfRow][];
        for (int i = 0; i < goalBoardArray.length; i++) {
            goalBoardArray[i] = new int[sizeOfRow];
        }

        int index = 1;
        if (zeroPositionIndexInSolution == -1) {
            for (int i = 0; i < goalBoardArray.length; i++) {
                for (int j = 0; j < goalBoardArray[i].length; j++) {
                    goalBoardArray[i][j] = index++;
                }
            }
            goalBoardArray[sizeOfRow - 1][sizeOfRow - 1] = 0;
        } else {
            for (int i = 0; i < goalBoardArray.length; i++) {
                for (int j = 0; j < goalBoardArray[i].length; j++) {
                    if (i * sizeOfRow + j == zeroPositionIndexInSolution) {
                        goalBoardArray[i][j] = 0;
                    } else {
                        goalBoardArray[i][j] = index++;
                    }
                }
            }
        }

        Board startBoard = new Board(startBoardArray, sizeOfRow, zeroPositionIndexInSolution);
        Board goalBoard = new Board(goalBoardArray, sizeOfRow, zeroPositionIndexInSolution);

        Node startNode = new Node(null, startBoard, goalBoard, null, 0);
        Node goalNode = new Node(null, goalBoard, goalBoard, null, 0);

        Search solution = new Search(startNode, goalNode);

        if (!solution.isPuzzleSolvable()) {
            System.out.println(-1);
            return;
        }

        Node foundNode;
        if (solution.isGoalNode(startNode)) {
            foundNode = startNode;
        } else {
            long startTime = System.currentTimeMillis();
            foundNode = solution.IDAStarAlgorithm();
            long endTime = System.currentTimeMillis();

            double totalRunningTime = (endTime - startTime) / 1000.0;
            System.out.println("Total time for finding the path (in seconds): " +
                DECIMAL_FORMAT_ROUND_TWO.format(totalRunningTime));
        }

        System.out.println(foundNode.getG());

        Deque<Node> solutionPathToRootDeque = foundNode.solutionPathToRoot();
        while (!solutionPathToRootDeque.isEmpty()) {
            Node currentNode = solutionPathToRootDeque.pop();
            if (currentNode.getParentNode() != null) {
                System.out.println(currentNode.getDirection());
            }
        }
    }
}