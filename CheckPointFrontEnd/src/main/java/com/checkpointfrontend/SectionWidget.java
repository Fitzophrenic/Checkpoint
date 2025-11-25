package com.checkpointfrontend;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class SectionWidget extends HBox {

    private Label nameLabel;
    private Button deleteButton;

    public SectionWidget(String sectionName) {
        nameLabel = new Label(sectionName);
        deleteButton = new Button("X");

        this.getChildren().addAll(nameLabel, deleteButton);
        this.setSpacing(10);
        this.setStyle("-fx-padding: 5; -fx-border-color: lightgray; -fx-border-radius: 5;");

        // Event handler to remove the section from its parent container
        deleteButton.setOnAction(e -> {
            if (this.getParent() instanceof HBox || this.getParent() instanceof VBox) {
                ((javafx.scene.layout.Pane)this.getParent()).getChildren().remove(this);
            }
        });
    }

    public String getSectionName() {
        return nameLabel.getText();
    }
}
