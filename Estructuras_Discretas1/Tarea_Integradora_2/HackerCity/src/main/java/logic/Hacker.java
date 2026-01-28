package logic;

import structure.Node;

import java.util.ArrayList;
import java.util.List;

public class Hacker {
    private Node currentPosition;
    private List<Node> collectedKeys;
    private List<Node> visitedNodes;

    public Hacker(Node startPosition){
        currentPosition = startPosition;
        collectedKeys = new ArrayList<Node>();
        visitedNodes = new ArrayList<Node>();
        visitedNodes.add(startPosition);

    }

    public void setCurrentPosition(Node currentPosition) {
        this.currentPosition = currentPosition;
    }

    public void setCollectedKeys(List<Node> collectedKeys) {
        this.collectedKeys = collectedKeys;
    }

    public void setVisitedNodes(List<Node> visitedNodes) {
        this.visitedNodes = visitedNodes;
    }

    public Node getCurrentPosition() {
        return currentPosition;
    }

    public List<Node> getCollectedKeys() {
        return collectedKeys;
    }

    public List<Node> getVisitedNodes() {
        return visitedNodes;
    }
    public void moveTo(Node nextNode){
        if(nextNode == null){
            return;
        }
        currentPosition = nextNode;
        visitedNodes.add(nextNode);
        if(hasKey(nextNode) && !collectedKeys.contains(nextNode)){
            collectedKeys.add(nextNode);
        }
    }
    public boolean hasKey(Node node){
        return node.getId().toLowerCase().contains("key");
    }
    public void reset(Node newStart){
        this.currentPosition = newStart;
        this.collectedKeys.clear();
        this.visitedNodes.clear();
        this.visitedNodes.add(newStart);
    }
}
