import com.jfoenix.controls.JFXListView;

import java.util.Calendar;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.text.TextAlignment;

public class DaysListComponent implements IComponent {
    private JFXListView<JFXListView> list = new JFXListView<>();

    public DaysListComponent() {
        this.list.getItems().addAll(
            this.getSubList("Today"),
            this.getSubList("Yesterday")
        );
    }

    public JFXListView getComponent() {
        return this.list;
    }

    private JFXListView<TimeEntry> getSubList(String labelText) {
        JFXListView<TimeEntry> subList = new JFXListView<>();

        subList.getItems().addAll(
            generateTimeEntry(0),
            generateTimeEntry(1),
            generateTimeEntry(2)
        );

        subList.setCellFactory(new TimeEntryCell.TimeEntryCellFactory());

        Label label = new Label(labelText);
        label.setTextAlignment(TextAlignment.LEFT);
        label.setAlignment(Pos.CENTER_LEFT);
        label.setStyle("-fx-text-fill: rgb(77, 77, 77); -fx-font-weight: bold;");

        subList.setGroupnode(label);

        return subList;
    }

    private TimeEntry generateTimeEntry(int index) {
        return TimeEntry.builder()
            .description("Some text really really long super large text " + index)
            .tags(new Tag[] {Tag.ADMINISTRATION, Tag.COMMUNICATION})
            .start(Calendar.getInstance().getTime())
            .build();
    }
}
