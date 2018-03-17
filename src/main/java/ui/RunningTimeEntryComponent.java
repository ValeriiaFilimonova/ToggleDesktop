package ui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;

import javafx.geometry.Insets;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class RunningTimeEntryComponent implements IComponent {
    static Color DARK_GRAY = Color.rgb(45, 43, 44);
    static Color LIGHT_GREEN = Color.rgb(77, 217, 101);

    private GridPane horizontalContainer = new GridPane();

    public RunningTimeEntryComponent() {
        JFXTextField entryTextBox = this.initEntryTextBox();
        JFXButton timerButton = this.initTimerButton();
        JFXButton startButton = this.initStartButton();

        horizontalContainer.setPadding(new Insets(10));
        horizontalContainer.add(entryTextBox, 0,0);
        horizontalContainer.add(timerButton, 1, 0);
        horizontalContainer.add(startButton, 2, 0);
        horizontalContainer.setBackground(new Background(new BackgroundFill(DARK_GRAY, CornerRadii.EMPTY, Insets.EMPTY)));
    }

    public GridPane getComponent() {
        return this.horizontalContainer;
    }

    private JFXTextField initEntryTextBox() {
        JFXTextField entryTextBox = new JFXTextField();

        entryTextBox.setFocusTraversable(false);
        entryTextBox.setPromptText("What are you doing?");
        entryTextBox.setFocusColor(Color.WHITE);
        entryTextBox.setUnFocusColor(Color.LIGHTGREY);
        entryTextBox.setStyle("-fx-text-fill: white; -fx-prompt-text-fill: lightgray;");

        GridPane.setHgrow(entryTextBox, Priority.ALWAYS);

        return entryTextBox;
    }

    private JFXButton initTimerButton() {
        JFXButton timerButton = new JFXButton();

        timerButton.setText("00:00:00");
        timerButton.setTextFill(Color.WHITE);
        timerButton.setFocusTraversable(false);

        GridPane.setHgrow(timerButton, Priority.NEVER);

        return timerButton;
    }

    private JFXButton initStartButton() {
        JFXButton startButton = new JFXButton();

        startButton.setText("Start");
        startButton.setTextFill(Color.WHITE);
        startButton.setBackground(new Background(new BackgroundFill(LIGHT_GREEN, CornerRadii.EMPTY, Insets.EMPTY)));

        GridPane.setHgrow(startButton, Priority.NEVER);

        return startButton;
    }
}
