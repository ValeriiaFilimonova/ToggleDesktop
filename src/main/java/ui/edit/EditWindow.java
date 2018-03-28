package ui.edit;

import com.jfoenix.controls.*;

import java.time.*;
import java.time.format.DateTimeFormatter;

import api.Cache;
import api.Project;
import api.Tag;
import api.TimeEntry;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.util.StringConverter;
import lombok.Getter;
import ui.IComponent;

public class EditWindow implements IComponent {
    private JFXDrawer drawer = new JFXDrawer();
    private Image closeIcon = new Image(this.getClass().getResourceAsStream("/cross.png"), 15, 15, true, true);
    private ImageView closeButton = new ImageView(closeIcon);
    private JFXTextField descriptionInput = new JFXTextField();
    private Label companyLabel = new Label("No Company");
    private JFXComboBox<Project> projectSelectInput = new JFXComboBox<>();
    private JFXTimePicker startTime = new JFXTimePicker();
    private JFXTimePicker endTime = new JFXTimePicker();
    private Label durationLabel = new Label("00:00:00");
    private JFXDatePicker datePicker = new JFXDatePicker();
    private MultiSelectStringView tagsSelectInput = new MultiSelectStringView(Tag.getAllAsStrings());

    private EditableTimeEntry editableEntry;

    public EditWindow(TimeEntry entry, double width) {
        editableEntry = new EditableTimeEntry(entry);

        closeButton.setOnMouseClicked((e) -> {
            ImageView source = (ImageView) e.getSource();
            JFXDrawersStack root = (JFXDrawersStack) source.getScene().getRoot();
            GridPane mainWindow = (GridPane) root.getContent();
            root.toggle(drawer);
            mainWindow.setEffect(null);
        });

        descriptionInput.textProperty().bindBidirectional(editableEntry.getDescriptionProperty());
        descriptionInput.setFocusTraversable(false);
        descriptionInput.getStyleClass().add("entry-edit-description");

        projectSelectInput.setCellFactory(new ProjectCell.ProjectCellFactory());
        projectSelectInput.setItems(FXCollections.observableArrayList(Cache.getInstance().getAllProjects()));
        projectSelectInput.valueProperty().bindBidirectional(editableEntry.getProjectProperty());
        projectSelectInput.setPromptText("No Project");
        projectSelectInput.getStyleClass().add("entry-edit-project");

        companyLabel.textProperty().bind(editableEntry.getCompanyProperty());

        durationLabel.textProperty().bind(editableEntry.getDurationProperty());
        durationLabel.getStyleClass().add("entry-edit-duration");

        startTime.valueProperty().bindBidirectional(editableEntry.getStartTimeProperty());
        endTime.valueProperty().bindBidirectional(editableEntry.getEndTimeProperty());

        datePicker.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                if (date == null) {
                    return null;
                }
                return DateTimeFormatter.ofPattern("dd.MM.yyyy").format(date);
            }

            @Override
            public LocalDate fromString(String dateString) {
                if (dateString == null || dateString.trim().isEmpty()) {
                    return null;
                }
                return LocalDate.parse(dateString, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            }
        });
        datePicker.valueProperty().bindBidirectional(editableEntry.getDateProperty());

        tagsSelectInput.selectedItemsProperty().bindBidirectional(editableEntry.getTagsProperty());

        GridPane pane = new GridPane();
        pane.add(closeButton, 2, 0);
        pane.add(descriptionInput, 0, 1, 3, 1);
        pane.add(projectSelectInput, 0, 2, 2, 1);
        pane.add(companyLabel, 2, 2);
        pane.add(startTime, 0, 3);
        pane.add(durationLabel, 1, 3);
        pane.add(endTime, 2, 3);
        pane.add(datePicker, 1, 4);
        pane.add(tagsSelectInput.getComponent(), 0, 5, 3, 1);

        pane.setPadding(new Insets(10, 20, 10, 20));
        pane.getStyleClass().add("edit-container");
        pane.setPrefWidth(width);
        pane.setVgap(10);
        pane.setHgap(10);

        descriptionInput.prefWidthProperty().bind(pane.prefWidthProperty().multiply(0.8));
        projectSelectInput.prefWidthProperty().bind(pane.prefWidthProperty().multiply(0.75));
        datePicker.prefWidthProperty().bind(pane.prefWidthProperty().multiply(0.5));

        GridPane.setHalignment(closeButton, HPos.RIGHT);
        GridPane.setValignment(closeButton, VPos.TOP);
        GridPane.setHgrow(descriptionInput, Priority.ALWAYS);
        GridPane.setHgrow(projectSelectInput, Priority.ALWAYS);
        GridPane.setHgrow(companyLabel, Priority.NEVER);
        GridPane.setHalignment(durationLabel, HPos.CENTER);
        GridPane.setHalignment(datePicker, HPos.LEFT);
        GridPane.setVgrow(tagsSelectInput.getComponent(), Priority.NEVER);

        drawer.setDirection(JFXDrawer.DrawerDirection.RIGHT);
        drawer.setDefaultDrawerSize(width);
        drawer.setSidePane(pane);
        drawer.setOverLayVisible(false);
        drawer.setOnDrawerClosed((e) -> {
            JFXDrawersStack root = (JFXDrawersStack) drawer.getScene().getRoot();
            GridPane mainWindow = (GridPane) root.getContent();
            if (drawer.isOpened()) {
                root.toggle(drawer);
            }
            mainWindow.setEffect(null);
        });
    }

    public JFXDrawer getComponent() {
        return drawer;
    }

    public static class EditableTimeEntry {
        @Getter
        private SimpleStringProperty descriptionProperty = new SimpleStringProperty();

        @Getter
        private SimpleObjectProperty<Project> projectProperty = new SimpleObjectProperty<>();

        @Getter
        private SimpleStringProperty companyProperty = new SimpleStringProperty();

        @Getter
        private SimpleObjectProperty<LocalTime> startTimeProperty = new SimpleObjectProperty<>();

        @Getter
        private SimpleStringProperty durationProperty = new SimpleStringProperty();

        @Getter
        private SimpleObjectProperty<LocalTime> endTimeProperty = new SimpleObjectProperty<>();

        @Getter
        private SimpleObjectProperty<LocalDate> dateProperty = new SimpleObjectProperty<>();

        @Getter
        private SimpleListProperty<String> tagsProperty = new SimpleListProperty<>();

        public EditableTimeEntry(TimeEntry entry) {
            descriptionProperty.set(entry.getDescription());

            if (entry.getProject() != null) {
                projectProperty.set(entry.getProject());
                companyProperty.set(entry.getProject().getCompanyName());
            }

            projectProperty.addListener((o, oldValue, newValue) -> companyProperty.set(newValue.getCompanyName()));

            if (entry.getStart() != null) {
                ZonedDateTime dateTime = entry.getStart().toInstant().atZone(ZoneId.systemDefault());
                startTimeProperty.set(dateTime.toLocalTime());
                dateProperty.set(dateTime.toLocalDate());
            }

            if (entry.getStop() != null) {
                ZonedDateTime dateTime = entry.getStop().toInstant().atZone(ZoneId.systemDefault());
                endTimeProperty.set(dateTime.toLocalTime());
            }

            startTimeProperty.addListener((observable, oldValue, newValue) -> updateDuration());
            endTimeProperty.addListener((observable, oldValue, newValue) -> updateDuration());

            tagsProperty.set(FXCollections.observableArrayList(entry.getTagsAsStringList()));

            updateDuration();
        }

        public void updateDuration() {
            LocalDateTime startDateTime = LocalDateTime.of(dateProperty.get(), startTimeProperty.get());
            LocalDateTime endDateTime = LocalDateTime.of(dateProperty.get(), endTimeProperty.get());

            Duration duration = Duration.between(startDateTime, endDateTime);

            long hours = duration.toHours();
            long minutes = duration.toMinutes() - hours * 60;
            long seconds = duration.getSeconds() - hours * 60 * 60 - minutes * 60;

            String text = String.format("%02d:%02d:%02d", hours, minutes, seconds);
            durationProperty.set(text);
        }
    }
}
