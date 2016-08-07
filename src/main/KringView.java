package main;

import javafx.scene.control.Label;

/**
 * Created by Stijn on 6/08/2016.
 */
public class KringView {
    public KringView (Kring kring, Label labelNaam) {
        setKring(kring);
        setLabelNaam(labelNaam);
    }

    public Kring getKring() {
        return kring;
    }

    public void setKring(Kring kring) {
        this.kring = kring;
    }

    public Label getLabelNaam() {
        return labelNaam;
    }

    public void setLabelNaam(Label labelNaam) {
        this.labelNaam = labelNaam;
    }

    private Kring kring;
    private Label labelNaam;

}
