package ui.edit;

import com.jfoenix.controls.JFXListCell;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.svg.SVGGlyph;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
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

public class MultiSelectStringView implements IComponent {
    private static int SELECTED_ITEMS_PANE_GAP = 3;
    private static int SELECTED_ITEM_PADDING = 3;

    @Getter
    private SimpleListProperty<String> selectedItemsProperty =
        new SimpleListProperty<>(FXCollections.observableArrayList());

    private ObservableList<String> pickList = FXCollections.observableArrayList();

    private Image removeIcon = new Image(this.getClass().getResourceAsStream("/cross.png"), 10, 10, true, true);
    private FlowPane selectedItemsPane = this.initSelectedItemsNode();
    private JFXListView<String> suggestionList = this.initSuggestionList();
    private JFXListView<JFXListView<String>> mainList = this.initMainComponent();

    public MultiSelectStringView(String... itemsToPickFrom) {
        if (itemsToPickFrom != null) {
            pickList.addAll(itemsToPickFrom);
        }
    }

    @Override
    public JFXListView getComponent() {
        return mainList;
    }

    public SimpleListProperty<String> selectedItemsProperty() {
        return selectedItemsProperty;
    }

    private FlowPane initSelectedItemsNode() {
        FlowPane selectedItemsPane = new FlowPane();

        selectedItemsPane.setPadding(new Insets(0, 5, 0, 5));
        selectedItemsPane.getStyleClass().add("selected-items-container");
        selectedItemsPane.setHgap(SELECTED_ITEMS_PANE_GAP);
        selectedItemsPane.setVgap(SELECTED_ITEMS_PANE_GAP);
        selectedItemsPane.setOnMouseClicked((e) -> {
            e.consume();
            ((FlowPane) e.getSource()).getChildren().forEach((l) -> l.fireEvent(e));
        });

        return selectedItemsPane;
    }

    private JFXListView<String> initSuggestionList() {
        JFXListView<String> suggestionList = new JFXListView<>();

        suggestionList.setItems(pickList);
        suggestionList.setGroupnode(selectedItemsPane);
        suggestionList.getStyleClass().add("suggestion-list");
        suggestionList.setOnMouseClicked((e) -> {
            String selectedItem = suggestionList.getSelectionModel().getSelectedItem();
            pickList.remove(selectedItem);

            if (pickList.size() == 0) {
                mainList.setExpanded(false);
            }

            if (selectedItem != null) {
                ImageView imageView = new ImageView(removeIcon);
                imageView.setOnMouseClicked((event) -> {
                    event.consume();
                    ImageView view = (ImageView) event.getSource();
                    Bounds bounds = view.localToScene(view.getBoundsInLocal());

                    if (bounds.contains(event.getSceneX() - SELECTED_ITEM_PADDING, event.getSceneY() - SELECTED_ITEM_PADDING)) {
                        Label clickedLabel = (Label) view.getParent();
                        selectedItemsPane.getChildren().remove(clickedLabel);
                        selectedItemsProperty.remove(clickedLabel.getText());
                        pickList.add(clickedLabel.getText());
                    }
                });

                Label label = new Label(selectedItem, imageView);
                label.setPadding(new Insets(SELECTED_ITEM_PADDING));
                label.getStyleClass().add("picked-item");
                label.setTextAlignment(TextAlignment.CENTER);
                label.setOnMouseClicked((event) -> {
                    event.consume();
                    label.getGraphic().fireEvent(event);
                });

                selectedItemsPane.getChildren().add(label);
                selectedItemsProperty.add(label.getText());
            }
        });

        return suggestionList;
    }

    private JFXListView<JFXListView<String>> initMainComponent() {
        JFXListView<JFXListView<String>> mainList = new JFXListView<>();

        mainList.getItems().add(suggestionList);
        mainList.setCellFactory((list) -> new SubListCell());
        mainList.getStyleClass().add("multi-select-list");

        return mainList;
    }

    private static class SubListCell extends JFXListCell<JFXListView<String>> {
        @Override
        protected void updateItem(JFXListView<String> list, boolean empty) {
            super.updateItem(list, empty);

            if (list != null) {
                VBox contentHolder = (VBox) list.getParent().getParent();
                StackPane groupNode = (StackPane) contentHolder.getChildren().get(0);
                VBox.setVgrow(groupNode, Priority.NEVER);
                FlowPane flowPane = (FlowPane) groupNode.getChildren().get(0);
                StackPane.setAlignment(flowPane, Pos.CENTER_LEFT);
                SVGGlyph dropIcon = (SVGGlyph) groupNode.getChildren().get(1);

                dropIcon.setOnMouseClicked(contentHolder.getOnMouseClicked());
                contentHolder.setOnMouseClicked((e) -> {
                    e.consume();
                    flowPane.fireEvent(e);
                });
            }
        }
    }
}
