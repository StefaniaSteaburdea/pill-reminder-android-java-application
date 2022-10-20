package com.example.medapp;

import java.util.Objects;

public class Medicamente {
    String nume;
    String culoare;
    String ora_referinta;
    String ora;
    int interval;
    String tip_alarma;
    String data;
    String data_referinta;
    String data_final;
    int nr_pastile;
    int compartiment;

    int getOraInt(){
        String[] time = ora.split ( ":" );
        int hour = Integer.parseInt ( time[0].trim() );
        return hour;
    }
    int getMinInt(){
        String[] time = ora.split ( ":" );
        int hour = Integer.parseInt ( time[1].trim() );
        return hour;
    }
    int getZiInt(){
        String[] date=data.split("-");
        int zi=Integer.parseInt(date[0].trim());
        return zi;
    }
    int getLunaInt(){
        String[] date=data.split("-");
        int luna=Integer.parseInt(date[1].trim());
        return luna;
    }
    int getAnInt(){
        String[] date=data.split("-");
        int an=Integer.parseInt(date[2].trim());
        return an;
    }

    public Medicamente(String nume, String culoare, String ora_referinta, int interval, String tip_alarma, String data_referinta, int nr_pastile, int compartiment, String data_final) {
        this.nume = nume;
        this.culoare = culoare;
        this.ora_referinta = ora_referinta;
        this.interval = interval;
        this.tip_alarma = tip_alarma;
        this.data_referinta = data_referinta;
        this.nr_pastile=nr_pastile;
        this.compartiment=compartiment;
    }
    public Medicamente(){
        this.nume = "";
        this.culoare = "";
        this.ora_referinta = "";
        this.tip_alarma = "";
        this.data_referinta = "";
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public void setCuloare(String culoare) {
        this.culoare = culoare;
    }

    public void setOra_referinta(String ora_referinta) {
        this.ora_referinta = ora_referinta;
    }

    public void setOra(String ora) {
        this.ora = ora;
    }


    public void setData_final(String data_final) {
        this.data_final = data_final;
    }

    public void setInterv(int interval) {
        this.interval = interval;
    }
    public void setInterv(String i) {
        System.out.println(i);
        if(i.equals("once a day"))
            this.interval=24;
        if(i.equals("twice a day"))
            this.interval=12;
        if(i.equals("once every two days"))
            this.interval=48;
    }
    public void setTip_alarma(String tip_alarma) {
        this.tip_alarma = tip_alarma;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setNr_pastile(int nr_pastile) {
        this.nr_pastile = nr_pastile;
    }

    public void setCompartiment(int compartiment) {
        this.compartiment = compartiment;
    }

    public void setData_referinta(String data_referinta) {
        this.data_referinta = data_referinta;
    }

    public String getNume() {
        return nume;
    }
    public int getNr_pastile(){return nr_pastile;}
    public int getCompartiment(){return compartiment;}

    public String getCuloare() {
        return culoare;
    }

    public String getData_final() {
        return data_final;
    }

    public String getOra_referinta() {
        return ora_referinta;
    }

    public String getOra() {
        return ora;
    }
    public int getInterval(){ return interval;}
    public String getIntervalString() {
        return String.valueOf(this.interval);
    }

    public String getTip_alarma() {
        return tip_alarma;
    }

    public String getData() {
        return data;
    }

    public String getData_referinta() {
        return data_referinta;
    }

    public String getIntervalStringUpd(){
        if(interval==24)
            return "once a day";
        else
            if(interval==12)
                return "twice a day";
            else
                if(interval==48)
                    return "once every two days";
                else
                    return "every x h";

    }
    public boolean verificare(){

        if(nume.equals(""))
        {
            System.out.println("nume");
            return false;
        }
        if(culoare.equals("")){

            System.out.println("culoare");
            return false;
        }
        if(ora_referinta.equals("")){

            System.out.println("ora_ref");
            return false;
        }
        if(tip_alarma.equals("")){

            System.out.println("tip");
            return false;
        }
        if(interval==0){

            System.out.println("interv");
            return false;
        }
        if(data_referinta.equals("")) {
            System.out.println("data");
            return false;
        }
        return true;
    }
    public boolean equals(Object o){
        // self check
        if (this == o)
            return true;
        // null check
        if (o == null)
            return false;
        // type check and cast
        if (getClass() != o.getClass())
            return false;
        Medicamente m= (Medicamente) o;
        // field comparison
        return Objects.equals(nume, m.nume)
                && Objects.equals(ora, m.ora);
    }

}
