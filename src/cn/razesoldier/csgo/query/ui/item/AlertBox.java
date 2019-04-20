package cn.razesoldier.csgo.query.ui.item;

import javafx.scene.control.Alert;

public class AlertBox {
    private Alert box;

    public AlertBox(Alert.AlertType type, String title) {
        box = new Alert(type);
        box.setTitle(title);
    }

    public AlertBox(Alert.AlertType type, String title, String msg) {
        box = new Alert(type);
        box.setTitle(title);
        box.setContentText(msg);
    }

    public void setMsg(String msg) {
        box.setContentText(msg);
    }

    public void show() {
        box.showAndWait();
    }
}
