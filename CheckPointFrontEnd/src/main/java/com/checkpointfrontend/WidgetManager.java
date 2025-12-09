package com.checkpointfrontend;

import javafx.scene.layout.Region;

public class WidgetManager extends Region {
    Widget currentWidget;
    public WidgetManager() {

    }
    public String convertDataToText() {
        return currentWidget.convertDataToText();
    }

    public void convertTextToData(String content) {
        currentWidget.convertTextToData(content);
    }

    public void clear() {
        this.getChildren().clear();
    }

    public void updateWidget(Widget widget) {
        currentWidget = widget;
        this.getChildren().add(currentWidget);
    }

    public void determineAndUpdateWidget(String content) {
        clear();
        String type = content.substring(0, content.indexOf(':'));
        String contentToSend = content.substring(content.indexOf(':')+1 );
        Widget newWidget = null;
        switch (type) {
            case "Timer":
                newWidget = new WidgetTimer();
                break;
            case "Board":
                newWidget = new WidgetBoard();
                break;
            default:
                break;
            
        }
        updateWidget(newWidget);
        newWidget.convertTextToData(contentToSend);
    }
}
