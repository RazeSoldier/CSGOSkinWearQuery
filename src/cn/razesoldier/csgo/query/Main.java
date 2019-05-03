package cn.razesoldier.csgo.query;

import cn.razesoldier.csgo.query.ui.action.ActionFactory;
import cn.razesoldier.csgo.query.ui.action.IAction;
import cn.razesoldier.csgo.query.ui.item.AlertBox;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.File;

/**
 * 图形程序入口点
 * 兼main.fxml控制器
 */
public class Main extends Application {
    final static public String rootPath = System.getProperty("user.dir");
    final static public String reportPath = rootPath + "/report";

    public ChoiceBox wearLevel;
    public TextField skinName;
    public Button queryButton;
    public Button parseButton;
    public TextField proxy;

    private Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        primaryStage = stage;
        primaryStage.setTitle("CSGO枪支磨损查询器");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();
    }

    public static void main(String[] args) {
        File reportDir = new File(reportPath);
        if (!reportDir.exists()) {
            // noinspection ResultOfMethodCallIgnored
            reportDir.mkdir();
        }
        launch(args);
    }

    /**
     * 当点击“查询”按钮时，执行此方法
     */
    @FXML
    private void onQuery() {
        IAction action = ActionFactory.make("query", this);
        AlertBox box = new AlertBox(Alert.AlertType.WARNING, "RuntimeException");
        Task<Boolean> task = new Task<Boolean>() {
            @Override
            protected Boolean call() {
                updateValue(true);
                updateMessage("查询中..");
                action.exec();
                return null;
            }

            @Override
            protected void succeeded() {
                updateValue(false);
                updateMessage("查询");
            }

            @Override
            protected void failed() {
                updateValue(false);
                updateMessage("查询");
            }
        };
        queryButton.disableProperty().bind(task.valueProperty());
        queryButton.textProperty().bind(task.messageProperty());
        task.setOnFailed((WorkerStateEvent event) -> {
            Exception e = (Exception)task.getException();
            if (e != null) {
                box.setMsg(e.getClass().getTypeName() + ": " + e.getLocalizedMessage());
                box.show();
            }
        });
        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();
    }

    /**
     * 当点击“分析报告”按钮时，执行此方法
     */
    @FXML
    private void onParse() {
        IAction action = ActionFactory.make("parse", this);
        try {
            action.exec();
        } catch (RuntimeException e) {
            AlertBox box = new AlertBox(Alert.AlertType.WARNING, "RuntimeException", e.getLocalizedMessage());
            box.show();
            e.printStackTrace();
        }
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }
}
