package ui.main;

import com.jfoenix.controls.JFXDrawersStack;
import com.jfoenix.controls.JFXListCell;

import api.TimeEntry;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Effect;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
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
                TimeEntry selectedEntry = entriesList.getSelectionModel().getSelectedItem();
                EditWindow editWindow = new EditWindow(selectedEntry, window.getWidth() * 0.8);
                JFXDrawersStack drawersStack = (JFXDrawersStack) entriesList.getScene().getRoot();

                drawersStack.toggle(editWindow.getComponent());
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
