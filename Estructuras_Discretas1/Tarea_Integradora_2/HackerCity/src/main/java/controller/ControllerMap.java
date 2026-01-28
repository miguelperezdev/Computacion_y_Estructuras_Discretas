package controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;
import logic.Hacker;
import map.GeneratorGraphMap;
import structure.*;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class ControllerMap {

    private static final double MOVE_SPEED = 20;
    private static final double VIEWPORT_WIDTH = 1280;
    private static final double VIEWPORT_HEIGHT = 720;
    @FXML
    private AnchorPane rootPane;
    private Canvas canvas;
    private GraphicsContext gc;
    private Graph cityGraph;
    private AdjacencyMatrix adjacencyMatrix;
    private Image mapImage;
    private double cameraX = 0;
    private double cameraY = 0;
    private Hacker hacker;
    private List<Node> hackerPath;
    private int hackerStep = 0;

    private Image[] hackerFrames;
    private int currentFrame = 0;

    private GraphStructureType currentStructure = GraphStructureType.ADJACENCY_LIST;
    private boolean useBFS = false;

    private int totalKeys = 0;
    private int totalPoints = 0;

    @FXML
    public void initialize() {
        showStartDialog();
        setupCanvas();
        loadHackerFrames();
        initializeGraph();
        initializeHacker();
        setupEvents();
        startRenderLoop();
    }


    private void setupCanvas() {
        mapImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/HackerCityMap.png")));
        canvas = new Canvas(rootPane.getWidth(), rootPane.getHeight());
        gc = canvas.getGraphicsContext2D();
        rootPane.getChildren().add(canvas);
        canvas.setFocusTraversable(true);

        // Escuchar cambios de tamaño en rootPane para adaptar el canvas
        rootPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            canvas.setWidth(newVal.doubleValue());
            draw();
        });

        rootPane.heightProperty().addListener((obs, oldVal, newVal) -> {
            canvas.setHeight(newVal.doubleValue());
            draw();
        });
    }

    private void setupEvents() {
        canvas.setOnKeyPressed(this::handleKeyPressed);
        canvas.setOnMouseClicked(mouseEvent -> {
            Node startNode = hacker.getCurrentPosition();
            Node targetNode = findClosestNode(mouseEvent.getX(), mouseEvent.getY());
            if (targetNode != null && targetNode.isWalkable() && !startNode.equals(targetNode)) {
                hackerPath = resolvePath(startNode, targetNode);
                hackerStep = 0;
                startHackerMovementThread();
            }
        });
        canvas.requestFocus();
    }

    private List<Node> resolvePath(Node start, Node end) {
        if (useBFS) {
            return PathFinder.bfsPath(cityGraph, start, end);
        } else {
            return (currentStructure == GraphStructureType.ADJACENCY_LIST)
                    ? PathFinder.dijkstraPath(cityGraph, start, end)
                    : PathFinder.dijkstraPathMatrix(adjacencyMatrix, start, end);
        }
    }

    private void startRenderLoop() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(50), e -> draw()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void loadHackerFrames() {
        hackerFrames = new Image[6];
        for (int i = 0; i < 6; i++) {
            hackerFrames[i] = new Image(getClass().getResourceAsStream("/images/Hacker/Hacker" + (i + 1) + ".png"));
        }
    }

    private void initializeGraph() {
        GeneratorGraphMap generator = new GeneratorGraphMap();
        cityGraph = new Graph();
        generator.createEdges(cityGraph);
        adjacencyMatrix = new AdjacencyMatrix();
        for (Node node : cityGraph.getNodes()) adjacencyMatrix.addNode(node);
        for (Node node : cityGraph.getNodes()) {
            for (Node neighbor : cityGraph.getNeighbors(node)) {
                int weight = (int) cityGraph.getEdgeWeight(node, neighbor);
                adjacencyMatrix.addEdge(new Edge(node, neighbor, weight, false));
            }
        }
    }

    private void showStartDialog() {
        Platform.runLater(() -> {
            List<String> choices = List.of("Hospital", "Estación de Policía", "Estación de Bomberos");
            javafx.scene.control.ChoiceDialog<String> dialog = new javafx.scene.control.ChoiceDialog<>("Hospital", choices);
            dialog.setTitle("Elegir objetivo");
            dialog.setHeaderText("¿Cuál es tu objetivo de hackeo?");
            dialog.setContentText("Elige uno:");

            dialog.showAndWait().ifPresent(selected -> {
                boolean passed = showRiddleFor(selected);
                if (passed) {
                    setupCanvas();
                    loadHackerFrames();
                    initializeGraph();
                    initializeHacker();
                    setupEvents();
                    startRenderLoop();
                } else {
                    showStartDialog();
                }
            });
        });
    }

    private boolean showRiddleFor(String target) {
        javafx.scene.control.TextInputDialog riddleDialog = new javafx.scene.control.TextInputDialog();
        riddleDialog.setTitle("Acertijo de Seguridad");
        switch (target) {
            case "Hospital" -> riddleDialog.setHeaderText("Tengo agujas pero no pincho. ¿Qué soy?");
            case "Estación de Policía" ->
                    riddleDialog.setHeaderText("Siempre estoy en la escena, pero nunca actúo. ¿Quién soy?");
            case "Estación de Bomberos" -> riddleDialog.setHeaderText("Traigo agua pero no llueve. ¿Quién soy?");
        }
        riddleDialog.setContentText("Tu respuesta:");

        return riddleDialog.showAndWait()
                .map(answer -> switch (target) {
                    case "Hospital" -> answer.equalsIgnoreCase("reloj") || answer.equalsIgnoreCase("el reloj");
                    case "Estación de Policía" ->
                            answer.equalsIgnoreCase("cámara") || answer.equalsIgnoreCase("la cámara");
                    case "Estación de Bomberos" ->
                            answer.equalsIgnoreCase("camión") || answer.equalsIgnoreCase("el camión");
                    default -> false;
                }).orElse(false);
    }

    private void initializeHacker() {
        Node startNode = findClosestNode(100, 100);
        Node goalNode = findClosestNode(600, 600);
        hacker = new Hacker(startNode);
        hackerPath = resolvePath(startNode, goalNode);
        monitorGoal(goalNode);
    }

    private void startHackerMovementThread() {
        if (hackerPath == null || hackerPath.isEmpty()) return;
        new Thread(() -> {
            while (hackerStep < hackerPath.size()) {
                Node nextNode = hackerPath.get(hackerStep);
                hacker.setCurrentPosition(nextNode);
                handleNodeHack(nextNode);
                Platform.runLater(() -> {
                    currentFrame = (currentFrame + 1) % hackerFrames.length;
                    draw();
                });
                hackerStep++;
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void handleNodeHack(Node node) {
        if (!node.isHacked()) {
            node.setHacked(true);
            hacker.moveTo(node);
            totalKeys++;
            totalPoints += 10;
        } else {
            hacker.moveTo(node);
            totalPoints += 1;
        }
    }

    private Node findClosestNode(double screenX, double screenY) {
        double worldX = screenX + cameraX;
        double worldY = screenY + cameraY;
        return cityGraph.getNodes().stream()
                .filter(Node::isWalkable)
                .min(Comparator.comparingDouble(node ->
                        Math.pow(node.getX() - worldX, 2) + Math.pow(node.getY() - worldY, 2)))
                .orElse(null);
    }

    private void draw() {
        int securityLevel = 0;
        Node pos = hacker.getCurrentPosition();
        for (Edge edge : cityGraph.getEdgesFrom(pos)) {
            if (edge.getSource().equals(pos)) {
                securityLevel = edge.getWeight();
                break;
            }
        }
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.drawImage(mapImage, cameraX, cameraY,
                canvas.getWidth(), canvas.getHeight(),  // tamaño real para mostrar
                0, 0,
                canvas.getWidth(), canvas.getHeight()); // área donde se dibuja

        drawGraph();
        drawHacker();

        gc.setFill(Color.color(0.1, 0.1, 0.1, 0.6));
        gc.fillRect(5, 5, 260, 160);
        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Consolas", 14));
        gc.fillText("Llaves recolectadas: " + totalKeys, 15, 25);
        gc.fillText("Puntuación total:   " + totalPoints, 15, 45);
        gc.fillText("Estructura:         " + currentStructure, 15, 65);
        gc.fillText("Algoritmo:          " + (useBFS ? "BFS" : "Dijkstra"), 15, 85);
        gc.fillText("[G] cambiar estructura", 15, 105);
        gc.fillText("[E] cambiar algoritmo", 15, 125);
        gc.fillText("Seguridad nodo:     " + securityLevel, 15, 145);
    }

    private void drawGraph() {
        gc.setStroke(Color.RED);
        gc.setFill(Color.BLACK);  // Para el texto, negro para que contraste
        gc.setFont(new Font("Arial", 12));

        for (Node node : cityGraph.getNodes()) {
            for (Node neighbor : cityGraph.getNeighbors(node)) {
                double x1 = node.getX() - cameraX;
                double y1 = node.getY() - cameraY;
                double x2 = neighbor.getX() - cameraX;
                double y2 = neighbor.getY() - cameraY;

                // Dibuja la línea de la arista
                gc.strokeLine(x1, y1, x2, y2);

                // Calcula el punto medio
                double midX = (x1 + x2) / 2;
                double midY = (y1 + y2) / 2;

                // Obtiene el peso o nivel de seguridad de la arista
                int securityLevel = (int) cityGraph.getEdgeWeight(node, neighbor);

                // Dibuja el nivel en el medio con un fondo para que se lea mejor
                String text = String.valueOf(securityLevel);

                // Fondo semi-transparente para que contraste
                gc.setFill(Color.color(1, 1, 1, 0.7));
                gc.fillRect(midX - 10, midY - 12, 20, 16);

                // Texto en negro
                gc.setFill(Color.BLACK);
                gc.fillText(text, midX - 6, midY);
            }
        }

        // Dibuja los nodos después para que estén arriba
        gc.setFill(Color.BLUE);
        for (Node node : cityGraph.getNodes()) {
            gc.fillOval(node.getX() - cameraX - 2.5, node.getY() - cameraY - 2.5, 12, 12);
        }
    }


    private void drawHacker() {
        gc.setFill(Color.LIGHTBLUE);
        for (Node visited : hacker.getVisitedNodes()) {
            double vx = visited.getX() - cameraX;
            double vy = visited.getY() - cameraY;
            gc.fillOval(vx - 2, vy - 2, 4, 4);
        }
        Node pos = hacker.getCurrentPosition();
        double screenX = pos.getX() - cameraX;
        double screenY = pos.getY() - cameraY;
        gc.drawImage(hackerFrames[currentFrame], screenX - 12, screenY - 20, 24, 40);
    }

    @FXML
    private void handleKeyPressed(KeyEvent event) {
        switch (event.getCode()) {
            case W -> cameraY = Math.max(0, cameraY - MOVE_SPEED);
            case S -> cameraY = Math.min(mapImage.getHeight() - canvas.getHeight(), cameraY + MOVE_SPEED);
            case A -> cameraX = Math.max(0, cameraX - MOVE_SPEED);
            case D -> cameraX = Math.min(mapImage.getWidth() - canvas.getWidth(), cameraX + MOVE_SPEED);
            case E -> {
                useBFS = !useBFS;
                System.out.println("Algoritmo cambiado a: " + (useBFS ? "BFS" : "Dijkstra"));
            }
            case G -> {
                currentStructure = (currentStructure == GraphStructureType.ADJACENCY_LIST)
                        ? GraphStructureType.ADJACENCY_MATRIX
                        : GraphStructureType.ADJACENCY_LIST;
                System.out.println("Estructura cambiada a: " + currentStructure);
            }
        }
        draw();
    }


    private void monitorGoal(Node goal) {
        new Thread(() -> {
            while (true) {
                if (hacker.getCurrentPosition().equals(goal)) {
                    Platform.runLater(() -> {
                        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                        alert.setTitle("¡Felicidades!");
                        alert.setHeaderText(null);
                        alert.setContentText("¡Has llegado a tu destino!");

                        javafx.scene.control.ButtonType retry = new javafx.scene.control.ButtonType("Volver a jugar");
                        alert.getButtonTypes().setAll(retry);

                        alert.showAndWait().ifPresent(btn -> {
                            if (btn == retry) {
                                totalKeys = 0;
                                totalPoints = 0;
                                hacker.getVisitedNodes().clear();
                                showStartDialog();
                            }
                        });
                    });
                    break;
                }
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}

