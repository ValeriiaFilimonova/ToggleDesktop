package ui.main;

import com.jfoenix.controls.JFXListView;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import api.TimeEntry;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ui.IComponent;

public class DaysListComponent implements IComponent {
    private JFXListView<EntriesListComponent> list = new JFXListView<>();
    private ObservableList<EntriesListComponent> items = FXCollections.observableArrayList();

    public DaysListComponent() {
        list.setItems(items);
        list.getStyleClass().add("days-list");
        list.setCellFactory(daysList -> {
            DayCell dayCell = new DayCell();
            dayCell.setOnUpdateListener(this::updateItem);
            dayCell.setOnDeleteListener(this::deleteItem);
            dayCell.getStyleClass().add("days-list-cell");
            return dayCell;
        });
    }

    public JFXListView<EntriesListComponent> getComponent() {
        return this.list;
    }

    public void addItem(TimeEntry timeEntry) {
        addEntryToSubList(timeEntry);
        this.list.refresh();
    }

    public void updateItem(TimeEntry oldTimeEntry, TimeEntry newTimeEntry) {
        removeEntryFromSubList(oldTimeEntry);
        addEntryToSubList(newTimeEntry);
        this.list.refresh();
    }

    public void deleteItem(TimeEntry timeEntry) {
        removeEntryFromSubList(timeEntry);
        this.list.refresh();
    }

    public void addItems(List<TimeEntry> timeEntries) {
        timeEntries
            .stream()
            .filter((e) -> e.getDuration() >= 0)
            .collect(Collectors.toCollection(ArrayDeque::new))
            .descendingIterator()
            .forEachRemaining(this::addEntryToSubList);
        this.list.refresh();
    }

    public String getEarliestDate() {
        ObservableList<EntriesListComponent> items = list.getItems();
        EntriesListComponent lastSubList = items.get(items.size() - 1);
        return lastSubList.getId();
    }

    private void addEntryToSubList(TimeEntry timeEntry) {
        Optional<EntriesListComponent> subListOptional = this.list.getItems().stream()
            .filter((subList) -> subList.getId().equals(timeEntry.getDate()))
            .findFirst();

        if (subListOptional.isPresent()) {
            subListOptional.get().addEntry(timeEntry);
        } else {
            // TODO temp
            if (timeEntry.todaysEntry()) {
                items.add(0, new EntriesListComponent(timeEntry));
            } else {
                items.add(new EntriesListComponent(timeEntry));
            }
        }
    }

    private void removeEntryFromSubList(TimeEntry timeEntry) {
        Optional<EntriesListComponent> subListOptional = this.list.getItems().stream()
            .filter((subList) -> subList.getId().equals(timeEntry.getDate()))
            .findFirst();

        if (subListOptional.isPresent()) {
            subListOptional.get().removeEntry(timeEntry);
        }
    }
}
