package com.checkpoint.checkpointbackend.JSONFormats.UserJSON;

public class JSONUserProjects {

    private String projectID;
    private String projectName;
    private String permissionLevel;
    
    public String getProjectID() {
        return projectID;
    }
    public String getProjectName() {
        return projectName;
    }

    public String getPermissionLevel() {
        return permissionLevel;
    }

    public void setProjectID(String projectID) {
        this.projectID = projectID;
    }
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
    public void setPermissionLevel(String permissionLevel) {
        this.permissionLevel = permissionLevel;
    }
}
