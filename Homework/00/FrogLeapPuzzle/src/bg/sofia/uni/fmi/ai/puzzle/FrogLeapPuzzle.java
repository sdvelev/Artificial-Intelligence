package bg.sofia.uni.fmi.ai.puzzle;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.Scanner;
import java.util.Set;

public class FrogLeapPuzzle {

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

    private static boolean isVisited(Character[] state, Set<Character[]> visitedStates){
        for (Character[] currentState : visitedStates) {
            if (Arrays.equals(state, currentState)) {
                return true;
            }
        }
        return false;
    }

    private static Character[] swapPositions(Character[] state, int firstIndex, int secondIndex){
        Character temporary = state[firstIndex];
        state[firstIndex] = state[secondIndex];
        state[secondIndex] = temporary;
        return state;
    }

    private static boolean rightLeapIfPossible(int indexOfEmptyRock, int rocksToJump, Character[] initialState,
                                               Set<Character[]> visitedStates, Deque<Character[]> stack) {
        if ((indexOfEmptyRock >= rocksToJump) &&
            (initialState[indexOfEmptyRock - rocksToJump] == GREATER_THAN_SYMBOL) &&
            (!isVisited((swapPositions(initialState.clone(), indexOfEmptyRock - rocksToJump, indexOfEmptyRock)),
                visitedStates))){
            stack.push(initialState.clone());
            swapPositions(initialState, indexOfEmptyRock - rocksToJump, indexOfEmptyRock);
            stack.push(initialState.clone());
            visitedStates.add(initialState.clone());
            return true;
        }
        return false;
    }

    private static boolean leftLeapIfPossible(int indexOfEmptyRock, int rocksToJump, Character[] initialState,
                                              Set<Character[]> visitedStates, Deque<Character[]> stack) {
        if ((indexOfEmptyRock < initialState.length - rocksToJump) &&
            (initialState[indexOfEmptyRock + rocksToJump] == LESS_THAN_SYMBOL) &&
            (!isVisited((swapPositions(initialState.clone(), indexOfEmptyRock + rocksToJump, indexOfEmptyRock)),
                visitedStates))){
            stack.push(initialState.clone());
            swapPositions(initialState, indexOfEmptyRock + rocksToJump, indexOfEmptyRock);
            stack.push(initialState.clone());
            visitedStates.add(initialState.clone());
            return true;
        }
        return false;
    }

    private static void printUniqueStates(int totalNumberOfRocks, Deque<Character[]> resultStack) {
        Character[] previousState = new Character[totalNumberOfRocks];
        while (!resultStack.isEmpty()) {
            Character[] currentState = resultStack.poll();
            if (Arrays.equals(currentState, previousState)) {
                continue;
            }
            for (Character character : currentState) {
                System.out.print(character);
            }
            System.out.println();
            System.arraycopy(currentState, 0, previousState, 0, totalNumberOfRocks);
        }
    }

    public static void main(String[] args) {

        //Input
        Scanner scanner = new Scanner(System.in);
        int numberOfFrogs = scanner.nextInt();

        //Preparation
        int totalNumberOfRocks = 2 * numberOfFrogs + 1;
        Character[] initialState = new Character[totalNumberOfRocks];
        Character[] goalState = new Character[totalNumberOfRocks];

        fulfillStates(initialState, goalState, numberOfFrogs);

        Deque<Character[]> stack = new ArrayDeque<>();
        Set<Character[]> visitedStates = new LinkedHashSet<>();
        stack.push(initialState.clone());

        //Algorithm
        while (!Arrays.equals(initialState, goalState)) {
            int indexOfEmptyRock = getIndexOfEmptyRock(initialState);
            if (rightLeapIfPossible(indexOfEmptyRock, 2, initialState, visitedStates, stack)) {
                continue;
            } else if (rightLeapIfPossible(indexOfEmptyRock, 1, initialState, visitedStates, stack)) {
                continue;
            } else if (leftLeapIfPossible(indexOfEmptyRock, 1, initialState, visitedStates, stack)) {
                continue;
            } else if (leftLeapIfPossible(indexOfEmptyRock, 2, initialState, visitedStates, stack)) {
                continue;
            }
            initialState = stack.pop();
        }

        Deque<Character[]> resultStack = new ArrayDeque<>();
        while (!stack.isEmpty()) {
            resultStack.push(stack.poll());
        }

        // Output
        printUniqueStates(totalNumberOfRocks, resultStack);
    }
}