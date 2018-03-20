package ui;

import com.jfoenix.controls.JFXListView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import api.TimeEntry;
import javafx.scene.control.Label;
import lombok.SneakyThrows;
import org.apache.commons.lang3.time.DateUtils;

// TODO get rid of scroll
public class EntriesListComponent extends JFXListView<TimeEntry> {
    private static String LABEL_DATE_FORMAT = "EEE, d MMM";

    private Label label = new Label();

    private Date date;
    private int sumDuration = 0;

    @SneakyThrows
    public EntriesListComponent(String dateString) {
        date = new SimpleDateFormat(TimeEntry.SHORT_DATE_FORMAT).parse(dateString);
        label.getStyleClass().add("sub-list-label");
        setId(dateString);
        setGroupnode(label);
        setCellFactory(new TimeEntryCell.TimeEntryCellFactory());
    }

    public EntriesListComponent(TimeEntry entry) {
        this(entry.getDate());
        addEntry(entry);
    }

    public void addEntry(TimeEntry entry) {
        sumDuration += entry.getDuration();
        getItems().add(entry);
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
