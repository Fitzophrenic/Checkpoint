package com.checkpointfrontend;


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
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
    private TextArea notesArea;
    private ListView<String> boardsList;

    @Override
    public void start(Stage stage) {
        stage.setTitle("Checkpoint");

        // --- Top Menu ---
        Button homeButton = new Button("Checkpoint");
        homeButton.getStyleClass().add("home-button"); // CSS class
        HBox topMenuBar = new HBox(10, homeButton);
        topMenuBar.setStyle("-fx-padding: 10; -fx-background-color: #18a438ff;");

        // --- Boards List ---
        boardsList = new ListView<>();
        boardsList.getStyleClass().add("list-view");

        TextField newBoardField = new TextField();
        newBoardField.setPromptText("New Board Name");
        Button addBoardBtn = new Button("Add Board");

        HBox addBoardBox = new HBox(10, newBoardField, addBoardBtn);
        VBox homeScreen = new VBox(10, new Label("Boards:"), boardsList, addBoardBox);
        homeScreen.setStyle("-fx-padding: 50;");
        homeScreen.getStyleClass().add("home-screen");

        // --- Sections and Notes ---
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

        // --- Main Layout ---
        mainLayout = new BorderPane();
        mainLayout.setTop(topMenuBar);
        mainLayout.setCenter(homeScreen);

        scene = new Scene(mainLayout, 800, 600);

        // --- Attach CSS ---
        scene.getStylesheets().add(getClass().getResource("/com/checkpointfrontend/style.css").toExternalForm());

        stage.setScene(scene);
        stage.show();

        // --- Event Handling ---
        boardsList.setOnMouseClicked(e -> {
            String board = boardsList.getSelectionModel().getSelectedItem();
            if (board != null) {
                sectionsList.getItems().clear();
                notesArea.clear();
                mainLayout.setCenter(docSplitPane);
            }
        });

        homeButton.setOnAction(e -> mainLayout.setCenter(homeScreen));

        sectionsList.setOnMouseClicked(e -> {
            String section = sectionsList.getSelectionModel().getSelectedItem();
            if (section != null) {
                notesArea.setPromptText("Notes for: " + section);
                notesArea.clear();
            }
        });

        addBoardBtn.setOnAction(e -> {
            String boardName = newBoardField.getText().trim();
            if (!boardName.isEmpty() && !boardsList.getItems().contains(boardName)) {
                boardsList.getItems().add(boardName);
                newBoardField.clear();
            }
        });

        addSectionButton.setOnAction(e -> {
            String sectionName = newSectionField.getText().trim();
            if (!sectionName.isEmpty() && !sectionsList.getItems().contains(sectionName)) {
                sectionsList.getItems().add(sectionName);
                newSectionField.clear();
            }
        });
    }

    public static void main(String[] args)  {
        httpClientCheckPoint homindisan = new httpClientCheckPoint();
        try {
            homindisan.http();
            System.out.println("Sdadjis");

        } catch (Exception ex) {
            System.err.println("asijdjis");
        }
        launch();
        
    }
}