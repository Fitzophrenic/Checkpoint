package com.checkpoint.checkpointbackend;
import java.util.List;

import org.springframework.stereotype.Component;


@Component

public class JSONProjectJSON {
    private String projectName;
    private List<JSONProjectUserPerm> users;

    
    public String getProjectName() {
        return projectName;
    }

    public List<JSONProjectUserPerm> getUsers() {
        return users;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public void setUsers(List<JSONProjectUserPerm> users) {
        this.users = users;
    }
    
}
