package com.checkpointfrontend.calendar;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DateJSONFormat {
    private String date;
    private List<EventJSONFormat> events;
    public DateJSONFormat() {
        events = new ArrayList<>();
    }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public List<EventJSONFormat> getEvents() { return events; }
    public void setEvents(List<EventJSONFormat> events) { this.events = events; }
    public void sortEventsByTime() {
        events.sort((e1, e2) -> parseStartTime(e1.getTimeFrame()).compareTo(parseStartTime(e2.getTimeFrame())));
    }

    private LocalTime parseStartTime(String timeFrame) {
        if (timeFrame == null || timeFrame.isEmpty()) {
            return LocalTime.MIN;
        }
        try {
            String tf = timeFrame.trim().toUpperCase();
            DateTimeFormatter format = DateTimeFormatter.ofPattern("h:mm a");
            if (tf.matches("\\d+\\s*(AM|PM)")) {
                tf = tf.replace("AM", ":00 AM").replace("PM", ":00 PM");
            }
            if (tf.contains("-")) {
                tf = tf.split("-")[0].trim();
            }
            return LocalTime.parse(tf, format);
        } catch (Exception ex) {
            return LocalTime.MIN;
        }
    }
}
