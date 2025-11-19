package com.checkpoint.checkpointbackend;
import org.springframework.stereotype.Component;

@Component

public class JSONProjectUserPerm {
    

    private String userID;
    private String permissionLevel;

    public String getUserID() {
        return userID;
    }

    public String getPermissionLevel() {
        return permissionLevel;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setPermissionLevel(String permissionLevel) {
        this.permissionLevel = permissionLevel;
    }
    
}
