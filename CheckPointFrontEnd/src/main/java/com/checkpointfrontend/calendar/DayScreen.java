package com.checkpointfrontend.calendar;

import java.time.LocalDate;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class DayScreen extends VBox{
    private final HBox header = new HBox();
    private final VBox events = new VBox(3);
    private final HBox addSection = new HBox();
    private final VBox botSec = new VBox(3);
    private final ScrollPane scrollPane = new ScrollPane(events);

    private DateJSONFormat day;
    private Scene calendarScreen;
    private CalendarScreen stage;
    private LocalDate date; 
    private boolean isProject;
    public DayScreen(DateJSONFormat dayRequested, CalendarScreen stage, Scene calVeiw, LocalDate date, boolean isProject) {
        day = dayRequested;
        calendarScreen = calVeiw;
        this.stage = stage;
        this.isProject = isProject;
        this.date = date;
        dayRequested.sortEventsByTime();
        if(!dayRequested.getEvents().isEmpty()) {
            for(EventJSONFormat event : dayRequested.getEvents()) {
                EventRepersentaionForDayVeiw veiw = new EventRepersentaionForDayVeiw(event, isProject,this);
                veiw.configureDeleteButton(dayRequested.getEvents());
                events.getChildren().add(veiw);
            }
        }
        initialize();
    }
    private void initialize(){
        this.getStylesheets().add(getClass().getResource("/com/checkpointfrontend/Calendar.css").toExternalForm());
        Button returnButton = new Button("Return");
        returnButton.setOnAction(e -> {
            stage.setScene(calendarScreen);
            stage.onReturn();
        });
        Label dateLabel = new Label(date.toString());
        dateLabel.getStyleClass().add("monthLabel");
        header.getStyleClass().add("header");
        header.getChildren().add(dateLabel);
        header.getChildren().add(returnButton);
        scrollPane.setPrefHeight(500);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        this.getChildren().add(header);
        this.getChildren().add(scrollPane);
        Button newEventButtton = new Button("add event");
        TextField newEventFeild = new TextField();
        newEventFeild.setPromptText("enter event name");
        ChoiceBox<String> colors = new ChoiceBox<>();
        colors.getItems().add("Red");
        colors.getItems().add("Blue");
        colors.getItems().add("Green");
        colors.getItems().add("Yellow");
        colors.getItems().add("Purple");
        colors.getSelectionModel().select(0);

        ChoiceBox<String> initialTime = new ChoiceBox<>();
        for (int hour = 0; hour < 24; hour++) {
            for (int min = 0; min < 60; min += 15) {
                String period = (hour < 12) ? "AM" : "PM";
                int hour12 = hour % 12;
                if (hour12 == 0) {
                    hour12 = 12;
                }
                String minute = String.format("%02d", min);
                String time = hour12 + ":" + minute + " " + period;
                initialTime.getItems().add(time);
            }
        }
        initialTime.getSelectionModel().select(0);

        ChoiceBox<String> endTime = new ChoiceBox<>();
        for (int hour = 0; hour < 24; hour++) {
            for (int min = 0; min < 60; min += 15) {
                String period = (hour < 12) ? "AM" : "PM";
                int hour12 = hour % 12;
                if (hour12 == 0) {
                    hour12 = 12;
                }
                String minute = String.format("%02d", min);
                String time = hour12 + ":" + minute + " " + period;
                endTime.getItems().add(time);
            }
        }
        endTime.getSelectionModel().select(0);
        CheckBox checkBox = new CheckBox("This event has an ending time");

        addSection.getChildren().addAll(newEventFeild, colors, initialTime);
        addSection.setStyle("-fx-alignment: center;");
        botSec.getChildren().add(addSection);
        botSec.getChildren().add(checkBox);
        botSec.getChildren().add(newEventButtton);
        botSec.getStyleClass().add("bottomSection");
        this.getChildren().add(botSec);
        
        newEventButtton.setOnMouseClicked(e -> {
            if(newEventFeild.getText().isBlank()) {
                return;
            }
            String color = colors.getValue();
            switch (color) {
                case "Red" -> {
                    color = "#b31a1a";
                }
                case "Blue" -> {
                    color = "#162870";
                }
                case "Green" -> {
                    color = "#085c11";
                }
                case "Yellow" -> {
                    color = "#b3d617ff";
                }
                case "Purple" -> {
                    color = "#48085cff";
                }
            }
            EventJSONFormat even = new EventJSONFormat();
            even.setEventName(newEventFeild.getText());
            even.setEventCol(color);
            String timeFrame = initialTime.getValue();
            if(checkBox.isSelected()) {
                timeFrame = timeFrame + "-" +endTime.getValue();
            }
            even.setTimeFrame(timeFrame);
            day.getEvents().add(even);
            EventRepersentaionForDayVeiw veiw = new EventRepersentaionForDayVeiw(even, isProject,this);
            veiw.configureDeleteButton(day.getEvents());
            events.getChildren().add(veiw);
            newEventFeild.clear();

        });

        checkBox.setOnAction(e -> {
            if (checkBox.isSelected()) {
                addSection.getChildren().add(endTime);
                checkBox.setText("This event doesn't have an end time");
            } else {
                addSection.getChildren().remove(endTime);
                checkBox.setText("This event has an ending time");
            }
        });
    }
    public void addEvent(EventJSONFormat event) {
        stage.addEventToUser(date, stage.getUser(), event);
    }
}
