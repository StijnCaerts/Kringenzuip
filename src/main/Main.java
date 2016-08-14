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
    private static Stage primaryStage;
    private static AutoSaveControl autoSaveControl = new AutoSaveControl();
    private static Controller controller;

    static HashSet<Kring> getKringen() {
        return kringen;
    }

    static Stage getPrimaryStage() {
        return primaryStage;
    }

    static AutoSaveControl getAutoSaveControl() {
        return autoSaveControl;
    }

    static Controller getController() {
        return controller;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader();
        Parent root = FXMLLoader.load(getClass().getResource("punten.fxml"));
        controller = loader.getController();
        Scene scene = new Scene(root);

        primaryStage.setTitle("Kringenzuip");
        primaryStage.getIcons().add(new Image(this.getClass().getResource("/media/flat_beer.png").toString()));

        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(event -> {
            Platform.exit();
            autoSaveControl.disable();
            System.exit(0);
        });

        primaryStage.show();
        Main.primaryStage = primaryStage;

        // enable AutoSave by default on launch
        autoSaveControl.enable();
    }
}
