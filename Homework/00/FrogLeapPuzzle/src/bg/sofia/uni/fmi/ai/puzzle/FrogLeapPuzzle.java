package bg.sofia.uni.fmi.ai.puzzle;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Scanner;

public class FrogLeapPuzzle {

    private static final int LESS_THAN_ASCII_CODE = 60;
    private static final int GREATER_THAN_ASCII_CODE = 62;
    private static final int UNDERSCORE_ASCII_CODE = 95;
    private static final char LESS_THAN_SYMBOL = '<';
    private static final char GREATER_THAN_SYMBOL = '>';
    private static final char UNDERSCORE_SYMBOL = '_';

    private static void fulfillStates(Character[] initialState, Character[] goalState, int numberOfFrogs) {
        int totalNumberOfRocks = 2 * numberOfFrogs + 1;
        for (int i = 0; i < totalNumberOfRocks; i++) {
            if (i < numberOfFrogs) {
                initialState[i] = GREATER_THAN_SYMBOL;
            } else if (i == numberOfFrogs) {
                initialState[i] = UNDERSCORE_SYMBOL;
            } else {
                initialState[i] = LESS_THAN_SYMBOL;
            }
        }

        for (int i = 0; i < totalNumberOfRocks; i++) {
            if ((int) initialState[i] == GREATER_THAN_ASCII_CODE) {
                goalState[i] = (char) LESS_THAN_ASCII_CODE;
            } else if ((int) initialState[i] == LESS_THAN_ASCII_CODE) {
                goalState[i] = (char) GREATER_THAN_ASCII_CODE;
            } else {
                goalState[i] = (char) UNDERSCORE_ASCII_CODE;
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

    private static boolean isVisited(Character[] state, List<Character[]> visitedStates){
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

    public static void main(String[] args) {

        //Input
        Scanner scanner = new Scanner(System.in);
        int numberOfFrogs = scanner.nextInt();

        //Algorithm
        int totalNumberOfRocks = 2 * numberOfFrogs + 1;
        Character[] initialState = new Character[totalNumberOfRocks];
        Character[] goalState = new Character[totalNumberOfRocks];

        fulfillStates(initialState, goalState, numberOfFrogs);

        Deque<Character[]> stack = new ArrayDeque<>();
        List<Character[]> visitedStates = new ArrayList<>();
        stack.push(initialState.clone());

        do {
            int indexOfEmptyRock = getIndexOfEmptyRock(initialState);
            if ((indexOfEmptyRock >= 2) &&
                (initialState[indexOfEmptyRock - 2] == GREATER_THAN_SYMBOL) &&
                (!isVisited((swapPositions(initialState.clone(), indexOfEmptyRock - 2, indexOfEmptyRock)), visitedStates))){
                stack.push(initialState.clone());
                swapPositions(initialState, indexOfEmptyRock - 2, indexOfEmptyRock);
                stack.push(initialState.clone());
                visitedStates.add(initialState.clone());
                continue;
            }

            if ((indexOfEmptyRock >= 1) &&
                (initialState[indexOfEmptyRock - 1] == GREATER_THAN_SYMBOL) &&
                (!isVisited((swapPositions(initialState.clone(), indexOfEmptyRock - 1, indexOfEmptyRock)), visitedStates))){
                stack.push(initialState.clone());
                swapPositions(initialState, indexOfEmptyRock - 1, indexOfEmptyRock);
                stack.push(initialState.clone());
                visitedStates.add(initialState.clone());
                continue;
            }

            if ((indexOfEmptyRock < initialState.length - 1) &&
                (initialState[indexOfEmptyRock + 1] == LESS_THAN_SYMBOL) &&
                (!isVisited((swapPositions(initialState.clone(), indexOfEmptyRock + 1, indexOfEmptyRock)), visitedStates))){
                stack.push(initialState.clone());
                swapPositions(initialState, indexOfEmptyRock + 1, indexOfEmptyRock);
                stack.push(initialState.clone());
                visitedStates.add(initialState.clone());
                continue;
            }

            if ((indexOfEmptyRock < initialState.length - 2) &&
                (initialState[indexOfEmptyRock + 2] == LESS_THAN_SYMBOL) &&
                (!isVisited((swapPositions(initialState.clone(), indexOfEmptyRock + 2, indexOfEmptyRock)), visitedStates))){
                stack.push(initialState.clone());
                swapPositions(initialState, indexOfEmptyRock + 2, indexOfEmptyRock);
                stack.push(initialState.clone());
                visitedStates.add(initialState.clone());
                continue;
            }

            initialState = stack.pop();
            initialState = initialState.clone();
        } while(!Arrays.equals(initialState, goalState));

        Deque<Character[]> resultStack = new ArrayDeque<>();
        while (!stack.isEmpty()) {
            resultStack.push(stack.poll());
        }

        // Output
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
}