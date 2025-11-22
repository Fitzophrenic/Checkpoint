package com.checkpoint.checkpointbackend;
import java.util.List;

import org.springframework.stereotype.Component;


@Component

public class JSONProjectJSON {
    private String projectID;
    private String projectName;
    private List<JSONProjectUserPerm> users;

    public String getProjectID() { return projectID; }
    public void setProjectID(String projectID) { this.projectID = projectID; }

    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }

    public List<JSONProjectUserPerm> getUsers() { return users; }
    public void setUsers(List<JSONProjectUserPerm> users) { this.users = users; }
}
