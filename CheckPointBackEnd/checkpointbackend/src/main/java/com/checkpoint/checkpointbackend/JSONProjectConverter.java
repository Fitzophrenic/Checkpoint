package com.checkpoint.checkpointbackend;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class JSONConverter implements AttributeConverter<JSONProjectJSON, String> {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(JSONProjectJSON attribute) {
        try {
            return mapper.writeValueAsString(attribute);
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to serialize JSON", e);
        }
    }

    @Override
    public JSONProjectJSON convertToEntityAttribute(String dbData) {
        try {
            return mapper.readValue(dbData, JSONProjectJSON.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to deserialize JSON", e);
        }
    }
}
