package ui.edit;

import com.jfoenix.controls.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import api.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.util.StringConverter;
import ui.IComponent;

public class EditWindow implements IComponent {
    static Project EMPTY_PROJECT = new Project("No project", "No project");

    private static int GRID_GAP = 10;
    private static int ICON_SIZE = 15;

    private JFXDrawer drawer = new JFXDrawer();
    private GridPane gridPane = this.initGridPane();
    private ImageView closeButton = this.initCloseButton();
    private JFXTextField descriptionInput = this.initDescriptionInput();
    private JFXComboBox<Project> projectSelectInput = this.initProjectSelectInput();
    private Label companyLabel = this.initCompanyLabel();
    private JFXTimePicker startTime = new JFXTimePicker();
    private JFXTimePicker endTime = new JFXTimePicker();
    private Label durationLabel = this.initDurationLabel();
    private JFXDatePicker datePicker = this.initDatePicker();
    private MultiSelectStringView<Tag> tagsSelectInput = this.initTagsSelectInput();
    private ImageView deleteButton = this.initDeleteButton();

    private EditableTimeEntry editableEntry;

    public EditWindow(TimeEntry entry, double width) {
        editableEntry = new EditableTimeEntry(entry);

        descriptionInput.textProperty().bindBidirectional(editableEntry.getDescriptionProperty());
        projectSelectInput.valueProperty().bindBidirectional(editableEntry.getProjectProperty());
        companyLabel.textProperty().bind(editableEntry.getCompanyProperty());
        durationLabel.textProperty().bind(editableEntry.getDurationProperty());
        startTime.valueProperty().bindBidirectional(editableEntry.getStartTimeProperty());
        endTime.valueProperty().bindBidirectional(editableEntry.getEndTimeProperty());
        datePicker.valueProperty().bindBidirectional(editableEntry.getDateProperty());
        tagsSelectInput.selectedItemsProperty().bindBidirectional(editableEntry.getTagsProperty());

        gridPane.add(closeButton, 2, 0);
        gridPane.add(descriptionInput, 0, 1, 3, 1);
        gridPane.add(projectSelectInput, 0, 2, 2, 1);
        gridPane.add(companyLabel, 2, 2);
        gridPane.add(startTime, 0, 3);
        gridPane.add(durationLabel, 1, 3);
        gridPane.add(endTime, 2, 3);
        gridPane.add(datePicker, 1, 4);
        gridPane.add(tagsSelectInput.getComponent(), 0, 5, 3, 1);
        gridPane.add(deleteButton, 2, 6);

        gridPane.setPrefWidth(width);

        drawer.setDirection(JFXDrawer.DrawerDirection.RIGHT);
        drawer.setDefaultDrawerSize(width);
        drawer.setSidePane(gridPane);
        drawer.setOverLayVisible(true);
        drawer.setOnDrawerClosed(this::onClose);
    }

    public JFXDrawer getComponent() {
        return drawer;
    }

    private GridPane initGridPane() {
        GridPane gridPane = new GridPane();

        gridPane.setPadding(new Insets(10, 20, 10, 20));
        gridPane.getStyleClass().add("edit-container");
        gridPane.setVgap(GRID_GAP);
        gridPane.setHgap(GRID_GAP);

        return gridPane;
    }

    private ImageView initCloseButton() {
        Image closeIcon =
            new Image(this.getClass().getResourceAsStream("/cross.png"), ICON_SIZE, ICON_SIZE, true, true);
        ImageView closeButton = new ImageView(closeIcon);

        closeButton.setOnMouseClicked(e -> drawer.close());

        GridPane.setHalignment(closeButton, HPos.RIGHT);
        GridPane.setValignment(closeButton, VPos.TOP);
        GridPane.setMargin(closeButton, new Insets(0, 0, 20, 0));

        return closeButton;
    }

    private JFXTextField initDescriptionInput() {
        JFXTextField descriptionInput = new JFXTextField();

        descriptionInput.setFocusTraversable(false);
        descriptionInput.getStyleClass().add("entry-edit-description");
        descriptionInput.prefWidthProperty().bind(gridPane.prefWidthProperty().multiply(0.8));

        GridPane.setHgrow(descriptionInput, Priority.ALWAYS);

        return descriptionInput;
    }

    private JFXComboBox<Project> initProjectSelectInput() {
        ObservableList<Project> projects = FXCollections.observableArrayList(EMPTY_PROJECT);
        projects.addAll(Cache.getInstance().getAllProjects());

        JFXComboBox<Project> projectSelectInput = new JFXComboBox<>();

        projectSelectInput.setItems(projects);
        projectSelectInput.getSelectionModel().selectFirst();
        projectSelectInput.getStyleClass().add("entry-edit-project");
        projectSelectInput.setCellFactory(new ProjectCell.ProjectCellFactory());
        projectSelectInput.prefWidthProperty().bind(gridPane.prefWidthProperty().multiply(0.75));

        GridPane.setHgrow(projectSelectInput, Priority.ALWAYS);

        return projectSelectInput;
    }

    private Label initCompanyLabel() {
        Label companyLabel = new Label("No Company");

        GridPane.setHgrow(companyLabel, Priority.NEVER);

        return companyLabel;
    }

    private Label initDurationLabel() {
        Label durationLabel = new Label("00:00:00");

        durationLabel.getStyleClass().add("entry-edit-duration");

        GridPane.setHalignment(durationLabel, HPos.CENTER);

        return durationLabel;
    }

    private JFXDatePicker initDatePicker() {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        JFXDatePicker datePicker = new JFXDatePicker();

        datePicker.prefWidthProperty().bind(gridPane.prefWidthProperty().multiply(0.55));
        datePicker.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                if (date == null) {
                    return null;
                }
                return formatter.format(date);
            }

            @Override
            public LocalDate fromString(String dateString) {
                if (dateString == null || dateString.trim().isEmpty()) {
                    return null;
                }
                return LocalDate.parse(dateString, formatter);
            }
        });

        GridPane.setHalignment(datePicker, HPos.LEFT);

        return datePicker;
    }

    private MultiSelectStringView<Tag> initTagsSelectInput() {
        MultiSelectStringView<Tag> tagsSelectInput = new MultiSelectStringView<>(Tag.values());

        GridPane.setVgrow(tagsSelectInput.getComponent(), Priority.ALWAYS);

        return tagsSelectInput;
    }

    private ImageView initDeleteButton() {
        Image deleteIcon = new Image(this.getClass().getResourceAsStream("/trash.png"), ICON_SIZE, ICON_SIZE, true, true);
        ImageView deleteButton = new ImageView(deleteIcon);

        // TODO define
        // deleteButton.setOnMouseClicked(this::onClose);

        GridPane.setHalignment(deleteButton, HPos.RIGHT);
        GridPane.setValignment(deleteButton, VPos.BOTTOM);
        GridPane.setMargin(deleteButton, new Insets(20, 0, 0, 0));

        return deleteButton;
    }

    private void onClose(Event event) {
        TimeEntry updatedTimeEntry = editableEntry.getUpdatedTimeEntry();
        ToggleClient.getInstance().updateTimeEntry(updatedTimeEntry);
    }
}
