package bg.sofia.uni.fmi.ai.ml;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class NBC {
    private final static int LAPLACE_LAMBDA = 1;
    private final static int NUMBER_OF_POSSIBLE_VALUES_FOR_CLASSES = 2;
    private final static int NUMBER_OF_FEATURES = 16;
    private final static int NUMBER_OF_POSSIBLE_VALUES_FOR_FEATURES = 3;


    private final List<Voter> voters;
    private List<Voter> trainSet;
    private List<Voter> testSet;
    private List<Double> accuracies;

    private long totalNumberOfRepublicansInTrainSet;
    private long totalNumberOfDemocratsInTrainSet;

    public NBC(Reader reader) {

        try (BufferedReader bufferedReader = new BufferedReader(reader)) {
            this.voters = bufferedReader.lines()
                .map(Voter::of)
                .collect(Collectors.toList());
        } catch (IOException e) {
            throw new IllegalArgumentException("The provided file could not be read", e);
        }
    }

    private void updateTotalNumbersOfEachParty() {
        this.totalNumberOfDemocratsInTrainSet = this.trainSet.stream()
            .filter(v -> v.party().equalsIgnoreCase("democrat"))
            .count();

        this.totalNumberOfRepublicansInTrainSet = this.trainSet.stream()
            .filter(v -> v.party().equalsIgnoreCase("republican"))
            .count();
    }

    private long numberOfVotersWithGivenClassAndFeature(String party, short feature, short numberOfFeature) {
        return this.trainSet.stream()
            .filter(v -> v.party().equalsIgnoreCase(party))
            .filter(v -> (v.getNumberedFeature(numberOfFeature) == feature))
            .count();
    }

    private String testNewInstance(Voter voterToTest) {
        double republicanGivenInstanceProbabilityLogarithm = 0;
        double democratGivenInstanceProbabilityLogarithm = 0;

        republicanGivenInstanceProbabilityLogarithm +=
            Math.log((double) (totalNumberOfRepublicansInTrainSet + LAPLACE_LAMBDA) / (trainSet.size() + NUMBER_OF_POSSIBLE_VALUES_FOR_CLASSES * LAPLACE_LAMBDA));

        democratGivenInstanceProbabilityLogarithm +=
            Math.log((double) (totalNumberOfDemocratsInTrainSet + LAPLACE_LAMBDA) / (trainSet.size() + NUMBER_OF_POSSIBLE_VALUES_FOR_CLASSES * LAPLACE_LAMBDA));

        for (short i = 1; i <= NUMBER_OF_FEATURES; i++) {
            republicanGivenInstanceProbabilityLogarithm +=
                Math.log(
                    (double) (numberOfVotersWithGivenClassAndFeature("republican", voterToTest.getNumberedFeature(i), i) +
                        LAPLACE_LAMBDA) /
                        (this.totalNumberOfRepublicansInTrainSet + NUMBER_OF_POSSIBLE_VALUES_FOR_FEATURES * LAPLACE_LAMBDA)
                );

            democratGivenInstanceProbabilityLogarithm +=
                Math.log(
                    (double) (numberOfVotersWithGivenClassAndFeature("democrat", voterToTest.getNumberedFeature(i), i) +
                        LAPLACE_LAMBDA) /
                        (this.totalNumberOfRepublicansInTrainSet + NUMBER_OF_POSSIBLE_VALUES_FOR_FEATURES * LAPLACE_LAMBDA)
                );
        }

        return
            Double.compare(republicanGivenInstanceProbabilityLogarithm, democratGivenInstanceProbabilityLogarithm) > 0
            ? "republican" : "democrat";
    }

    private void classifier(int roundNumber) {

        int numberOfGuesses = 0;
        int totalInstancesInTestSet = this.testSet.size();

        updateTotalNumbersOfEachParty();

        for (int i = 0; i < totalInstancesInTestSet; i++) {
            Voter currentVoter = this.testSet.get(i);
            String realValueParty = currentVoter.party();
            String guessedParty = testNewInstance(currentVoter);

            if (realValueParty.equalsIgnoreCase(guessedParty)) {
                ++numberOfGuesses;
            }
        }

        double currentAccuracy = 100. * numberOfGuesses / totalInstancesInTestSet;
        this.accuracies.add(currentAccuracy);
        System.out.println("Current accuracy on round " + roundNumber + " is " + currentAccuracy + "%");
    }

    public void tenFoldCrossValidation() {

        long numberOfInstances = numberOfInstances();
        Collections.shuffle(this.voters);
        for (int i = 0; i < 10; i++) {
            List<Voter> trainSet = new ArrayList<>();
            List<Voter> testSet = new ArrayList<>();

            for (int j = 0; j < numberOfInstances; j++) {
                if (j % 10 == i) {
                    testSet.add(this.voters.get(j));
                } else {
                    trainSet.add(this.voters.get(j));
                }
            }

            //Main Logic
            this.trainSet = trainSet;
            this.testSet = testSet;
            this.accuracies = new ArrayList<>();

            classifier(i + 1);

            //System.out.println(i + " testSet: " + testSet.size() + " trainSet: " + trainSet.size());
        }

        double sumAccuracy = 0;
        int numberOfAccuracies = accuracies.size();

        for (int i = 0; i < numberOfAccuracies; i++) {
            sumAccuracy += this.accuracies.get(i);
        }

        System.out.println("Final average accuracy is: " + sumAccuracy / numberOfAccuracies + "%");
    }

    public long numberOfInstances() {
        return this.voters.size();
    }

    public List<Voter> getVoters() {
        return voters;
    }

    public static void main(String[] args) throws IOException {
        Reader reader = new FileReader("./data/house-votes-84.data");
        NBC naiveBayesClassifier = new NBC(reader);
        naiveBayesClassifier.tenFoldCrossValidation();
        reader.close();
    }
}
