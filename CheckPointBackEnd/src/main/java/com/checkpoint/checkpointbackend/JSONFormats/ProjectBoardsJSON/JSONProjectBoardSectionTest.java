package com.checkpoint.checkpointbackend.JSONFormats.ProjectBoardsJSON;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

//getters and setters again
public class JSONProjectBoardSectionTest {

    @Test
    public void gettersAndSetters_workAsExpected() {
        JSONProjectBoardSection section = new JSONProjectBoardSection();

        section.setBoardName("Backlog");
        section.setContent("{\"items\":[]}");

        assertEquals("Backlog", section.getBoardName());
        assertEquals("{\"items\":[]}", section.getContent());
    }
}
