package bg.sofia.uni.fmi.ai.ml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PatientCollection {
    private final static int NUMBER_OF_ATTRIBUTES_IN_PATIENT = 10;
    private final static String RECURRENCE_EVENT_STRING = "recurrence-events";
    private final static String NO_RECURRENCE_EVENT_STRING = "no-recurrence-events";
    private final static int RECURRENCE_EVENT_INDEX = 1;
    private final static int NO_RECURRENCE_EVENT_INDEX = 0;
    private final static int LOG_TWO_BASE = 2;


    private List<Patient> patientCollectionList;
    private List<Set<String>> patientAttributeValuesList;
    private List<List<Map<String, Integer>>> featureValuesOccurrence;
    int numberOfNoRecurrenceTarget;
    int numberOfRecurrenceTarget;
    int numberOfPatients;

    public PatientCollection() {
        this.numberOfPatients = 0;
        this.numberOfRecurrenceTarget = 0;
        this.numberOfNoRecurrenceTarget = 0;
        this.patientCollectionList = new ArrayList<>();
        this.patientAttributeValuesList = new ArrayList<>();
        this.featureValuesOccurrence = new ArrayList<>();
    }

    public PatientCollection(List<Patient> patientsList) {
        this.numberOfPatients = patientsList.size();
        this.numberOfRecurrenceTarget = 0;
        this.numberOfNoRecurrenceTarget = 0;
        this.patientCollectionList = patientsList;
        this.patientAttributeValuesList = updateAttributeValuesList();
        this.featureValuesOccurrence = updateFeatureValuesOccurrence();
    }

    private List<Set<String>> updateAttributeValuesList() {
        List<Set<String>> patientAttributeValuesList = new ArrayList<>();
        for (short i = 0; i < NUMBER_OF_ATTRIBUTES_IN_PATIENT; i++) {
            patientAttributeValuesList.add(new HashSet<>());
        }

        for (int i = 0; i < numberOfPatients; i++) {
            Patient currentPatient = patientCollectionList.get(i);
            for (short j = 0; j < NUMBER_OF_ATTRIBUTES_IN_PATIENT; j++) {
                patientAttributeValuesList.get(j).add(currentPatient.getNumberedFeature(j));
            }
        }

        return patientAttributeValuesList;
    }

    private List<List<Map<String, Integer>>> updateFeatureValuesOccurrence() {
        List<List<Map<String, Integer>>> featureValuesOccurrence = new ArrayList<>();
        featureValuesOccurrence.add(new ArrayList<>());
        featureValuesOccurrence.add(new ArrayList<>());

        for (int i = 0; i < NUMBER_OF_ATTRIBUTES_IN_PATIENT; i++) {
            featureValuesOccurrence.get(NO_RECURRENCE_EVENT_INDEX).add(new HashMap<>());
            featureValuesOccurrence.get(RECURRENCE_EVENT_INDEX).add(new HashMap<>());
        }

        for (int i = 0; i < numberOfPatients; i++) {
            Patient currentPatient = patientCollectionList.get(i);
            if (currentPatient.recurrenceEvents().equalsIgnoreCase(RECURRENCE_EVENT_STRING)) {
                ++numberOfRecurrenceTarget;
                for (short j = 0; j < NUMBER_OF_ATTRIBUTES_IN_PATIENT; j++) {
//                    featureValuesOccurrence.get(RECURRENCE_EVENT_INDEX).get(j)
//                        .putIfAbsent(currentPatient.getNumberedFeature(j),
//                            featureValuesOccurrence.get(RECURRENCE_EVENT_INDEX).get(j)
//                                .get(currentPatient.getNumberedFeature(j)));
                    featureValuesOccurrence.get(RECURRENCE_EVENT_INDEX).get(j)
                            .putIfAbsent(currentPatient.getNumberedFeature(j), 0);
                    featureValuesOccurrence.get(RECURRENCE_EVENT_INDEX).get(j)
                        .merge(currentPatient.getNumberedFeature(j), 1, Integer::sum);
                }
            } else if (currentPatient.recurrenceEvents().equalsIgnoreCase(NO_RECURRENCE_EVENT_STRING)) {
                ++numberOfNoRecurrenceTarget;
                for (short j = 0; j < NUMBER_OF_ATTRIBUTES_IN_PATIENT; j++) {
                    featureValuesOccurrence.get(NO_RECURRENCE_EVENT_INDEX).get(j)
                        .putIfAbsent(currentPatient.getNumberedFeature(j), 0);
                    featureValuesOccurrence.get(NO_RECURRENCE_EVENT_INDEX).get(j)
                        .merge(currentPatient.getNumberedFeature(j), 1, Integer::sum);
                }
            }
        }

        return featureValuesOccurrence;
    }

    public double calculateEntropy(int firstValue, int secondValue) {
        if (firstValue == 0 || secondValue == 0) {
            return 0;
        }

        if (firstValue == secondValue) {
            return 1;
        }

        double sumOfValues = firstValue + secondValue;
        double firstValueProbability = firstValue / sumOfValues;
        double secondValueProbability = secondValue / sumOfValues;

        return -firstValueProbability * (Math.log(firstValueProbability) / Math.log(LOG_TWO_BASE))
            -secondValueProbability * (Math.log(secondValueProbability) / Math.log(LOG_TWO_BASE));
    }

    private double calculateEntropyOfTargetWithAttribute(int attributePosition) {
        double result = 0;

        for (Map.Entry<String, Integer> currentAttributeValueRecurrenceEntry :
            this.featureValuesOccurrence.get(RECURRENCE_EVENT_INDEX).get(attributePosition).entrySet()) {
            String currentAttributeValueName = currentAttributeValueRecurrenceEntry.getKey();
            int currentAttributeValueRecurrenceCounter = currentAttributeValueRecurrenceEntry.getValue();

            Integer currentAttributeValueNoRecurrenceCounter;
            if (this.featureValuesOccurrence.get(NO_RECURRENCE_EVENT_INDEX)
                .get(attributePosition) != null) {
                currentAttributeValueNoRecurrenceCounter = this.featureValuesOccurrence.get(NO_RECURRENCE_EVENT_INDEX)
                    .get(attributePosition).get(currentAttributeValueName);
            } else {
                currentAttributeValueNoRecurrenceCounter = 0;
            }

            if (currentAttributeValueNoRecurrenceCounter == null) {
                currentAttributeValueNoRecurrenceCounter = 0;
            }

            result += ((currentAttributeValueRecurrenceCounter + currentAttributeValueNoRecurrenceCounter)
                / (double) numberOfPatients)
                * calculateEntropy(currentAttributeValueRecurrenceCounter, currentAttributeValueNoRecurrenceCounter);
        }

        return result;
    }

    private double calculateInformationGain(int attributePosition) {
        return calculateEntropy(numberOfRecurrenceTarget, numberOfNoRecurrenceTarget) -
            calculateEntropyOfTargetWithAttribute(attributePosition);
    }

    public int findBestDecisionAttribute(Set<Integer> visitedFeaturePositionsSet) {
        double maxInformationGain = Double.MIN_VALUE;
        int attributeWithMaxInformationGainPosition = 0;

        for (int i = 1; i < NUMBER_OF_ATTRIBUTES_IN_PATIENT && !visitedFeaturePositionsSet.contains(i); i++) {
            double currentAttributeInformationGain = calculateInformationGain(i);

            if (currentAttributeInformationGain > maxInformationGain) {
                maxInformationGain = currentAttributeInformationGain;
                attributeWithMaxInformationGainPosition = i;
            }
        }

        return attributeWithMaxInformationGainPosition;
    }

    public PatientCollection findPatientsWithGivenAttributeValue(int attributePosition, String attributeValue) {
        List<Patient> foundPatientsList = new ArrayList<>();
        for (Patient currentPatient : this.patientCollectionList) {
            if (currentPatient.getNumberedFeature(attributePosition).equalsIgnoreCase(attributeValue)) {
                foundPatientsList.add(currentPatient);
            }
        }

        return new PatientCollection(foundPatientsList);
    }

    public String findRecurrenceTargetPrevalence(TreeNode treeNode) {
        if (numberOfRecurrenceTarget > numberOfNoRecurrenceTarget) {
            return RECURRENCE_EVENT_STRING;
        } else if (numberOfNoRecurrenceTarget > numberOfRecurrenceTarget) {
            return NO_RECURRENCE_EVENT_STRING;
        }

        TreeNode parentTreeNode = treeNode.getParentTreeNode();
        if (parentTreeNode.isRecurrenceEvent()) {
            return RECURRENCE_EVENT_STRING;
        }
        return NO_RECURRENCE_EVENT_STRING;
    }

    public int getNumberOfNoRecurrenceTarget() {
        return numberOfNoRecurrenceTarget;
    }

    public int getNumberOfRecurrenceTarget() {
        return numberOfRecurrenceTarget;
    }

    public int getNumberOfPatients() {
        return numberOfPatients;
    }

    public List<Set<String>> getPatientAttributeValuesList() {
        return patientAttributeValuesList;
    }
}
