package main;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.chart.XYChart;
import javafx.scene.paint.Color;

/**
 * Created by Stijn on 5/08/2016.
 * Kring object wordt gebruikt om alle informatie van een kring bij te houden, namelijk
 * de naam, gekozen kleur, het aantal consumpties
 */

public class Kring {
    private String naam;
    private SimpleIntegerProperty aantal;
    private Color kleur;

    private XYChart.Data<String, Number> dataRef = null;

    public Kring (String naam, Color kleur) {
        setNaam(naam);
        aantal = new SimpleIntegerProperty(0);

        // update Y-value on graph when aantal is changed
        aantal.addListener((observable, oldValue, newValue) -> {
                    if (null != dataRef) dataRef.setYValue(newValue);
        });

        setKleur(kleur);
    }

    public Kring(String naam, Color kleur, int aantal) {
        setNaam(naam);
        setAantal(aantal);
        setKleur(kleur);
    }

    String getNaam() {
        return naam;
    }

    private void setNaam(String naam) {
        this.naam = naam;
    }

    int getAantal() {
        return aantal.getValue();
    }

    private void setAantal(int aantal) {
        this.aantal.setValue(aantal);
    }

    void verhoogAantal() {
        setAantal(getAantal() + 1);
    }

    void verhoogAantalMetVijf() {
        setAantal(getAantal() + 5);
    }

    void verlaagAantal() {
        if (getAantal() > 0) {
            setAantal(getAantal() - 1);
        } else {
            setAantal(0);
        }
    }

    Color getKleur() {
        return kleur;
    }

    private void setKleur(Color kleur) {
        this.kleur = kleur;
    }

    void setDataRef(XYChart.Data<String, Number> data) {
        dataRef = data;
    }

    @Override
    public String toString() {
        return getNaam() + "\t" + getKleur().toString() + "\t" + Integer.toString(getAantal());
    }
}
