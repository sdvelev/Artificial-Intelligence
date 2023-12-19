package bg.sofia.uni.fmi.ai.ml;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DecisionTreePostPruning extends DecisionTree {
    private final static int FOLD_CROSS_VALIDATION_COEFFICIENT = 10;
    private final static int MAX_NUMBER_OF_VISITED_FEATURES_BEFORE_PRUNING = 9;

    private List<Patient> trainList;
    private List<Patient> validationList;

    public DecisionTreePostPruning(PatientCollection patientCollection, List<Patient> validationList, List<Set<String>> patientAttributeValuesList) {
        this.trainList = patientCollection.getPatientCollectionList();
        this.validationList = validationList;

        //formTrainAndValidationLists(patientCollection);
        constructAttributeTreeNode(patientCollection, null, patientAttributeValuesList);

        for (Patient currentPatient : validationList) {
            addErrorsEstimationOnTreeNodesConcerned(currentPatient);
        }

        executePostPruningProcedure(super.getRootTreeNode());
    }

    private void formTrainAndValidationLists(PatientCollection patientCollection) {
        List<Patient> currentTrainList = patientCollection.getPatientCollectionList();

        Collections.shuffle(currentTrainList);

        for (int i = 0; i < currentTrainList.size(); i++) {
            if (i % FOLD_CROSS_VALIDATION_COEFFICIENT == 0) {
                validationList.add(currentTrainList.get(i));
            } else {
                trainList.add(currentTrainList.get(i));
            }
        }
    }

    @Override
    public void constructAttributeValueTreeNode(int attributePosition, String attributeValue, TreeNode parentTreeNode,
                                                PatientCollection patientCollection,
                                                List<Set<String>> patientAttributeValuesList) {
        if (isEntropyZero(patientCollection) ||
            parentTreeNode.getVisitedFeaturePositionsSet().size() == MAX_NUMBER_OF_VISITED_FEATURES_BEFORE_PRUNING) {
            TreeNode leafTreeNode = new TreeNode();
            leafTreeNode.setRepresentedFeatureValue(attributeValue);
            leafTreeNode.setTargetLeafNode(true);
            leafTreeNode.setParentTreeNode(parentTreeNode);
            leafTreeNode.setRecurrenceEvent(patientCollection.isRecurrenceTargetPrevalence(leafTreeNode));
            parentTreeNode.getChildrenTreeNodesList().add(leafTreeNode);
            return;
        }

        TreeNode childTreeNode = new TreeNode();
        childTreeNode.setRepresentedFeaturePosition(attributePosition);
        childTreeNode.setRepresentedFeatureValue(attributeValue);
        childTreeNode.setParentTreeNode(parentTreeNode);
        childTreeNode.setVisitedFeaturePositionsSet(new HashSet<>(parentTreeNode.getVisitedFeaturePositionsSet()));

        // TODO Missing in original
        // childTreeNode.setRecurrenceEvent(patientCollection.isRecurrenceTargetPrevalence(childTreeNode));

        parentTreeNode.getChildrenTreeNodesList().add(childTreeNode);

        PatientCollection updatedPatientCollection = patientCollection
            .findPatientsWithGivenAttributeValue(attributePosition, attributeValue);

        constructAttributeTreeNode(updatedPatientCollection, childTreeNode, patientAttributeValuesList);
    }

    private void addErrorsEstimationOnTreeNodesConcerned(Patient currentPatient) {
        TreeNode currentTreeNode = super.getRootTreeNode();

        while (true) {
            int currentAttributePosition = currentTreeNode.getRepresentedFeaturePosition();
            String currentAttributeValue = currentPatient.getNumberedFeature(currentAttributePosition);

            for (int i = 0; i < currentTreeNode.getChildrenTreeNodesList().size(); i++) {
                TreeNode currentChild = currentTreeNode.getChildrenTreeNodesList().get(i);
                if (currentChild.getRepresentedFeatureValue().equalsIgnoreCase(currentAttributeValue)) {
                    if ((currentPatient.getNumberedFeature(0).equalsIgnoreCase(RECURRENCE_EVENT_STRING) &&
                        !currentChild.isRecurrenceEvent()) ||
                        (currentPatient.getNumberedFeature(0).equalsIgnoreCase(NO_RECURRENCE_EVENT_STRING) &&
                            currentChild.isRecurrenceEvent())) {
                        currentChild.incrementNumberOfErrorsForPostPruning();
                    }

                    if (currentChild.isTargetLeafNode()) {
                        return;
                    }

                    currentTreeNode = currentChild.getChildrenTreeNodesList().get(0);
                    break;
                }
            }
        }
    }

    private void executePostPruningProcedure(TreeNode currentTreeNode) {
        if (currentTreeNode.isTargetLeafNode()) {
            int currentLeafTreeNodeNumberOfErrors = currentTreeNode.getNumberOfErrorsForPostPruning();

            if (currentTreeNode.getParentTreeNode() != null &&
            currentTreeNode.getParentTreeNode().getParentTreeNode() != null) {
                int parentTreeNodeNumberOfErrors = currentTreeNode.getParentTreeNode()
                    .getParentTreeNode().getNumberOfErrorsForPostPruning();
                if (currentLeafTreeNodeNumberOfErrors > parentTreeNodeNumberOfErrors) {
                    currentTreeNode.getParentTreeNode().getParentTreeNode().setTargetLeafNode(true);
                }
            }

            return;
        }

        for (TreeNode currentChildTreeNode : currentTreeNode.getChildrenTreeNodesList()) {
            executePostPruningProcedure(currentChildTreeNode);
        }
    }
}