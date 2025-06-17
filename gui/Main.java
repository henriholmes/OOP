package gui;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        MainView view = new MainView(primaryStage);
        view.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}