package com.checkpoint.checkpointbackend;
import org.springframework.stereotype.Component;


  @Component
public class JSONProjectUserPerm {

    private String username;
    private String permissionLevel;

    public String getUsername() {
        return username;
    }

    public String getPermissionLevel() {
        return permissionLevel;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPermissionLevel(String permissionLevel) {
        this.permissionLevel = permissionLevel;
    }
}
    

