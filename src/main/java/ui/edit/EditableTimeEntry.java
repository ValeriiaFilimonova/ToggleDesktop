package ui.edit;

import java.time.*;

import api.Project;
import api.Tag;
import api.TimeEntry;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import lombok.Getter;

class EditableTimeEntry {
    private TimeEntry originalEntry;

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
    private SimpleListProperty<Tag> tagsProperty = new SimpleListProperty<>();

    public EditableTimeEntry(TimeEntry entry) {
        originalEntry = entry;
        descriptionProperty.set(entry.getDescription());

        if (entry.getProject() != null) {
            projectProperty.set(entry.getProject());
            companyProperty.set(entry.getProject().getCompanyName());
        } else {
            projectProperty.set(EditWindow.EMPTY_PROJECT);
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

        tagsProperty.set(FXCollections.observableArrayList(entry.getTags()));

        updateDuration();
    }

    public TimeEntry getUpdatedTimeEntry() {
        TimeEntry newEntry = originalEntry.clone();

        newEntry.setDescription(descriptionProperty.get());
        newEntry.setTags(tagsProperty.get().toArray(new Tag[] {}));

        Project project = projectProperty.get();
        if (project.equals(EditWindow.EMPTY_PROJECT)) {
            newEntry.setPid(null);
        } else {
            newEntry.setPid(project.getId());
        }

        return newEntry;
    }

    private void updateDuration() {
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
