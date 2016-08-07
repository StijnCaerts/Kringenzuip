package main;

import javafx.scene.paint.Color;

/**
 * Created by Stijn on 5/08/2016.
 */
public class Kring {
    public Kring (String naam, Color kleur) {
        setNaam(naam);
        setAantal(0);
        setKleur(kleur);
    }

    public String getNaam() {
        return naam;
    }

    public void setNaam(String naam) {
        this.naam = naam;
    }

    public int getAantal() {
        return aantal;
    }

    public void verhoogAantal() {
        setAantal(getAantal() + 1);
    }

    public void verlaagAantal() {
        if (getAantal() > 0) {
            setAantal(getAantal() - 1);
        } else {
            setAantal(0);
        }
    }

    private void setAantal(int aantal) {
        this.aantal = aantal;
    }

    public Color getKleur() {
        return kleur;
    }

    public void setKleur(Color kleur) {
        this.kleur = kleur;
    }

    private String naam;
    private int aantal;
    private Color kleur;
}
