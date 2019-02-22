package ui.settings;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXChipView;
import com.jfoenix.controls.JFXComboBox;

import api.Cache;
import api.Project;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import static api.Cache.EMPTY_PROJECT;

public class SettingsWindow {
    private Scene scene;
    private GridPane mainContainer = new GridPane();

    private JFXComboBox<Project> projectSelectInput = this.initProjectSelectInput();
    private JFXChipView<String> spellIgnoreDictionary = new JFXChipView<String>();
    private JFXButton saveButton = new JFXButton("Save");

    private SettingsWindow() {
        saveButton.getStyleClass().add("common-button");
        saveButton.setDisable(true);
        GridPane.setHalignment(saveButton, HPos.RIGHT);
        GridPane.setValignment(saveButton, VPos.BOTTOM);
        GridPane.setVgrow(saveButton, Priority.ALWAYS);
        GridPane.setHgrow(saveButton, Priority.ALWAYS);

        mainContainer.add(createLabel("Default project:"), 0, 0);
        mainContainer.add(projectSelectInput, 0, 1);
        mainContainer.add(createLabel("Spell check ignore list:"), 0, 2);
        mainContainer.add(spellIgnoreDictionary, 0, 3);
        mainContainer.add(saveButton, 0, 4);
        mainContainer.setPadding(new Insets(10));
        mainContainer.getStyleClass().add("settings-window");

        scene = new Scene(mainContainer, 500, 600);
        scene.getStylesheets().add((getClass().getResource("/styles.css")).toExternalForm());
    }

    private Region createLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("config-name-label");

        GridPane.setHalignment(label, HPos.LEFT);
        GridPane.setHgrow(label, Priority.NEVER);

        return label;
    }

    private JFXComboBox<Project> initProjectSelectInput() {
        ObservableList<Project> projects = FXCollections.observableArrayList(EMPTY_PROJECT);
        projects.addAll(Cache.getInstance().getAllProjects());

        JFXComboBox<Project> projectSelectInput = new JFXComboBox<>();

        projectSelectInput.setItems(projects);
        projectSelectInput.getSelectionModel().selectFirst();
        projectSelectInput.setCellFactory(new DefaultProjectCell.DefaultProjectCellFactory());
        projectSelectInput.prefWidthProperty().bind(mainContainer.prefWidthProperty());

        GridPane.setHgrow(projectSelectInput, Priority.ALWAYS);

        return projectSelectInput;
    }

    public static Scene createScene() {
        return new SettingsWindow().scene;
    }
}
