package com.checkpoint.checkpointbackend.JSONFormats.ProjectBoardsJSON;

import java.util.List;

public class JSONProjectBoard {
     private List<JSONProjectBoardSection> sections;

    public List<JSONProjectBoardSection> getSections() { return sections; }
    public void setSections(List<JSONProjectBoardSection> sections) { this.sections = sections; }
}
