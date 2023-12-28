package bg.sofia.uni.fmi.ai.ml;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SingleLayerPerceptron {
    private final static double INITIAL_RANDOM_WEIGHT = 0.01;//0.5;
    private final static int MAX_EPOCHS = 1000;//10000;
    private final static double LEARNING_RATE = 0.01;//0.0001;
    private final static double CRITICAL_VALUE = 0.5;//1;

    private final static Random RANDOM_GENERATOR = new Random();


    private List<List<Integer>> inputLayer;
    private List<Integer> outputLayer;
    private List<Double> weightsList;
    private double biasValue;

    public SingleLayerPerceptron() {
        this.inputLayer = new ArrayList<>();
        this.outputLayer = new ArrayList<>();
        this.weightsList = new ArrayList<>();
        this.biasValue = 0;
    }

    public SingleLayerPerceptron(List<List<Integer>> inputLayer, List<Integer> outputLayer, double biasValue) {
        this.inputLayer = inputLayer;
        this.outputLayer = outputLayer;
        this.weightsList = new ArrayList<>();
        this.biasValue = biasValue;
        initializeRandomWeights();
    }

    private void initializeRandomWeights() {
        if (!inputLayer.isEmpty()) {
            int numberOfElementsInRow = inputLayer.get(0).size();

            for (int i = 0; i < numberOfElementsInRow; i++) {
                weightsList.add(-INITIAL_RANDOM_WEIGHT + RANDOM_GENERATOR.nextDouble());
            }
        }
    }

    public void trainPerceptron() {
        for (int i = 0; i < MAX_EPOCHS; i++) {
            int totalError = 0;

            for (int j = 0; j < outputLayer.size(); j++) {
                List<Integer> currentInput = inputLayer.get(j);
                int currentPredictedValue = getPredictedValue(currentInput);
                int currentError = outputLayer.get(j) - currentPredictedValue;

                biasValue += LEARNING_RATE * currentError;

                for (int m = 0; m < inputLayer.get(0).size(); m++) {
                    weightsList.set(m, weightsList.get(m) + LEARNING_RATE * currentError * inputLayer.get(j).get(m));
                }

                totalError += currentError;
            }

            if (stopCriteria(totalError)) {
                break;
            }
        }
    }

    private boolean stopCriteria(int totalError) {
        return totalError == 0;
    }

    public int getPredictedValue(List<Integer> inputData) {
        double calculatedActivationFunction = getActivationFunction(inputData);
        return calculatedActivationFunction > CRITICAL_VALUE ? 1 : 0;
    }

    private double getActivationFunction(List<Integer> inputData) {
        double result = biasValue;
        for (int i = 0; i < inputData.size(); i++) {
            result += inputData.get(i) * weightsList.get(i);
        }
        return result;
    }
}