package com.wolfmobileapps.zapisy;

public class Wydarzenie {


    public Wydarzenie(String wydarzenieTytul, String wydarzenieData, float wydarzenieCena, String wydarzenieOpis, String wydarzenieRegulamin, float wydarzenieDystans, float wydarzenieUczestnicyIlosc, boolean wydarzenieHistoria) {
        this.wydarzenieTytul = wydarzenieTytul;
        this.wydarzenieData = wydarzenieData;
        this.wydarzenieCena = wydarzenieCena;
        this.wydarzenieOpis = wydarzenieOpis;
        this.wydarzenieRegulamin = wydarzenieRegulamin;
        this.wydarzenieDystans = wydarzenieDystans;
        this.wydarzenieUczestnicyIlosc = wydarzenieUczestnicyIlosc;
        this.wydarzenieHistoria = wydarzenieHistoria;
    }

    public String getWydarzenieTytul() {
        return wydarzenieTytul;
    }

    public void setWydarzenieTytul(String wydarzenieTytul) {
        this.wydarzenieTytul = wydarzenieTytul;
    }

    public String getWydarzenieData() {
        return wydarzenieData;
    }

    public void setWydarzenieData(String wydarzenieData) {
        this.wydarzenieData = wydarzenieData;
    }

    public float getWydarzenieCena() {
        return wydarzenieCena;
    }

    public void setWydarzenieCena(float wydarzenieCena) {
        this.wydarzenieCena = wydarzenieCena;
    }

    public String getWydarzenieOpis() {
        return wydarzenieOpis;
    }

    public void setWydarzenieOpis(String wydarzenieOpis) {
        this.wydarzenieOpis = wydarzenieOpis;
    }

    public String getWydarzenieRegulamin() {
        return wydarzenieRegulamin;
    }

    public void setWydarzenieRegulamin(String wydarzenieRegulamin) {
        this.wydarzenieRegulamin = wydarzenieRegulamin;
    }

    public float getWydarzenieDystans() {
        return wydarzenieDystans;
    }

    public void setWydarzenieDystans(float wydarzenieDystans) {
        this.wydarzenieDystans = wydarzenieDystans;
    }

    public float getWydarzenieUczestnicyIlosc() {
        return wydarzenieUczestnicyIlosc;
    }

    public void setWydarzenieUczestnicyIlosc(float wydarzenieUczestnicyIlosc) {
        this.wydarzenieUczestnicyIlosc = wydarzenieUczestnicyIlosc;
    }

    public boolean getWydarzenieHistoria() {
        return wydarzenieHistoria;
    }

    public void setWydarzenieHistoria(boolean wydarzenieHistoria) {
        this.wydarzenieHistoria = wydarzenieHistoria;
    }

    private String wydarzenieTytul;
    private String wydarzenieData;
    private float wydarzenieCena;
    private String wydarzenieOpis;
    private String wydarzenieRegulamin;
    private float wydarzenieDystans;
    private float wydarzenieUczestnicyIlosc;
    private boolean wydarzenieHistoria;

    public Wydarzenie() {
    }

}


