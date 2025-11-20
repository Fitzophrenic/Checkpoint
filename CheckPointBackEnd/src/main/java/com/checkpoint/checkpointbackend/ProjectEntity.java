package com.checkpoint.checkpointbackend;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Project")
public class ProjectEntity {

    @Id
    private String projectID;

    @Column(nullable = false, length = 50)
    private String projectName;

    @Column(nullable = false, length = 12)
    private String ownerID;

    @Column(columnDefinition = "JSON")
    @Convert(converter = JSONProjectConverter.class)
    private JSONProjectJSON projectJSON;

    @Column(columnDefinition = "JSON")
    @Convert(converter = JSONProjectBoardConverter.class)
    private JSONProjectBoard boardsJSON;

}
