package structure;

import interfaces.Igraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Graph implements Igraph {
    private List<Node> nodes;
    private List<Edge> edges;
    private Map<Node, List<Edge>> adjacencyList;

    public Graph() {
        nodes = new ArrayList<>();
        edges = new ArrayList<>();
        adjacencyList = new HashMap<>();
    }

    @Override
    public void addNode(Node node) {
        if (!nodes.contains(node)) {
            nodes.add(node);
            adjacencyList.put(node, new ArrayList<>());
        }
    }

    @Override
    public void addEdge(Edge edge) {
        Node source = edge.getSource();
        Node destination = edge.getDestination();
        addNode(source);
        addNode(destination);
        edges.add(edge);
        adjacencyList.get(source).add(edge);
    }

    @Override
    public List<Node> getNodes() {
        return new ArrayList<>(nodes);
    }

    public List<Edge> getEdgesFrom(Node source) {
        return new ArrayList<>(adjacencyList.getOrDefault(source, new ArrayList<>()));
    }

    @Override
    public List<Node> getNeighbors(Node node) {
        List<Node> neighbors = new ArrayList<>();
        for (Edge edge : getEdgesFrom(node)) {
            neighbors.add(edge.getDestination());
        }
        return neighbors;
    }

    @Override
    public double getEdgeWeight(Node source, Node destination) {
        for (Edge edge : getEdgesFrom(source)) {
            if (edge.getDestination().equals(destination)) {
                return edge.getWeight();
            }
        }
        return Double.POSITIVE_INFINITY;
    }
}
