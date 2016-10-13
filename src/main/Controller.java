package main;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Pair;

import java.io.*;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class Controller implements javafx.fxml.Initializable {

    @FXML private GridPane gridPane;
    @FXML
    private Pane pane;
    @FXML
    private MenuItem exportCSV;
    private int row = 0;
    @FXML
    private CheckMenuItem autoSave;
    @FXML
    private MenuItem grafiekOpenen;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Main.setController(this);
    }

    private MenuItem getGrafiekOpenen() {
        return this.grafiekOpenen;
    }

    void uncheckAutoSave() {
        autoSave.setSelected(false);
    }

    @FXML
    protected void handleKringToevoegen(ActionEvent event) {
        // Create the custom dialog.
        Dialog<Pair<String, Color>> dialog = new Dialog<>();
        dialog.setTitle("Kring toevoegen");
        dialog.setHeaderText("Een nieuwe kring toevoegen");

        // Get the stage
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        // Add a custom icon
        stage.getIcons().add(new Image(this.getClass().getResource("/media/ic_group_add.png").toString()));

        // Set the icon (must be included in the project).
        dialog.setGraphic(new ImageView(new Image(this.getClass().getResource("/media/ic_group_add.png").toString())));

        // Set the button types.
        ButtonType addButtonType = new ButtonType("Toevoegen", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Annuleren", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, cancelButtonType);

        // Create the kring and kleur labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField kring = new TextField();
        kring.setPromptText("Kring");
        ColorPicker colorPicker = new ColorPicker();
        colorPicker.setPromptText("Kleur");

        grid.add(new Label("Kring:"), 0, 0);
        grid.add(kring, 1, 0);
        grid.add(new Label("Kleur:"), 0, 1);
        grid.add(colorPicker, 1, 1);

        // Enable/Disable button depending on whether a kring was entered.
        Node addButton = dialog.getDialogPane().lookupButton(addButtonType);
        addButton.setDisable(true);

        // Do some validation (using the Java 8 lambda syntax).
        kring.textProperty().addListener((observable, oldValue, newValue) -> addButton.setDisable(newValue.trim().isEmpty()));

        dialog.getDialogPane().setContent(grid);

        // Request focus on the kring field by default.
        Platform.runLater(kring::requestFocus);

        // Convert the result to a kring-kleur-pair when the button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                return new Pair<>(kring.getText(), colorPicker.getValue());
            }
            return null;
        });

        Optional<Pair<String, Color>> result = dialog.showAndWait();
        result.ifPresent(kringKleur -> kringToevoegen(kringKleur.getKey(), kringKleur.getValue()));

        if (exportCSV.isDisable()) {
            exportCSV.setDisable(false);
        }
    }

    @FXML
    protected void handleSluiten(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    protected void handleExporteren(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exporteren naar CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Kommagescheiden waarden", "*.csv"));
        Stage stage = (Stage) pane.getScene().getWindow();
        File selectedFile = fileChooser.showSaveDialog(stage);
        if (selectedFile != null) {
            // write to file
            PrintWriter writer;
            try {
                writer = new PrintWriter(selectedFile, "UTF-8");
                writer.println("kring\tkleur\taantal");
                for (Kring kring : Main.getKringen()) {
                    writer.println(kring.toString());
                }
                writer.close();
            } catch (FileNotFoundException | UnsupportedEncodingException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                // Get the stage
                Stage stageAlert = (Stage) alert.getDialogPane().getScene().getWindow();
                // Add a custom icon
                stageAlert.getIcons().add(new Image(this.getClass().getResource("/media/ic_file_format_csv_close.png").toString()));

                alert.setGraphic(new ImageView(new Image(this.getClass().getResource("/media/ic_file_format_csv_close.png").toString())));
                alert.setTitle("Exporteren mislukt");
                alert.setHeaderText("Problemen bij opslaan");
                alert.setContentText("Het exporteren naar een CSV-bestand is mislukt.");
                alert.show();
            }
        }
    }

    @FXML
    protected void handleImporteren(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("CSV importeren");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Kommagescheiden waarden", "*.csv"));
        Stage stage = (Stage) pane.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            BufferedReader reader;
            try {
                reader = new BufferedReader(new FileReader(selectedFile));
                String header = reader.readLine();
                if (!header.equals("kring\tkleur\taantal")) {
                    throw new IllegalArgumentException();
                } else {
                    String line;
                    String[] array;
                    while ((line = reader.readLine()) != null) {
                        array = line.split("\t");
                        if (array.length != 3) throw new IllegalArgumentException();
                        String naam = array[0];
                        Color kleur = Color.web(array[1]);
                        int aantal = Integer.parseInt(array[2]);
                        kringToevoegen(naam, kleur, aantal);
                    }
                }
            } catch (IOException | IllegalArgumentException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                if (e.getClass() == IllegalArgumentException.class) {
                    // bestand voldoet niet aan de verwachte indeling.
                    System.out.println("kaka");
                }
                alert.show();
            }
        }
    }

    @FXML
    protected void handleAutoSave(ActionEvent event) {
        // isSelected verandert reeds voordat de klik wordt afgehandeld
        if (autoSave.isSelected()) {
            // AutoSave inschakelen
            Main.getAutoSaveControl().enable();
        } else {
            // AutoSave uitschakelen
            Main.getAutoSaveControl().disable();
        }
    }

    @FXML
    protected void handleGrafiekOpenen(ActionEvent event) {
        initializeGrafiek(null);
    }

    private void kringToevoegen(String naam, Color kleur) {
        kringToevoegen(naam, kleur, 0);
    }

    private void kringToevoegen(String naam, Color kleur, int aantal) {
        // Maak een nieuwe Kring aan
        Kring kring = new Kring(naam, kleur, aantal);
        Main.getKringen().add(kring);

        weergaveKringToevoegen(kring);
    }

    private void weergaveKringToevoegen(Kring kring) {
        // Maak de weergave-objecten voor de nieuwe kring aan
        Label labelNaam = new Label(kring.getNaam());
        labelNaam.setFont(new Font("System Bold", 16));
        labelNaam.setWrapText(true);

        Label labelAantal = new Label(Integer.toString(kring.getAantal()));

        Button buttonPlus = new Button("+");
        buttonPlus.setFont(new Font(16));
        buttonPlus.setOnAction((ActionEvent e) -> {
            kring.verhoogAantal();
            labelAantal.setText(Integer.toString(kring.getAantal()));
        });

        Button buttonPlus5 = new Button("+5");
        buttonPlus5.setFont(new Font(16));
        buttonPlus5.setOnAction((ActionEvent e) -> {
            kring.verhoogAantalMetVijf();
            labelAantal.setText(Integer.toString(kring.getAantal()));
        });

        Button buttonMin = new Button("-");
        buttonMin.setOnAction((ActionEvent e) -> {
            kring.verlaagAantal();
            labelAantal.setText(Integer.toString(kring.getAantal()));
        });

        gridPane.add(labelNaam,0, row);
        gridPane.add(buttonMin, 1, row);
        gridPane.add(buttonPlus, 2, row);
        gridPane.add(buttonPlus5, 3, row);
        gridPane.add(labelAantal, 4, row);

        row++;

        if (Main.getGrafiek() != null) {
            final XYChart.Data<String, Number> data = new XYChart.Data(kring.getNaam(), kring.getAantal());
            kring.setDataRef(data);
            data.nodeProperty().addListener(new ChangeListener<Node>() {
                @Override
                public void changed(ObservableValue<? extends Node> ov, Node oldNode, final Node node) {
                    if (node != null) {
                        setNodeStyle(data, kring);
                        displayLabelForData(data);
                    }
                }
            });
            Main.getSeries().getData().add(data);
        }

    }


    void initializeGrafiek(Window owner) {
        Stage stage = new Stage();
        stage.setTitle("Kringenzuip");
        stage.initOwner(owner);
        stage.getIcons().add(new Image(this.getClass().getResource("/media/flat_beer.png").toString()));
        Main.setGrafiek(stage);
        getGrafiekOpenen().setDisable(true);
        stage.setOnCloseRequest(event -> {
            for (Kring kring : Main.getKringen()) {
                kring.setDataRef(null);
            }
            Main.setGrafiek(null);
            Main.setSeries(null);
            getGrafiekOpenen().setDisable(false);
        });

        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        final BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setLegendVisible(false);

        XYChart.Series series = new XYChart.Series();
        Main.setSeries(series);

        /*for (Kring kring : Main.getKringen()) {
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
        }*/

        Scene scene = new Scene(barChart, 800, 600);
        barChart.setData(getChartData());
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

        final Text dataText = new Text(Integer.toString(data.getYValue().intValue()));

        // update label when Y value changes
        // why is it initially displaying as a float now?
        data.YValueProperty().addListener((ov, oldValue, newValue) ->
            dataText.setText(Integer.toString(data.getYValue().intValue()))
        );

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

    private ObservableList<XYChart.Series<String, Number>> getChartData() {
        ObservableList<XYChart.Series<String, Number>> answer = FXCollections.observableArrayList();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        Main.setSeries(series);

        for (Kring kring : Main.getKringen()) {
            final XYChart.Data<String, Number> data = new XYChart.Data(kring.getNaam(), kring.getAantal());
            kring.setDataRef(data);
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

        answer.add(series);
        return answer;
    }
}
