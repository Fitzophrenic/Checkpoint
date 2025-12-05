package com.checkpointfrontend.calendar;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class CalendarDate extends Region {

    private final Label dayNumLabel;
    private final VBox box = new VBox(2);
    private final VBox eventsBox = new VBox(2);
    private final ScrollPane scrollPane = new ScrollPane(eventsBox);

    private static final int CELL_HEIGHT = 80;

    public CalendarDate(DateJSONFormat dateInfo) {
        dayNumLabel = new Label(dateInfo.getDate());
        box.getChildren().add(dayNumLabel);

        for (EventJSONFormat event : dateInfo.getEvents()) {
            EventRepersentaionForCalendarVeiw eventLabel = new EventRepersentaionForCalendarVeiw(event);
            eventLabel.setMaxWidth(Double.MAX_VALUE);
            eventsBox.getChildren().add(eventLabel);
        }

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        box.getChildren().add(scrollPane);

        this.setMinHeight(CELL_HEIGHT);
        this.setMaxHeight(CELL_HEIGHT);
        this.setPrefHeight(CELL_HEIGHT);

        box.setFillWidth(true);
        box.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(box, Priority.ALWAYS);

        this.getChildren().add(box);

        setupHoverEffects();
    }

    public CalendarDate(String dateInfo) {
        dayNumLabel = new Label(dateInfo);
        box.getChildren().add(dayNumLabel);

        this.setMinHeight(CELL_HEIGHT);
        this.setMaxHeight(CELL_HEIGHT);
        this.setPrefHeight(CELL_HEIGHT);

        this.getChildren().add(box);
        if(dateInfo.equals("")) {
            return;
        }
        setupHoverEffects();
    }

    private void setupHoverEffects() {
        this.setOnMouseEntered(e -> this.setStyle("-fx-background-color: #70b488ff;"));
        this.setOnMouseExited(e -> this.setStyle("-fx-background-color: #f5f5f5;"));

        scrollPane.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-background-insets: 0;" +
            "-fx-padding: 0;" +
            "-fx-focus-color: transparent;" +
            "-fx-faint-focus-color: transparent;" +
            "-fx-border-color: transparent;"
        );

        scrollPane.applyCss();
        scrollPane.layout();

        Platform.runLater(() -> {
            Node vp = scrollPane.lookup(".viewport");
            if (vp instanceof Region region) {
                region.setStyle("-fx-background-color: transparent;");
            }
        });

        box.setStyle("-fx-background-color: transparent;");
        eventsBox.setStyle("-fx-background-color: transparent;");
    }

    @Override
    protected void layoutChildren() {
        box.resizeRelocate(0, 0, getWidth(), getHeight());
    }
}