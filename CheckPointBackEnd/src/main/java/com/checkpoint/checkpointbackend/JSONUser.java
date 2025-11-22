package com.checkpoint.checkpointbackend;

import java.util.List;

public class JSONUser {
    private String userID;
    private String userName;
    private List<JSONUserProjects> projects;
    public String getuserID() { return userID; }
    public void setuserID(String userID) { this.userID = userID; }

    public String getuserName() { return userName; }
    public void setuserName(String userName) { this.userName = userName; }

    public List<JSONUserProjects> getprojects() { return projects; }
    public void setUsers(List<JSONUserProjects> projects) { this.projects = projects; }
}
