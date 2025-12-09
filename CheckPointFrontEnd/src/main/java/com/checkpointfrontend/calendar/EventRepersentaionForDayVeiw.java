package com.checkpointfrontend.calendar;


import java.util.List;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class EventRepersentaionForDayVeiw extends HBox{
    private Label eventName;
    private Button deleteEvent;
    private Button addEventToPersonalCalendar;
    private EventJSONFormat event;
    public EventRepersentaionForDayVeiw(EventJSONFormat event, boolean isProject, DayScreen screen) {
        this.setSpacing(10);
        this.event = event;
        eventName = new Label(event.getEventName() + " " + event.getTimeFrame());
        eventName.setStyle("-fx-text-fill: white;");
        eventName.setEllipsisString("...");
        eventName.setMaxWidth(Double.MAX_VALUE);
        deleteEvent = new Button("X"); 
        deleteEvent.setStyle("-fx-text-fill: white;");
        this.getChildren().addAll(eventName);

        if(isProject) {
            addEventToPersonalCalendar = new Button("add to your calendar");
            this.getChildren().add(addEventToPersonalCalendar);
            // Parent p = this.getParent();
            // Parent p2 = p.getParent();
            // Parent p3 = p2.getParent();
            // DayScreen screen;
            // if (p3 instanceof DayScreen dayScreen) {
            //     screen = dayScreen;
            // } else {
            //     return;
            // }
            addEventToPersonalCalendar.setOnAction(e -> {
                screen.addEvent(event);
            });
        }
        this.getChildren().addAll(deleteEvent);
        if(event.getEventCol() != null && !event.getEventCol().isEmpty()) {
            this.setStyle(String.format("-fx-background-color: %s; -fx-padding: 10; -fx-alignment: center;", event.getEventCol()));
        } else {
            this.setStyle("-fx-padding: 10;");
        }
    }
    public void configureDeleteButton(List<EventJSONFormat> events) {
        deleteEvent.setOnAction(e -> {
            events.remove(event);
            this.getChildren().clear();
            this.setStyle(String.format("-fx-background-color: transparent; "));
        });
        
    }
}
