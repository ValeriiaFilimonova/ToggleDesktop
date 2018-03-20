package ui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import api.TimeEntry;
import api.ToggleClient;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import lombok.SneakyThrows;

public class MainWindowScene {
    private static int DEFAULT_ENTRIES_COUNT = 3;

    private Scene scene;
    private GridPane mainContainer = new GridPane();
    private RunningTimeEntryComponent runningTimeEntryComponent = new RunningTimeEntryComponent();
    private DaysListComponent daysListComponent = new DaysListComponent();
    private JFXButton showMoreButton = new JFXButton("Show more");

    public MainWindowScene() {
        GridPane runningTimeEntry = runningTimeEntryComponent.getComponent();
        GridPane.setVgrow(runningTimeEntry, Priority.NEVER);
        GridPane.setHgrow(runningTimeEntry, Priority.ALWAYS);

        mainContainer.add(runningTimeEntry, 0, 0);
        mainContainer.add(daysListComponent.getComponent(), 0, 1);
        GridPane.setVgrow(daysListComponent.getComponent(), Priority.ALWAYS);

        // TODO style the button
        showMoreButton.setVisible(false);
        showMoreButton.setOnAction((event) -> this.getMoreEntries(daysListComponent.getEarliestDate()));
        mainContainer.add(showMoreButton, 0, 2);
        GridPane.setVgrow(showMoreButton, Priority.NEVER);
        GridPane.setHalignment(showMoreButton, HPos.CENTER);

        scene = new Scene(mainContainer, 500, 600);
        scene.getStylesheets().add((getClass().getResource("/listView.css")).toExternalForm());

        loadEntries(); // TODO add progress bar
    }

    public Scene getScene() {
        return scene;
    }

    private void loadEntries() {
        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() {
                ToggleClient toggleClient = ToggleClient.getInstance();
                List<TimeEntry> timeEntries =
                    toggleClient.getTimeEntriesBeforeDate(Calendar.getInstance().getTime(), DEFAULT_ENTRIES_COUNT);
                daysListComponent.addItems(timeEntries);
                showMoreButton.setVisible(true);
                return null;
            }
        };

        new Thread(task).start();
    }

    @SneakyThrows
    private void getMoreEntries(String dateString) {
        Date dateInstance = new SimpleDateFormat(TimeEntry.SHORT_DATE_FORMAT).parse(dateString);

        // TODO try to use smth not blocking
        Platform.runLater(() -> {
                ToggleClient toggleClient = ToggleClient.getInstance();
                List<TimeEntry> timeEntries =
                    toggleClient.getTimeEntriesBeforeDate(dateInstance, DEFAULT_ENTRIES_COUNT + 2);
                daysListComponent.addItems(timeEntries);
        });
    }
}
