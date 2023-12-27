package bg.sofia.uni.fmi.ai.ml;

import java.util.ArrayList;
import java.util.List;

public class Neuron {
    private List<Connection> inputConnectionsList;
    private Connection biasValue;
    private double activationFunctionValue;

    public Neuron() {
        this.inputConnectionsList = new ArrayList<>();
        this.biasValue = new Connection();
        this.activationFunctionValue = 0;
    }

    public void calculateActivationFunctionValue() {
        double totalResult = biasValue.getWeight();
        for (Connection currentConnection : inputConnectionsList) {
            Neuron currentStartNeuron = currentConnection.getStartNeuron();
            Neuron currentFinishNeuron = currentConnection.getFinishNeuron();
            double currentWeight = currentConnection.getWeight();
            double currentActivationFunctionValue = currentStartNeuron.getActivationFunctionValue();
            totalResult += currentActivationFunctionValue * currentWeight;
        }

        activationFunctionValue = sigmoidFunction(totalResult);
    }

    public Connection addInputConnection(Neuron startNeuron) {
        Connection connectionToAdd = new Connection(startNeuron, this);
        inputConnectionsList.add(connectionToAdd);
        return connectionToAdd;
    }

    public void addBiasConnection(Neuron startNeuron) {
        biasValue = addInputConnection(startNeuron);
    }

    private double sigmoidFunction(double x) {
        return 1 / (1 + Math.exp(-x));
    }


    public double getActivationFunctionValue() {
        return activationFunctionValue;
    }

    public List<Connection> getInputConnectionsList() {
        return inputConnectionsList;
    }

    public void setInputConnectionsList(List<Connection> inputConnectionsList) {
        this.inputConnectionsList = inputConnectionsList;
    }

    public Connection getBiasValue() {
        return biasValue;
    }

    public void setBiasValue(Connection biasValue) {
        this.biasValue = biasValue;
    }

    public void setActivationFunctionValue(double activationFunctionValue) {
        this.activationFunctionValue = activationFunctionValue;
    }
}
