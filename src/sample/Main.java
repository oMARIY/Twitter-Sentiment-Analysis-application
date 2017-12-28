package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Twitter Sentiment Analysis App");
        primaryStage.setScene(new Scene(root, 1200, 700, Color.ALICEBLUE));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
