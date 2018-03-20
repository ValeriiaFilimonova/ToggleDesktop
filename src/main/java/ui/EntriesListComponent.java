package ui;

import com.jfoenix.controls.JFXListView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import api.TimeEntry;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import lombok.SneakyThrows;
import org.apache.commons.lang3.time.DateUtils;

public class EntriesListComponent extends JFXListView<TimeEntry> {
    private static String LABEL_DATE_FORMAT = "EEE, d MMM";

    private Label label = new Label();
    private ObservableList<TimeEntry> items = FXCollections.observableArrayList();
    private Date date;
    private int sumDuration = 0;

    @SneakyThrows
    public EntriesListComponent(String dateString) {
        date = new SimpleDateFormat(TimeEntry.SHORT_DATE_FORMAT).parse(dateString);
        label.getStyleClass().add("sub-list-label");

        setId(dateString);
        setItems(items);
        setGroupnode(label);
        setCellFactory(new TimeEntryCell.TimeEntryCellFactory());

        StackPane.setAlignment(label, Pos.CENTER_LEFT);
    }

    public EntriesListComponent(TimeEntry entry) {
        this(entry.getDate());
        addEntry(entry);
    }

    public void addEntry(TimeEntry entry) {
        sumDuration += entry.getDuration();
        items.add(entry);
    }

    public void updateLabelText() {
        String labelText = getDateText() + " " + TimeEntry.formatDuration(sumDuration);
        label.setText(labelText);
    }

    @SneakyThrows
    private String getDateText() {
        Date today = Calendar.getInstance().getTime();

        if (DateUtils.isSameDay(date, today)) {
            return "Today";
        }

        if (DateUtils.isSameDay(date, DateUtils.addDays(today, -1))) {
            return "Yesterday";
        }

        return new SimpleDateFormat(LABEL_DATE_FORMAT).format(date);
    }
}
