package com.example.medapp;

public class Cutie {

    private String uid_user;
    private String ora_urm;
    private String nume_medicament;
    private int luat;
    private int eliberat;
    private int eroareEliberarePastila;
    private String poza;

    public Cutie(String uid_user, String ora_urm, String nume_medicament, int luat,int eliberat, int eroareEliberarePastila,String poza) {
        this.uid_user = uid_user;
        this.ora_urm = ora_urm;
        this.nume_medicament = nume_medicament;
        this.luat = luat;
        this.poza=poza;
    }

    public String getUid_user() {
        return uid_user;
    }

    public String getOra_urm() {
        return ora_urm;
    }

    public String getNume_medicament() {
        return nume_medicament;
    }

    public String getPoza() {
        return poza;
    }

    public int getLuat() {
        return luat;
    }

    public int getEliberat() {
        return eliberat;
    }
    public int getEroareEliberarePastila(){
        return eroareEliberarePastila;
    }
}
