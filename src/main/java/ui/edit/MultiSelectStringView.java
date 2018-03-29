package ui.edit;

import com.jfoenix.controls.JFXListCell;
import com.jfoenix.controls.JFXListView;

import java.util.List;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import lombok.Getter;
import ui.IComponent;

public class MultiSelectStringView<T> implements IComponent {
    private static int SELECTED_ITEMS_PANE_GAP = 3;
    private static int SELECTED_ITEM_PADDING = 3;

    @Getter
    private SimpleListProperty<T> selectedItemsProperty = new SimpleListProperty<>(FXCollections.observableArrayList());

    private ObservableList<T> pickList = FXCollections.observableArrayList();

    private Image removeIcon = new Image(this.getClass().getResourceAsStream("/cross.png"), 10, 10, true, true);
    private FlowPane selectedItemsPane = this.initSelectedItemsPane();
    private JFXListView<T> suggestionList = this.initSuggestionList();
    private JFXListView<JFXListView<T>> mainList = this.initMainComponent();

    public MultiSelectStringView(T... itemsToPickFrom) {
        if (itemsToPickFrom != null) {
            pickList.addAll(itemsToPickFrom);
        }

        selectedItemsProperty.addListener((ListChangeListener<? super T>) (e) -> {
            e.next();

            if (e.wasAdded()) {
                List<? extends T> addedItems = e.getAddedSubList();

                for (T item : addedItems) {
                    pickList.remove(item);
                    selectedItemsPane.getChildren().add(initSelectedItemLabel(item));
                }
            }
            if (e.wasRemoved()) {
                List<? extends T> removedItems = e.getRemoved();

                for (T item : removedItems) {
                    pickList.add(item);
                    selectedItemsPane.getChildren().removeIf(node -> ((Label) node).getText().equals(item.toString()));
                }
            }
        });
    }

    @Override
    public JFXListView getComponent() {
        return mainList;
    }

    public SimpleListProperty<T> selectedItemsProperty() {
        return selectedItemsProperty;
    }

    private FlowPane initSelectedItemsPane() {
        FlowPane selectedItemsPane = new FlowPane();

        selectedItemsPane.setPadding(new Insets(0, 5, 0, 5));
        selectedItemsPane.setHgap(SELECTED_ITEMS_PANE_GAP);
        selectedItemsPane.setVgap(SELECTED_ITEMS_PANE_GAP);
        selectedItemsPane.setOnMouseClicked((e) -> {
            e.consume();
            ((FlowPane) e.getSource()).getChildren().forEach((l) -> l.fireEvent(e));
        });

        return selectedItemsPane;
    }

    private JFXListView<T> initSuggestionList() {
        JFXListView<T> suggestionList = new JFXListView<>();

        suggestionList.setItems(pickList);
        suggestionList.setGroupnode(selectedItemsPane);
        suggestionList.getStyleClass().add("suggestion-list");
        suggestionList.setCellFactory((item) -> new SuggestionCell<T>());
        suggestionList.setOnMouseClicked((e) -> {
            T selectedItem = suggestionList.getSelectionModel().getSelectedItem();

            if (selectedItem != null) {
                selectedItemsProperty.add(selectedItem);
            }
        });

        return suggestionList;
    }

    private JFXListView<JFXListView<T>> initMainComponent() {
        JFXListView<JFXListView<T>> mainList = new JFXListView<>();

        mainList.getItems().add(suggestionList);
        mainList.setCellFactory((list) -> new SubListCell<T>());
        mainList.getStyleClass().add("multi-select-list");

        return mainList;
    }

    private Label initSelectedItemLabel(T selectedItem) {
        Label label = new Label(selectedItem.toString(), initRemoveIcon());
        label.setPadding(new Insets(SELECTED_ITEM_PADDING));
        label.getStyleClass().add("picked-item");
        label.setTextAlignment(TextAlignment.CENTER);
        label.setOnMouseClicked((event) -> {
            event.consume();
            label.getGraphic().fireEvent(event);
        });

        return label;
    }

    private ImageView initRemoveIcon() {
        ImageView imageView = new ImageView(removeIcon);
        imageView.setOnMouseClicked((event) -> {
            ImageView view = (ImageView) event.getSource();
            Bounds bounds = view.localToScene(view.getBoundsInLocal());

            if (bounds.contains(event.getSceneX(), event.getSceneY())) {
                event.consume();
                Label clickedLabel = (Label) view.getParent();
                T selected = selectedItemsProperty.stream()
                    .filter((t) -> t.toString().equals(clickedLabel.getText()))
                    .findAny().orElse(null);

                if (selected != null) {
                    selectedItemsProperty.remove(selected);
                }
            }
        });

        return imageView;
    }

    private static class SuggestionCell<T> extends JFXListCell<T> {
        @Override
        protected void updateItem(T item, boolean empty) {
            super.updateItem(item, empty);

            if (item != null) {
                this.setText(item.toString());
                this.setGraphic(null);
                this.getStyleClass().add("suggestion-item");
            }
        }
    }

    private static class SubListCell<T> extends JFXListCell<JFXListView<T>> {
        @Override
        protected void updateItem(JFXListView<T> list, boolean empty) {
            super.updateItem(list, empty);

            if (list != null) {
                VBox contentHolder = (VBox) list.getParent().getParent();

                StackPane groupNode = (StackPane) contentHolder.getChildren().get(0);
                groupNode.getStyleClass().add("selected-items-container");
                VBox.setVgrow(groupNode, Priority.NEVER);

                FlowPane flowPane = (FlowPane) groupNode.getChildren().get(0);
                flowPane.setMaxWidth(500 * 0.8 * 0.8); // todo whatafuck
                StackPane.setAlignment(flowPane, Pos.CENTER_LEFT);
                StackPane.setMargin(flowPane, new Insets(0, 3, 0, 3));
            }
        }
    }
}
