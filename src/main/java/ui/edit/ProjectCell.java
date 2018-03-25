package ui.edit;

import api.Color;
import api.Project;
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
        label.setStyle("-fx-text-fill: " + Color.getColorRgb(project.getColorId()));

        setGraphic(label);
    }

    public static class ProjectCellFactory implements Callback<ListView<Project>, ListCell<Project>> {
        @Override
        public ListCell<Project> call(ListView<Project> list) {
            return new ProjectCell();
        }
    }
}
