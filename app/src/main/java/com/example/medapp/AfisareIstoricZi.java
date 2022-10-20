package com.example.medapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AfisareIstoricZi extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseReference database;
    MedicamenteIstoricZiAdapter medicamenteIstoricZiAdapter;
    ArrayList<Istoric> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_afisare_istoric_zi);


        ProgramFragment.ok=4;
        Button back = findViewById(R.id.back);
        TextView date = findViewById(R.id.print_date);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(AfisareIstoricZi.this,PaginaPrincipalaUtilizator.class);
                startActivity(i);
            }
        });

        Intent intent=getIntent();
        String data=intent.getStringExtra("date");
        date.setText(data);

        recyclerView = findViewById(R.id.programAzi);
        String uid= FirebaseAuth.getInstance().getUid();
        database = FirebaseDatabase.getInstance().getReference().child("istoric").child(uid).child(data);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<Istoric>();
        medicamenteIstoricZiAdapter = new MedicamenteIstoricZiAdapter(this, list);
        recyclerView.setAdapter(medicamenteIstoricZiAdapter);
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    for (DataSnapshot dataSnapshot2 : dataSnapshot.getChildren()){
                        Istoric m = dataSnapshot2.getValue(Istoric.class);
                        list.add(m);}
                }
                medicamenteIstoricZiAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}