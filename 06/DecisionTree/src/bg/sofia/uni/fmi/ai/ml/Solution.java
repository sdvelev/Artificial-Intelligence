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

public class Solution {
    private final static String PATH_TO_DATA = "./data/breast-cancer.data";
    private final static int FOLD_CROSS_VALIDATION_COEFFICIENT = 10;
    private final static String RECURRENCE_EVENT_STRING = "recurrence-events";
    private final static String NO_RECURRENCE_EVENT_STRING = "no-recurrence-events";
    private final static DecimalFormat DECIMAL_FORMAT_ROUND_TWO = new DecimalFormat("0.00");

    private List<Patient> patients;
    private List<Patient> trainList;
    private List<Patient> testList;
    private List<Patient> validationList;
    private final List<Double> accuraciesListPrePruning;
    private final List<Double> accuraciesListPostPruning;
    private long totalNumberOfRecurrenceEventsInTrainList;
    private long totalNumberOfNoRecurrenceEventsInTrainList;

    public Solution(Reader reader) {
        try (BufferedReader bufferedReader = new BufferedReader(reader)) {
            this.patients = bufferedReader.lines()
                .map(Patient::of)
                .collect(Collectors.toList());
        } catch (IOException e) {
            throw new IllegalArgumentException("The provided file could not be processed", e);
        }

        this.trainList = new ArrayList<>();
        this.testList = new ArrayList<>();
        this.validationList = new ArrayList<>();
        this.accuraciesListPrePruning = new ArrayList<>();
        this.accuraciesListPostPruning = new ArrayList<>();
    }

    private void updateTotalNumberOfPatientsInEachRecurrence() {
        this.totalNumberOfRecurrenceEventsInTrainList = this.trainList.stream()
            .filter(v -> v.recurrenceEvents().equalsIgnoreCase(RECURRENCE_EVENT_STRING))
            .count();

        this.totalNumberOfNoRecurrenceEventsInTrainList = this.trainList.stream()
            .filter(v -> v.recurrenceEvents().equalsIgnoreCase(NO_RECURRENCE_EVENT_STRING))
            .count();
    }

    private long getNumberOfPatients() {
        return this.patients.size();
    }

    private void modelDT(int numberOfRound) {
        int numberOfTrueGuessesPrePruning = 0;
        int numberOfTrueGuessesPostPruning = 0;
        int totalInstancesInTestSet = this.testList.size();

        updateTotalNumberOfPatientsInEachRecurrence();

        PatientCollection patientCollection = new PatientCollection(trainList);

        List<Patient> allDatalist = new ArrayList<>(trainList);
        allDatalist.addAll(testList);

        PatientCollection patientCollectionAllData = new PatientCollection(allDatalist);

        DecisionTreePrePruning decisionTreePrePruning = new DecisionTreePrePruning(patientCollection,
            patientCollectionAllData.getPatientPossibleAttributeValuesList());


        List<Patient> newTrainList = new ArrayList<>();
        for (int i = 0; i < trainList.size(); i++) {
            if (i % FOLD_CROSS_VALIDATION_COEFFICIENT == 0) {
                validationList.add(trainList.get(i));
            } else {
                newTrainList.add(trainList.get(i));
            }
        }

        /*DecisionTreePostPruning decisionTreePostPruning = new DecisionTreePostPruning(new PatientCollection(newTrainList),
            validationList,
            patientCollectionAllData.getPatientPossibleAttributeValuesList());*/


        for (Patient currentPatient : this.testList) {
            if (decisionTreePrePruning.isCorrectPredictedResultByTraversingDecisionTree(currentPatient)) {
                ++numberOfTrueGuessesPrePruning;
            }

           /* if (decisionTreePostPruning.isCorrectPredictedResultByTraversingDecisionTree(currentPatient)) {
                ++numberOfTrueGuessesPostPruning;
            }*/
        }

        double currentAccuracyPrePruning = 100.0 * numberOfTrueGuessesPrePruning / totalInstancesInTestSet;
        accuraciesListPrePruning.add(currentAccuracyPrePruning);

        double currentAccuracyPostPruning = 100.0 * numberOfTrueGuessesPostPruning / totalInstancesInTestSet;
        accuraciesListPostPruning.add(currentAccuracyPostPruning);

        System.out.println("Accuracy of decision tree on round " + numberOfRound + " is " +
            DECIMAL_FORMAT_ROUND_TWO.format(currentAccuracyPrePruning) + "% Successfully guessed: " +
            numberOfTrueGuessesPrePruning + "/" + totalInstancesInTestSet);

        /*System.out.println("Accuracy of decision tree with postpruning on round " + numberOfRound + " is " +
            DECIMAL_FORMAT_ROUND_TWO.format(currentAccuracyPostPruning) + "% Successfully guessed: " +
            numberOfTrueGuessesPostPruning + "/" + totalInstancesInTestSet);*/
    }

    public void foldCrossValidation() {
        long numberOfPatients = getNumberOfPatients();
        Collections.shuffle(this.patients);

        for (int i = 0; i < FOLD_CROSS_VALIDATION_COEFFICIENT; i++) {
            List<Patient> trainList = new ArrayList<>();
            List<Patient> testList = new ArrayList<>();

            for (int j = 0; j < numberOfPatients; j++) {
                if (j % FOLD_CROSS_VALIDATION_COEFFICIENT == i) {
                    testList.add(this.patients.get(j));
                } else {
                    trainList.add(this.patients.get(j));
                }
            }

            this.trainList = trainList;
            this.testList = testList;

            modelDT(i + 1);
        }

        double sumOfAccuraciesPrePruning = 0;
        double sumOfAccuraciesPostPruning = 0;

        int numberOfAccuraciesInList = accuraciesListPrePruning.size();

        for (double currentAccuracy : accuraciesListPrePruning) {
            sumOfAccuraciesPrePruning += currentAccuracy;
        }

        for (double currentAccuracy : accuraciesListPostPruning) {
            sumOfAccuraciesPostPruning += currentAccuracy;
        }

        System.out.println("Final average accuracy of decision tree technique is: " +
            DECIMAL_FORMAT_ROUND_TWO.format(sumOfAccuraciesPrePruning / numberOfAccuraciesInList) + "%");

        /*System.out.println("Final average accuracy of decision tree with postpruning is: " +
            DECIMAL_FORMAT_ROUND_TWO.format(sumOfAccuraciesPostPruning / numberOfAccuraciesInList) + "%");*/
    }

    public static void main(String[] args) throws IOException {
        Reader reader = new FileReader(PATH_TO_DATA);
        Solution decisionTreeSolution = new Solution(reader);
        decisionTreeSolution.foldCrossValidation();
        reader.close();
    }
}