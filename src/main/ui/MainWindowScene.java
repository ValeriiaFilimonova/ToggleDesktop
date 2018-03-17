import com.jfoenix.controls.JFXListView;

import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class MainWindowScene {
    private Scene scene;
    private GridPane mainContainer = new GridPane();

    private RunningTimeEntryComponent runningTimeEntryComponent = new RunningTimeEntryComponent();
    private DaysListComponent daysListComponent = new DaysListComponent();

    public MainWindowScene() {
        GridPane runningTimeEntry = runningTimeEntryComponent.getComponent();
        JFXListView daysList = daysListComponent.getComponent();

        mainContainer.add(runningTimeEntry, 0, 0);
        mainContainer.add(daysList, 0, 1);

        GridPane.setVgrow(runningTimeEntry, Priority.NEVER);
        GridPane.setHgrow(runningTimeEntry, Priority.ALWAYS);
        GridPane.setVgrow(daysList, Priority.ALWAYS);

        scene = new Scene(mainContainer, 400, 500);
        scene.getStylesheets().add((getClass().getResource("/listView.css")).toExternalForm());
    }

    public Scene getScene() {
        return scene;
    }
}
