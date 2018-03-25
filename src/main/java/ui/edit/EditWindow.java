package ui.edit;

import com.jfoenix.controls.*;

import java.time.*;
import java.time.format.DateTimeFormatter;

import api.Cache;
import api.Project;
import api.Tag;
import api.TimeEntry;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.util.StringConverter;
import lombok.Getter;

public class EditWindow {
    private JFXPopup popup = new JFXPopup();
    private JFXTextField descriptionInput = new JFXTextField();
    private Label companyLabel = new Label("No Company");
    private JFXComboBox<Project> projectSelectInput = new JFXComboBox<>();
    private JFXTimePicker startTime = new JFXTimePicker();
    private JFXTimePicker endTime = new JFXTimePicker();
    private Label durationLabel = new Label("00:00:00");
    private JFXDatePicker datePicker = new JFXDatePicker();
    private JFXComboBox<Tag> tagsSelectInput = new JFXComboBox();

    private EditableTimeEntry editableEntry;

    public EditWindow(TimeEntry entry, double width) {
        editableEntry = new EditableTimeEntry(entry);

        descriptionInput.textProperty().bindBidirectional(editableEntry.getDescriptionProperty());
        descriptionInput.setFocusTraversable(false);

        projectSelectInput.setCellFactory(new ProjectCell.ProjectCellFactory());
        projectSelectInput.setItems(FXCollections.observableArrayList(Cache.getInstance().getAllProjects()));
        projectSelectInput.valueProperty().bindBidirectional(editableEntry.getProjectProperty());
        projectSelectInput.setPromptText("No Project");

        companyLabel.textProperty().bind(editableEntry.getCompanyProperty());

        durationLabel.textProperty().bind(editableEntry.getDurationProperty());
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

        tagsSelectInput.setCellFactory(new TagCell.TagCellFactory());
        tagsSelectInput.setItems(FXCollections.observableArrayList(Tag.values()));
        tagsSelectInput.valueProperty().bindBidirectional(editableEntry.getTagProperty());

        GridPane pane = new GridPane();
        pane.add(descriptionInput, 0, 0, 3, 1);
        pane.add(projectSelectInput, 0, 1, 2, 1);
        pane.add(companyLabel, 2, 1);
        pane.add(startTime, 0, 2);
        pane.add(durationLabel, 1, 2);
        pane.add(endTime, 2, 2);
        pane.add(datePicker, 1, 3, 2, 1);
        pane.add(tagsSelectInput, 0, 4, 3, 1);

        pane.setPadding(new Insets(20));
        pane.setPrefWidth(width);
        pane.setVgap(10);
        pane.setHgap(10);

        descriptionInput.prefWidthProperty().bind(pane.prefWidthProperty().multiply(0.8));
        projectSelectInput.prefWidthProperty().bind(pane.prefWidthProperty().multiply(0.666666));
        durationLabel.prefWidthProperty().bind(pane.prefWidthProperty().multiply(0.333333));
        datePicker.prefWidthProperty().bind(pane.prefWidthProperty().multiply(0.666666));
        tagsSelectInput.prefWidthProperty().bind(pane.prefWidthProperty().subtract(20 * 2));

        GridPane.setHgrow(descriptionInput, Priority.ALWAYS);
        GridPane.setHgrow(projectSelectInput, Priority.ALWAYS);
        GridPane.setHgrow(companyLabel, Priority.NEVER);
        GridPane.setHalignment(durationLabel, HPos.CENTER);
        GridPane.setHalignment(datePicker, HPos.LEFT);

        popup.setPopupContent(pane);
        popup.setAutoFix(true);
    }

    public JFXPopup getWindow() {
        return popup;
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
        private SimpleObjectProperty<Tag> tagProperty = new SimpleObjectProperty<>();

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

            if (entry.getFirstTag() != null) {
                tagProperty.set(entry.getFirstTag());
            }

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
