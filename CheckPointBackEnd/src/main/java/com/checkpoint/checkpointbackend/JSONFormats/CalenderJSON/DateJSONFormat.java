package com.checkpoint.checkpointbackend.JSONFormats.CalenderJSON;

import java.util.List;

public class DateJSONFormat {
    private String date;
    private List<EventJSONFormat> events;

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public List<EventJSONFormat> getEvents() { return events; }
    public void setEvents(List<EventJSONFormat> events) { this.events = events; }
}
