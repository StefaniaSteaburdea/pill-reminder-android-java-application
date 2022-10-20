package com.example.medapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class GestionareCutie extends AppCompatActivity {

    private static String cutie="";
    private EditText t1;
    private ImageView imageView;
    ProgressDialog pd;
    public static String getCutie() {
        return cutie;
    }

    public static void setCutie(String cutie) {
        GestionareCutie.cutie = cutie;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestionare_cutie);

        LinearLayout l1 = findViewById(R.id.c1);
        LinearLayout l2 = findViewById(R.id.c2);
        t1=findViewById(R.id.txt1);
        TextView t2 = findViewById(R.id.txt2);
        imageView=findViewById(R.id.imagineDozator);
        Button salvare = findViewById(R.id.salvare);
        Button stergere = findViewById(R.id.stergere);
        String uid= FirebaseAuth.getInstance().getUid();

        if(!getCutie().equals("")){
            l1.setVisibility(View.GONE);
            l2.setVisibility(View.VISIBLE);
            t2.setText(getCutie());
            stergere.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("cutie").removeValue();
                    FirebaseDatabase.getInstance().getReference().child(getCutie()).removeValue();
                    setCutie("");
                    Intent i=new Intent(GestionareCutie.this,GestionareCutie.class);
                    startActivity(i);
                    finish();
                }
            });

            //citire din db pt imagine
           citirePoza(uid);

        }
        else{
            l1.setVisibility(View.VISIBLE);
            l2.setVisibility(View.GONE);

            salvare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //citirea codului din EditText
                    String cod_cutie=t1.getText().toString();

                    if(!cod_cutie.equals("")){
                        setCutie(cod_cutie);
                        //in users adaug campul cutie
                        DatabaseReference db= FirebaseDatabase.getInstance().getReference();
                        Map<String, Object> map=new HashMap<>();
                        map.put("cutie",cod_cutie);
                        db.child("users").child(uid).updateChildren(map);

                        //codul cutiei: adaug datele
                        Cutie c= new Cutie(uid,"","",0,0,0,"");
                        db.child(cod_cutie).setValue(c);
                        ProgramFragment.setariAlarme.trimiteMedUrmCutie(uid);
                        Intent i=new Intent(GestionareCutie.this,GestionareCutie.class);
                        startActivity(i);
                        finish();
                    }
                }
            });
        }


    }
    public void citirePoza(String uid){
        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child(GestionareCutie.getCutie()).child("poza");
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue()!=null){
                    String x;
                    x= (String) snapshot.getValue();
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference(uid+"/"+x);
                    try{
                        pd=new ProgressDialog(GestionareCutie.this);
                        pd.setMessage("Se cauta o imagine ...");
                        pd.setCancelable(false);
                        pd.show();

                        File localFile=File.createTempFile("locFile",".jpg");
                        Log.d("Gestionare cutie", "Creare imagine");
                        storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                if(pd.isShowing()){
                                    pd.dismiss();
                                }

                                Log.d("Gestionare cutie", "Imagine gasita cu succes");
                                Bitmap bitmap=BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                imageView.setVisibility(View.VISIBLE);
                                imageView.setImageBitmap(bitmap);

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                if(pd.isShowing()){
                                    pd.dismiss();
                                }
                            }
                        });
                    }catch (Exception e){
                        Log.d("Gestionare cutie","eroare poza");
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}