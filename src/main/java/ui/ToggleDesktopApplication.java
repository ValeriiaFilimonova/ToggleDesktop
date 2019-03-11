package ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ui.main.MainWindow;
import ui.settings.SettingsWindow;

public class ToggleDesktopApplication extends Application {
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
//        Scene scene = new MainWindow().getScene();
        Scene scene = SettingsWindow.createScene();
        stage.setTitle("Toggle Desktop");
        stage.minWidthProperty().setValue(scene.getWidth());
        stage.setScene(scene);
        stage.show();
    }
}