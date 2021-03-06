package ui.main;

import com.jfoenix.controls.JFXListView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import api.TimeEntry;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.lang3.time.DateUtils;

public class EntriesListComponent extends JFXListView<TimeEntry> {
    private static String LABEL_DATE_FORMAT = "EEE, d MMM";

    private HBox groupNode;
    private Label durationLabel = new Label("");
    private Label warningLabel = new Label("!!");
    private ObservableList<TimeEntry> items = FXCollections.observableArrayList();
    private Date date;
    private int sumDuration = 0;
    @Getter
    private int entriesWithErrorsCount = 0;

    @SneakyThrows
    public EntriesListComponent(String dateString) {
        date = new SimpleDateFormat(TimeEntry.SHORT_DATE_FORMAT).parse(dateString);
        groupNode = initGroupNode();

        setId(dateString);
        setItems(items);
        setGroupnode(groupNode);
        getStyleClass().add("entries-list");
        setCellFactory(new TimeEntryCell.TimeEntryCellFactory());

        StackPane.setAlignment(groupNode, Pos.CENTER_LEFT);
    }

    public EntriesListComponent(TimeEntry entry) {
        this(entry.getDate());
        addEntry(entry);
    }

    public void addEntry(TimeEntry entry) {
        sumDuration += entry.getDuration();

        if (entry.hasSpellingErrors()) {
            entriesWithErrorsCount++;
            warningLabel.setVisible(true);
        }

        items.add(entry);
        items.sort((entry1, entry2) -> entry2.compareTo(entry1));
    }

    public void removeEntry(TimeEntry entry) {
        sumDuration -= entry.getDuration();

        if (entry.hasSpellingErrors()) {
            entriesWithErrorsCount--;
            if (entriesWithErrorsCount == 0) {
                warningLabel.setVisible(false);
            }
        }

        items.remove(entry);
    }

    public void updateLabelText() {
        durationLabel.setText(TimeEntry.formatDuration(sumDuration));
    }

    private HBox initGroupNode() {
        Label dayLabel = new Label(getDateText());
        dayLabel.getStyleClass().add("entries-list-header-day");
        dayLabel.setPadding(new Insets(0, 10, 0, 10));

        durationLabel.getStyleClass().add("entries-list-header-duration");
        warningLabel.getStyleClass().add("entries-list-header-warning");
        warningLabel.setVisible(false);

        HBox container = new HBox(dayLabel, durationLabel, warningLabel);
        container.getStyleClass().add("entries-list-header");
        return container;
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
