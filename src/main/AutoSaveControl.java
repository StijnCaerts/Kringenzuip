package main;

import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import static java.util.concurrent.TimeUnit.MINUTES;

/**
 * Created by Stijn on 13/08/2016.
 */
class AutoSaveControl {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture saveHandle;

    AutoSaveControl() {
        ScheduledThreadPoolExecutor executor = (ScheduledThreadPoolExecutor) scheduler;
        executor.setRemoveOnCancelPolicy(true);
    }

    public void enable() {
        if (saveHandle == null) {
            final Runnable save = new Runnable() {
                @Override
                public void run() {
                    if (Main.getKringen().size() > 0) {
                        // perform save
                        PrintWriter writer;
                        String outputPath = "./KringenzuipAutoSave.txt";
                        try {
                            writer = new PrintWriter(outputPath, "UTF-8");
                            writer.println("kring\tkleur\taantal");
                            for (Kring kring : Main.getKringen()) {
                                writer.println(kring.toString());
                            }
                            writer.close();
                        } catch (FileNotFoundException e) {
                            try {
                                File file = new File(outputPath);
                                file.createNewFile();
                                writer = new PrintWriter(file, "UTF-8");
                                writer.println("kring\tkleur\taantal");
                                for (Kring kring : Main.getKringen()) {
                                    writer.println(kring.toString());
                                }
                                writer.close();
                            } catch (IOException eIO) {
                                // disable autosaving and display error message
                                // TODO uncheck in GUI

                                disable();

                                // display error message
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                // Get the stage
                                Stage stageAlert = (Stage) alert.getDialogPane().getScene().getWindow();
                                // Add a custom icon
                                stageAlert.getIcons().add(new Image(this.getClass().getResource("/media/ic_file_format_csv_close.png").toString()));
                                alert.setGraphic(new ImageView(new Image(this.getClass().getResource("/media/ic_file_format_csv_close.png").toString())));
                                alert.setTitle("AutoSave mislukt");
                                alert.setHeaderText("Problemen bij AutoSave");
                                alert.setContentText("Het automatisch exporteren naar een CSV-bestand is mislukt en AutoSave werd uitgeschakeld.\nJe kan een andere locatie instellen en AutoSave terug activeren.");
                                alert.show();
                            }
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            saveHandle = scheduler.scheduleAtFixedRate(save, 15, 15, MINUTES);
        } else {
            System.err.println("AutoSave enable called while it is already active.");
        }
    }

    public void disable() {
        if (!(saveHandle == null)) {
            saveHandle.cancel(false);
            saveHandle = null;
        } else {
            System.err.println("AutoSave disable called while it is already deactivated.");
        }
    }

}
