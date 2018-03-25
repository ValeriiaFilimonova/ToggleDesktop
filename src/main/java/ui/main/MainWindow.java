package ui.main;

import com.jfoenix.controls.JFXButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import api.TimeEntry;
import api.ToggleClient;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

public class MainWindow {
    private static int DEFAULT_DAYS_COUNT = 3;

    private Scene scene;
    private GridPane mainContainer = new GridPane();
    private RunningTimeEntryComponent runningTimeEntryComponent = this.initRunningEntryComponent();
    private DaysListComponent daysListComponent = new DaysListComponent();
    private JFXButton showMoreButton = new JFXButton("Show more");

    public MainWindow() {
        runningTimeEntryComponent.setOnStopAction((entry) -> daysListComponent.addItem(entry));
        GridPane runningTimeEntry = runningTimeEntryComponent.getComponent();
        GridPane.setVgrow(runningTimeEntry, Priority.NEVER);
        GridPane.setHgrow(runningTimeEntry, Priority.ALWAYS);

        mainContainer.add(runningTimeEntry, 0, 0);
        mainContainer.add(daysListComponent.getComponent(), 0, 1);
        GridPane.setVgrow(daysListComponent.getComponent(), Priority.ALWAYS);

        showMoreButton.setVisible(false);
        showMoreButton.getStyleClass().add("show-more-button");
        showMoreButton.setOnAction((event) -> this.getMoreEntries(daysListComponent.getEarliestDate()));
        mainContainer.add(showMoreButton, 0, 2);
        GridPane.setVgrow(showMoreButton, Priority.NEVER);
        GridPane.setHalignment(showMoreButton, HPos.CENTER);

        mainContainer.getStyleClass().add("main-window");

        scene = new Scene(mainContainer, 500, 600);
        scene.getStylesheets().add((getClass().getResource("/styles.css")).toExternalForm());

        loadEntries();
    }

    public Scene getScene() {
        return scene;
    }

    private RunningTimeEntryComponent initRunningEntryComponent() {
        ToggleClient toggleClient = ToggleClient.getInstance();
        TimeEntry entry = toggleClient.getRunningTimeEntry();

        if (entry != null && entry.getId() != null) {
            return new RunningTimeEntryComponent(entry);
        }
        return new RunningTimeEntryComponent();
    }

    private void loadEntries() {
        LoadService service = new LoadService(Calendar.getInstance().getTime(), DEFAULT_DAYS_COUNT);
        service.setOnSucceeded(t -> {
            List<TimeEntry> timeEntries = (List<TimeEntry>) t.getSource().getValue();
            if (timeEntries.size() > 0) {
                showMoreButton.setVisible(true);
            }
            daysListComponent.addItems(timeEntries);
        });
        service.start();
    }

    @SneakyThrows
    private void getMoreEntries(String dateString) {
        LoadService service = new LoadService(new SimpleDateFormat(TimeEntry.SHORT_DATE_FORMAT).parse(dateString),
            DEFAULT_DAYS_COUNT + 2);
        service.setOnSucceeded(t -> {
            List<TimeEntry> timeEntries = (List<TimeEntry>) t.getSource().getValue();
            if (timeEntries.size() == 0) {
                showMoreButton.setVisible(false);
            }
            daysListComponent.addItems(timeEntries);
        });
        service.start();
    }

    @AllArgsConstructor
    private static class LoadService extends Service<List<TimeEntry>> {
        private Date date;
        private int count;

        @Override
        protected Task<List<TimeEntry>> createTask() {
            return new Task<List<TimeEntry>>() {
                @Override
                protected List<TimeEntry> call() {
                    ToggleClient toggleClient = ToggleClient.getInstance();
                    return toggleClient.getTimeEntriesBeforeDate(date, count);
                }
            };
        }
    }
}
