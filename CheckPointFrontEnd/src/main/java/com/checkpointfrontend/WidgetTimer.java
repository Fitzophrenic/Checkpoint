package com.checkpointfrontend;

import javafx.animation.AnimationTimer;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;

public class WidgetTimer extends Widget {
    private long startTime = 0;
    private boolean running = false;
    private long elapsedTime = 0; // in nanoseconds
    private Label timerLabel = new Label("00:00:00");
    private ListView<String> times = new ListView<>();
    private final FlowPane flowPane;
    private final BorderPane BorderPane = new BorderPane();

    @Override
    public String convertDataToText() {
        StringBuilder result = new StringBuilder();
        result.append("Timer:");
        if(!times.getItems().isEmpty()) {
            for(String time : times.getItems()) {
                String addedItem = time + " ";
                result.append(addedItem); // potentially adds enters which might mess with the thing
            }
        }
        return result.toString();
    }

    @Override
    public void convertTextToData(String content) {
        System.out.println(content);
        String[] reputInData = content.split("⠀");
        for (String string : reputInData) {
            if (!string.isBlank() && !string.equals("Timer")) {
                times.getItems().add(string.trim());
            }
        }
    }
    public WidgetTimer() {
        Button startButton = new Button("Start");
        Button stopButton = new Button("Stop");
        Button resetButton = new Button("Reset");
        Button submitButton = new Button("Submit Time");
        TextField nameTimerPurpose = new TextField();
        Button nameTimerPurposeButton = new Button("Set Purpose");
        timerLabel.getStyleClass().add("timerLabel");

        this.getStylesheets().add(getClass().getResource("/com/checkpointfrontend/timer.css").toExternalForm());

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
            String timerPurp = nameTimerPurpose.getText();
            if(timerPurp.contains("⠀")) {
                return;
            }
            String result = timerPurp + " " + formattedTime + "⠀"; //invisable unicode for reparsing
            times.getItems().add(result);
        });
        flowPane = new FlowPane();
        flowPane.getChildren().addAll(timerLabel, startButton, stopButton, resetButton, submitButton, nameTimerPurpose, nameTimerPurposeButton);
        BorderPane.setTop(flowPane);
        BorderPane.setBottom(times);
        this.getChildren().add(BorderPane);


        this.setStyle("-fx-padding: 20px; -fx-alignment: center;");
        this.setPrefSize(300, 300);
    }
    private String formatTime(long nanos) {
        long totalSeconds = nanos / 1_000_000_000;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
