package main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.image.Image;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Window;

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

        initializeGrafiek(primaryStage);
        primaryStage.show();

        Main.primaryStage = primaryStage;
        // enable AutoSave by default on launch
        autoSaveControl.enable();
    }

    private void initializeGrafiek(Window owner) {
        Stage stage = new Stage();
        stage.setTitle("Kringenzuip");
        stage.initOwner(owner);
        stage.getIcons().add(new Image(this.getClass().getResource("/media/flat_beer.png").toString()));

        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        final BarChart<String, Number> barChart = new BarChart<String, Number>(xAxis, yAxis);
        barChart.setLegendVisible(false);

        XYChart.Series series = new XYChart.Series();

        for (Kring kring : kringen) {
            final XYChart.Data<String, Number> data = new XYChart.Data(kring.getNaam(), kring.getAantal());
            data.nodeProperty().addListener(new ChangeListener<Node>() {
                @Override
                public void changed(ObservableValue<? extends Node> ov, Node oldNode, final Node node) {
                    if (node != null) {
                        setNodeStyle(data, kring);
                        displayLabelForData(data);
                    }
                }
            });
            series.getData().add(data);
        }

        Scene scene = new Scene(barChart, 800, 600);
        barChart.getData().add(series);
        stage.setScene(scene);

        stage.show();
    }

    private void setNodeStyle(XYChart.Data<String, Number> data, Kring kring) {
        Node node = data.getNode();
        node.setStyle("-fx-bar-fill: #" + kring.getKleur().toString().substring(2, 8) + ";");
    }

    /**
     * places a text label with a bar's value above a bar node for a given XYChart.Data
     */
    private void displayLabelForData(XYChart.Data<String, Number> data) {
        final Node node = data.getNode();
        final Text dataText = new Text(data.getYValue() + "");
        node.parentProperty().addListener(new ChangeListener<Parent>() {
            @Override
            public void changed(ObservableValue<? extends Parent> ov, Parent oldParent, Parent parent) {
                Group parentGroup = (Group) parent;
                parentGroup.getChildren().add(dataText);
            }
        });

        node.boundsInParentProperty().addListener(new ChangeListener<Bounds>() {
            @Override
            public void changed(ObservableValue<? extends Bounds> ov, Bounds oldBounds, Bounds bounds) {
                dataText.setLayoutX(
                        Math.round(
                                bounds.getMinX() + bounds.getWidth() / 2 - dataText.prefWidth(-1) / 2
                        )
                );
                dataText.setLayoutY(
                        Math.round(
                                bounds.getMinY() - dataText.prefHeight(-1) * 0.5
                        )
                );
            }
        });
    }
}
