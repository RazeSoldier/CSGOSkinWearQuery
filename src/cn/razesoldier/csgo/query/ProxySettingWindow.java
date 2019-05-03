package cn.razesoldier.csgo.query;

import cn.razesoldier.csgo.query.ui.item.AlertBox;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

/**
 * proxy-setting.fxml的控制器
 */
public class ProxySettingWindow {
    public TextField proxy_text;

    @FXML
    public void onSave() {
        String proxy = proxy_text.getText();
        if (!proxy.equals("")) {
            if (!proxy.matches("[0-9]*\\.[0-9]*\\.[0-9]*\\.[0-9]*:[0-9]*")) {
                AlertBox box = new AlertBox(Alert.AlertType.WARNING, "错误的IP地址", "非法的代理IP地址");
                box.show();
                return;
            }
        }
        Config.getInstance().set("proxy", proxy);
    }
}
