package com.example.medapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AfisareSupraveghetori extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseReference database;
    SupraveghetoriAdapter supraveghetoriAdapter;
    ArrayList<Supraveghetori> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_afisare_supraveghetori);

        ProgramFragment.ok=4;
        recyclerView = findViewById(R.id.afisare_s);
        String uid= FirebaseAuth.getInstance().getUid();
        database = FirebaseDatabase.getInstance().getReference().child("supraveghetori").child(uid);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(AfisareSupraveghetori.this));
        list = new ArrayList<>();
        supraveghetoriAdapter = new SupraveghetoriAdapter(AfisareSupraveghetori.this,list);
        recyclerView.setAdapter(supraveghetoriAdapter);
        database.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                    Supraveghetori s = dataSnapshot.getValue(Supraveghetori.class);
                    list.add(s);
                }
                supraveghetoriAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        Button adauga = findViewById(R.id.buton_adaugare_s);
        adauga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(AfisareSupraveghetori.this,FormularSupraveghetori.class);
                startActivity(i);
                finish();
            }
        });
    }
}