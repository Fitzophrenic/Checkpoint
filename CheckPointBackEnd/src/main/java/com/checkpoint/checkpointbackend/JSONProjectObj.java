package com.checkpoint.checkpointbackend;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.springframework.stereotype.Component;

@Entity
@Component

public class JSONProjectObj {
    @Id
    private String id;

    @Convert(converter = JSONProjectConverter.class)
    @Column(columnDefinition = "json")
    private JSONProjectJSON payload;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public JSONProjectJSON getPayload() {
        return payload;
    }

    public void setPayload(JSONProjectJSON payload) {
        this.payload = payload;
    }


    
}
