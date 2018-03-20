package ui;

import com.jfoenix.controls.JFXListView;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import api.TimeEntry;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DaysListComponent implements IComponent {
    private JFXListView<EntriesListComponent> list = new JFXListView<>();
    private ObservableList<EntriesListComponent> items = FXCollections.observableArrayList();

    public DaysListComponent() {
        list.setItems(items);
        list.setCellFactory(new DayCell.DayCellFactory());
    }

    public JFXListView<EntriesListComponent> getComponent() {
        return this.list;
    }

    public void addItems(List<TimeEntry> timeEntries) {
        timeEntries
            .stream()
            .collect(Collectors.toCollection(ArrayDeque::new))
            .descendingIterator()
            .forEachRemaining((entry) -> {
                Optional<EntriesListComponent> subListOptional = this.list.getItems().stream()
                    .filter((subList) -> subList.getId().equals(entry.getDate()))
                    .findFirst();

                if (subListOptional.isPresent()) {
                    subListOptional.get().addEntry(entry);
                } else {
                    items.add(new EntriesListComponent(entry));
                }
            });
    }

    public String getEarliestDate() {
        ObservableList<EntriesListComponent> items = list.getItems();
        EntriesListComponent lastSubList = items.get(items.size() - 1);
        return lastSubList.getId();
    }
}
