package bg.sofia.uni.fmi.ai.ml;

import com.sun.source.tree.Tree;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public abstract class DecisionTree {
    protected final static String RECURRENCE_EVENT_STRING = "recurrence-events";
    private final static String NO_RECURRENCE_EVENT_STRING = "no-recurrence-events";

    private TreeNode rootTreeNode = new TreeNode();

    protected void constructAttributeTreeNode(PatientCollection patientCollection, TreeNode parentTreeNode,
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
        childTreeNode.setRecurrenceEvent(patientCollection.findRecurrenceTargetPrevalence(childTreeNode)
            .equalsIgnoreCase(RECURRENCE_EVENT_STRING));

        Set<String> currentAttributeValuesSet = patientAttributeValuesList.get(bestAttributePosition);
        for (String currentAttributeValue : currentAttributeValuesSet) {
            constructAttributeValueTreeNode(bestAttributePosition, currentAttributeValue, childTreeNode,
                patientCollection, patientAttributeValuesList);
        }
    }

    public abstract void constructAttributeValueTreeNode(int attributePosition, String attributeValue,
                                                         TreeNode parentTreeNode, PatientCollection patientCollection,
                                                         List<Set<String>> patientAttributeValuesList);

    public String findPredictedResultFromDecisionTree(Patient patientToTest) {
        TreeNode currentTreeNode = this.rootTreeNode;
        while (true) {
            int currentAttributePosition = currentTreeNode.getRepresentedFeaturePosition();
            String currentAttributeValue = patientToTest.getNumberedFeature(currentAttributePosition);

            for (int i = 0; i < currentTreeNode.getChildrenTreeNodesList().size(); i++) {
                TreeNode currentChild = currentTreeNode.getChildrenTreeNodesList().get(i);
                if (currentChild.getRepresentedFeatureValue().equalsIgnoreCase(currentAttributeValue)) {
                    if (currentChild.isTargetLeafNode()) {
                        // TODO Instead of isRecurrence I return the feature value
                        return currentChild.getRepresentedFeatureValue();
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

    public void printDecisionTree() {
        Queue<TreeNode> queue = new ArrayDeque<>();
        queue.add(rootTreeNode);
        TreeNode currentNode;

        while (!queue.isEmpty()) {
            currentNode = queue.element();
            queue.remove();
            System.out.println("Node: " + currentNode.getRepresentedFeatureValue());
            if (currentNode.isTargetLeafNode()) {
                System.out.println("Leaf: " + currentNode.isRecurrenceEvent());
            }

            for (int i = 0; i < currentNode.getChildrenTreeNodesList().size(); i++) {
                queue.add(currentNode.getChildrenTreeNodesList().get(i));
            }
        }
    }



}
