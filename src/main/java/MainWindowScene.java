import com.jfoenix.controls.JFXListView;

import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class MainWindowScene {
    private Scene scene;
    private GridPane mainContainer = new GridPane();
    private GridPane runningTimeEntry = new RunningTimeEntryComponent().getComponent();

    public MainWindowScene() {
        scene = new Scene(mainContainer, 400, 500);

        mainContainer.add(runningTimeEntry, 0, 0);

        GridPane.setVgrow(runningTimeEntry, Priority.NEVER);
        GridPane.setHgrow(runningTimeEntry, Priority.ALWAYS);
    }

    public Scene getScene() {
        return scene;
    }
}
