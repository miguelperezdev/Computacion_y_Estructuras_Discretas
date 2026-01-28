package gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;

import java.io.File;
import java.net.URL;
import java.util.Objects;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {

            showInstructions();

            setupMainMap(primaryStage);

        } catch (Exception e) {
            handleStartupError(e);
        }
    }

    private void showInstructions() throws Exception {
        URL instructionsUrl = getClass().getResource("/fxml/instructions_view.fxml");
        if (instructionsUrl == null) {
            throw new IllegalStateException("¡Archivo instructions_view.fxml no encontrado!");
        }

        FXMLLoader instructionLoader = new FXMLLoader(instructionsUrl);
        Parent instructionRoot = instructionLoader.load();

        Stage instructionStage = new Stage();
        instructionStage.setTitle("Instrucciones del Simulador");
        instructionStage.setScene(new Scene(instructionRoot));

        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/icon.png")));
        instructionStage.getIcons().add(icon);

        centerStage(instructionStage);

        instructionStage.showAndWait();
    }

    private void setupMainMap(Stage primaryStage) throws Exception {
        URL fxmlUrl = getClass().getResource("/fxml/MapView.fxml");
        if (fxmlUrl == null) {
            System.err.println("El archivo no se encontró. Buscando en:");
            System.err.println("1. " + new File("src/main/resources/fxml/MapView.fxml").getAbsolutePath());
            System.err.println("2. " + new File("target/classes/fxml/MapView.fxml").getAbsolutePath());
            throw new IllegalStateException("¡Archivo MapView.fxml no encontrado en el classpath!");
        }

        System.out.println("Cargando FXML desde: " + fxmlUrl);
        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Parent root = loader.load();

        // Obtener las dimensiones de la pantalla
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

        // Crear escena que se adapte al tamaño de la pantalla
        Scene scene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight());

        // Configurar el ícono
        try {
            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/icon.png")));
            primaryStage.getIcons().add(icon);
        } catch (Exception e) {
            System.err.println("No se pudo cargar el ícono: " + e.getMessage());
        }

        // Configurar la ventana principal
        primaryStage.setScene(scene);
        primaryStage.setTitle("HackerCity");

        // Maximizar la ventana para adaptarse a la pantalla
        primaryStage.setMaximized(true);

        primaryStage.show();
    }

    private void centerStage(Stage stage) {
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((screenBounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((screenBounds.getHeight() - stage.getHeight()) / 2);
    }

    private void handleStartupError(Exception e) {
        System.err.println("ERROR FATAL:");
        e.printStackTrace();

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error de aplicación");
        alert.setHeaderText("No se pudo cargar la interfaz");
        alert.setContentText(e.getMessage());

        Platform.runLater(() -> {
            alert.showAndWait();
            Platform.exit();
            System.exit(1);
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}