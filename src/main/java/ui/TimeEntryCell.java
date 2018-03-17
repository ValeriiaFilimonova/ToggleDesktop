package ui;

import java.io.InputStream;

import api.Color;
import api.Company;
import api.Project;
import api.TimeEntry;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;

public class TimeEntryCell extends ListCell<TimeEntry> {
    @Override
    protected void updateItem(TimeEntry item, boolean empty) {
        super.updateItem(item, empty);

        if (item == null) {
            return;
        }

        setGraphic(this.getItemGraphics(item));
    }

    private Node getItemGraphics(TimeEntry item) {
        GridPane container = new GridPane();

        if (item.getDescription() != null) {
            container.add(getDescriptionNode(item), 0, 0);
        }

        if (item.getProject() != null) {
            container.add(getProjectNode(item), 0, 1);
        }

        if (item.getTags() != null) {
            container.add(getTagsCountNode(item), 1, 0, 1, 2);
        }

        container.add(getDurationNode(item), 2, 0, 1, 2);

        return container;
    }

    private Node getDescriptionNode(TimeEntry item) {
        Label descriptionLabel = new Label(item.getDescription());
        descriptionLabel.getStyleClass().add("time-entry-description");

        StackPane descriptionPane = new StackPane();
        descriptionPane.setMinWidth(0);
        descriptionPane.setPrefWidth(1);
        descriptionPane.getChildren().add(descriptionLabel);
        StackPane.setAlignment(descriptionLabel, Pos.CENTER_LEFT);

        GridPane.setHgrow(descriptionPane, Priority.ALWAYS);

        return descriptionPane;
    }

    private Node getProjectNode(TimeEntry item) {
        Project project = item.getProject();
        String labelText = project.getName();
        Company company = project.getCompany();

        if (company != null) {
            labelText = company.getName() + " • " + labelText;
        }

        Label projectLabel = new Label(labelText);
        projectLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + Color.getColorRgb(project.getColorId()));

        GridPane.setHgrow(projectLabel, Priority.ALWAYS);

        return projectLabel;
    }

    private Node getTagsCountNode(TimeEntry item) {
        InputStream input = this.getClass().getResourceAsStream("/tag.png");
        Image image = new Image(input, 30, 30, true, true);

        String tagsCount = Integer.toString(item.getTags().length);
        Label tagsCountLabel = new Label(tagsCount, new ImageView(image));

        GridPane.setHgrow(tagsCountLabel, Priority.NEVER);
        GridPane.setMargin(tagsCountLabel, new Insets(0, 10, 0, 10));

        return tagsCountLabel;
    }

    private Node getDurationNode(TimeEntry item) {
        int duration = item.getDurationInMinutes();
        int hours = duration / 60;
        int minutes = duration - hours * 60;
        String labelText = String.format("%dh %02dmin", hours, minutes);

        Label durationLabel = new Label(labelText);
        durationLabel.getStyleClass().add("time-entry-duration");

        GridPane.setHgrow(durationLabel, Priority.NEVER);

        return durationLabel;
    }

    public static class TimeEntryCellFactory implements Callback<ListView<TimeEntry>, ListCell<TimeEntry>> {
        @Override
        public ListCell<TimeEntry> call(ListView<TimeEntry> list) {
            TimeEntryCell timeEntryCell = new TimeEntryCell();
            timeEntryCell.getStyleClass().add("time-entry-list-cell");
            return timeEntryCell;
        }
    }
}
