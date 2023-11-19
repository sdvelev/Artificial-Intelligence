package bg.sofia.uni.fmi.ai.puzzle;

import java.util.List;
import java.util.Scanner;

public class TicTacToe3 {
    private final static int MAX_PLAYER_ROUND = 1;
    private final static int MIN_PLAYER_ROUND = -1;
    private final static int EMPTY_CELL = 0;

    private final static char COMPUTER_FIRST_SIGN = 'c';
    private final static char PLAYER_FIRST_SIGN = 'i';

    private final static int ROWS_NUMBER = 3;
    private final static int COLS_NUMBER = 3;

    private final static Scanner SCANNER = new Scanner(System.in);

    private final int[][] gameBoard;
    private int occupiedPositions;
    private int playerRoleInAlgorithm;
    private int computerRoleInAlgorithm;

    public TicTacToe3(char firstPlayer) {
        this.gameBoard = new int[ROWS_NUMBER][COLS_NUMBER];
        this.occupiedPositions = 0;
        if (firstPlayer == COMPUTER_FIRST_SIGN) {
            this.computerRoleInAlgorithm = MAX_PLAYER_ROUND;
            this.playerRoleInAlgorithm = MIN_PLAYER_ROUND;
        } else if (firstPlayer == PLAYER_FIRST_SIGN) {
            this.playerRoleInAlgorithm = MAX_PLAYER_ROUND;
            this.computerRoleInAlgorithm = MIN_PLAYER_ROUND;
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
    }

    private void computerRound() {
        if (isGameFinished()) {
            return;
        }

        int currentDepth = 0;
        int currentAlpha = Integer.MIN_VALUE;
        int currentBeta = Integer.MAX_VALUE;

        List<Integer> resultConfiguration = alphaBetaDecision(currentDepth, this.computerRoleInAlgorithm, currentAlpha, currentBeta);
        int resultRow =  resultConfiguration.get(1);
        int resultCol = resultConfiguration.get(2);

        this.gameBoard[resultRow][resultCol] = computerRoleInAlgorithm;
        this.occupiedPositions++;
        System.out.println("The board after the computer move:");
        this.printBoard();
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

        if (myRow < 0 || myRow > ROWS_NUMBER || myCol < 0 || myCol > COLS_NUMBER ||
        this.gameBoard[myRow][myCol] != EMPTY_CELL) {
            while (gameBoard[myRow][myCol] != EMPTY_CELL) {
                System.out.println("The typed move is not a correct one. Try again:");
                myRow = SCANNER.nextInt();
                myCol = SCANNER.nextInt();
                --myRow;
                --myCol;
            }
        }

        this.gameBoard[myRow][myCol] = playerRoleInAlgorithm;
        this.occupiedPositions++;
        System.out.println("The board after your move:");
        this.printBoard();
    }

    private void printBoard() {
        for (int i = 0; i < ROWS_NUMBER; i++) {
            for (int j = 0; j < COLS_NUMBER; j++) {
                char player = gameBoard[i][j] == computerRoleInAlgorithm  ? 'o' : 'x';
                System.out.print(gameBoard[i][j] == EMPTY_CELL ? '-' : player);
            }
            System.out.println();
        }
    }

    private int calculateMaxDepth() {
        return ROWS_NUMBER * COLS_NUMBER - occupiedPositions;
    }

    private List<Integer> alphaBetaDecision(int currentDepth, int currentPlayer, int alpha, int beta) {
        if (isGameFinished() || currentDepth == calculateMaxDepth()) {
            int evaluateValue;
            if (isThereRowWinner() == MAX_PLAYER_ROUND || isThereColWinner() == MAX_PLAYER_ROUND ||
            isThereDiagonalWinner() == MAX_PLAYER_ROUND) {
                evaluateValue = MAX_PLAYER_ROUND;
            } else if (isThereRowWinner() == MIN_PLAYER_ROUND || isThereColWinner() == MIN_PLAYER_ROUND ||
                isThereDiagonalWinner() == MIN_PLAYER_ROUND) {
                evaluateValue = MIN_PLAYER_ROUND;
            } else {
                evaluateValue = 0;
            }
            return List.of(evaluateValue, -1, -1, currentDepth);
        }

        List<Integer> soFarBestConfiguration;
        if (currentPlayer == MAX_PLAYER_ROUND) {
            soFarBestConfiguration = List.of(Integer.MIN_VALUE, -1, -1, currentDepth);
        } else {
            soFarBestConfiguration = List.of(Integer.MAX_VALUE, -1, -1 ,currentDepth);
        }

        for (int i = 0; i < ROWS_NUMBER; i++) {
            for (int j = 0; j < COLS_NUMBER; j++) {
                if (this.gameBoard[i][j] == EMPTY_CELL) {
                    this.gameBoard[i][j] = currentPlayer;
                    List<Integer> currentResultConfiguration = alphaBetaDecision(currentDepth + 1,
                        -currentPlayer, alpha, beta);
                    this.gameBoard[i][j] = EMPTY_CELL;

                    int resultMove = currentResultConfiguration.get(0);
                    int resultDepth = currentResultConfiguration.get(3);

                    int bestSoFarMove = soFarBestConfiguration.get(0);
                    int bestSoFarDepth = soFarBestConfiguration.get(3);

                    if (currentPlayer == MAX_PLAYER_ROUND) {
                        if (resultMove > bestSoFarMove ||
                            (resultMove == bestSoFarMove && resultDepth < bestSoFarDepth)) {
                            soFarBestConfiguration = List.of(resultMove, i, j, resultDepth);
                        }

                        alpha = Math.max(alpha, soFarBestConfiguration.get(0));
                    } else {
                        if (resultMove < bestSoFarMove ||
                            (resultMove == bestSoFarMove && resultDepth < bestSoFarDepth)) {
                            soFarBestConfiguration = List.of(resultMove, i, j, resultDepth);
                        }

                        beta = Math.min(beta, soFarBestConfiguration.get(0));
                    }

                    if (alpha >= beta) {
                        break;
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

    private int isThereRowWinner() {
        for (int i = 0; i < ROWS_NUMBER; i++) {
            if (this.gameBoard[i][0] == this.gameBoard[i][1] && this.gameBoard[i][1] == this.gameBoard[i][2]) {
                return this.gameBoard[i][0];
            }
        }

        return 0;
    }

    private int isThereColWinner() {
        for (int i = 0; i < ROWS_NUMBER; i++) {
            if (this.gameBoard[0][i] == this.gameBoard[1][i] && this.gameBoard[1][i] == this.gameBoard[2][i]) {
                return this.gameBoard[0][i];
            }
        }

        return 0;
    }

    private int isThereDiagonalWinner() {
        if (this.gameBoard[0][0] == this.gameBoard[1][1] && this.gameBoard[1][1] == this.gameBoard[2][2]) {
            return this.gameBoard[0][0];
        }

        if (this.gameBoard[0][2] == this.gameBoard[1][1] && this.gameBoard[1][1] == this.gameBoard[2][0]) {
            return this.gameBoard[0][2];
        }

        return 0;
    }

    public static void main(String[] args) {
        System.out.println("You are playing Tic-Tac-Toe against algorithm.");
        System.out.println("Who will start first - you or the computer? Type \"i\" for you and \"c\" for the computer");
        char firstPlayer = SCANNER.next().charAt(0);

        TicTacToe3 ticTacToe = new TicTacToe3(firstPlayer);
        ticTacToe.algorithm();
        SCANNER.close();
    }
}