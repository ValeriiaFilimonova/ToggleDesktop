package ui.settings;

import api.toggle.Color;
import api.toggle.Project;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class DefaultProjectCell extends ListCell<Project> {
    @Override
    protected void updateItem(Project project, boolean empty) {
        super.updateItem(project, empty);

        if (project == null) {
            return;
        }

        String labelText = project.getName();

        if (project.getCompany() != null) {
            labelText = project.getCompany().getName() + "." + labelText;
        }

        Label label = new Label(labelText);
        String color = project.getColorId() == null ? "rgb(119, 136, 153)" : Color.getColorRgb(project.getColorId());

        label.setStyle("-fx-text-fill: " + color);

        setGraphic(label);
    }

    public static class DefaultProjectCellFactory implements Callback<ListView<Project>, ListCell<Project>> {
        @Override
        public ListCell<Project> call(ListView<Project> list) {
            DefaultProjectCell cell = new DefaultProjectCell();
            cell.getStyleClass().add("project-cell");
            return cell;
        }
    }
}
