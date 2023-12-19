package bg.sofia.uni.fmi.ai.ml;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class TreeNode {
    private final static int UNKNOWN = -1;
    private final static String EMPTY_STRING = "";

    private int representedFeaturePosition;
    private String representedFeatureValue;
    private boolean isRecurrenceEvent;
    private boolean isTargetLeafNode;
    private TreeNode parentTreeNode;
    private List<TreeNode> childrenTreeNodesList;
    private Set<Integer> visitedFeaturePositionsSet;
    private int numberOfErrorsForPostPruning;

    public TreeNode() {
        this.representedFeaturePosition = UNKNOWN;
        this.representedFeatureValue = EMPTY_STRING;
        this.isRecurrenceEvent = false;
        this.isTargetLeafNode = false;
        this.parentTreeNode = null;
        this.childrenTreeNodesList = new ArrayList<>();
        this.visitedFeaturePositionsSet = new HashSet<>();
        this.numberOfErrorsForPostPruning = 0;

    }

    public TreeNode(int representedFeaturePosition, String representedFeatureValue, boolean isRecurrenceEvent,
                    boolean isTargetLeafNode, TreeNode parentTreeNode, List<TreeNode> childrenTreeNodesList,
                    Set<Integer> visitedFeaturePositionsSet, int numberOfErrorsForPostPruning) {
        this.representedFeaturePosition = representedFeaturePosition;
        this.representedFeatureValue = representedFeatureValue;
        this.isRecurrenceEvent = isRecurrenceEvent;
        this.isTargetLeafNode = isTargetLeafNode;
        this.parentTreeNode = parentTreeNode;
        this.childrenTreeNodesList = childrenTreeNodesList;
        this.visitedFeaturePositionsSet = visitedFeaturePositionsSet;
        this.numberOfErrorsForPostPruning = numberOfErrorsForPostPruning;
    }

    public TreeNode getParentTreeNode() {
        return parentTreeNode;
    }

    public boolean isRecurrenceEvent() {
        return isRecurrenceEvent;
    }

    public int getRepresentedFeaturePosition() {
        return representedFeaturePosition;
    }

    public void setRepresentedFeaturePosition(int representedFeaturePosition) {
        this.representedFeaturePosition = representedFeaturePosition;
    }

    public String getRepresentedFeatureValue() {
        return representedFeatureValue;
    }

    public void setRepresentedFeatureValue(String representedFeatureValue) {
        this.representedFeatureValue = representedFeatureValue;
    }

    public void setRecurrenceEvent(boolean recurrenceEvent) {
        isRecurrenceEvent = recurrenceEvent;
    }

    public boolean isTargetLeafNode() {
        return isTargetLeafNode;
    }

    public void setTargetLeafNode(boolean targetLeafNode) {
        isTargetLeafNode = targetLeafNode;
    }

    public void setParentTreeNode(TreeNode parentTreeNode) {
        this.parentTreeNode = parentTreeNode;
    }

    public List<TreeNode> getChildrenTreeNodesList() {
        return childrenTreeNodesList;
    }

    public void setChildrenTreeNodesList(List<TreeNode> childrenTreeNodesList) {
        this.childrenTreeNodesList = childrenTreeNodesList;
    }

    public Set<Integer> getVisitedFeaturePositionsSet() {
        return visitedFeaturePositionsSet;
    }

    public void setVisitedFeaturePositionsSet(Set<Integer> visitedFeaturePositionsSet) {
        this.visitedFeaturePositionsSet = visitedFeaturePositionsSet;
    }

    public int getNumberOfErrorsForPostPruning() {
        return numberOfErrorsForPostPruning;
    }

    public void incrementNumberOfErrorsForPostPruning() {
        ++numberOfErrorsForPostPruning;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TreeNode treeNode = (TreeNode) o;
        return representedFeaturePosition == treeNode.representedFeaturePosition &&
            isRecurrenceEvent == treeNode.isRecurrenceEvent && isTargetLeafNode == treeNode.isTargetLeafNode &&
            Objects.equals(representedFeatureValue, treeNode.representedFeatureValue) &&
            Objects.equals(parentTreeNode, treeNode.parentTreeNode) &&
            Objects.equals(childrenTreeNodesList, treeNode.childrenTreeNodesList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(representedFeaturePosition, representedFeatureValue, isRecurrenceEvent, isTargetLeafNode,
            parentTreeNode, childrenTreeNodesList);
    }
}