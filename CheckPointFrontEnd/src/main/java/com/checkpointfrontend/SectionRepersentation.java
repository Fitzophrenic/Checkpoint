package com.checkpointfrontend;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class SectionRepersentation extends HBox{
    private Label boardNameLabel;
    private Button deleteSection;
    private String projectID;
    private String boardName;
    public SectionRepersentation(String boardName, CheckPoint mainRef, String projectID) {
        boardNameLabel = new Label();
        this.projectID= projectID;
        this.boardName = boardName;
        this.setSpacing(10);
        boardNameLabel.setText(boardName);
        boardNameLabel.setStyle("-fx-text-fill: white;");
        boardNameLabel.setEllipsisString("...");
        boardNameLabel.setMaxWidth(Double.MAX_VALUE);
        deleteSection = new Button("X"); 
        deleteSection.setStyle("-fx-text-fill: white;");
        this.getChildren().addAll(boardNameLabel);
        this.setOnMouseEntered(e -> this.setStyle("-fx-background-color: #3e8662ff; -fx-background-radius: 8; -fx-padding: 10; -fx-alignment: center;"));
        this.setOnMouseExited(e -> this.setStyle("-fx-background-color: #055b27ff; -fx-background-radius: 8; -fx-padding: 10; -fx-alignment: center;"));
        this.getChildren().addAll(deleteSection);
        this.setStyle(String.format("-fx-background-color: #055b27ff; -fx-background-radius: 8; -fx-padding: 10; -fx-alignment: center;"));
        deleteSection.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                            "Delete this section?", 
                            ButtonType.YES, ButtonType.NO);
                alert.setHeaderText(null);

                if (alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
                    mainRef.getHttpClient().deleteBoardSection(mainRef.getUserName(),projectID,boardName);
                    mainRef.populateSections();

                }
            });
    }
    public String getProjectID() {
        return projectID;
    }
    public String getBoardName() {
        return boardName;
    }
}
