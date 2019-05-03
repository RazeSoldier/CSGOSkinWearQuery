package cn.razesoldier.csgo.query.ui.action;

import cn.razesoldier.csgo.query.Config;
import cn.razesoldier.csgo.query.Main;
import cn.razesoldier.csgo.query.ProxySettingWindow;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SettingProxyAction implements IAction {
    private Config config;

    SettingProxyAction(Main main) {
        config = Config.getInstance();
    }

    public void exec() throws Exception {
        Stage stage = new Stage();
        stage.setTitle("代理设置");
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("proxy-setting.fxml"));
        Parent root = fxmlLoader.load();
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);

        ProxySettingWindow controller = fxmlLoader.getController();
        if (config.containsKey("proxy")) {
            controller.proxy_text.setText(config.get("proxy"));
        }

        stage.showAndWait();
    }
}
