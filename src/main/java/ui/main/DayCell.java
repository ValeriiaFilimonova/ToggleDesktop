package ui.main;

import com.jfoenix.controls.JFXDrawersStack;
import com.jfoenix.controls.JFXListCell;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import api.TimeEntry;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Window;
import lombok.Setter;
import ui.edit.EditWindow;

public class DayCell extends JFXListCell<EntriesListComponent> {
    @Setter
    private BiConsumer<TimeEntry, TimeEntry> onUpdateListener;
    @Setter
    private Consumer<TimeEntry> onDeleteListener;

    @Override
    protected void updateItem(EntriesListComponent entriesList, boolean empty) {
        if (entriesList == null) {
            super.updateItem(entriesList, true);
        } else {
            entriesList.updateDurationLabel();
            entriesList.setPrefHeight(40.5 * entriesList.getItems().size()); // TODO get rid of magic number

            super.updateItem(entriesList, empty);

            entriesList.setOnExpend(event -> {
                if (event.getEventType() == MouseEvent.MOUSE_CLICKED) {
                    entriesList.setExpanded(!entriesList.isExpanded());
                }
            });

            entriesList.setOnMouseClicked((MouseEvent e) -> {
                Window window = entriesList.getScene().getWindow();
                TimeEntry selectedEntry = entriesList.getSelectionModel().getSelectedItem();
                EditWindow editWindow = new EditWindow(selectedEntry, window.getWidth() * 0.8);
                JFXDrawersStack drawersStack = (JFXDrawersStack) entriesList.getScene().getRoot();

                editWindow.setOnUpdateListener(this.onUpdateListener);
                editWindow.setOnDeleteListener(this.onDeleteListener);
                drawersStack.toggle(editWindow.getComponent());
            });

            if (entriesList.isExpanded()) {
                MouseEvent mouseClick = generateExpandEvent(entriesList);
                entriesList.getParent().fireEvent(mouseClick);
            }
        }
    }

    private MouseEvent generateExpandEvent(EntriesListComponent entriesList) {
        HBox node = entriesList.getGroupNode();
        Bounds localBounds = node.getBoundsInLocal();
        Point2D sceneRelatedCoordinates = node.localToScene(localBounds.getMinX(), localBounds.getMinY());
        Point2D screenRelatedCoordinates = node.localToScreen(localBounds.getMinX(), localBounds.getMinY());

        return new MouseEvent(
            MouseEvent.MOUSE_CLICKED,
            sceneRelatedCoordinates.getX(),
            sceneRelatedCoordinates.getY(),
            screenRelatedCoordinates.getX(),
            screenRelatedCoordinates.getY(),
            MouseButton.PRIMARY,
            1,
            false, false, false, false, true,
            false, false, true, false, true,
            null
        );
    }
}
