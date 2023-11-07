package bg.sofia.uni.fmi.ai.puzzle;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class NQueens {
    private final static char QUEEN_SYMBOL = '*';
    private final static char EMPTY_SQUARE_SYMBOL = '_';
    private final static Random RANDOM_GENERATOR = new Random();
    private static final DecimalFormat DECIMAL_FORMAT_ROUND_TWO = new DecimalFormat("0.00");

    private final int numberOfQueens;
    private final int[] queensBoard;
    private final int[] numberOfQueensInRow;
    private final int[] numberOfQueensInLeftDiagonal;
    private final int[] numberOfQueensInRightDiagonal;
    private final List<Integer> potentialQueensToBeChosen;
    private final int maxMovesBeforeRestart;

    public NQueens(int numberOfQueens) {
        this.numberOfQueens = numberOfQueens;
        this.queensBoard = new int[numberOfQueens];
        this.numberOfQueensInRow = new int[numberOfQueens];
        this.numberOfQueensInLeftDiagonal = new int [2 * numberOfQueens - 1]; // number of left diagonals for any queensBoard
        this.numberOfQueensInRightDiagonal = new int [2 * numberOfQueens - 1]; // number of right diagonals for any queensBoard
        this.potentialQueensToBeChosen = new ArrayList<>();
        this.maxMovesBeforeRestart = 3 * numberOfQueens;
        constructRandomBoard();
    }

    public long algorithm() {
        int movesCounter = 0;

        while (true) {
            findNumberOfQueensInEntries();
            findQueensByColIndexWithMaxConflicts();

            if (potentialQueensToBeChosen.isEmpty()) {
                long endTime = System.currentTimeMillis();

                if (numberOfQueens <= 100) {
                    printSolutionBoard();
                }

                return endTime;
            }

            int randomlyChosenColIndex = potentialQueensToBeChosen
                .get(RANDOM_GENERATOR.nextInt(potentialQueensToBeChosen.size()));
            findQueensByRowIndexWithMinConflicts(randomlyChosenColIndex);

            if (!potentialQueensToBeChosen.isEmpty()) {
                int randomlyChosenRowIndex = potentialQueensToBeChosen
                    .get(RANDOM_GENERATOR.nextInt(potentialQueensToBeChosen.size()));
                queensBoard[randomlyChosenColIndex] = randomlyChosenRowIndex;
            }

            ++movesCounter;
            if (movesCounter > maxMovesBeforeRestart) {
                movesCounter = 0;
                constructRandomBoard();
            }
        }
    }

    private void findNumberOfQueensInEntries() {
        Arrays.fill(numberOfQueensInRow, 0);
        Arrays.fill(numberOfQueensInLeftDiagonal, 0);
        Arrays.fill(numberOfQueensInRightDiagonal, 0);

        for (int colIndex = 0; colIndex < numberOfQueens; colIndex++) {
            int rowIndex = queensBoard[colIndex];
            // left diagonals start from the upper right corner
            int leftDiagonalIndex = numberOfQueens - 1 + rowIndex - colIndex;
            // right diagonals start from the upper left corner
            int rightDiagonalIndex = rowIndex + colIndex;

            ++numberOfQueensInRow[rowIndex];
            ++numberOfQueensInLeftDiagonal[leftDiagonalIndex];
            ++numberOfQueensInRightDiagonal[rightDiagonalIndex];
        }
    }

    private void findQueensByColIndexWithMaxConflicts() {
        int maxConflicts = 1;
        potentialQueensToBeChosen.clear();

        for (int colIndex = 0; colIndex < numberOfQueens; colIndex++) {
            int rowIndex = queensBoard[colIndex];
            int conflictsForCurrentQueen = findConflictsFor(rowIndex, colIndex);

            if (conflictsForCurrentQueen > maxConflicts) {
                maxConflicts = conflictsForCurrentQueen;
                potentialQueensToBeChosen.clear();
                potentialQueensToBeChosen.add(colIndex);
            } else if (conflictsForCurrentQueen == maxConflicts) {
                potentialQueensToBeChosen.add(colIndex);
            }
        }
    }

    private void findQueensByRowIndexWithMinConflicts(int colIndex) {
        int rowIndex = queensBoard[colIndex];
        int minConflicts = findConflictsFor(rowIndex, colIndex);
        potentialQueensToBeChosen.clear();

        for (int currentRowIndex = 0; currentRowIndex < numberOfQueens; currentRowIndex++) {
            // the queen in on that row
            if (currentRowIndex == rowIndex) {
                continue;
            }

            int currentRowConflicts = findConflictsFor(currentRowIndex, colIndex);
            if (currentRowConflicts < minConflicts) {
                minConflicts = currentRowConflicts;
                potentialQueensToBeChosen.clear();
                potentialQueensToBeChosen.add(currentRowIndex);
            } else if (currentRowConflicts == minConflicts) {
                potentialQueensToBeChosen.add(currentRowIndex);
            }
        }
    }

    private int findConflictsFor(int rowIndex, int colIndex) {
        // left diagonals start from the upper right corner
        int leftDiagonalIndex = numberOfQueens - 1 + rowIndex - colIndex;
        // right diagonals start from the upper left corner
        int rightDiagonalIndex = rowIndex + colIndex;

        return numberOfQueensInRow[rowIndex] - 1 +
            numberOfQueensInLeftDiagonal[leftDiagonalIndex] - 1 +
            numberOfQueensInRightDiagonal[rightDiagonalIndex] - 1;
    }

    private void constructRandomBoard() {
        // index - for the col, value of the index - for the row (count upside down)
        for (int colIndex = 0; colIndex < numberOfQueens; colIndex++) {
            queensBoard[colIndex] = numberOfQueens - colIndex - 1;
        }

        // exchange of queens positions
        for (int colIndex = 0; colIndex < numberOfQueens; colIndex++) {
            int newColIndex = RANDOM_GENERATOR.nextInt(numberOfQueens);
            int previousRowIndex = queensBoard[colIndex];
            queensBoard[colIndex] = queensBoard[newColIndex];
            queensBoard[newColIndex] = previousRowIndex;
        }
    }

    private void printSolutionBoard() {
        for (int rowIndex = 0; rowIndex < numberOfQueens; rowIndex++) {
            for (int colIndex = 0; colIndex < numberOfQueens; colIndex++) {
                if (queensBoard[colIndex] == rowIndex) {
                    System.out.print(QUEEN_SYMBOL);
                } else {
                    System.out.print(EMPTY_SQUARE_SYMBOL);
                }
            }
            System.out.println();
        }

//        System.out.println(Arrays.toString(queensBoard));
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int numberOfQueens = scanner.nextInt();
        scanner.close();

        if (numberOfQueens <= 0 || numberOfQueens == 2 || numberOfQueens == 3) {
            System.out.println(-1);
            return;
        }

        NQueens puzzle = new NQueens(numberOfQueens);
        long startTime = System.currentTimeMillis();
        long endTime = puzzle.algorithm();

        double totalRunningTime = (endTime - startTime) / 1000.0;

        if (numberOfQueens > 100) {
            System.out.println(DECIMAL_FORMAT_ROUND_TWO.format(totalRunningTime));
        }
    }
}