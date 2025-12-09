package com.checkpointfrontend.calendar;

import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

public class EventRepersentaionForCalendarVeiw extends HBox{
    private Label eventName;
    public EventRepersentaionForCalendarVeiw(EventJSONFormat event) {
        eventName = new Label(event.getEventName() + " " + event.getTimeFrame());
        eventName.setStyle("-fx-text-fill: white;");
        eventName.setEllipsisString("...");
        eventName.setMaxWidth(Double.MAX_VALUE);
        Tooltip tooltip = new Tooltip(event.getEventName() + " " + event.getTimeFrame());
        Tooltip.install(this, tooltip);
        tooltip.setShowDelay(Duration.millis(50));
        tooltip.setStyle(String.format(
            """
                -fx-background-color: %s;
                -fx-text-fill: white;
                -fx-padding: 8;
                -fx-font-size: 14px;
                -fx-background-radius: 8;
                -fx-border-radius: 8;
                -fx-border-width: 1;
            """
        , event.getEventCol()));
        this.getChildren().addAll(eventName);
        if(event.getEventCol() != null && !event.getEventCol().isEmpty()) {
            this.setStyle(String.format("-fx-background-color: %s; -fx-padding: 10; -fx-background-radius: 8; -fx-alignment: center;", event.getEventCol()));
        } else {
            this.setStyle("-fx-padding: 10;");
        }
    }
}
