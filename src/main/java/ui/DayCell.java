package ui;

import com.jfoenix.controls.JFXListCell;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class DayCell extends JFXListCell<EntriesListComponent> {
    @Override
    protected void updateItem(EntriesListComponent entriesList, boolean empty) {
        if (entriesList == null) {
            super.updateItem(entriesList, true);
        } else {
            entriesList.updateLabelText();
            entriesList.setPrefHeight(40.5 * entriesList.getItems().size()); // TODO get rif of magic number

            super.updateItem(entriesList, empty);
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
