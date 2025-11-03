import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class CheckpointFE extends Application {

    private BorderPane MainLayout;
    private SplitPane DocSplitPane;
    private ListView<String> SectionsList;
    private TextArea NotesArea;
    private ListView<String> BoardsList;

    @Override
    public void start(Stage StartContainer) {
        StartContainer.setTitle("Checkpoint");

        // --- Top Menu ---
        Button HomeButton = new Button("Checkpoint");
        HomeButton.getStyleClass().add("home-button"); // CSS class
        HBox TopMenuBar = new HBox(10, HomeButton);
        TopMenuBar.setStyle("-fx-padding: 10; -fx-background-color: #18a438ff;");

        // --- Boards List ---
        BoardsList = new ListView<>();
        BoardsList.getStyleClass().add("list-view");

        TextField newBoardField = new TextField();
        newBoardField.setPromptText("New Board Name");
        Button addBoardBtn = new Button("Add Board");

        HBox addBoardBox = new HBox(10, newBoardField, addBoardBtn);
        VBox HomeScreen = new VBox(10, new Label("Boards:"), BoardsList, addBoardBox);
        HomeScreen.setStyle("-fx-padding: 50;");
        HomeScreen.getStyleClass().add("home-screen");

        // --- Sections and Notes ---
        SectionsList = new ListView<>();
        SectionsList.getStyleClass().add("list-view");

        NotesArea = new TextArea();
        NotesArea.setPromptText("Write your notes here...");
        NotesArea.getStyleClass().add("text-area");

        TextField NewSectionField = new TextField();
        NewSectionField.setPromptText("New Section Name");
        Button AddSectionButton = new Button("Add Section");

        VBox sectionBox = new VBox(10, SectionsList, new HBox(10, NewSectionField, AddSectionButton));
        sectionBox.setPrefWidth(250);

        DocSplitPane = new SplitPane();
        DocSplitPane.getItems().addAll(sectionBox, NotesArea);
        DocSplitPane.setDividerPositions(0.3);

        // --- Main Layout ---
        MainLayout = new BorderPane();
        MainLayout.setTop(TopMenuBar);
        MainLayout.setCenter(HomeScreen);

        Scene scene = new Scene(MainLayout, 800, 600);

        // --- Attach CSS ---
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        StartContainer.setScene(scene);
        StartContainer.show();

        // --- Event Handling ---
        BoardsList.setOnMouseClicked(e -> {
            if (BoardsList.getSelectionModel().getSelectedItem() != null) {
                SectionsList.getItems().clear();
                NotesArea.clear();
                MainLayout.setCenter(DocSplitPane);
            }
        });

        HomeButton.setOnAction(e -> MainLayout.setCenter(HomeScreen));

        SectionsList.setOnMouseClicked(e -> {
            String Section = SectionsList.getSelectionModel().getSelectedItem();
            if (Section != null) {
                NotesArea.setPromptText("Notes for: " + Section);
                NotesArea.clear();
            }
        });

        addBoardBtn.setOnAction(e -> {
            String BoardName = newBoardField.getText().trim();
            if (!BoardName.isEmpty() && !BoardsList.getItems().contains(BoardName)) {
                BoardsList.getItems().add(BoardName);
                newBoardField.clear();
            }
        });

        AddSectionButton.setOnAction(e -> {
            String SectionName = NewSectionField.getText().trim();
            if (!SectionName.isEmpty() && !SectionsList.getItems().contains(SectionName)) {
                SectionsList.getItems().add(SectionName);
                NewSectionField.clear();
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
