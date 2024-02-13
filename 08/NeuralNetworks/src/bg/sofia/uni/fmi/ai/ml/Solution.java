package bg.sofia.uni.fmi.ai.ml;

import java.util.List;

public class Solution {
    private final static double BIAS_VALUE = 0.5;

    public static void main(String[] args) {
        List<List<Integer>> inputLayer = List.of(
            List.of(0, 0),
            List.of(0, 1),
            List.of(1, 0),
            List.of(1, 1));

        List<List<Double>> inputLayerDouble = List.of(
            List.of(0., 0.),
            List.of(0., 1.),
            List.of(1., 0.),
            List.of(1., 1.));

        List<Integer> outputLayerLogicalAnd = List.of(0, 0, 0, 1);
        List<Integer> outputLayerLogicalOr = List.of(0, 1, 1, 1);
        List<Double> outputLayerLogicalXor = List.of(0., 1., 1., 0.);

        SingleLayerPerceptron singleLayerPerceptronLogicalAnd = new SingleLayerPerceptron(inputLayer, outputLayerLogicalAnd,
            BIAS_VALUE);
        singleLayerPerceptronLogicalAnd.trainPerceptron();

        for (int i = 0; i < inputLayer.size(); i++) {
            System.out.println("The predicted result for values " + inputLayer.get(i).get(0) + " && " + inputLayer.get(i).get(1)
            + " is: " + singleLayerPerceptronLogicalAnd.getPredictedValue(inputLayer.get(i)));
        }

        System.out.println();

        SingleLayerPerceptron singleLayerPerceptronLogicalOr = new SingleLayerPerceptron(inputLayer, outputLayerLogicalOr,
            BIAS_VALUE);
        singleLayerPerceptronLogicalOr.trainPerceptron();

        for (int i = 0; i < inputLayer.size(); i++) {
            System.out.println("The predicted result for values " + inputLayer.get(i).get(0) + " || " + inputLayer.get(i).get(1)
                + " is: " + singleLayerPerceptronLogicalOr.getPredictedValue(inputLayer.get(i)));
        }

        System.out.println();

        MultiLayerNetwork multiLayerNetwork = new MultiLayerNetwork(inputLayerDouble, outputLayerLogicalXor, 2, 2, 1);
        List<Double> predictedValues =  multiLayerNetwork.trainNetwork();

        for (int i = 0; i < predictedValues.size(); i++) {
            System.out.println("The predicted result for values " +
                (inputLayerDouble.get(i).get(0)).intValue() + " ⊕ " + inputLayerDouble.get(i).get(1).intValue()
                + " is: " + predictedValues.get (i));
        }
    }
}