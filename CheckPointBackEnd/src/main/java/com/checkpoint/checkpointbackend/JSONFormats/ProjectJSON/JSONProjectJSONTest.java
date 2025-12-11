package com.checkpoint.checkpointbackend.JSONFormats.ProjectJSON;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

//getter/setter tests
public class JSONProjectJSONTest {

    @Test
    public void gettersAndSetters_workAsExpected() {
        JSONProjectJSON pj = new JSONProjectJSON();

        pj.setProjectID("p1");
        pj.setProjectName("Test Project");

        JSONProjectUserPerm user1 = new JSONProjectUserPerm();
        user1.setUserID("u1");
        user1.setPermissionLevel("o");

        pj.setUsers(List.of(user1));

        assertEquals("p1", pj.getProjectID());
        assertEquals("Test Project", pj.getProjectName());
        assertNotNull(pj.getUsers());
        assertEquals(1, pj.getUsers().size());
        assertEquals("u1", pj.getUsers().get(0).getUserID());
    }
}
