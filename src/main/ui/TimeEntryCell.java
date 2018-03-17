import java.io.InputStream;

import javafx.geometry.HPos;
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
        InputStream input = this.getClass().getResourceAsStream("/tag.png");
        Image image = new Image(input, 30, 30, true, true);

        Label descriptionLabel = new Label(item.getDescription());
        descriptionLabel.setStyle("-fx-font-size: 14px;");

        StackPane descriptionPane = new StackPane();
        descriptionPane.setMinWidth(0);
        descriptionPane.setPrefWidth(1);
        descriptionPane.getChildren().add(descriptionLabel);
        StackPane.setAlignment(descriptionLabel, Pos.CENTER_LEFT);

        Label company = new Label(item.getProject().getCompany().getName() + " •");
        company.setStyle("-fx-font-size: 11px;");
        Label project = new Label(item.getProject().getName());
        project.setStyle("-fx-font-size: 11px;");
        Label tagsCount = new Label(Integer.toString(item.getTags().length), new ImageView(image));
        Label duration = new Label("1h 33min");
        duration.setStyle("-fx-text-fill: lightslategray; -fx-font-size: 16px;");

        GridPane container = new GridPane();

        container.add(descriptionPane, 0, 0, 2, 1);
        container.add(company, 0, 1);
        container.add(project, 1, 1);
        container.add(tagsCount, 2, 0, 1, 2);
        container.add(duration, 3, 0, 1, 2);

        GridPane.setHgrow(descriptionPane, Priority.ALWAYS);
        GridPane.setMargin(company, new Insets(0, 5, 0, 0));
        GridPane.setHgrow(project, Priority.ALWAYS);
        GridPane.setHgrow(tagsCount, Priority.NEVER);
        GridPane.setMargin(tagsCount, new Insets(0, 10, 0, 10));
        GridPane.setHalignment(duration, HPos.RIGHT);
        GridPane.setHgrow(duration, Priority.NEVER);

        return container;
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
