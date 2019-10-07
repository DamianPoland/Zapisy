package com.wolfmobileapps.zapisy;

public class Wydarzenie {

    private String tytul;
    private String miejsceICzas;
    private String opis;
    private int cena;

    public Wydarzenie(String tytul, String miejsceICza, String opis, int cena) {
        this.tytul = tytul;
        this.miejsceICzas = miejsceICza;
        this.opis = opis;
        this.cena = cena;
    }

    public Wydarzenie() {
    }

    public String getTytul() {
        return tytul;
    }

    public void setTytul(String tytul) {
        this.tytul = tytul;
    }

    public String getMiejsceICzas() {
        return miejsceICzas;
    }

    public void setMiejsceICzas(String miejsceICzas) {
        this.miejsceICzas = miejsceICzas;
    }

    public String getOpis() {
        return opis;
    }

    public void setOpis(String opis) {
        this.opis = opis;
    }

    public int getCena() {
        return cena;
    }

    public void setCena(int cena) {
        this.cena = cena;
    }
}


