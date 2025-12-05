package com.checkpointfrontend.calendar;


import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CalendarJSONConverter {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static String convertToString(YearsJSONFormat board) {
        try {
            return mapper.writeValueAsString(board);
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to serialize YearsJSONFormat", e);
        }
    }

    public static YearsJSONFormat convertFromString(String json) {
        try {
            return mapper.readValue(json, YearsJSONFormat.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to deserialize YearsJSONFormat", e);
        }
    }

    public static YearsJSONFormat convertFromMap(Map<String, Object> map) {
        try {
            String json = mapper.writeValueAsString(map);
            return mapper.readValue(json, YearsJSONFormat.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to convert Map to YearsJSONFormat", e);
        }
    }

    public static String convertMapToString(Map<String, Object> map) {
        try {
            return mapper.writeValueAsString(map);
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to serialize Map", e);
        }
    }
}
