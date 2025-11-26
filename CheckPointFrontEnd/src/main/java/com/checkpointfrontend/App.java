package com.checkpointfrontend;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class App extends Application {

    private static Scene scene;

    private BorderPane mainLayout;
    private SplitPane docSplitPane;
    private ListView<String> sectionsList;
    private WidgetManager sectionFocus;
    private ListView<String> boardsList;
    private String username = ""; // change later when login has it
    private String currentProjectID = "";
    private String currentProjectSection = "";
    private static final httpClientCheckPoint httpClient =  new httpClientCheckPoint();
    private final Map<String, String> userProjects = new HashMap<>();
    private final List<Map<String, Object>> sections = new ArrayList<>();
    
    private Stage window;

    private Scene loginScene;
    private Scene createAccountScene;
    private Scene homeScene;

         @Override
        public void start(Stage stage) {
            this.window = stage;
            window.setTitle("Checkpoint");

            loginScene = createLoginScene();
            createAccountScene = createCreateAccountScene();

            window.setScene(loginScene);
            window.show();
        }

    private Scene createLoginScene() {
        Label title = new Label("Log in");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");
        HBox titleBar = new HBox(title);
        titleBar.setStyle("-fx-background-color: #18a438; -fx-padding: 10;");
        titleBar.setAlignment(Pos.CENTER_LEFT);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        HBox buttons = new HBox(10);
        Button loginBtn = new Button("Log in");
        Button createBtn = new Button("Create");
        Button cancelBtn = new Button("Cancel");
        buttons.getChildren().addAll(loginBtn, createBtn, cancelBtn);
        buttons.setAlignment(Pos.CENTER);

        VBox layout = new VBox(10, titleBar, new Label("Username:"), usernameField, new Label("Password:"), passwordField, buttons);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-padding: 20; -fx-background-color: white;");

        Scene scene = new Scene(layout, 400, 300);

        // Button actions
        loginBtn.setOnAction(e -> {
            String user = usernameField.getText().trim();
            String password = passwordField.getText().trim();
            if (!user.isEmpty() && !password.isEmpty()) {
                Map<String,Object> userInfo = httpClient.loginUser(user, password);
                if(userInfo != null && userInfo.get("username") != null) {
                    username = (String) userInfo.get("username");
                    // httpClient.requestUserJson(username);
                    initializeHomeScene();
                    window.setScene(homeScene);
                }
            }
        });

        createBtn.setOnAction(e -> window.setScene(createAccountScene));
        cancelBtn.setOnAction(e -> window.close());

        return scene;
    }

    private Scene createCreateAccountScene() {
        Label title = new Label("Create Log in");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");
        HBox titleBar = new HBox(title);
        titleBar.setStyle("-fx-background-color: #18a438; -fx-padding: 10;");
        titleBar.setAlignment(Pos.CENTER_LEFT);

        TextField usernameField = new TextField();
        usernameField.setPromptText("username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("password");

        HBox buttons = new HBox(10);
        Button createBtn = new Button("Create account");
        Button cancelBtn = new Button("Cancel");
        buttons.getChildren().addAll(createBtn, cancelBtn);
        buttons.setAlignment(Pos.CENTER);

        VBox layout = new VBox(10, titleBar, new Label("Choose username:"), usernameField, new Label("Choose password:"), passwordField, buttons);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-padding: 20; -fx-background-color: white;");

        Scene scene = new Scene(layout, 400, 300);

        // More button actions
        createBtn.setOnAction(e -> {
            String user = usernameField.getText().trim();
            String password = passwordField.getText().trim();
            if (!user.isEmpty() && !password.isEmpty()) {
                Map<String,Object> userInfo = httpClient.createUser(user, password);
                if(userInfo != null && userInfo.get("username") != null) {
                    username = (String) userInfo.get("username");
                    File userFile = new File("user_info.txt");
                    try (FileWriter fw = new FileWriter(userFile)) {
                        fw.write(String.format("username: %s%n", username));
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    initializeHomeScene();
                    window.setScene(homeScene);
                }
            }
        });

        cancelBtn.setOnAction(e -> window.setScene(loginScene));

        return scene;
    }
    public void initializeHomeScene() {


        // Temorary user set here
        // setupUser("monkey dave");
        // httpClient.requestUserJson(username);
        // Menu Bar
        Button homeButton = new Button("Checkpoint");
        homeButton.getStyleClass().add("home-button"); // CSS class
        HBox topMenuBar = new HBox(10, homeButton);
        topMenuBar.setStyle("-fx-padding: 10; -fx-background-color: #18a438ff;");

        // Board List
        boardsList = new ListView<>();
        boardsList.getStyleClass().add("list-view");

        TextField newBoardField = new TextField();
        newBoardField.setPromptText("New Board Name");
        Button addBoardBtn = new Button("Add Board");

        
        HBox addBoardBox = new HBox(10, newBoardField, addBoardBtn);
        VBox homeScreen = new VBox(10, new Label("Boards:"), boardsList, addBoardBox);
        homeScreen.setStyle("-fx-padding: 50;");
        homeScreen.getStyleClass().add("home-screen");

        // Section & Notes Area
        sectionsList = new ListView<>();
        sectionsList.getStyleClass().add("list-view");

        sectionFocus = new WidgetManager();
        

        TextField newSectionField = new TextField();
        newSectionField.setPromptText("New Section Name");
        Button addSectionButton = new Button("Add Section");
        Button addTimerButton = new Button("Add Timer");
        Button shareProjectButton = new Button("Share Project");

        VBox sectionBox = new VBox(10, sectionsList, new HBox(10, newSectionField, addSectionButton, addTimerButton, shareProjectButton));
        sectionBox.setPrefWidth(250);

        docSplitPane = new SplitPane();
        docSplitPane.getItems().addAll(sectionBox, sectionFocus);
        docSplitPane.setDividerPositions(0.3);

        // Main Layout of Pane
        mainLayout = new BorderPane();
        mainLayout.setTop(topMenuBar);
        mainLayout.setCenter(homeScreen);
        homeScene = new Scene(mainLayout, 800, 600);
        homeScene.getStylesheets().add(getClass().getResource("/com/checkpointfrontend/style.css").toExternalForm());

        // CSS

        // Handlers
        boardsList.setOnMouseClicked(e -> {//opens a board / project
            String board = boardsList.getSelectionModel().getSelectedItem();
            if (board != null) {
                sectionsList.getItems().clear();
                sectionFocus.clear();
                mainLayout.setCenter(docSplitPane);
                currentProjectID = userProjects.get(board);
                populateSections();
            }
        });

        homeButton.setOnAction(e -> {
            mainLayout.setCenter(homeScreen);
            String section = sectionsList.getSelectionModel().getSelectedItem();
            for (Map<String, Object> sectio : sections) {

                if (sectio.get("boardName").equals(section)) {
                    String text = sectionFocus.convertDataToText();
                    httpClient.updateBoardSection(username, currentProjectID, currentProjectSection, text);

                    sectio.put("content", text);

                    break;
                }
            }
            currentProjectSection = "";
        });

        sectionsList.setOnMouseClicked(e -> {// open NEW section
            String section = sectionsList.getSelectionModel().getSelectedItem();
            if (section == null || section.equals(currentProjectSection)) {
                return;
            }
            for (Map<String, Object> sectio : sections) {

                if (sectio.get("boardName").equals(currentProjectSection)) {
                    String text = sectionFocus.convertDataToText();
                    

                    httpClient.updateBoardSection(username, currentProjectID, currentProjectSection, text);

                    sectio.put("content", text);

                    break;
                }
            }

            //sectionFocus.setPromptText("Notes for: " + section);
            sectionFocus.clear();
            String content =httpClient.getBoardSection(currentProjectID, section).get("content").toString();
            String multiLineContent = content.replace("\\n", "\n");
            if (multiLineContent.startsWith("\"") && multiLineContent.endsWith("\"")) {
                multiLineContent = multiLineContent.substring(1, multiLineContent.length() - 1);
            }
            sectionFocus.determineAndUpdateWidget(multiLineContent);
            // notesArea.setText(content);
            currentProjectSection = section;
        });

        addBoardBtn.setOnAction(e -> {//create board
            String boardName = newBoardField.getText().trim();
            
            if (!boardName.isEmpty() && !boardsList.getItems().contains(boardName)) {
                boardsList.getItems().add(boardName);
                newBoardField.clear();
                Map<String, Object> idRes = httpClient.createProject(boardName, username); //track
                String projectIDtmp = (String) idRes.get("projectID");
                userProjects.put(boardName, projectIDtmp);
                
            }
        });
       
        addSectionButton.setOnAction(e -> { // create new section
            String sectionName = newSectionField.getText().trim();
            Map<String, Object> mapTmpOnly = new HashMap<>();
            if (!sectionName.isEmpty() && !sectionsList.getItems().contains(sectionName)) {
                sectionsList.getItems().add(sectionName);
                newSectionField.clear();
                httpClient.addBoardToProject(username, currentProjectID, sectionName, "Board:");
                mapTmpOnly.put("boardName", sectionName);
                mapTmpOnly.put("content", "Board:");
                sections.add(mapTmpOnly);
            }
        });
        addTimerButton.setOnAction(e -> { // create new section
            String sectionName = newSectionField.getText().trim();
            Map<String, Object> mapTmpOnly = new HashMap<>();
            if (!sectionName.isEmpty() && !sectionsList.getItems().contains(sectionName)) {
                sectionsList.getItems().add(sectionName);
                newSectionField.clear();
                httpClient.addBoardToProject(username, currentProjectID, sectionName, "Timer:");
                mapTmpOnly.put("boardName", sectionName);
                mapTmpOnly.put("content", "Timer:");
                sections.add(mapTmpOnly);
            }
        });
        shareProjectButton.setOnAction(e -> {
            ShareProject shareProjectStage = new ShareProject(window, username, currentProjectID, httpClient);
        });
                populateBoards();

    }
    @SuppressWarnings("unchecked")
    private void populateSections() {
        sections.clear();
        Map<String, Object> boardMap = httpClient.getProjectBoards(currentProjectID);
        Object obj = boardMap.get("sections");
        if(!(obj instanceof ArrayList<?>)) {
            return;
        }
        ArrayList<?> boardContent = (ArrayList<?>) obj;
        for (Object item : boardContent) {
            if (item instanceof Map<?, ?>) {

                sections.add((Map<String, Object>) item);
            } else {
            }
        }
        for (Map<String, Object> section : sections) {
            String boardName = section.get("boardName") != null ? section.get("boardName").toString() : "";
            sectionsList.getItems().add(boardName);
        }
        System.out.println(boardContent);
    }
    @SuppressWarnings("unchecked")
    private void populateBoards() {
        Map<String, Object> boardMap = httpClient.requestUserJson(username);
        Object obj = boardMap.get("projects");            

        System.out.println(obj);
        if(!(obj instanceof ArrayList<?>)) {
            return;
        }

        ArrayList<?> boardContent = (ArrayList<?>) obj;
        for (Object item : boardContent) {
            if (item instanceof Map<?, ?>) {
                        System.out.println("4");

                Map<String, Object> map = (Map<String, Object>) item;
                String projectName = map.get("projectName") != null ? map.get("projectName").toString() : "";
                String projectID = map.get("projectID") != null ? map.get("projectID").toString() : "";

                userProjects.put(projectName, projectID);
                boardsList.getItems().add(projectName);

            } else {
            }
        }
    }

    private void setupUser(String userName) {
        File userFile = new File("user_info.txt");
        if(userFile.exists()) {
            System.out.println("user already exists");
            File file = new File("user_info.txt");
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] partsInText = line.split(":", 2);
                    String userInfoType = partsInText[0].trim();
                    String arg1 = partsInText.length > 1 ? partsInText[1].trim() : null;
                    System.out.println(line);

                    switch (userInfoType) {
                        case "username":
                            username = arg1;
                            break;
                        default:
                            // do nothing
                            break;
                    }

                }
            } catch (IOException ex) {
            }

            return;
        }
        Map<String,Object> userInfo = httpClient.createUser(userName, "dumbass password");
        username = (String) userInfo.get("username");
        httpClient.requestUserJson(userName);
        try(FileWriter fw = new FileWriter(userFile)) {
            fw.write(String.format("username: %s",userName));

        } catch (IOException ex) {
        }
    }
    public static void main(String[] args)  {
        
        launch();
        
    }
}