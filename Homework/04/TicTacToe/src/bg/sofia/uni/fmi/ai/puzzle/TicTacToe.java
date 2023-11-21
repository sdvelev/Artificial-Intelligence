package bg.sofia.uni.fmi.ai.puzzle;

import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class TicTacToe {
    private final static char COMPUTER_FIRST_SIGN = 'c';
    private final static char PLAYER_FIRST_SIGN = 'i';
    private final static char COMPUTER_SIGN_ON_GAME_BOARD = 'o';
    private final static char PLAYER_SIGN_ON_GAME_BOARD = 'x';
    private final static char EMPTY_CELL_SIGN = '-';
    private final static int ROWS_NUMBER = 3;
    private final static int COLS_NUMBER = 3;
    private final static int MAX_PLAYER_ROUND = 1;
    private final static int MIN_PLAYER_ROUND = -1;
    private final static int EMPTY_CELL = 0;
    private final static Scanner SCANNER = new Scanner(System.in);

    private final int[][] gameBoard;
    private int occupiedPositions;
    private final int  playerRoleInAlgorithm;
    private final int computerRoleInAlgorithm;

    public TicTacToe(char firstPlayer) {
        this.gameBoard = new int[ROWS_NUMBER][COLS_NUMBER];
        this.occupiedPositions = 0;
        if (firstPlayer == COMPUTER_FIRST_SIGN) {
            this.computerRoleInAlgorithm = MAX_PLAYER_ROUND;
            this.playerRoleInAlgorithm = MIN_PLAYER_ROUND;
        } else if (firstPlayer == PLAYER_FIRST_SIGN) {
            this.playerRoleInAlgorithm = MAX_PLAYER_ROUND;
            this.computerRoleInAlgorithm = MIN_PLAYER_ROUND;
        } else {
            throw new RuntimeException("The provided input is not as specified");
        }
    }

    public void algorithm() {
        if (this.computerRoleInAlgorithm == MAX_PLAYER_ROUND) {
            computerRound();
        }

        do {
            playerRound();
            computerRound();
        } while (!isGameFinished());

        int winner = findWinner();
        if (computerRoleInAlgorithm == MAX_PLAYER_ROUND) {
            switch (winner) {
                case MAX_PLAYER_ROUND -> System.out.println("\nThe computer won this game of tic-tac-toe!");
                case MIN_PLAYER_ROUND -> System.out.println("\nYou won this game of tic-tac-toe!");
                default -> System.out.println("\nThere is a draw!");
            }
        } else {
            switch (winner) {
                case MAX_PLAYER_ROUND -> System.out.println("\nYou won this game of tic-tac-toe!");
                case MIN_PLAYER_ROUND -> System.out.println("\nThe computer won this game of tic-tac-toe!");
                default -> System.out.println("\nIt is a draw in this game!");
            }
        }
    }

    private void computerRound() {
        if (isGameFinished()) {
            return;
        }

        int currentDepth = 0;
        int currentAlpha = Integer.MIN_VALUE;
        int currentBeta = Integer.MAX_VALUE;

        List<Integer> resultConfiguration = alphaBetaDecision(currentDepth, computerRoleInAlgorithm,
            currentAlpha, currentBeta);
        int resultRow =  resultConfiguration.get(1);
        int resultCol = resultConfiguration.get(2);

        gameBoard[resultRow][resultCol] = computerRoleInAlgorithm;
        occupiedPositions++;
        System.out.println("The game board after the computer move:");
        printBoard();
    }

    private void playerRound() {
        if (isGameFinished()) {
            return;
        }

        System.out.println("Type your move:");
        int myRow = SCANNER.nextInt();
        int myCol = SCANNER.nextInt();
        --myRow;
        --myCol;

        while (myRow < 0 || myRow >= ROWS_NUMBER || myCol < 0 || myCol >= COLS_NUMBER ||
            gameBoard[myRow][myCol] != EMPTY_CELL) {
            System.out.println("The typed move is not a correct one. Try again:");
            myRow = SCANNER.nextInt();
            myCol = SCANNER.nextInt();
            --myRow;
            --myCol;
        }

        gameBoard[myRow][myCol] = playerRoleInAlgorithm;
        occupiedPositions++;
        System.out.println("The game board after your move:");
        printBoard();
    }

    private void printBoard() {
        for (int i = 0; i < ROWS_NUMBER; i++) {
            for (int j = 0; j < COLS_NUMBER; j++) {
                if (gameBoard[i][j] == computerRoleInAlgorithm) {
                    System.out.print(COMPUTER_SIGN_ON_GAME_BOARD);
                } else if (gameBoard[i][j] == playerRoleInAlgorithm) {
                    System.out.print(PLAYER_SIGN_ON_GAME_BOARD);
                } else {
                    System.out.print(EMPTY_CELL_SIGN);
                }
            }
            System.out.println();
        }
    }

    private List<Integer> alphaBetaDecision(int currentDepth, int currentPlayer, int alpha, int beta) {
        if (isGameFinished() || currentDepth + occupiedPositions == ROWS_NUMBER * COLS_NUMBER) {
            int evaluateValue;
            if (isThereRowWinner() == MAX_PLAYER_ROUND || isThereColWinner() == MAX_PLAYER_ROUND ||
            isThereDiagonalWinner() == MAX_PLAYER_ROUND) {
                evaluateValue = MAX_PLAYER_ROUND;
            } else if (isThereRowWinner() == MIN_PLAYER_ROUND || isThereColWinner() == MIN_PLAYER_ROUND ||
                isThereDiagonalWinner() == MIN_PLAYER_ROUND) {
                evaluateValue = MIN_PLAYER_ROUND;
            } else {
                evaluateValue = EMPTY_CELL;
            }

            return List.of(evaluateValue, 0, 0, currentDepth);
        }

        List<Integer> soFarBestConfiguration;
        if (currentPlayer == MAX_PLAYER_ROUND) {
            soFarBestConfiguration = List.of(Integer.MIN_VALUE, 0, 0, currentDepth);
        } else {
            soFarBestConfiguration = List.of(Integer.MAX_VALUE, 0, 0 ,currentDepth);
        }

        outerLoop:
        for (int i = 0; i < ROWS_NUMBER; i++) {
            for (int j = 0; j < COLS_NUMBER; j++) {
                if (this.gameBoard[i][j] == EMPTY_CELL) {
                    this.gameBoard[i][j] = currentPlayer;
                    List<Integer> currentResultConfiguration = alphaBetaDecision(currentDepth + 1,
                        -currentPlayer, alpha, beta);
                    this.gameBoard[i][j] = EMPTY_CELL;

                    int resultMove = currentResultConfiguration.get(0);
                    int resultDepth = currentResultConfiguration.get(3);

                    int soFarBestMove = soFarBestConfiguration.get(0);
                    int soFarBestDepth = soFarBestConfiguration.get(3);

                    if (currentPlayer == MAX_PLAYER_ROUND) {
                        if (resultMove > soFarBestMove ||
                            (resultMove == soFarBestMove && resultDepth < soFarBestDepth)) {
                            soFarBestConfiguration = List.of(resultMove, i, j, resultDepth);
                        }

                        alpha = Math.max(alpha, soFarBestConfiguration.get(0));
                    } else {
                        if (resultMove < soFarBestMove ||
                            (resultMove == soFarBestMove && resultDepth < soFarBestDepth)) {
                            soFarBestConfiguration = List.of(resultMove, i, j, resultDepth);
                        }

                        beta = Math.min(beta, soFarBestConfiguration.get(0));
                    }

                    if (alpha >= beta) {
                        break outerLoop;
                    }
                }
            }
        }

        return soFarBestConfiguration;
    }

    private boolean isGameFinished() {
        return isThereRowWinner() != 0 || isThereColWinner() != 0 || isThereDiagonalWinner() != 0
            || occupiedPositions == ROWS_NUMBER * COLS_NUMBER;
    }

    private int findWinner() {
        int potentialRowWinner = isThereRowWinner();
        if (potentialRowWinner != 0) {
            return potentialRowWinner;
        }

        int potentialColWinner = isThereColWinner();
        if (potentialColWinner != 0) {
            return potentialColWinner;
        }

        return isThereDiagonalWinner();
    }

    private int isThereRowWinner() {
        Set<Integer> storedValue = new HashSet<>();
        for (int i = 0; i < ROWS_NUMBER; i++) {
            if (this.gameBoard[i][0] == this.gameBoard[i][1] && this.gameBoard[i][1] == this.gameBoard[i][2]) {
                storedValue.add(gameBoard[i][0]);
            }
        }

        storedValue.remove(0);
        if (storedValue.isEmpty()) {
            return 0;
        } else {
            return storedValue.stream().findFirst().get();
        }
    }

    private int isThereColWinner() {
        Set<Integer> storedValue = new HashSet<>();
        for (int i = 0; i < ROWS_NUMBER; i++) {
            if (this.gameBoard[0][i] == this.gameBoard[1][i] && this.gameBoard[1][i] == this.gameBoard[2][i]) {
                storedValue.add(gameBoard[0][i]);
            }
        }

        storedValue.remove(0);
        if (storedValue.isEmpty()) {
            return 0;
        } else {
            return storedValue.stream().findFirst().get();
        }
    }

    private int isThereDiagonalWinner() {
        Set<Integer> storedValue = new HashSet<>();
        if (this.gameBoard[0][0] == this.gameBoard[1][1] && this.gameBoard[1][1] == this.gameBoard[2][2]) {
            storedValue.add(gameBoard[0][0]);
        }

        if (this.gameBoard[0][2] == this.gameBoard[1][1] && this.gameBoard[1][1] == this.gameBoard[2][0]) {
            storedValue.add(gameBoard[0][2]);
        }

        storedValue.remove(0);
        if (storedValue.isEmpty()) {
            return 0;
        } else {
            return storedValue.stream().findFirst().get();
        }
    }

    public static void main(String[] args) {
        System.out.println("You are playing Tic-Tac-Toe against algorithm.");
        System.out.println("Who will start first - you or the computer? Type \"i\" for you and \"c\" for the computer");
        char firstPlayer = SCANNER.next().charAt(0);

        TicTacToe ticTacToe = new TicTacToe(firstPlayer);
        ticTacToe.algorithm();
        SCANNER.close();
    }
}