import com.jfoenix.controls.JFXListView;

import javafx.scene.control.Label;

public class DaysListComponent implements IComponent {
    private JFXListView<JFXListView> list = new JFXListView<>();
    private JFXListView<Label> subList = new JFXListView<>();

    public DaysListComponent() {
        for (int i = 0; i < 4; i++) {
            this.subList.getItems().add(new Label("SubItem " + i));
        }
        this.list.getItems().add(this.subList);
    }

    public JFXListView getComponent() {
        return this.list;
    }
}
