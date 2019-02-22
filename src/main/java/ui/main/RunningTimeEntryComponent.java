package ui.main;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;

import java.util.function.Consumer;

import api.TimeEntry;
import api.ToggleClient;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.joda.time.Interval;
import org.joda.time.Period;
import ui.IComponent;

public class RunningTimeEntryComponent implements IComponent {
    private static String DEFAULT_TIMER_LABEL_TEXT = "00:00:00";

    private GridPane horizontalContainer = new GridPane();
    private JFXTextField entryTextBox = this.initEntryTextBox();
    private Label timerLabel = this.initTimerLabel();
    private JFXButton startButton = this.initStartButton();
    private JFXButton stopButton = this.initStopButton();
    private Timeline timer = this.initTimer();

    private StringProperty entryDescription = new SimpleStringProperty();
    private long startTime;
    private TimeEntry timeEntry;

    private Consumer<TimeEntry> stopClickListener;

    private ToggleClient toggleClient = ToggleClient.getInstance();

    public RunningTimeEntryComponent() {
        entryDescription.bindBidirectional(entryTextBox.textProperty());

        horizontalContainer.setPadding(new Insets(10));
        horizontalContainer.add(entryTextBox, 0, 0);
        horizontalContainer.add(timerLabel, 1, 0);
        horizontalContainer.add(startButton, 2, 0);
        horizontalContainer.add(stopButton, 2, 0);
        horizontalContainer.getStyleClass().add("running-entry-container");
    }

    public RunningTimeEntryComponent(TimeEntry entry) {
        this();
        timeEntry = entry;
        entryDescription.setValue(entry.getDescription());
        startTime = entry.getStart().getTime();
        startButton.setVisible(false);
        stopButton.setVisible(true);
        timer.play();
    }

    public GridPane getComponent() {
        return this.horizontalContainer;
    }

    public void setOnStopAction(Consumer<TimeEntry> listener) {
        stopClickListener = listener;
    }

    private JFXTextField initEntryTextBox() {
        entryTextBox = new JFXTextField();
        entryTextBox.setFocusTraversable(false);
        entryTextBox.setPromptText("What are you doing?");
        entryTextBox.setFocusColor(Color.WHITE);
        entryTextBox.setUnFocusColor(Color.LIGHTGREY);
        entryTextBox.getStyleClass().add("description-text-input");

        GridPane.setHgrow(entryTextBox, Priority.ALWAYS);

        return entryTextBox;
    }

    private Label initTimerLabel() {
        timerLabel = new Label();
        timerLabel.setText(DEFAULT_TIMER_LABEL_TEXT);
        timerLabel.setTextFill(Color.WHITE);
        timerLabel.setFocusTraversable(false);
        timerLabel.setPadding(new Insets(0, 10, 0, 10));

        GridPane.setHgrow(timerLabel, Priority.NEVER);

        return timerLabel;
    }

    private JFXButton initStartButton() {
        startButton = new JFXButton();
        startButton.setText("Start");
        startButton.getStyleClass().add("common-button");
        startButton.setOnAction((event) -> {
            timeEntry = toggleClient.startTimeEntry();
            startTime = System.currentTimeMillis();
            timer.play();
            startButton.setVisible(false);
            stopButton.setVisible(true);
        });

        GridPane.setHgrow(startButton, Priority.NEVER);

        return startButton;
    }

    private JFXButton initStopButton() {
        stopButton = new JFXButton();
        stopButton.setText("Stop");
        stopButton.setVisible(false);
        stopButton.getStyleClass().add("common-button");
        stopButton.setOnAction((event) -> {
            timer.stop();
            timeEntry.setDuration((int)((System.currentTimeMillis() - startTime) / 1000));
            timeEntry.setDescription(entryDescription.getValue());
            timeEntry = toggleClient.updateTimeEntry(timeEntry);
            startTime = 0;
            entryTextBox.clear();
            startButton.setVisible(true);
            stopButton.setVisible(false);
            timerLabel.setText(DEFAULT_TIMER_LABEL_TEXT);

            if (stopClickListener != null) {
                stopClickListener.accept(timeEntry);
            }
        });

        GridPane.setHgrow(stopButton, Priority.NEVER);

        return stopButton;
    }

    private Timeline initTimer() {
        KeyFrame frame = new KeyFrame(Duration.seconds(1), (event) -> {
            Interval interval = new Interval(startTime, System.currentTimeMillis());
            Period period = interval.toPeriod();
            String text = String.format("%02d:%02d:%02d", period.getHours(), period.getMinutes(), period.getSeconds());
            timerLabel.setText(text);
        });
        Timeline timeline = new Timeline(frame);
        timeline.setCycleCount(Timeline.INDEFINITE);
        return timeline;
    }
}