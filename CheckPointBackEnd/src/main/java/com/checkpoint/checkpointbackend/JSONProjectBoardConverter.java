package com.checkpoint.checkpointbackend;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
@Component
public class JSONProjectBoardConverter implements AttributeConverter<JSONProjectBoard, String>{
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(JSONProjectBoard attribute) {
        try {
            return mapper.writeValueAsString(attribute);
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to serialize JSON", e);
        }
    }

    @Override
    public JSONProjectBoard convertToEntityAttribute(String dbData) {
        try {
            return mapper.readValue(dbData, JSONProjectBoard.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to deserialize JSON", e);
        }
    }
}
