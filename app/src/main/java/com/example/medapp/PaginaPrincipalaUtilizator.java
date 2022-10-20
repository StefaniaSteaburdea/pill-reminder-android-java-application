package com.example.medapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class PaginaPrincipalaUtilizator extends AppCompatActivity {

    //intent get tip_fragment

    @NonNull
    BottomNavigationView bottomNavigationView;
    ProgramFragment pf=new ProgramFragment();
    MedicatieFragment mf=new MedicatieFragment();
    SetariFragment sf=new SetariFragment();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagina_principala_utilizator);

        bottomNavigationView=findViewById(R.id.bottomNavigationView);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragm,pf).commit();

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.program:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragm,pf).commit();
                    return true;
                case R.id.medicatie:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragm,mf).commit();
                    return true;
                case R.id.setari:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragm,sf).commit();
                    return true;
            }
            return false;}
        });

}
    @Override
    protected void onResume() {
        super.onResume();
        ProgramFragment.ok=0;
    }

    @Override
    protected void onPause() {
        super.onPause();
        ProgramFragment.ok=5;
    }

}