package ui.edit;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXTimePicker;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import api.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.util.Duration;
import javafx.util.StringConverter;
import lombok.Setter;
import net.loomchild.segment.util.IORuntimeException;
import org.fxmisc.richtext.StyleClassedTextArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.languagetool.JLanguageTool;
import org.languagetool.language.BritishEnglish;
import org.languagetool.rules.RuleMatch;
import ui.IComponent;

public class EditWindow implements IComponent {
    static Project EMPTY_PROJECT = new Project("No project", "No project");

    private static int GRID_GAP = 10;
    private static int ICON_SIZE = 15;

    private JFXDrawer drawer = new JFXDrawer();
    private GridPane gridPane = this.initGridPane();
    private ImageView closeButton = this.initCloseButton();
    private StyleClassedTextArea descriptionInput = this.initDescriptionInput();
    private JFXComboBox<Project> projectSelectInput = this.initProjectSelectInput();
    private Label companyLabel = this.initCompanyLabel();
    private JFXTimePicker startTime = new JFXTimePicker();
    private JFXTimePicker endTime = new JFXTimePicker();
    private Label durationLabel = this.initDurationLabel();
    private JFXDatePicker datePicker = this.initDatePicker();
    private MultiSelectStringView<Tag> tagsSelectInput = this.initTagsSelectInput();
    private ImageView deleteButton = this.initDeleteButton();

    private EditableTimeEntry editableEntry;
    @Setter
    private BiConsumer<TimeEntry, TimeEntry> onUpdateListener;
    @Setter
    private Consumer<TimeEntry> onDeleteListener;

    private JLanguageTool languageTool = new JLanguageTool(new BritishEnglish());

    private int inputChangeCounter = 0;
    private int spellCheckCounter = 0;

    private KeyFrame spellCheckFrame = new KeyFrame(Duration.seconds(5), (event) -> {
        if (inputChangeCounter != spellCheckCounter) {
            spellCheckCounter = inputChangeCounter;
            try {
                String text = editableEntry.getDescriptionProperty().getValue();
                StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
                List<RuleMatch> matches = languageTool.check(text);
                int lastChecked = 0;

                for (RuleMatch match : matches) {
                    int start = match.getFromPos();
                    int end = match.getToPos();

                    spansBuilder.add(Collections.emptyList(), start - lastChecked);
                    spansBuilder.add(Collections.singleton("underlined"), end - start);
                    lastChecked = end;

                    System.out.println(String.format("Error %d-%d: %s", start, end, text.substring(start, end)));
                }

                if (lastChecked != text.length()) {
                    spansBuilder.add(Collections.emptyList(), text.length() - lastChecked);
                }

                StyleSpans<Collection<String>> spans = spansBuilder.create();
                descriptionInput.setStyleSpans(0, spans);
            } catch (IOException e) {
                throw new IORuntimeException(e);
            }
        }
    });

    private Timeline timeline = new Timeline(spellCheckFrame);

    public EditWindow(TimeEntry entry, double width) {
        editableEntry = new EditableTimeEntry(entry);

        // bind properties

        descriptionInput.replaceText(0, 0, entry.getDescription());
        descriptionInput.textProperty().addListener((obs, oldValue, newValue) -> {
            editableEntry.getDescriptionProperty().setValue(newValue);
            inputChangeCounter++;
        });
        projectSelectInput.valueProperty().bindBidirectional(editableEntry.getProjectProperty());
        companyLabel.textProperty().bind(editableEntry.getCompanyProperty());
        durationLabel.textProperty().bind(editableEntry.getDurationProperty());
        startTime.valueProperty().bindBidirectional(editableEntry.getStartTimeProperty());
        endTime.valueProperty().bindBidirectional(editableEntry.getEndTimeProperty());
        datePicker.valueProperty().bindBidirectional(editableEntry.getDateProperty());
        tagsSelectInput.selectedItemsProperty().bindBidirectional(editableEntry.getTagsProperty());

        // place elements in grid

        gridPane.add(closeButton, 2, 0);
        gridPane.add(descriptionInput, 0, 1, 3, 1);
        gridPane.add(projectSelectInput, 0, 2, 2, 1);
        gridPane.add(companyLabel, 2, 2);
        gridPane.add(startTime, 0, 3);
        gridPane.add(durationLabel, 1, 3);
        gridPane.add(endTime, 2, 3);
        gridPane.add(datePicker, 1, 4);
        gridPane.add(tagsSelectInput.getComponent(), 0, 5, 3, 1);
        gridPane.add(deleteButton, 2, 6);

        RowConstraints descriptionRow = new RowConstraints() {{
            setPercentHeight(30);
        }};

        gridPane.setPrefWidth(width);
        gridPane.getRowConstraints().addAll(new RowConstraints(), descriptionRow);

        // init drawer

        drawer.setDirection(JFXDrawer.DrawerDirection.RIGHT);
        drawer.setDefaultDrawerSize(width);
        drawer.setSidePane(gridPane);
        drawer.setOverLayVisible(true);
        drawer.setOnDrawerClosed(this::onClose);

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public JFXDrawer getComponent() {
        return drawer;
    }

    private GridPane initGridPane() {
        GridPane gridPane = new GridPane();

        gridPane.setPadding(new Insets(10, 20, 10, 20));
        gridPane.getStyleClass().add("edit-container");
        gridPane.setVgap(GRID_GAP);
        gridPane.setHgap(GRID_GAP);

        return gridPane;
    }

    private ImageView initCloseButton() {
        Image closeIcon =
            new Image(this.getClass().getResourceAsStream("/cross.png"), ICON_SIZE, ICON_SIZE, true, true);
        ImageView closeButton = new ImageView(closeIcon);

        closeButton.setOnMouseClicked(e -> drawer.close());

        GridPane.setHalignment(closeButton, HPos.RIGHT);
        GridPane.setValignment(closeButton, VPos.TOP);
        GridPane.setMargin(closeButton, new Insets(0, 0, 20, 0));

        return closeButton;
    }

    private StyleClassedTextArea initDescriptionInput() {
        StyleClassedTextArea descriptionInput = new StyleClassedTextArea();

        descriptionInput.setWrapText(true);
        descriptionInput.setFocusTraversable(true);
        descriptionInput.getStyleClass().add("entry-edit-description");

        GridPane.setHgrow(descriptionInput, Priority.ALWAYS);

        return descriptionInput;
    }

    private JFXComboBox<Project> initProjectSelectInput() {
        ObservableList<Project> projects = FXCollections.observableArrayList(EMPTY_PROJECT);
        projects.addAll(Cache.getInstance().getAllProjects());

        JFXComboBox<Project> projectSelectInput = new JFXComboBox<>();

        projectSelectInput.setItems(projects);
        projectSelectInput.getSelectionModel().selectFirst();
        projectSelectInput.getStyleClass().add("entry-edit-project");
        projectSelectInput.setCellFactory(new ProjectCell.ProjectCellFactory());
        projectSelectInput.prefWidthProperty().bind(gridPane.prefWidthProperty().multiply(0.75));

        GridPane.setHgrow(projectSelectInput, Priority.ALWAYS);

        return projectSelectInput;
    }

    private Label initCompanyLabel() {
        Label companyLabel = new Label("No Company");

        GridPane.setHgrow(companyLabel, Priority.NEVER);

        return companyLabel;
    }

    private Label initDurationLabel() {
        Label durationLabel = new Label("00:00:00");

        durationLabel.getStyleClass().add("entry-edit-duration");

        GridPane.setHalignment(durationLabel, HPos.CENTER);

        return durationLabel;
    }

    private JFXDatePicker initDatePicker() {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        JFXDatePicker datePicker = new JFXDatePicker();

        datePicker.prefWidthProperty().bind(gridPane.prefWidthProperty().multiply(0.55));
        datePicker.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                if (date == null) {
                    return null;
                }
                return formatter.format(date);
            }

            @Override
            public LocalDate fromString(String dateString) {
                if (dateString == null || dateString.trim().isEmpty()) {
                    return null;
                }
                return LocalDate.parse(dateString, formatter);
            }
        });

        GridPane.setHalignment(datePicker, HPos.LEFT);

        return datePicker;
    }

    private MultiSelectStringView<Tag> initTagsSelectInput() {
        MultiSelectStringView<Tag> tagsSelectInput = new MultiSelectStringView<>(Tag.values());

        GridPane.setVgrow(tagsSelectInput.getComponent(), Priority.ALWAYS);

        return tagsSelectInput;
    }

    private ImageView initDeleteButton() {
        InputStream input = this.getClass().getResourceAsStream("/trash.png");
        Image deleteIcon = new Image(input, ICON_SIZE, ICON_SIZE, true, true);
        ImageView deleteButton = new ImageView(deleteIcon);

        deleteButton.setOnMouseClicked(e -> {
            TimeEntry entryToDelete = editableEntry.getOriginalEntry();
            ToggleClient.getInstance().deleteTimeEntry(entryToDelete);
            editableEntry = null;
            drawer.close();

            if (onDeleteListener != null) {
                onDeleteListener.accept(entryToDelete);
            }
        });

        GridPane.setHalignment(deleteButton, HPos.RIGHT);
        GridPane.setValignment(deleteButton, VPos.BOTTOM);
        GridPane.setMargin(deleteButton, new Insets(20, 0, 0, 0));

        return deleteButton;
    }

    private void onClose(Event event) {
        if (editableEntry != null && editableEntry.isEntryChanged()) {
            TimeEntry updatedEntry = editableEntry.getUpdatedTimeEntry();
            TimeEntry entryToUpdate = ToggleClient.getInstance().updateTimeEntry(updatedEntry);

            if (onUpdateListener != null) {
                onUpdateListener.accept(editableEntry.getOriginalEntry(), entryToUpdate);
            }
        }

        timeline.stop();
    }
}
