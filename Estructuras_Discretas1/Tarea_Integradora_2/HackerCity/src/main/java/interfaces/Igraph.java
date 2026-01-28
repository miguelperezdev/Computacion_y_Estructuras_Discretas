package interfaces;

import structure.Edge;
import structure.Node;

import java.util.List;

public interface Igraph {
    void addNode(Node node);
    void addEdge(Edge edge);
    List<Node> getNodes();
    List<Node> getNeighbors(Node node);
    double getEdgeWeight(Node source, Node destination);
}
