package com.checkpoint.checkpointbackend;

public class JSONProjectJSON {
    private String projectName;
    private List<UserPermission> users;

    // Getters
    public String getProjectName() {
        return projectName;
    }

    public List<UserPermission> getUsers() {
        return users;
    }

    // Setters
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public void setUsers(List<UserPermission> users) {
        this.users = users;
    }
}
