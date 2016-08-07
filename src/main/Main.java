package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.HashSet;
import java.util.LinkedList;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("punten.fxml"));
        Scene scene = new Scene(root);

        primaryStage.setTitle("Kringenzuip");
        primaryStage.getIcons().add(new Image("file:flat_beer.png"));

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static HashSet<Kring> getKringen() {
        return kringen;
    }

    private static HashSet<Kring> kringen = new HashSet<>();

    public static void main(String[] args) {
        launch(args);
    }
}
