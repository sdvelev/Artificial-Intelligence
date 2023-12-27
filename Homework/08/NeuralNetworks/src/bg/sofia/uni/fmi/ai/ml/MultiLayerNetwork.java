package bg.sofia.uni.fmi.ai.ml;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MultiLayerNetwork {
    private final static double INITIAL_RANDOM_WEIGHT = 0.5;
    private final static double INITIAL_RANDOM_WEIGHT_STEP = 0.05;
    private final static int MAX_EPOCHS = 100000;
    private final static double LEARNING_RATE = 0.1;
    private final static double STOP_CRITERIA_MIN_ERROR = 0.001;

    private final static Random RANDOM_GENERATOR = new Random();

    private List<Neuron> inputLayerList;
    private List<Neuron> hiddenLayerList;
    private List<Neuron> outputLayerList;
    private Neuron biasNeuron;

    private int numberOfNeuronsInInputLayer;
    private int numberOfNeuronsInHiddenLayer;
    private int numberOfNeuronsInOutputLayer;

    private List<List<Double>> inputValues;
    private List<Double> outputValues;
    private List<Double> predictedValues;

    public MultiLayerNetwork(List<List<Double>> inputValues, List<Double> outputValues,
                             int numberOfNeuronsInInputLayer, int numberOfNeuronsInHiddenLayer,
                             int numberOfNeuronsInOutputLayer) {
        this.inputLayerList = new ArrayList<>();
        this.hiddenLayerList = new ArrayList<>();
        this.outputLayerList = new ArrayList<>();
        this.biasNeuron = new Neuron();
        biasNeuron.setActivationFunctionValue(1);

        this.numberOfNeuronsInInputLayer = numberOfNeuronsInInputLayer;
        this.numberOfNeuronsInHiddenLayer = numberOfNeuronsInHiddenLayer;
        this.numberOfNeuronsInOutputLayer = numberOfNeuronsInOutputLayer;

        this.inputValues = inputValues;
        this.outputValues = outputValues;
        this.predictedValues = new ArrayList<>();

        fillLayersInitialization();
        applyRandomWeightsInitialization();
    }

    public void trainNetwork() {
        for (int i = 0; i < MAX_EPOCHS; i++) {
            double totalError = 0;
            for (int j = 0; j < outputValues.size(); j++) {
                setInputValues(inputValues.get(j));

                for (Neuron currentNeuron : hiddenLayerList) {
                    currentNeuron.calculateActivationFunctionValue();
                }

                for (Neuron currentNeuron : outputLayerList) {
                    currentNeuron.calculateActivationFunctionValue();
                }

                double predictedOutput = outputLayerList.get(0).getActivationFunctionValue();
                double expectedOutput = outputValues.get(j);

                predictedValues.add(predictedOutput);

                double currentError = (expectedOutput - predictedOutput) * (expectedOutput - predictedOutput);
                totalError += currentError;

                applyBackpropagation(expectedOutput);
            }

            if (stopCriteria(totalError)) {
                for (int j = 0; j < predictedValues.size(); j++) {
                    System.out.println("The predicted result for values " +
                        (inputValues.get(j).get(0)).intValue() + " ⊕ " + inputValues.get(j).get(1).intValue()
                        + " is: " + predictedValues.get (j));
                }
                break;
            } else {
                predictedValues.clear();
            }
        }
    }

    private boolean stopCriteria(double totalError) {
        return totalError <= STOP_CRITERIA_MIN_ERROR;
    }

//    private void applyBackpropagation(double expectedOutput) {
//        for (Neuron neuron : outputLayerList) {
//            List<Connection> connections = neuron.getInputConnectionsList();
//            for (Connection connection : connections) {
//                double ok = neuron.getActivationFunctionValue();
//                double tk = expectedOutput;
//                double deltak = ok * (1 - ok) * (tk - ok);
//                double x = connection.getStartNeuron().getActivationFunctionValue();
//                double weight = connection.getWeight() + LEARNING_RATE * deltak * x;
//                connection.setWeight(weight);
//            }
//        }
//
//        int k = 0;
//        for (Neuron neuron : hiddenLayerList) {
//            List<Connection> connections = neuron.getInputConnectionsList();
//            for (Connection connection : connections) {
//                double oh = neuron.getActivationFunctionValue();
//                double sum = 0;
//                for (Neuron outputNeuron : outputLayerList) {
//                    double ok = outputNeuron.getActivationFunctionValue();
//                    double tk = expectedOutput;
//                    double deltak = ok * (1 - ok) * (tk - ok);
//                    double wkh = outputNeuron.getInputConnectionsList().get(k).getWeight();
//                    sum += deltak * wkh;
//                }
//                double deltah = oh * (1 - oh) * sum;
//                double x = connection.getStartNeuron().getActivationFunctionValue();
//                double weight = connection.getWeight() + LEARNING_RATE * deltah * x;
//                connection.setWeight(weight);
//            }
//
//            k++;
//        }
//    }

    private void applyBackpropagation(double expectedOutput) {
        updateOutputLayerWeights(expectedOutput);
        updateHiddenLayerWeights(expectedOutput);
    }

    private void updateOutputLayerWeights(double expectedOutput) {
        for (Neuron neuron : outputLayerList) {
            List<Connection> connections = neuron.getInputConnectionsList();
            for (Connection connection : connections) {
                double activation = neuron.getActivationFunctionValue();
                double errorGradient = calculateErrorGradient(activation, expectedOutput);
                double inputActivation = connection.getStartNeuron().getActivationFunctionValue();
                double weightUpdate = LEARNING_RATE * errorGradient * inputActivation;
                connection.setWeight(connection.getWeight() + weightUpdate);
            }
        }
    }

    private void updateHiddenLayerWeights(double expectedOutput) {
        int outputLayerIndex = 0;
        for (Neuron hiddenNeuron : hiddenLayerList) {
            List<Connection> connections = hiddenNeuron.getInputConnectionsList();
            double hiddenActivation = hiddenNeuron.getActivationFunctionValue();
            double sum = calculateSumForHiddenNeuron(expectedOutput, outputLayerIndex);

            int connectionIndex = 0;
            for (Connection connection : connections) {
                double inputActivation = connection.getStartNeuron().getActivationFunctionValue();
                double errorGradient = hiddenActivation * (1 - hiddenActivation) * sum;
                double weightUpdate = LEARNING_RATE * errorGradient * inputActivation;
                connection.setWeight(connection.getWeight() + weightUpdate);
                connectionIndex++;
            }
            outputLayerIndex++;
        }
    }

    private double calculateSumForHiddenNeuron(double expectedOutput, int outputLayerIndex) {
        double sum = 0;
        for (Neuron outputNeuron : outputLayerList) {
            double outputActivation = outputNeuron.getActivationFunctionValue();
            double errorGradient = calculateErrorGradient(outputActivation, expectedOutput);
            double weight = outputNeuron.getInputConnectionsList().get(outputLayerIndex).getWeight();
            sum += errorGradient * weight;
        }
        return sum;
    }

    private double calculateErrorGradient(double activation, double expectedOutput) {
        return activation * (1 - activation) * (expectedOutput - activation);
    }


    private void setInputValues(List<Double> currentInputValues) {
        for (int i = 0; i < currentInputValues.size(); i++) {
            inputLayerList.get(i).setActivationFunctionValue(currentInputValues.get(i));
        }
    }

    private void fillLayersInitialization() {
        for (int i = 0; i < numberOfNeuronsInInputLayer; i++) {
            this.inputLayerList.add(new Neuron());
        }

        for (int i = 0; i < numberOfNeuronsInHiddenLayer; i++) {
            Neuron neuronToAdd = new Neuron();

            for (int j = 0; j < numberOfNeuronsInInputLayer; j++) {
                neuronToAdd.addInputConnection(inputLayerList.get(j));
            }

            neuronToAdd.addBiasConnection(biasNeuron);
            this.hiddenLayerList.add(neuronToAdd);
        }

        for (int i = 0; i < numberOfNeuronsInOutputLayer; i++) {
            Neuron neuronToAdd = new Neuron();

            for (int j = 0; j < numberOfNeuronsInHiddenLayer; j++) {
                neuronToAdd.addInputConnection(hiddenLayerList.get(j));
            }

            neuronToAdd.addBiasConnection(biasNeuron);
            this.outputLayerList.add(neuronToAdd);
        }
    }

    private void applyRandomWeightsInitialization() {
        initializeLayerWeights(hiddenLayerList);
        initializeLayerWeights(outputLayerList);
    }

    private void initializeLayerWeights(List<Neuron> currentLayer) {
        for (Neuron currentNeuron : currentLayer) {
            List<Connection> currentNeuronConnections = currentNeuron.getInputConnectionsList();
            for (int i = 0; i < currentNeuronConnections.size(); i++) {
                double currentWeight = (i == currentNeuronConnections.size() - 1) ? INITIAL_RANDOM_WEIGHT : getRandomWeight();
                currentNeuronConnections.get(i).setWeight(currentWeight);
            }
        }
    }

    private double getRandomWeight() {
        return -INITIAL_RANDOM_WEIGHT_STEP + 2 * INITIAL_RANDOM_WEIGHT_STEP * RANDOM_GENERATOR.nextDouble();
    }
}
