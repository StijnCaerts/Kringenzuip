package main;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Optional;

public class Controller {

    @FXML private GridPane gridPane;
    @FXML
    private Pane pane;
    @FXML
    private MenuItem exportCSV;
    private int row = 0;

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
                writer.println("kring,kleur,aantal");
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

    private void kringToevoegen(String naam, Color kleur) {
        // Maak een nieuwe Kring aan
        Kring kring = new Kring(naam, kleur);
        Main.getKringen().add(kring);

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

        Button buttonMin = new Button("-");
        buttonMin.setOnAction((ActionEvent e) -> {
            kring.verlaagAantal();
            labelAantal.setText(Integer.toString(kring.getAantal()));
        });

        gridPane.add(labelNaam,0, row);
        gridPane.add(buttonMin, 1, row);
        gridPane.add(buttonPlus, 2, row);
        gridPane.add(labelAantal, 3, row);

        row++;
    }
}
