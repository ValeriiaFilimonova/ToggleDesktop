package ui.main;

import com.jfoenix.controls.JFXDrawersStack;
import com.jfoenix.controls.JFXListCell;

import java.util.function.Consumer;

import api.TimeEntry;
import javafx.scene.input.MouseEvent;
import javafx.stage.Window;
import ui.edit.EditWindow;

public class DayCell extends JFXListCell<EntriesListComponent> {
    private Consumer<TimeEntry> onUpdateListener;

    public DayCell(Consumer<TimeEntry> onUpdateListener) {
        this.onUpdateListener = onUpdateListener;
    }

    @Override
    protected void updateItem(EntriesListComponent entriesList, boolean empty) {
        if (entriesList == null) {
            super.updateItem(entriesList, true);
        } else {
            entriesList.updateLabelText();
            entriesList.setPrefHeight(40.5 * entriesList.getItems().size()); // TODO get rid of magic number

            super.updateItem(entriesList, empty);

            entriesList.setOnMouseClicked((MouseEvent e) -> {
                Window window = entriesList.getScene().getWindow();
                TimeEntry selectedEntry = entriesList.getSelectionModel().getSelectedItem();
                EditWindow editWindow = new EditWindow(selectedEntry, window.getWidth() * 0.8);
                JFXDrawersStack drawersStack = (JFXDrawersStack) entriesList.getScene().getRoot();

                editWindow.setOnUpdateListener(this.onUpdateListener);
                drawersStack.toggle(editWindow.getComponent());
            });
        }
    }
}
