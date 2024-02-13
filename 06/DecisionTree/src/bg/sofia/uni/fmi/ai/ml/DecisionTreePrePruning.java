package bg.sofia.uni.fmi.ai.ml;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DecisionTreePrePruning extends DecisionTree {
    private final static int MAX_NUMBER_OF_VISITED_FEATURES_BEFORE_PRUNING = 9;
    private final static int K_MINIMUM_NUMBER_OF_PATIENTS_IN_TRAIN_SET = 20;

    public DecisionTreePrePruning(PatientCollection patientCollection, List<Set<String>> patientAttributeValuesList) {
        constructAttributeTreeNode(patientCollection, null, patientAttributeValuesList);
    }

    @Override
    public void constructAttributeValueTreeNode(int attributePosition, String attributeValue, TreeNode parentTreeNode,
                                                PatientCollection patientCollection,
                                                List<Set<String>> patientAttributeValuesList) {
        // PrePruning process - turning into leaf node
        if (isEntropyZero(patientCollection) ||
            parentTreeNode.getVisitedFeaturePositionsSet().size() == MAX_NUMBER_OF_VISITED_FEATURES_BEFORE_PRUNING ||
            patientCollection.getNumberOfPatients() < K_MINIMUM_NUMBER_OF_PATIENTS_IN_TRAIN_SET) {
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
        childTreeNode.setVisitedFeaturePositionsSet(new HashSet<>(parentTreeNode.getVisitedFeaturePositionsSet()));
        childTreeNode.setParentTreeNode(parentTreeNode);
        childTreeNode.setRecurrenceEvent(patientCollection.isRecurrenceTargetPrevalence(childTreeNode));
        parentTreeNode.getChildrenTreeNodesList().add(childTreeNode);

        PatientCollection updatedPatientCollection = patientCollection
            .findPatientsWithGivenAttributeValue(attributePosition, attributeValue);

        constructAttributeTreeNode(updatedPatientCollection, childTreeNode, patientAttributeValuesList);
    }
}