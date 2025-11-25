package com.checkpointfrontend;

import javafx.scene.control.TextArea;

public class WidgetBoard extends  Widget{
    TextArea notesSection = new TextArea();
    @Override
    public String convertDataToText() {
        return "Board:" + notesSection.getText();
    }

    @Override
    public void convertTextToData(String content) {
        notesSection.setText(content);
    }
    public WidgetBoard() {
        notesSection.setPromptText("Write your notes here...");
        notesSection.getStyleClass().add("text-area");
        this.getChildren().add(notesSection);
        this.setPrefSize(300, 300);

    }
}
