package bg.sofia.uni.fmi.ai.puzzle;

import java.text.DecimalFormat;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Scanner;

public class FrogLeapPuzzle {
    private static final DecimalFormat DECIMAL_FORMAT_ROUND_TWO = new DecimalFormat("0.00");

    private static final int LESS_THAN_ASCII_CODE = 60;
    private static final int GREATER_THAN_ASCII_CODE = 62;
    private static final int UNDERSCORE_ASCII_CODE = 95;
    private static final Character LESS_THAN_SYMBOL = '<';
    private static final Character GREATER_THAN_SYMBOL = '>';
    private static final Character UNDERSCORE_SYMBOL = '_';

    private static void fulfillStates(Character[] initialState, Character[] goalState, int numberOfFrogs) {
        int totalNumberOfRocks = 2 * numberOfFrogs + 1;
        for (int i = 0; i < totalNumberOfRocks; i++) {
            if (i < numberOfFrogs) {
                initialState[i] = (char) GREATER_THAN_ASCII_CODE;
                goalState[i] = (char) LESS_THAN_ASCII_CODE;
            } else if (i == numberOfFrogs) {
                initialState[i] = (char) UNDERSCORE_ASCII_CODE;
                goalState[i] = (char) UNDERSCORE_ASCII_CODE;
            } else {
                initialState[i] = (char) LESS_THAN_ASCII_CODE;
                goalState[i] = (char) GREATER_THAN_ASCII_CODE;
            }
        }

    }
    private static int getIndexOfEmptyRock(Character[] state){
        for (int i = 0; i < state.length; i++){
            if (state[i] == UNDERSCORE_SYMBOL) {
                return i;
            }
        }
        return -1;
    }

    private static void swapPositions(Character[] state, int firstIndex, int secondIndex){
        Character temporary = state[firstIndex];
        state[firstIndex] = state[secondIndex];
        state[secondIndex] = temporary;
    }

    private static Character[] rightLeapIfPossible(int indexOfEmptyRock, int rocksToJump, Character[] initialState) {
        if ((indexOfEmptyRock >= rocksToJump) &&
            (initialState[indexOfEmptyRock - rocksToJump] == GREATER_THAN_SYMBOL)) {
            swapPositions(initialState, indexOfEmptyRock - rocksToJump, indexOfEmptyRock);
            return initialState;
        }
        return null;
    }

    private static Character[] leftLeapIfPossible(int indexOfEmptyRock, int rocksToJump, Character[] initialState) {
        if ((indexOfEmptyRock < initialState.length - rocksToJump) &&
            (initialState[indexOfEmptyRock + rocksToJump] == LESS_THAN_SYMBOL)) {
            swapPositions(initialState, indexOfEmptyRock + rocksToJump, indexOfEmptyRock);
            return initialState;
        }
        return null;
    }

    private static boolean dfsAlgorithm(Character[] initialState, Character[] goalState, Deque<Character[]> stack) {
        if (initialState == null) {
            return false;
        }

        if (Arrays.equals(initialState, goalState)) {
            return true;
        }

        int indexOfEmptyRock = getIndexOfEmptyRock(initialState);

        Character[] twoRocksRightLeap = rightLeapIfPossible(indexOfEmptyRock, 2, initialState.clone());
        if (dfsAlgorithm(twoRocksRightLeap, goalState, stack)) {
            stack.push(twoRocksRightLeap);
            return true;
        }

        Character[] oneRockRightLeap = rightLeapIfPossible(indexOfEmptyRock, 1, initialState.clone());
        if (dfsAlgorithm(oneRockRightLeap, goalState, stack)) {
            stack.push(oneRockRightLeap);
            return true;
        }

        Character[] oneRockLeftLeap = leftLeapIfPossible(indexOfEmptyRock, 1, initialState.clone());
        if (dfsAlgorithm(oneRockLeftLeap, goalState, stack)) {
            stack.push(oneRockLeftLeap);
            return true;
        }

        Character[] twoRocksLeftLeap = leftLeapIfPossible(indexOfEmptyRock, 2, initialState.clone());
        if (dfsAlgorithm(twoRocksLeftLeap, goalState, stack)) {
            stack.push(twoRocksLeftLeap);
            return true;
        }

        return false;
    }

    private static void printStatesReversed(Deque<Character[]> resultStack) {
        while (!resultStack.isEmpty()) {
            Character[] currentState = resultStack.poll();

            for (Character character : currentState) {
                System.out.print(character);
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        //Input
        Scanner scanner = new Scanner(System.in);
        int numberOfFrogs = scanner.nextInt();
        scanner.close();

        //Preparation
        int totalNumberOfRocks = 2 * numberOfFrogs + 1;
        Character[] initialState = new Character[totalNumberOfRocks];
        Character[] goalState = new Character[totalNumberOfRocks];

        fulfillStates(initialState, goalState, numberOfFrogs);

        Deque<Character[]> stack = new ArrayDeque<>();

        //Algorithm
        long startTime = System.currentTimeMillis();
        dfsAlgorithm(initialState.clone(), goalState, stack);
        stack.push(initialState);
        long endTime = System.currentTimeMillis();

        double totalRunningTime = (endTime - startTime) / 1000.0;
        System.out.println("Total time for finding the path (in seconds): " +
            DECIMAL_FORMAT_ROUND_TWO.format(totalRunningTime));

        // Output
        printStatesReversed(stack);
    }
}