package sample;

import compiler.LrParser;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("compiler");
        primaryStage.setScene(new Scene(root, 1100, 760));
        primaryStage.setResizable(false);
        LrParser.initC();
        LrParser.initLrTable();
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
