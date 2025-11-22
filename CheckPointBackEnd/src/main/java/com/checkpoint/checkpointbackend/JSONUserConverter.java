package com.checkpoint.checkpointbackend;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
@Component
public class JSONUserConverter implements AttributeConverter<JSONUser, String>{
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(JSONUser attribute) {
        try {
            return mapper.writeValueAsString(attribute);
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to serialize JSON", e);
        }
    }

    @Override
    public JSONUser convertToEntityAttribute(String dbData) {
        try {
            return mapper.readValue(dbData, JSONUser.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to deserialize JSON", e);
        }
    }
}
