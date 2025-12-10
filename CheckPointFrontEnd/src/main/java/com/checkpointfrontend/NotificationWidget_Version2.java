package com.checkpointfrontend;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.effect.DropShadow;
import javafx.util.Duration;

public class NotificationWidget extends StackPane {

    private final VBox notificationsContainer = new VBox(8);

    // default display time for non-persistent notifications
    private Duration defaultDuration = Duration.seconds(4);

    public NotificationWidget() {
        getStyleClass().add("notification-widget");
        notificationsContainer.setPickOnBounds(false);
        notificationsContainer.setPadding(new Insets(8));
        notificationsContainer.setAlignment(Pos.TOP_RIGHT);
        getChildren().add(notificationsContainer);

        // optional drop shadow so notifications stand out
        setEffect(new DropShadow(8, Color.gray(0, 0.35)));
        setMouseTransparent(false);
    }

    //transient notification
    public void show(String text) {
        show(text, defaultDuration, false);
    }

    //duration null or Duration.INDEFINITE - becomes persistent until closed
    public void show(String text, Duration duration, boolean emphasize) {
        HBox card = new HBox();
        card.getStyleClass().add("notification-card");
        if (emphasize) card.getStyleClass().add("notification-emphasize");
        card.setAlignment(Pos.CENTER_LEFT);
        card.setSpacing(8);
        card.setPadding(new Insets(8));

        Label lbl = new Label(text);
        lbl.getStyleClass().add("notification-label");
        lbl.setWrapText(true);
        HBox.setHgrow(lbl, Priority.ALWAYS);

        Button close = new Button("✕");
        close.getStyleClass().add("notification-close");
        close.setOnAction(e -> dismiss(card));

        card.getChildren().addAll(lbl, close);

        // add to top
        notificationsContainer.getChildren().add(0, card);

        // entrance animation
        FadeTransition in = new FadeTransition(Duration.millis(220), card);
        in.setFromValue(0.0);
        in.setToValue(1.0);
        in.play();

        if (duration != null && !duration.isIndefinite()) {
            PauseTransition pause = new PauseTransition(duration);
            pause.setOnFinished(e -> dismiss(card));
            // pause/resume on hover
            card.addEventHandler(MouseEvent.MOUSE_ENTERED, (ev) -> pause.pause());
            card.addEventHandler(MouseEvent.MOUSE_EXITED, (ev) -> pause.play());
            pause.play();
        }
    }

    //fade out
    private void dismiss(HBox card) {
        FadeTransition out = new FadeTransition(Duration.millis(180), card);
        out.setFromValue(card.getOpacity());
        out.setToValue(0.0);
        out.setOnFinished(e -> notificationsContainer.getChildren().remove(card));
        out.play();
    }

    //clear
    public void clearAll() {
        notificationsContainer.getChildren().clear();
    }

    //set default duration
    public void setDefaultDuration(Duration duration) {
        if (duration != null) this.defaultDuration = duration;
    }
}