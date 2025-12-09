package com.checkpointfrontend;

import javafx.scene.layout.Region;

public abstract class Widget extends Region {
    public abstract String convertDataToText();
    public abstract void convertTextToData(String content);
    
}
