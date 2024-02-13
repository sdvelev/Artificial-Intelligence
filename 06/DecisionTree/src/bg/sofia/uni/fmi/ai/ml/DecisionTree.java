package bg.sofia.uni.fmi.ai.ml;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class DecisionTree {
    protected final static String RECURRENCE_EVENT_STRING = "recurrence-events";
    protected final static String NO_RECURRENCE_EVENT_STRING = "no-recurrence-events";

    private TreeNode rootTreeNode = new TreeNode();

    public void constructAttributeTreeNode(PatientCollection patientCollection, TreeNode parentTreeNode,
                                            List<Set<String>> patientAttributeValuesList) {
        TreeNode childTreeNode;

        if (parentTreeNode == null) {
            rootTreeNode = new TreeNode();
            childTreeNode = rootTreeNode;
        } else {
            childTreeNode = new TreeNode();
            childTreeNode.setVisitedFeaturePositionsSet(parentTreeNode.getVisitedFeaturePositionsSet());
            parentTreeNode.getChildrenTreeNodesList().add(childTreeNode);
        }

        int bestAttributePosition = patientCollection
            .findBestDecisionAttribute(childTreeNode.getVisitedFeaturePositionsSet());

        childTreeNode.setRepresentedFeaturePosition(bestAttributePosition);
        childTreeNode.getVisitedFeaturePositionsSet().add(bestAttributePosition);

        childTreeNode.setParentTreeNode(parentTreeNode);
        childTreeNode.setRecurrenceEvent(patientCollection.isRecurrenceTargetPrevalence(childTreeNode));

        Set<String> currentAttributeValuesSet =  patientAttributeValuesList.get(bestAttributePosition);
        for (String currentAttributeValue : currentAttributeValuesSet) {
            constructAttributeValueTreeNode(bestAttributePosition, currentAttributeValue, childTreeNode,
                patientCollection, patientAttributeValuesList);
        }
    }

    public abstract void constructAttributeValueTreeNode(int attributePosition, String attributeValue,
                                                         TreeNode parentTreeNode, PatientCollection patientCollection,
                                                         List<Set<String>> patientAttributeValuesList);

    public boolean isCorrectPredictedResultByTraversingDecisionTree(Patient patientToTest) {
        TreeNode currentTreeNode = this.rootTreeNode;

        while (true) {
            int currentAttributePosition = currentTreeNode.getRepresentedFeaturePosition();
            String currentAttributeValue = patientToTest.getNumberedFeature(currentAttributePosition);

            for (int i = 0; i < currentTreeNode.getChildrenTreeNodesList().size(); i++) {
                TreeNode currentChild = currentTreeNode.getChildrenTreeNodesList().get(i);
                if (currentChild.getRepresentedFeatureValue().equalsIgnoreCase(currentAttributeValue)) {
                    if (currentChild.isTargetLeafNode()) {
                        if (currentChild.isRecurrenceEvent()) {
                            return patientToTest.getNumberedFeature(0).equals(RECURRENCE_EVENT_STRING);
                        } else {
                            return patientToTest.getNumberedFeature(0).equals(NO_RECURRENCE_EVENT_STRING);
                        }
                    }

                    currentTreeNode = currentChild.getChildrenTreeNodesList().get(0);
                    break;
                }
            }
        }
    }

    protected boolean isEntropyZero(PatientCollection patientCollection) {
        int compareResult = Double.compare(patientCollection.calculateEntropy(patientCollection.getNumberOfRecurrenceTarget(),
            patientCollection.getNumberOfNoRecurrenceTarget()),0);

        return compareResult == 0;
    }

    protected TreeNode getRootTreeNode() {
        return rootTreeNode;
    }
}