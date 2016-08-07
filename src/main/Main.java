package main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.HashSet;

public class Main extends Application {

    private static HashSet<Kring> kringen = new HashSet<>();

    static HashSet<Kring> getKringen() {
        return kringen;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("punten.fxml"));
        Scene scene = new Scene(root);

        primaryStage.setTitle("Kringenzuip");
        primaryStage.getIcons().add(new Image(this.getClass().getResource("/media/flat_beer.png").toString()));

        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(event -> Platform.exit());

        primaryStage.show();
    }
}
