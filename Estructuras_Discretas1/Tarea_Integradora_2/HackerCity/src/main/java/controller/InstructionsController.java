package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class InstructionsController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button okeyButton;

    @FXML
    void closeWindow(ActionEvent event) {
        Stage stage = (Stage) okeyButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    void initialize() {
        assert okeyButton != null : "fx:id=\"okeyButton\" was not injected: check your FXML file 'instructions_view.fxml'.";

    }

}
