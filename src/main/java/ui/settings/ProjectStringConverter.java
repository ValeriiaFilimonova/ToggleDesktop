package ui.settings;

import api.Project;
import javafx.util.StringConverter;

public class ProjectStringConverter extends StringConverter<Project> {
    @Override
    public String toString(Project project) {
        if (project == null) {
            return null;
        }

        String labelText = project.getName();

        if (project.getCompany() != null) {
            labelText = project.getCompany().getName() + "." + labelText;
        }

        return labelText;
    }

    @Override
    public Project fromString(String string) {
        return null;
    }
}
