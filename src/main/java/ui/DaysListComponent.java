package ui;

import com.jfoenix.controls.JFXListView;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import api.TimeEntry;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.text.TextAlignment;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.time.DateUtils;

@NoArgsConstructor
public class DaysListComponent implements IComponent {
    private static String LABEL_DATE_FORMAT = "EEE, d MMM";

    private JFXListView<JFXListView> list = new JFXListView<>();

    public JFXListView getComponent() {
        return this.list;
    }

    public void addItems(List<TimeEntry> timeEntries) {
        timeEntries
            .stream()
            .collect(Collectors.toCollection(ArrayDeque::new))
            .descendingIterator()
            .forEachRemaining((entry) -> {
                Optional<JFXListView> subListOptional = this.list.getItems().stream()
                    .filter((subList) -> subList.getId().equals(entry.getDate()))
                    .findFirst();

                if (subListOptional.isPresent()) {
                    JFXListView<TimeEntry> subList = subListOptional.get();
                    subList.getItems().add(entry);
                } else {
                    JFXListView<TimeEntry> subList = createSubList(entry.getDate());
                    subList.getItems().add(entry);
                    this.list.getItems().add(subList);
                }
            });
    }

    private JFXListView<TimeEntry> createSubList(String date) {
        JFXListView<TimeEntry> subList = new JFXListView<>();
        subList.setId(date);
        subList.setGroupnode(createSubListLabel(date));
        subList.setCellFactory(new TimeEntryCell.TimeEntryCellFactory());
        return subList;
    }

    @SneakyThrows
    private Label createSubListLabel(String dateString) {
        Date today = Calendar.getInstance().getTime();
        Date dateInstance = new SimpleDateFormat(TimeEntry.SHORT_DATE_FORMAT).parse(dateString);
        String labelText = new SimpleDateFormat(DaysListComponent.LABEL_DATE_FORMAT).format(dateInstance);

        if (DateUtils.isSameDay(dateInstance, today)) {
            labelText = "Today";
        }

        if (DateUtils.isSameDay(dateInstance, DateUtils.addDays(today, -1))) {
            labelText = "Yesterday";
        }

        Label label = new Label(labelText);
        label.setTextAlignment(TextAlignment.LEFT);
        label.setAlignment(Pos.CENTER_LEFT);
        label.getStyleClass().add("sub-list-label");

        return label;
    }
}
