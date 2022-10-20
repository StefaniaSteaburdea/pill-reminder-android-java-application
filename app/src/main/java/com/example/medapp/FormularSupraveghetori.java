package com.example.medapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FormularSupraveghetori extends AppCompatActivity {

    private EditText nume;
    private EditText telefon;
    DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formular_supraveghetori);

        nume=findViewById(R.id.Nume);
        telefon=findViewById(R.id.Telefon);
        Button salvare = findViewById(R.id.salvare);

        salvare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String nume1=nume.getText().toString();
                String telefon1=telefon.getText().toString();
                dbRef= FirebaseDatabase.getInstance().getReference();

                if(nume1.equals("")){
                    Toast.makeText(FormularSupraveghetori.this, "Completati numele",Toast.LENGTH_SHORT).show();
                }
                else
                if(telefon1.equals(""))
                {
                    Toast.makeText(FormularSupraveghetori.this, "Completati numărul de telefon",Toast.LENGTH_SHORT).show();

                }
                else
                {
                    Supraveghetori supraveghetori=new Supraveghetori(nume1, telefon1);
                    dbRef.child("supraveghetori").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(nume1).setValue(supraveghetori).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                Intent i=new Intent(FormularSupraveghetori.this, com.example.medapp.AfisareSupraveghetori.class);
                                startActivity(i);
                                finish();
                            }
                            else
                            {
                                Toast.makeText(FormularSupraveghetori.this, "Eroare adaugare", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }
}