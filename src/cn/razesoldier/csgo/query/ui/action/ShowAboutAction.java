package cn.razesoldier.csgo.query.ui.action;

import cn.razesoldier.csgo.query.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * about.fxml的控制器
 */
public class ShowAboutAction implements IAction {
    @FXML
    private Hyperlink link;

    public void exec() throws Exception {
        Stage stage = new Stage();
        stage.setTitle("关于");
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("about.fxml"));
        Parent root = fxmlLoader.load();
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    /**
     * 当点击源代码的超链接时，执行此方法
     */
    @FXML
    private void onClickLink() {
        try {
            Desktop.getDesktop().browse(new URI(link.getText()));
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
    }
}
