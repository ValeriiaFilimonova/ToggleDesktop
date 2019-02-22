package ui.main;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawersStack;

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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import reports.ReportBuilder;
import ui.reports.ReportErrorsWindow;
import ui.settings.SettingsWindow;

public class MainWindow {
    private static int DEFAULT_DAYS_COUNT = 1;

    private RunningTimeEntryComponent runningTimeEntryComponent = this.initRunningEntryComponent();
    private DaysListComponent daysListComponent = new DaysListComponent();

    private Image exportIcon = new Image(this.getClass().getResourceAsStream("/export.png"), 30, 30, true, true);
    private Image settingsIcon = new Image(this.getClass().getResourceAsStream("/settings.png"), 30, 30, true, true);

    private Scene scene;
    private GridPane mainContainer = new GridPane();
    private JFXButton showMoreButton = new JFXButton("Show more");
    private JFXButton exportButton = new JFXButton(null, new ImageView(exportIcon));
    private JFXButton settingsButton = new JFXButton(null, new ImageView(settingsIcon));

    public MainWindow() {
        runningTimeEntryComponent.setOnStopAction((entry) -> daysListComponent.addItem(entry));
        GridPane.setVgrow(runningTimeEntryComponent.getComponent(), Priority.NEVER);
        GridPane.setHgrow(runningTimeEntryComponent.getComponent(), Priority.ALWAYS);

        GridPane.setVgrow(daysListComponent.getComponent(), Priority.ALWAYS);
        GridPane.setHgrow(daysListComponent.getComponent(), Priority.ALWAYS);

        showMoreButton.setVisible(false);
        showMoreButton.getStyleClass().add("show-more-button");
        showMoreButton.setOnAction((event) -> this.getMoreEntries(daysListComponent.getEarliestDate()));
        GridPane.setVgrow(showMoreButton, Priority.NEVER);
        GridPane.setHgrow(showMoreButton, Priority.ALWAYS);
        GridPane.setHalignment(showMoreButton, HPos.CENTER);

        exportButton.getStyleClass().add("export-button");
        exportButton.setOnAction((event) -> {
            try {
                ReportBuilder.build();
            } catch (ReportBuilder.BuildReportException exception) {
                ReportErrorsWindow errorsWindow = new ReportErrorsWindow(exception.getErrors(), 250);
                JFXDrawersStack drawersStack = (JFXDrawersStack) getScene().getRoot();
                drawersStack.toggle(errorsWindow.getComponent());
            }
        });

        settingsButton.getStyleClass().add("settings-button");
        settingsButton.setOnAction((event) -> {
            Stage stage = new Stage();
            stage.setTitle("Settings");
            stage.setScene(SettingsWindow.create());
            stage.setX(scene.getWindow().getX() + 50);
            stage.setY(scene.getWindow().getY() + 50);
            stage.show();
        });

        mainContainer.add(runningTimeEntryComponent.getComponent(), 0, 0, 3, 1);
        mainContainer.add(daysListComponent.getComponent(), 0, 1, 3, 1);
        mainContainer.add(exportButton, 0, 2);
        mainContainer.add(showMoreButton, 1, 2);
        mainContainer.add(settingsButton, 2, 2);
        mainContainer.getStyleClass().add("main-window");

        JFXDrawersStack drawersStack = new JFXDrawersStack();
        drawersStack.setContent(mainContainer);

        scene = new Scene(drawersStack, 500, 600);
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
        Date date = new SimpleDateFormat(TimeEntry.SHORT_DATE_FORMAT).parse(dateString);
        LoadService service = new LoadService(date, DEFAULT_DAYS_COUNT + 2);
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
