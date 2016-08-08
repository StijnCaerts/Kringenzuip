package main;

import javafx.scene.paint.Color;

/**
 * Created by Stijn on 5/08/2016.
 * Kring object wordt gebruikt om alle informatie van een kring bij te houden, namelijk
 * de naam, gekozen kleur, het aantal consumpties
 */

public class Kring {
    private String naam;
    private int aantal;
    private Color kleur;

    public Kring (String naam, Color kleur) {
        setNaam(naam);
        setAantal(0);
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
        return aantal;
    }

    private void setAantal(int aantal) {
        this.aantal = aantal;
    }

    void verhoogAantal() {
        setAantal(getAantal() + 1);
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

    @Override
    public String toString() {
        return getNaam() + "\t" + getKleur().toString() + "\t" + Integer.toString(getAantal());
    }
}
