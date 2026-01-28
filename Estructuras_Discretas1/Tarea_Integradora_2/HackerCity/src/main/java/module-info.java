module gui {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires javafx.graphics;
    requires java.desktop;


    opens gui to javafx.fxml;
    opens controller to javafx.fxml;

    exports controller;
    exports gui;
}