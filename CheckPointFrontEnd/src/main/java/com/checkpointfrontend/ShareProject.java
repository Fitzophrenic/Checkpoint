package com.checkpointfrontend;

import java.util.List;
import java.util.Map;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

public class ShareProject extends Stage{
    private final FlowPane flowPane;
    private final ListView<String> users = new ListView<>();
    private final BorderPane entirePane = new BorderPane();
    private final BorderPane functionalityPane = new BorderPane();
    private final String currentProject;
    private final httpClientCheckPoint client;
    public ShareProject(Stage window, String currentUser, String currentProject, httpClientCheckPoint client) {
        flowPane = new FlowPane();
        this.setTitle("Share Window");
        this.currentProject = currentProject;
        this.client = client;
        updateVeiw();
        Label headLabel = new Label("Share project");
        headLabel.getStyleClass().add("headLabel");
        headLabel.setMaxWidth(Double.MAX_VALUE);
        BorderPane.setAlignment(headLabel, Pos.CENTER);
        TextField usernameToUpdate = new TextField();
        ChoiceBox<String> commands = new ChoiceBox<>();
        commands.getItems().add("Owner");
        commands.getItems().add("Editor");
        commands.getItems().add("Reader");
        commands.getItems().add("Remove");
        Button submitButton = new Button("Excecute");
        flowPane.getChildren().addAll(usernameToUpdate, commands, submitButton);
        functionalityPane.setTop(users);
        functionalityPane.setBottom(flowPane);
        entirePane.setTop(headLabel);
        entirePane.setBottom(functionalityPane);
        Scene shareSceen = new Scene(entirePane, 400, 500);
        shareSceen.getStylesheets().add(getClass().getResource("/com/checkpointfrontend/ShareWindow.css").toExternalForm());
        
        submitButton.setOnAction(e -> {
            String command = commands.getValue();
            String updateUser = usernameToUpdate.getText();
            if(updateUser.contains(":")){
                updateUser = updateUser.substring(0, updateUser.indexOf(':'));
            }
            if(command.equals("Remove")) {
                client.removeUserFromProject(currentProject, updateUser, currentUser);
                // users.getItems().add(updateUser + ": Removed");
                updateVeiw();
                return;
            }
            if(command.equals("Owner")) {
                client.changePermissionLevel(currentProject, updateUser, currentUser, "o");
                // users.getItems().add(updateUser + ": Owner");
                updateVeiw();

                return;
            }if(command.equals("Editor")) {
                client.changePermissionLevel(currentProject, updateUser, currentUser, "w");
                // users.getItems().add(updateUser + ": Editor");
                updateVeiw();
                return;
            }if(command.equals("Reader")) {
                client.changePermissionLevel(currentProject, updateUser, currentUser, "r");
                // users.getItems().add(updateUser + ": Reader");
                updateVeiw();

            }
            
        });
        this.setScene(shareSceen);
        this.initOwner(window);
        this.setOnShown(f -> {
        this.setX(window.getX() + window.getWidth()/2  - this.getWidth()/2);
        this.setY(window.getY() + window.getHeight()/2 - this.getHeight()/2);
        });
        this.show();
    }
    private void updateVeiw() {
        users.getItems().clear();
        Map<String, Object> data = client.requestProjectJSON(currentProject); 
        List<Map<String, Object>> usersFJSON = (List<Map<String, Object>>) data.get("users");

        for (Map<String, Object> user : usersFJSON) {
            String username = (String) user.get("username");
            String permissionLevel = (String) user.get("permissionLevel");
            switch(permissionLevel) {
                case "o" -> {
                    permissionLevel = "Owner";
                }
                case "w" -> {
                    permissionLevel = "Editor";
                }
                case "r" -> {
                    permissionLevel = "Reader";
                }
            }
            users.getItems().add(username + ": " + permissionLevel);

        }
    }
}
