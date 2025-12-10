package com.checkpointfrontend;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;

public class TimerWidget extends Application {

    private long startTime = 0;
    private boolean running = false;
    private long elapsedTime = 0; // in nanoseconds
    private Label timerLabel = new Label("00:00:00");

    private httpClientCheckPoint client = new httpClientCheckPoint();

    @Override
    public void start(Stage primaryStage) {

        Button startButton = new Button("Start");
        Button stopButton = new Button("Stop");
        Button resetButton = new Button("Reset");
        Button submitButton = new Button("Submit Time");

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (running) {
                    elapsedTime = now - startTime;
                    timerLabel.setText(formatTime(elapsedTime));
                }
            }
        };
        timer.start();

        startButton.setOnAction(e -> {
            if (!running) {
                startTime = System.nanoTime() - elapsedTime;
                running = true;
            }
        });

        stopButton.setOnAction(e -> running = false);

        resetButton.setOnAction(e -> {
            running = false;
            elapsedTime = 0;
            timerLabel.setText("00:00:00");
        });

        submitButton.setOnAction(e -> {
            running = false;
            String formattedTime = formatTime(elapsedTime);
            System.out.println("Submitting time: " + formattedTime);
            // Example: send to backend via httpClientCheckPoint
            // Replace "userID" and "projectID" with actual values
            client.addBoardToProject("userID", "projectID", "TimerEntry", formattedTime);
        });

        VBox root = new VBox(10, timerLabel, startButton, stopButton, resetButton, submitButton);
        root.setStyle("-fx-padding: 20px; -fx-alignment: center;");

        Scene scene = new Scene(root, 300, 250);
        primaryStage.setTitle("Timer Widget");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private String formatTime(long nanos) {
        long totalSeconds = nanos / 1_000_000_000;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
