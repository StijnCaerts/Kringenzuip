package main;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.Optional;

public class Controller {

    @FXML private GridPane gridPane;

    @FXML protected void handleKringToevoegen(ActionEvent event) {
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
        Node loginButton = dialog.getDialogPane().lookupButton(addButtonType);
        loginButton.setDisable(true);

        // Do some validation (using the Java 8 lambda syntax).
        kring.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty());
        });

        dialog.getDialogPane().setContent(grid);

        // Request focus on the kring field by default.
        Platform.runLater(() -> kring.requestFocus());

        // Convert the result to a kring-kleur-pair when the button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                return new Pair<>(kring.getText(), colorPicker.getValue());
            }
            return null;
        });

        Optional<Pair<String, Color>> result = dialog.showAndWait();
        result.ifPresent(kringKleur -> {
            kringToevoegen(kringKleur.getKey(), kringKleur.getValue());
        });

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

    int row = 0;
}