package bg.sofia.uni.fmi.ai.ml;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DecisionTreePrePruning extends DecisionTree {

    private final static int K = 50;

    public DecisionTreePrePruning(PatientCollection patientCollection, List<Set<String>> patientAttributeValuesList) {
        constructAttributeTreeNode(patientCollection, null, patientAttributeValuesList);
    }

    @Override
    public void constructAttributeValueTreeNode(int attributePosition, String attributeValue, TreeNode parentTreeNode,
                                                PatientCollection patientCollection,
                                                List<Set<String>> patientAttributeValuesList) {
        if (isEntropyZero(patientCollection) || parentTreeNode.getVisitedFeaturePositionsSet().size() == 9
            ||patientCollection.getNumberOfPatients() < K) {
            TreeNode leafTreeNode = new TreeNode();
            leafTreeNode.setRepresentedFeatureValue(attributeValue);
            leafTreeNode.setTargetLeafNode(true);
            leafTreeNode.setParentTreeNode(parentTreeNode);
            leafTreeNode.setRecurrenceEvent(patientCollection.findRecurrenceTargetPrevalence(leafTreeNode));
            parentTreeNode.getChildrenTreeNodesList().add(leafTreeNode);
            return;
        }

        TreeNode childTreeNode = new TreeNode();
        childTreeNode.setRepresentedFeaturePosition(attributePosition);
        childTreeNode.setRepresentedFeatureValue(attributeValue);
        childTreeNode.setVisitedFeaturePositionsSet(new HashSet<>(parentTreeNode.getVisitedFeaturePositionsSet()));
        childTreeNode.setParentTreeNode(parentTreeNode);
        childTreeNode.setRecurrenceEvent(patientCollection.findRecurrenceTargetPrevalence(childTreeNode));
        parentTreeNode.getChildrenTreeNodesList().add(childTreeNode);
        PatientCollection updatedPatientCollection = patientCollection
            .findPatientsWithGivenAttributeValue(attributePosition, attributeValue);
        constructAttributeTreeNode(updatedPatientCollection, childTreeNode, patientAttributeValuesList);
    }
}
