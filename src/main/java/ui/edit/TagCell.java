package ui.edit;

import api.Tag;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class TagCell extends ListCell<Tag> {
    @Override
    protected void updateItem(Tag tag, boolean empty) {
        super.updateItem(tag, empty);

        if (tag == null) {
            return;
        }

        setGraphic(new Label(tag.toValue()));
    }

    public static class TagCellFactory implements Callback<ListView<Tag>, ListCell<Tag>> {
        @Override
        public ListCell<Tag> call(ListView<Tag> list) {
            return new TagCell();
        }
    }
}
