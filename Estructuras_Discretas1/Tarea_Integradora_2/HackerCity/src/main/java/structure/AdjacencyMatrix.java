package structure;

import interfaces.Igraph;

import java.util.ArrayList;
import java.util.List;

public class AdjacencyMatrix implements Igraph {
    private static final int SIZE = 80;
    private int[][] adjacencyMatrix;
    private List<Node> nodes;

    public AdjacencyMatrix() {
        this.adjacencyMatrix = new int[SIZE][SIZE];
        this.nodes = new ArrayList<>(SIZE);
        initializeMatrix();
    }

    public int[][] getAdjacencyMatrix() {
        return adjacencyMatrix;
    }

    private void initializeMatrix() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                adjacencyMatrix[i][j] = (i == j) ? 0 : Integer.MAX_VALUE;
            }
        }
    }

    @Override
    public void addNode(Node node) {
        if (nodes.size() < SIZE && !nodes.contains(node)) {
            nodes.add(node);
        }
    }

    @Override
    public void addEdge(Edge edge) {
        Node source = edge.getSource();
        Node destination = edge.getDestination();

        int i = nodes.indexOf(source);
        int j = nodes.indexOf(destination);

        if (i == -1 || j == -1) {
            throw new IllegalArgumentException("Uno de los nodos no está registrado.");
        }

        adjacencyMatrix[i][j] = edge.getWeight();
        if (!edge.isDirected()) {
            adjacencyMatrix[j][i] = edge.getWeight();
        }
    }

    @Override
    public List<Node> getNodes() {
        return new ArrayList<>(nodes);
    }

    @Override
    public List<Node> getNeighbors(Node node) {
        List<Node> neighbors = new ArrayList<>();
        int idx = getNodeIndex(node);
        if (idx == -1) return neighbors;

        for (int j = 0; j < nodes.size(); j++) {
            if (adjacencyMatrix[idx][j] != Integer.MAX_VALUE && adjacencyMatrix[idx][j] != 0) {
                neighbors.add(nodes.get(j));
            }
        }
        return neighbors;
    }

    @Override
    public double getEdgeWeight(Node source, Node destination) {
        int i = getNodeIndex(source);
        int j = getNodeIndex(destination);

        if (i == -1 || j == -1) return Double.POSITIVE_INFINITY;

        return adjacencyMatrix[i][j];
    }

    public int getNodeIndex(Node node) {
        return nodes.indexOf(node);
    }
    public void clear() {
        nodes.clear();
        initializeMatrix();
    }

}
