package com.checkpointfrontend;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class App extends Application {

    private static Scene scene;
    private Stage window;

    private BorderPane mainLayout;
    private SplitPane docSplitPane;
    private ListView<String> sectionsList;
    private TextArea notesArea;
    private ListView<String> boardsList;

    private String userID = ""; // set after login
    private String currentProjectID = "";
    private String currentProjectSection = "";

    private static final httpClientCheckPoint httpClient = new httpClientCheckPoint();
    private final Map<String, String> userProjects = new HashMap<>();
    private final List<Map<String, Object>> sections = new ArrayList<>();

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
        if (!user.isEmpty()) {
            Map<String,Object> userInfo = httpClient.createUser(user);
            if(userInfo != null && userInfo.get("userID") != null) {
                userID = (String) userInfo.get("userID");
                httpClient.requestUserJson(userID);
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
        if (!user.isEmpty()) {
            Map<String,Object> userInfo = httpClient.createUser(user);
            if(userInfo != null && userInfo.get("userID") != null) {
                userID = (String) userInfo.get("userID");
                File userFile = new File("user_info.txt");
                try (FileWriter fw = new FileWriter(userFile)) {
                    fw.write(String.format("userID: %s%n", userID));
                    fw.write(String.format("userName: %s", user));
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
    // MAIN HOME SCENE (after login)
    private void initializeHomeScene() {
        // Top Menu
        Button homeButton = new Button("Checkpoint");
        homeButton.getStyleClass().add("home-button");
        HBox topMenuBar = new HBox(10, homeButton);
        topMenuBar.setStyle("-fx-padding: 10; -fx-background-color: #18a438ff;");

        // Boards List
        boardsList = new ListView<>();
        boardsList.getStyleClass().add("list-view");

        TextField newBoardField = new TextField();
        newBoardField.setPromptText("New Board Name");
        Button addBoardBtn = new Button("Add Board");
        HBox addBoardBox = new HBox(10, newBoardField, addBoardBtn);
        VBox homeScreenContent = new VBox(10, new Label("Boards:"), boardsList, addBoardBox);
        homeScreenContent.setStyle("-fx-padding: 50;");
        homeScreenContent.getStyleClass().add("home-screen");

        // Sections & Notes
        sectionsList = new ListView<>();
        sectionsList.getStyleClass().add("list-view");
        notesArea = new TextArea();
        notesArea.setPromptText("Write your notes here...");
        notesArea.getStyleClass().add("text-area");

        TextField newSectionField = new TextField();
        newSectionField.setPromptText("New Section Name");
        Button addSectionButton = new Button("Add Section");

        VBox sectionBox = new VBox(10, sectionsList, new HBox(10, newSectionField, addSectionButton));
        sectionBox.setPrefWidth(250);

        docSplitPane = new SplitPane();
        docSplitPane.getItems().addAll(sectionBox, notesArea);
        docSplitPane.setDividerPositions(0.3);

        // Layout
        mainLayout = new BorderPane();
        mainLayout.setTop(topMenuBar);
        mainLayout.setCenter(homeScreenContent);

        homeScene = new Scene(mainLayout, 800, 600);
        homeScene.getStylesheets().add(getClass().getResource("/com/checkpointfrontend/style.css").toExternalForm());

        // Handlers
        // Board click
        boardsList.setOnMouseClicked(e -> {
            String board = boardsList.getSelectionModel().getSelectedItem();
            if (board != null) {
                sectionsList.getItems().clear();
                notesArea.clear();
                mainLayout.setCenter(docSplitPane);
                currentProjectID = userProjects.get(board);
                populateSections();
            }
        });

        // Home button
        homeButton.setOnAction(e -> {
            mainLayout.setCenter(homeScreenContent);
            String section = sectionsList.getSelectionModel().getSelectedItem();
            for (Map<String, Object> sectio : sections) {
                if (sectio.get("boardName").equals(section)) {
                    String text = notesArea.getText();
                    httpClient.updateBoardSection(userID, currentProjectID, currentProjectSection, text);
                    sectio.put("content", text);
                    break;
                }
            }
            currentProjectSection = "";
        });

        // Section click
        sectionsList.setOnMouseClicked(e -> {
            String section = sectionsList.getSelectionModel().getSelectedItem();
            if (section == null || section.equals(currentProjectSection)) return;
            for (Map<String, Object> sectio : sections) {
                if (sectio.get("boardName").equals(currentProjectSection)) {
                    String text = notesArea.getText();
                    httpClient.updateBoardSection(userID, currentProjectID, currentProjectSection, text);
                    sectio.put("content", text);
                    break;
                }
            }
            notesArea.setPromptText("Notes for: " + section);
            notesArea.clear();
            String content = httpClient.getBoardSection(currentProjectID, section).get("content").toString();
            String multiLineContent = content.replace("\\n", "\n");
            if (multiLineContent.startsWith("\"") && multiLineContent.endsWith("\"")) {
                multiLineContent = multiLineContent.substring(1, multiLineContent.length() - 1);
            }
            notesArea.setText(multiLineContent);
            currentProjectSection = section;
        });

        // Add Board
        addBoardBtn.setOnAction(e -> {
            String boardName = newBoardField.getText().trim();
            if (!boardName.isEmpty() && !boardsList.getItems().contains(boardName)) {
                boardsList.getItems().add(boardName);
                newBoardField.clear();
                Map<String, Object> idRes = httpClient.createProject(boardName, userID);
                userProjects.put(boardName, (String) idRes.get("projectID"));
            }
        });

        // Add Section
        addSectionButton.setOnAction(e -> {
            String sectionName = newSectionField.getText().trim();
            if (!sectionName.isEmpty() && !sectionsList.getItems().contains(sectionName)) {
                sectionsList.getItems().add(sectionName);
                newSectionField.clear();
                httpClient.addBoardToProject(userID, currentProjectID, sectionName, "");
                Map<String, Object> mapTmpOnly = new HashMap<>();
                mapTmpOnly.put("boardName", sectionName);
                mapTmpOnly.put("content", "");
                sections.add(mapTmpOnly);
            }
        });

        populateBoards();
    }

    // Sections & Boards
    @SuppressWarnings("unchecked")
    private void populateSections() {
        sections.clear();
        sectionsList.getItems().clear();
        Map<String, Object> boardMap = httpClient.getProjectBoards(currentProjectID);
        Object obj = boardMap.get("sections");
        if (!(obj instanceof ArrayList<?>)) return;
        ArrayList<?> boardContent = (ArrayList<?>) obj;
        for (Object item : boardContent) {
            if (item instanceof Map<?, ?>) sections.add((Map<String, Object>) item);
        }
        for (Map<String, Object> section : sections) {
            String boardName = section.get("boardName") != null ? section.get("boardName").toString() : "";
            sectionsList.getItems().add(boardName);
        }
    }

    @SuppressWarnings("unchecked")
    private void populateBoards() {
        boardsList.getItems().clear();
        userProjects.clear();
        Map<String, Object> boardMap = httpClient.requestUserJson(userID);
        Object obj = boardMap.get("projects");
        if (!(obj instanceof ArrayList<?>)) return;
        ArrayList<?> boardContent = (ArrayList<?>) obj;
        for (Object item : boardContent) {
            if (item instanceof Map<?, ?>) {
                Map<String, Object> map = (Map<String, Object>) item;
                String projectName = map.get("projectName") != null ? map.get("projectName").toString() : "";
                String projectID = map.get("projectID") != null ? map.get("projectID").toString() : "";
                userProjects.put(projectName, projectID);
                boardsList.getItems().add(projectName);
            }
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
