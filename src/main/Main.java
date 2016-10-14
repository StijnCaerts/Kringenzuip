package main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.XYChart;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.HashSet;

public class Main extends Application {

    private static HashSet<Kring> kringen = new HashSet<>();
    private static Stage primaryStage;
    private static AutoSaveControl autoSaveControl = new AutoSaveControl();
    private static Controller controller;
    private static Stage grafiek;
    private static XYChart.Series series;

    static HashSet<Kring> getKringen() {
        return kringen;
    }

    static boolean kringAlreadyExists(String naam) {
        HashSet<Kring> kringen = getKringen();
        for (Kring kring : kringen) {
            if (kring.getNaam().equals(naam)) {
                return true;
            }
        }
        return false;
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

    static void setController(Controller controller) {
        Main.controller = controller;
    }

    static Stage getGrafiek() {
        return grafiek;
    }

    static void setGrafiek(Stage grafiek) {
        Main.grafiek = grafiek;
    }

    static XYChart.Series getSeries() {
        return series;
    }

    static void setSeries(XYChart.Series series) {
        Main.series = series;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader();
        Parent root = FXMLLoader.load(getClass().getResource("punten.fxml"));
        Scene scene = new Scene(root);

        primaryStage.setTitle("Kringenzuip");
        primaryStage.getIcons().add(new Image(this.getClass().getResource("/media/flat_beer.png").toString()));

        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(event -> {
            Platform.exit();
            autoSaveControl.disable();
            System.exit(0);
        });

        getController().initializeGrafiek(primaryStage);
        primaryStage.show();

        Main.primaryStage = primaryStage;
        // enable AutoSave by default on launch
        autoSaveControl.enable();
    }


}
