package com.example.medapp;
public class Utilizatori {
    private String numeComplet;
    private String telefon;
    private String adresaMail;
    private String parola;
    private String tipUtilizator;
    private String uid;
    private String cutie;
    public Utilizatori(){}


    public Utilizatori(String numeComplet, String adresaMail, String telefon, String tipUtilizator, String cutie){
        this.numeComplet=numeComplet;
        this.telefon=telefon;
        this.adresaMail=adresaMail;
        this.tipUtilizator=tipUtilizator;
        this.cutie=cutie;
    }

    public String getNumeComplet() {
        return numeComplet;
    }

    public String getTelefon() {
        return telefon;
    }

    public String getAdresaMail() {
        return adresaMail;
    }

    public String getParola() {
        return parola;
    }

    public String getUid() {
        return uid;
    }
    public String getTipUtilizator() {
        return tipUtilizator;
    }

    public String cutie() {
        return cutie;
    }
}