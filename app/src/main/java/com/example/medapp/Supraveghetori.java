package com.example.medapp;

public class Supraveghetori {
    private String nume;
    private String telefon;

    public Supraveghetori(String nume, String telefon) {
        this.nume = nume;
        this.telefon = telefon;
    }

    public Supraveghetori(){}

    public String getNume() {
        return nume;
    }

    public String getTelefon() {
        if(telefon.equals(""))
            return " - ";
        return telefon;
    }


}

