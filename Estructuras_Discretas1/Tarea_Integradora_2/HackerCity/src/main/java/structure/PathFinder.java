package structure;

import java.util.*;

public class PathFinder {
    public static List<Node>  dijkstraPath(Graph graph, Node startNode, Node endNode){
        if(startNode == null || endNode == null){
            throw new IllegalArgumentException("Alguno de los nodos o los dos estan vacios");
        }
        Map<Node, Integer> weight =  new HashMap<>();
        Map<Node, Node> previous =   new HashMap<>();
        Set<Node> visited = new HashSet<>();
        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingInt(weight :: get));
        for(Node node : graph.getNodes()){
            weight.put(node, Integer.MAX_VALUE);
        }
        weight.put(startNode, 0);
        queue.add(startNode);

        while(!queue.isEmpty()){
            Node current = queue.poll();
            if(!visited.contains(current)){
                visited.add(current);
                List<Edge> edges = graph.getEdgesFrom(current);
                for(Edge edge : edges){
                    Node neighbor = edge.getDestination();
                    if(!visited.contains(neighbor)){
                        int newDistance = weight.get(current) + edge.getWeight();
                        int currentDistance = weight.get(neighbor);

                        if(newDistance < currentDistance){
                            weight.put(neighbor, newDistance);
                            previous.put(neighbor, current);
                            queue.add(neighbor);
                        }

                    }
                }
            }
        }

        List<Node> path = new ArrayList<>();
        Node current = endNode;
        while(current != null){
            path.add(0, current);
            current = previous.get(current);

        }
        if(path.isEmpty() || !path.get(0).equals(startNode)){
            return Collections.emptyList();
        }
        return path;

    }
    public static List<Node> bfsPath(Graph graph, Node startNode, Node endNode){
        if(startNode == null || endNode == null){
            throw new IllegalArgumentException("Alguno de los nodos o los dos estan vacios");
        }
        Queue<Node> queue = new LinkedList<>();
        Map<Node, Node> cameFrom = new HashMap<>();
        Set<Node> visited = new HashSet<>();
        queue.add(startNode);
        visited.add(startNode);
        while(!queue.isEmpty()){
            Node current = queue.poll();
            if(current.equals(endNode)){
                return reconstructPath(cameFrom, endNode);
            }
            for(Node neighbor : graph.getNeighbors(current)){
                if(!visited.contains(neighbor)){
                    visited.add(neighbor);
                    cameFrom.put(neighbor, current);
                    queue.add(neighbor);
                }
            }
        }
        return Collections.emptyList();
    }

    public static List<Node> reconstructPath(Map<Node, Node> cameFrom, Node endNode){
        List<Node> path = new ArrayList<>();
        Node current = endNode;
        while(current != null){
            path.add(0, current);
            current = cameFrom.get(current);

        }
        return path;

    }
    public static List<Node> dijkstraPathMatrix(AdjacencyMatrix matrix, Node start, Node target) {
        int n = matrix.getNodes().size();
        int[][] weights = matrix.getAdjacencyMatrix(); // matriz de adyacencia de pesos

        double[] dist = new double[n];
        boolean[] visited = new boolean[n];
        Node[] prev = new Node[n];

        // Inicialización
        for (int i = 0; i < n; i++) {
            dist[i] = Double.MAX_VALUE;
            prev[i] = null;
        }

        int startIndex = matrix.getNodes().indexOf(start);
        dist[startIndex] = 0;

        for (int i = 0; i < n; i++) {
            // Encontrar el nodo no visitado con menor distancia
            int u = -1;
            for (int j = 0; j < n; j++) {
                if (!visited[j] && (u == -1 || dist[j] < dist[u])) {
                    u = j;
                }
            }

            if (dist[u] == Double.MAX_VALUE) break;
            visited[u] = true;

            for (int v = 0; v < n; v++) {
                if (weights[u][v] > 0 && !visited[v]) {
                    double alt = dist[u] + weights[u][v];
                    if (alt < dist[v]) {
                        dist[v] = alt;
                        prev[v] = matrix.getNodes().get(u);
                    }
                }
            }
        }

        // Reconstruir el camino
        List<Node> path = new ArrayList<>();
        int targetIndex = matrix.getNodes().indexOf(target);
        for (Node at = matrix.getNodes().get(targetIndex); at != null; at = prev[matrix.getNodes().indexOf(at)]) {
            path.add(0, at);
        }

        if (path.isEmpty() || !path.get(0).equals(start)) {
            return Collections.emptyList(); // no hay camino
        }

        return path;
    }


}
