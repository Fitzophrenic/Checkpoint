package com.checkpoint.checkpointbackend.JSONFormats.UserJSON;

import java.util.List;

public class JSONUser {
    private String username;
    private List<JSONUserProjects> projects;
   
    public String getuserName() { return username; }
    public void setuserName(String username) { this.username = username; }

    public List<JSONUserProjects> getprojects() { return projects; }
    public void setUsers(List<JSONUserProjects> projects) { this.projects = projects; }
}
