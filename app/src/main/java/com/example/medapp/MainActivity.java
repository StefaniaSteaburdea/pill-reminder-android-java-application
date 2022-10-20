package com.example.medapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private EditText adresaMail;
    private EditText parola;
    private FirebaseAuth fauth;

    Button autentificare;
    TextView moveToInregistrare;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        autentificare=findViewById(R.id.autentificare);
        moveToInregistrare=findViewById(R.id.buton_inregistrare);
        adresaMail=findViewById(R.id.Adresa_mail_utilizatori);
        parola=findViewById(R.id.Parola_utilizatori);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            String uid=FirebaseAuth.getInstance().getUid();
            cauta_cutie(uid);
            Intent i = new Intent(MainActivity.this, com.example.medapp.PaginaPrincipalaUtilizator.class);
            startActivity(i);
            finish();
        }

        autentificare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String adresaMail1 = adresaMail.getText().toString();
                String parola1 = parola.getText().toString();
                fauth=FirebaseAuth.getInstance();
                if(adresaMail1.isEmpty()||parola1.isEmpty()){
                    Toast.makeText(MainActivity.this, "Completati ambele campuri", Toast.LENGTH_SHORT).show();
                } else if(!Patterns.EMAIL_ADDRESS.matcher(adresaMail1).matches())
                {
                    Toast.makeText(MainActivity.this,"Adaugati o adresa de mail valida!", Toast.LENGTH_SHORT).show();
                }
                else
                {

                    fauth.signInWithEmailAndPassword(adresaMail1,parola1).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                if(user!=null){
                                    Intent i = new Intent(MainActivity.this, com.example.medapp.PaginaPrincipalaUtilizator.class);
                                    startActivity(i);
                                    finish();
                                }

                            }
                            else
                            {
                                Toast.makeText(MainActivity.this,"Datele nu au fost introduse corect",Toast.LENGTH_SHORT).show();
                            }
                        }

                    });
                }}
        });

        moveToInregistrare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, com.example.medapp.Inregistrare.class);
                startActivity(i);
                finish();
            }
        });

    }
    public void cauta_cutie(String uid){
        System.out.println("cauta cutie");
        DatabaseReference db= FirebaseDatabase.getInstance().getReference().child("users").child(uid);
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String s=(String) snapshot.child("cutie").getValue();
                if(s!=null&&!s.equals("")) {
                    GestionareCutie.setCutie(s);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("MainActivity","Eroare cautare cutie");
            }
        });
    }
}