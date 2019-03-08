package ui.edit;

import api.toggle.Color;
import api.toggle.Project;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class ProjectCell extends ListCell<Project> {
    @Override
    protected void updateItem(Project project, boolean empty) {
        super.updateItem(project, empty);

        if (project == null) {
            return;
        }

        Label label = new Label(project.getName());
        String color = project.getColorId() == null ? "rgb(119, 136, 153)" : Color.getColorRgb(project.getColorId());

        label.setStyle("-fx-text-fill: " + color);

        setGraphic(label);
    }

    public static class ProjectCellFactory implements Callback<ListView<Project>, ListCell<Project>> {
        @Override
        public ListCell<Project> call(ListView<Project> list) {
            ProjectCell cell = new ProjectCell();
            cell.getStyleClass().add("project-cell");
            return cell;
        }
    }
}
