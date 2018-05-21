package ui.reports;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXPopup;

import java.util.List;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import reports.InvalidEntryException;
import ui.IComponent;

public class ReportErrorsWindow {
    private JFXPopup popup = new JFXPopup();
    private VBox container = new VBox();

    public ReportErrorsWindow(List<InvalidEntryException> errors) {
        Label header = new Label("Export Results");
        header.getStyleClass().add("report-header-label");
        container.getChildren().add(header);

        for (InvalidEntryException error : errors) {
            String text = String.format("Entry at %s: %s", error.getEntry().getFullDate(), error.getMessage());
            Label label = new Label(text);
            label.getStyleClass().add("report-error-label");
            container.getChildren().add(label);
        }

        container.setSpacing(10);
        container.setPadding(new Insets(10));
        container.getStyleClass().add("report-errors-container");

        popup.setPopupContent(new ScrollPane(container));
        popup.setHideOnEscape(false);
        popup.setAutoFix(true);
    }

    public void show(Node node, double width, double height) {
        popup.setPrefSize(width, height);
        popup.setMinSize(width, height);
        popup.setMaxSize(width, height);
        popup.show(node,
            JFXPopup.PopupVPosition.TOP,
            JFXPopup.PopupHPosition.LEFT,
            -width, 0
        );
    }
}
