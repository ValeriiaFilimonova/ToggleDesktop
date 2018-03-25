package ui.main;

import com.jfoenix.controls.JFXListCell;
import com.jfoenix.controls.JFXPopup;

import api.TimeEntry;
import javafx.scene.Group;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Window;
import javafx.util.Callback;
import ui.edit.EditWindow;

public class DayCell extends JFXListCell<EntriesListComponent> {
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
                Group parent = (Group) entriesList.getParent().getParent().getParent().getParent();
                TimeEntry selectedEntry = entriesList.getSelectionModel().getSelectedItem();
                EditWindow editWindow = new EditWindow(selectedEntry, window.getWidth() * 0.8);
                JFXPopup popup = editWindow.getWindow();

                popup.show(parent, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT, window.getWidth(), 0);
            });
        }
    }

    public static class DayCellFactory implements
        Callback<ListView<EntriesListComponent>, ListCell<EntriesListComponent>> {
        @Override
        public ListCell<EntriesListComponent> call(ListView<EntriesListComponent> daysList) {
            DayCell dayCell = new DayCell();
            dayCell.getStyleClass().add("days-list-cell");
            return dayCell;
        }
    }
}
