package ui.settings;

import com.jfoenix.controls.JFXButton;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class SettingsWindow {
    private Scene scene;
    private GridPane mainContainer = new GridPane();

    private JFXButton saveButton = new JFXButton("Save");

    private SettingsWindow() {
        saveButton.getStyleClass().add("common-button");
        saveButton.setDisable(true);
        GridPane.setHalignment(saveButton, HPos.RIGHT);
        GridPane.setValignment(saveButton, VPos.BOTTOM);
        GridPane.setVgrow(saveButton, Priority.ALWAYS); // temp
        GridPane.setHgrow(saveButton, Priority.ALWAYS);

        mainContainer.add(saveButton, 0, 2);
        mainContainer.setPadding(new Insets(10));
        mainContainer.getStyleClass().add("settings-window");

        scene = new Scene(mainContainer, 500, 600);
        scene.getStylesheets().add((getClass().getResource("/styles.css")).toExternalForm());
    }

    public static Scene create() {
        return new SettingsWindow().scene;
    }
}
