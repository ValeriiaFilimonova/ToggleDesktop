package ui.reports;

import com.jfoenix.controls.JFXDrawer;

import java.util.List;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import reports.InvalidEntryException;
import ui.IComponent;

public class ReportErrorsWindow implements IComponent {
    private JFXDrawer drawer = new JFXDrawer();
    private VBox container = new VBox();

    public ReportErrorsWindow(List<InvalidEntryException> errors, double height) {
        for (InvalidEntryException error : errors) {
            String text = String.format("Entry at %s: %s", error.getEntry().getFullDate(), error.getMessage());
            Label label = new Label(text);
            label.getStyleClass().add("report-error-label");
            container.getChildren().add(label);
        }

        container.setSpacing(10);
        container.setPadding(new Insets(10));
        container.getStyleClass().add("report-errors-container");

        drawer.setDirection(JFXDrawer.DrawerDirection.BOTTOM);
        drawer.setDefaultDrawerSize(height);
        drawer.setPrefHeight(height);
        drawer.setSidePane(new ScrollPane(container));
        drawer.setOverLayVisible(true);
    }

    public JFXDrawer getComponent() {
        return drawer;
    }
}
