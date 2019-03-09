package ui.settings;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXChipView;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;

import api.toggle.Cache;
import api.toggle.Project;
import api.toggle.ToggleClientException;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

import static api.toggle.Cache.EMPTY_PROJECT;

public class SettingsWindow {
    private static String UNFOCUSED_SCROLL_PANE_STYLE = "unfocused-scroll-pane";
    private static String FOCUSED_SCROLL_PANE_STYLE = "focused-scroll-pane";

    private Scene scene;
    private GridPane mainContainer = new GridPane();

    private JFXTextField apiTokenInput = this.initApiTokenInput();
    private JFXComboBox<Project> projectSelectInput = this.initProjectSelectInput();
    private ScrollPane scrollableIgnoreListInput = this.initSpellIgnoreListInput();
    private JFXButton saveButton = new JFXButton("Save");

    private SettingsWindow() {
        saveButton.getStyleClass().add("common-button");
        saveButton.setDisable(true);
        GridPane.setHalignment(saveButton, HPos.RIGHT);
        GridPane.setValignment(saveButton, VPos.BOTTOM);
        GridPane.setVgrow(saveButton, Priority.ALWAYS);
        GridPane.setHgrow(saveButton, Priority.ALWAYS);

        int row = 0;

        mainContainer.add(createLabel("Toggle API token:"), 0, row++);
        mainContainer.add(apiTokenInput, 0, row++);
        mainContainer.add(createLabel("Default project:"), 0, row++);
        mainContainer.add(projectSelectInput, 0, row++);
        mainContainer.add(createLabel("Spell check ignore list:"), 0, row++);
        mainContainer.add(scrollableIgnoreListInput, 0, row++);
        mainContainer.add(saveButton, 0, row);

        mainContainer.setPadding(new Insets(15));
        mainContainer.setVgap(10);
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

    private JFXTextField initApiTokenInput() {
        JFXTextField apiTokenInput = new JFXTextField();

        apiTokenInput.setFocusColor(Color.rgb(0, 150, 130, 1));
        apiTokenInput.setFocusTraversable(false);
        apiTokenInput.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                setScrollPaneFocus(scrollableIgnoreListInput, false);
            }
        });

        GridPane.setHgrow(apiTokenInput, Priority.ALWAYS);

        return apiTokenInput;
    }

    private JFXComboBox<Project> initProjectSelectInput() {
        ObservableList<Project> projects = FXCollections.observableArrayList(EMPTY_PROJECT);
        JFXComboBox<Project> projectSelectInput = new JFXComboBox<>();

        try {
            projects.addAll(Cache.getInstance().getAllProjects());
        } catch (ToggleClientException e) {
            projectSelectInput.setDisable(true);
        }

        projectSelectInput.setItems(projects);
        projectSelectInput.setFocusTraversable(false);
        projectSelectInput.setConverter(new ProjectStringConverter());
        projectSelectInput.getSelectionModel().selectFirst();
        projectSelectInput.setCellFactory(new DefaultProjectCell.DefaultProjectCellFactory());
        projectSelectInput.prefWidthProperty().bind(mainContainer.widthProperty());
        projectSelectInput.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                setScrollPaneFocus(scrollableIgnoreListInput, false);
            }
        });

        GridPane.setHgrow(projectSelectInput, Priority.ALWAYS);

        return projectSelectInput;
    }

    private ScrollPane initSpellIgnoreListInput() {
        JFXChipView<String> ignoreListInput = new JFXChipView<>();
        ScrollPane scrollPane = new ScrollPane(ignoreListInput);

        scrollPane.getStyleClass().addAll("chip-view-scroll-pane", "unfocused-scroll-pane");
        scrollPane.prefHeightProperty().bind(mainContainer.widthProperty().multiply(0.5));

        ignoreListInput.prefWidthProperty().bind(scrollPane.widthProperty().multiply(0.92));
        ignoreListInput.prefHeightProperty().bind(scrollPane.heightProperty().multiply(0.92));
        ignoreListInput.heightProperty().addListener((observable, oldValue, newValue) -> scrollPane.setVvalue(1D));

        // TODO fix somehow
        ignoreListInput.setOnMouseClicked(event -> setScrollPaneFocus(scrollPane, true));

        GridPane.setVgrow(scrollPane, Priority.NEVER);

        return scrollPane;
    }

    private static void setScrollPaneFocus(ScrollPane scrollPane, boolean focus) {
        if (focus && scrollPane.getStyleClass().indexOf(UNFOCUSED_SCROLL_PANE_STYLE) >= 0) {
            scrollPane.getStyleClass().remove(UNFOCUSED_SCROLL_PANE_STYLE);
            scrollPane.getStyleClass().add(FOCUSED_SCROLL_PANE_STYLE);
        }
        if (!focus && scrollPane.getStyleClass().indexOf(FOCUSED_SCROLL_PANE_STYLE) >= 0) {
            scrollPane.getStyleClass().remove(FOCUSED_SCROLL_PANE_STYLE);
            scrollPane.getStyleClass().add(UNFOCUSED_SCROLL_PANE_STYLE);
        }
    }

    public static Scene createScene() {
        return new SettingsWindow().scene;
    }
}
