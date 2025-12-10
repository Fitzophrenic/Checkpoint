package com.checkpointfrontend;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class ProjectRepersentationForMainView extends HBox{
    private Label eventName;
    private Button deleteEvent;
    private String projectID;
    public ProjectRepersentationForMainView(String projectName, CheckPoint mainRef, String projectID) {
        eventName = new Label();
        this.projectID= projectID;
        this.setSpacing(10);
        eventName.setText(projectName);
        eventName.setStyle("-fx-text-fill: white;");
        eventName.setEllipsisString("...");
        eventName.setMaxWidth(Double.MAX_VALUE);
        deleteEvent = new Button("X"); 
        deleteEvent.setStyle("-fx-text-fill: white;");
        this.getChildren().addAll(eventName);
        this.setOnMouseEntered(e -> this.setStyle("-fx-background-color: #3e8662ff; -fx-background-radius: 8; -fx-padding: 10; -fx-alignment: center;"));
        this.setOnMouseExited(e -> this.setStyle("-fx-background-color: #055b27ff; -fx-background-radius: 8; -fx-padding: 10; -fx-alignment: center;"));
        this.getChildren().addAll(deleteEvent);
        this.setStyle(String.format("-fx-background-color: #055b27ff; -fx-background-radius: 8; -fx-padding: 10; -fx-alignment: center;"));
        deleteEvent.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                            "Delete this project? / Remove yourself from this project?", 
                            ButtonType.YES, ButtonType.NO);
                alert.setHeaderText(null);

                if (alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
                    this.getChildren().clear();
                    mainRef.getBoardsList().getItems().remove(this);
                    mainRef.getHttpClient().deleteProject(projectID, mainRef.getUserName());
                    this.setStyle(String.format("-fx-background-color: transparent; "));
                }
            });
    }
    public String getProjectID() {
        return projectID;
    }
}
