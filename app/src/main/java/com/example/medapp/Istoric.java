package com.example.medapp;

import java.util.Comparator;
import java.util.Locale;

public class Istoric {
    private String data;
    private String ora;
    private String nume;
    private boolean luat;

    public Istoric(String data, String ora, String nume, boolean luat) {
        this.data = data;
        this.ora = ora;
        this.nume = nume;
        this.luat = luat;
    }
   public Istoric(){}


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

    public String getData() {
        return data;
    }

    public String getOra() {
        return ora;
    }

    public String getNume() {
        return nume;
    }

    public boolean isLuat() {
        return luat;
    }

    public static Comparator<Istoric> ordonare = new Comparator<Istoric>() {

        public int compare(Istoric o1, Istoric o2) {
            return o1.getOra().compareTo(o2.getOra());
        }};

    public boolean equals(Object o){
        if (!(o instanceof Istoric))
            return false;
        return this.getOra().equals(((Istoric) o).getOra())&&this.getNume().equals(((Istoric) o).getNume())&&this.getData().equals(((Istoric) o).getData());
    }

    public void setOraaa(int nr_min) {
        int h=nr_min/60;
        int min=nr_min%60;
        this.ora=String.format(Locale.getDefault(),"%02d:%02d",h,min);
    }

}
