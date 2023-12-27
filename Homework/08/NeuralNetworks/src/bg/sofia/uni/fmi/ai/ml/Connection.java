package bg.sofia.uni.fmi.ai.ml;

public class Connection {

    private Neuron startNeuron;
    private Neuron finishNeuron;
    private double weight;

    public Connection() {

    }

    public Connection(Neuron startNeuron, Neuron finishNeuron, double weight) {
        this.startNeuron = startNeuron;
        this.finishNeuron = finishNeuron;
        this.weight = weight;
    }

    public Connection(Neuron startNeuron, Neuron finishNeuron) {
        this.startNeuron = startNeuron;
        this.finishNeuron = finishNeuron;
        this.weight = 0;
    }

    public Neuron getStartNeuron() {
        return startNeuron;
    }

    public void setStartNeuron(Neuron startNeuron) {
        this.startNeuron = startNeuron;
    }

    public Neuron getFinishNeuron() {
        return finishNeuron;
    }

    public void setFinishNeuron(Neuron finishNeuron) {
        this.finishNeuron = finishNeuron;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
