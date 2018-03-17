package ui;

import com.jfoenix.controls.JFXListView;

import java.util.Calendar;

import api.ToggleClient;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class MainWindowScene {
    private Scene scene;

    private RunningTimeEntryComponent runningTimeEntryComponent = new RunningTimeEntryComponent();
    private DaysListComponent daysListComponent = new DaysListComponent();

    public MainWindowScene() {
        ToggleClient toggleClient = ToggleClient.getInstance();
        daysListComponent.addItems(toggleClient.getPreviousTimeEntries(Calendar.getInstance().getTime(), 3));
        scene = new Scene(initMainContainer(), 400, 500);
        scene.getStylesheets().add((getClass().getResource("/listView.css")).toExternalForm());
    }

    public Scene getScene() {
        return scene;
    }

    private GridPane initMainContainer() {
        GridPane runningTimeEntry = runningTimeEntryComponent.getComponent();
        GridPane.setVgrow(runningTimeEntry, Priority.NEVER);
        GridPane.setHgrow(runningTimeEntry, Priority.ALWAYS);

        JFXListView daysList = daysListComponent.getComponent();
        GridPane.setVgrow(daysList, Priority.ALWAYS);

        GridPane mainContainer = new GridPane();
        mainContainer.add(runningTimeEntry, 0, 0);
        mainContainer.add(daysList, 0, 1);
        return mainContainer;
    }
}
