package bg.sofia.uni.fmi.ai.ml;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class NBC {
    private final static int LAPLACE_LAMBDA = 1;
    private final static int FOLD_CROSS_VALIDATION_COEFFICIENT = 10;
    private final static int NUMBER_OF_FEATURES = 16;
    private final static int NUMBER_OF_POSSIBLE_VALUES_FOR_CLASSES = 2;
    private final static int NUMBER_OF_POSSIBLE_VALUES_FOR_FEATURES = 3;
    private final static String DEMOCRAT_PARTY_STRING = "democrat";
    private final static String REPUBLICAN_PARTY_STRING = "republican";
    private final static String PATH_TO_DATA = "./data/house-votes-84.data";
    private final static DecimalFormat DECIMAL_FORMAT_ROUND_TWO = new DecimalFormat("0.00");

    private final List<Voter> voters;
    private List<Voter> trainList;
    private List<Voter> testList;
    private final List<Double> accuraciesList;
    private long totalNumberOfRepublicansInTrainList;
    private long totalNumberOfDemocratsInTrainList;

    public NBC(Reader reader) {
        try (BufferedReader bufferedReader = new BufferedReader(reader)) {
            this.voters = bufferedReader.lines()
                .map(Voter::of)
                .collect(Collectors.toList());
        } catch (IOException e) {
            throw new IllegalArgumentException("The provided file could not be processed", e);
        }

        this.trainList = new ArrayList<>();
        this.testList = new ArrayList<>();
        this.accuraciesList = new ArrayList<>();
    }

    private long getNumberOfVoters() {
        return this.voters.size();
    }

    private void updateTotalNumberOfVotersForEachParty() {
        this.totalNumberOfDemocratsInTrainList = this.trainList.stream()
            .filter(v -> v.party().equalsIgnoreCase(DEMOCRAT_PARTY_STRING))
            .count();

        this.totalNumberOfRepublicansInTrainList = this.trainList.stream()
            .filter(v -> v.party().equalsIgnoreCase(REPUBLICAN_PARTY_STRING))
            .count();
    }

    private long findNumberOfVotersWithGivenClassAndFeature(String party, short featureResultAsNumber, short numberOfFeature) {
        return this.trainList.stream()
            .filter(v -> v.party().equalsIgnoreCase(party))
            .filter(v -> (v.getNumberedFeature(numberOfFeature) == featureResultAsNumber))
            .count();
    }

    private String testWithNewVoter(Voter voterToTest) {
        double republicanGivenInstanceProbabilityLogarithm = 0;
        double democratGivenInstanceProbabilityLogarithm = 0;

        republicanGivenInstanceProbabilityLogarithm +=
            Math.log((double) (totalNumberOfRepublicansInTrainList + LAPLACE_LAMBDA) /
                (trainList.size() + NUMBER_OF_POSSIBLE_VALUES_FOR_CLASSES * LAPLACE_LAMBDA));

        democratGivenInstanceProbabilityLogarithm +=
            Math.log((double) (totalNumberOfDemocratsInTrainList + LAPLACE_LAMBDA) /
                (trainList.size() + NUMBER_OF_POSSIBLE_VALUES_FOR_CLASSES * LAPLACE_LAMBDA));

        for (short i = 1; i <= NUMBER_OF_FEATURES; i++) {
            republicanGivenInstanceProbabilityLogarithm +=
                Math.log(
                    (double) (findNumberOfVotersWithGivenClassAndFeature(REPUBLICAN_PARTY_STRING, voterToTest.getNumberedFeature(i), i) +
                        LAPLACE_LAMBDA) /
                        (this.totalNumberOfRepublicansInTrainList + NUMBER_OF_POSSIBLE_VALUES_FOR_FEATURES * LAPLACE_LAMBDA)
                );

            democratGivenInstanceProbabilityLogarithm +=
                Math.log(
                    (double) (findNumberOfVotersWithGivenClassAndFeature(DEMOCRAT_PARTY_STRING, voterToTest.getNumberedFeature(i), i) +
                        LAPLACE_LAMBDA) /
                        (this.totalNumberOfDemocratsInTrainList + NUMBER_OF_POSSIBLE_VALUES_FOR_FEATURES * LAPLACE_LAMBDA)
                );
        }

        return
            Double.compare(republicanGivenInstanceProbabilityLogarithm, democratGivenInstanceProbabilityLogarithm) > 0
            ? REPUBLICAN_PARTY_STRING : DEMOCRAT_PARTY_STRING;
    }

    private void modelNBC(int numberOfRound) {
        int numberOfTrueGuesses = 0;
        int totalInstancesInTestSet = this.testList.size();

        updateTotalNumberOfVotersForEachParty();
        for (Voter currentVoter : this.testList) {
            String realValueParty = currentVoter.party();
            String guessedParty = testWithNewVoter(currentVoter);

            if (realValueParty.equalsIgnoreCase(guessedParty)) {
                ++numberOfTrueGuesses;
            }
        }

        double currentAccuracy = 100.0 * numberOfTrueGuesses / totalInstancesInTestSet;
        accuraciesList.add(currentAccuracy);

        System.out.println("Accuracy on round " + numberOfRound + " is " + DECIMAL_FORMAT_ROUND_TWO.format(currentAccuracy)
            + "% Successfully guessed: " + numberOfTrueGuesses + "/" + totalInstancesInTestSet);
    }

    public void foldCrossValidation() {
        long numberOfVoters = getNumberOfVoters();
        Collections.shuffle(this.voters);

        for (int i = 0; i < FOLD_CROSS_VALIDATION_COEFFICIENT; i++) {
            List<Voter> trainList = new ArrayList<>();
            List<Voter> testList = new ArrayList<>();

            for (int j = 0; j < numberOfVoters; j++) {
                if (j % FOLD_CROSS_VALIDATION_COEFFICIENT == i) {
                    testList.add(this.voters.get(j));
                } else {
                    trainList.add(this.voters.get(j));
                }
            }

            this.trainList = trainList;
            this.testList = testList;

            modelNBC(i + 1);
        }

        double sumOfAccuracies = 0;
        int numberOfAccuraciesInList = accuraciesList.size();

        for (double currentAccuracy : accuraciesList) {
            sumOfAccuracies += currentAccuracy;
        }

        System.out.println("Final average accuracy is: " +
            DECIMAL_FORMAT_ROUND_TWO.format(sumOfAccuracies / numberOfAccuraciesInList) + "%");
    }

    public static void main(String[] args) throws IOException {
        Reader reader = new FileReader(PATH_TO_DATA);
        NBC naiveBayesClassifier = new NBC(reader);
        naiveBayesClassifier.foldCrossValidation();
        reader.close();
    }
}