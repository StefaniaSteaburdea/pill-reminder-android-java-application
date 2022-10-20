package com.example.medapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Inregistrare extends AppCompatActivity {
    private Button moveToPacient;
    private EditText numeComplet;
    private EditText telefon;
    private EditText adresaMail;
    private EditText parola;
    private EditText confirmareParola;
    private DatabaseReference dbRef;
    private FirebaseAuth fauth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inregistrare);
        TextView moveToAutentificare = findViewById(R.id.buton_autentificare);
        numeComplet=findViewById(R.id.nume);
        telefon=findViewById(R.id.Telefon);
        adresaMail=findViewById(R.id.Adresa_mail);
        parola=findViewById(R.id.Parola);
        confirmareParola=findViewById(R.id.Confirmare_parola);
        Button creare = findViewById(R.id.creare);
        dbRef= FirebaseDatabase.getInstance().getReference();
        fauth=FirebaseAuth.getInstance();

        moveToAutentificare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(Inregistrare.this, com.example.medapp.MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        creare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String numeComplet1=numeComplet.getText().toString();
                String telefon1=telefon.getText().toString();
                String adresaMail1=adresaMail.getText().toString();
                String parola1=parola.getText().toString();
                String confirmareParola1=confirmareParola.getText().toString();

                if (numeComplet1.equals("") ||  telefon1.equals("")||adresaMail1.equals("")||parola1.equals("")||confirmareParola1.equals("")){
                    Toast.makeText(Inregistrare.this,"Completați toate câmpurile", Toast.LENGTH_SHORT).show();
                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(adresaMail1).matches())
                {
                    Toast.makeText(Inregistrare.this,"Adaugati o adresa de mail valida!", Toast.LENGTH_SHORT).show();
                }
                else if(parola.length()<6){
                    Toast.makeText(Inregistrare.this,"Adaugati o parola de cel putin 6 caractere", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (parola1.equals(confirmareParola1)) {
                        fauth.createUserWithEmailAndPassword(adresaMail1,parola1).
                                addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful())
                                        {
                                            System.out.println(numeComplet1+adresaMail1+telefon1);
                                            try{
                                                Utilizatori user = new Utilizatori(numeComplet1,  adresaMail1, telefon1, "0","");
                                                dbRef.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful())
                                                        {
                                                            Toast.makeText(Inregistrare.this, "Cont creat", Toast.LENGTH_SHORT).show();
                                                            Intent i=new Intent(Inregistrare.this, com.example.medapp.MainActivity.class);
                                                            startActivity(i);
                                                            finish();
                                                        }
                                                        else
                                                        {
                                                            Toast.makeText(Inregistrare.this, "eroare creare cont", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }catch(Exception e){
                                                Toast.makeText(Inregistrare.this, "Eroare creare cont!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                });

                    } else {
                        Toast.makeText(Inregistrare.this, "Introduceți aceeași parolă", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}