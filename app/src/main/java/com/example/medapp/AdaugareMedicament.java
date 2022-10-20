package com.example.medapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Locale;

public class AdaugareMedicament extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


     String[] items={"","white","red","orange","yellow","purple","blue","green","pink"};
     String[] items2={"","alarm","notification"};
     String[] items3={"","once a day","twice a day","once every two days", "every x h"};
    private Spinner spinner;
    private Spinner spinner2;
    private Spinner spinner3;
     ArrayAdapter<String> adapter;
     ArrayAdapter<String> adapter2;
     ArrayAdapter<String> adapter3;
    private Button timeButton;
    private Button dateButton;
    private DatePickerDialog.OnDateSetListener setListener;
    private DatePickerDialog.OnDateSetListener setListener2;
    private Button dataf;
    private EditText numeET;
    private EditText compartimentET;
    private DatabaseReference dbRef;
    LinearLayout xh;
    EditText x;
    TextView compartimentTxt;
    boolean getX=false;
    String uid;
    int nr_pastile;
    int ora, minut;
    Medicamente medicament=new Medicamente();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adaugare_medicament);

            String s=getIntent().getStringExtra("update");
            uid= FirebaseAuth.getInstance().getUid();
            numeET=findViewById(R.id.Nume_medicament);
            compartimentET=findViewById(R.id.Numar_compartiment);
        Button salvare = findViewById(R.id.salvare);
            dbRef= FirebaseDatabase.getInstance().getReference();
        Button anulare = findViewById(R.id.anulare);
            xh=findViewById(R.id.text_x);
            x=findViewById(R.id.val_x);
            compartimentTxt=findViewById(R.id.compartiment_txt);

            timeButton=findViewById(R.id.ora_referinta);

            if(GestionareCutie.getCutie().equals("")){

                compartimentET.setVisibility(View.GONE);
                compartimentTxt.setVisibility(View.GONE);
            }


            dateButton=findViewById(R.id.data);
            Calendar c= Calendar.getInstance();
            final int an=c.get(Calendar.YEAR);
            final int luna=c.get(Calendar.MONTH);
            final int zi=c.get(Calendar.DAY_OF_MONTH);
            dateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatePickerDialog datePickerDialog=new DatePickerDialog(AdaugareMedicament.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,setListener,an,luna,zi);
                    datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));
                    datePickerDialog.show();
                }
            });
            dataf=findViewById(R.id.dataf);
            dataf.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatePickerDialog datePickerDialog=new DatePickerDialog(AdaugareMedicament.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,setListener2,an,luna,zi);
                    datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));
                    datePickerDialog.show();
                }
            });
            setListener=new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                    i1=i1+1;
                    String data=String.format(Locale.getDefault(),"%02d-%02d-%04d",i2,i1,i);
                    dateButton.setText(data);
                    medicament.setData_referinta(data);

                }
            };
        setListener2=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                i1=i1+1;
                String data=String.format(Locale.getDefault(),"%02d-%02d-%04d",i2,i1,i);
                dataf.setText(data);
                medicament.setData_final(data);
            }
        };
            spinner=findViewById(R.id.dropdown);
            adapter=new ArrayAdapter<String>(this,R.layout.item_dropdown,items);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(this);

            spinner2=findViewById(R.id.dropdown2);
            adapter2=new ArrayAdapter<String>(this,R.layout.item_dropdown,items2);
            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner2.setAdapter(adapter2);
            spinner2.setOnItemSelectedListener(this);

            spinner3=findViewById(R.id.dropdown3);
            adapter3=new ArrayAdapter<String>(this,R.layout.item_dropdown,items3);
            adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner3.setAdapter(adapter3);
            spinner3.setOnItemSelectedListener(this);

            salvare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(s!=null&&s.equals("da"))
                    {
                        String n=getIntent().getStringExtra("nume");
                        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("medicament").child(uid).child(n);
                        database.removeValue();
                    }
                    String numeMedicament1 = numeET.getText().toString();
                    String upperString = numeMedicament1.substring(0, 1).toUpperCase() + numeMedicament1.substring(1).toLowerCase();
                    medicament.setNume(upperString);
                    medicament.setNr_pastile(0);

                    if(getX){
                        String x1=x.getText().toString();
                        medicament.setInterv(Integer.parseInt(x1));
                    }

                    if(!GestionareCutie.getCutie().equals("")){
                        String nrCompartiment1=compartimentET.getText().toString();
                        medicament.setCompartiment(Integer.parseInt(nrCompartiment1));
                    }
                    else{
                        medicament.setCompartiment(0);
                    }

                    //verificare daca toate campurile sunt completate
                    if (!medicament.verificare()){
                        Toast.makeText(AdaugareMedicament.this,"All the fields should be completed", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Log.d("nr pastile !!!!!",""+medicament.getNr_pastile());
                        medicament.setNr_pastile(nr_pastile);
                        dbRef.child("medicament").child(uid).child(medicament.getNume()).setValue(medicament).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {
                                    ProgramFragment.setariAlarme.stergeIstoricAzi(medicament);
                                    if(!GestionareCutie.getCutie().equals("")){
                                        ProgramFragment.setariAlarme.trimiteMedUrmCutie(uid);
                                    }
                                    Toast.makeText(AdaugareMedicament.this, "Saved", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                                else
                                {
                                    Toast.makeText(AdaugareMedicament.this, "The medicine could not be saved", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                }
            });

        anulare =findViewById(R.id.anulare);
        anulare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if(s!=null&&s.equals("da"))
        {
            Log.d("Update:","confirmare update");
            update();
        }
        }
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String text=adapterView.getItemAtPosition(i).toString();
        if(adapterView.equals(spinner))
            medicament.setCuloare(text);
        if(adapterView.equals(spinner2))
        {
            medicament.setTip_alarma(text);
        }
        if(adapterView.equals(spinner3)){

            medicament.setInterv(text);
            if(text.equals("la x ore")){
                xh.setVisibility(View.VISIBLE);
                getX=true;
            }
            else{
                xh.setVisibility(View.GONE);
                getX=false;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void popTimePicker(View v) {
        TimePickerDialog.OnTimeSetListener onTimeSetListener=new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                ora=i;
                minut=i1;
                String oraStr=String.format(Locale.getDefault(),"%02d:%02d",ora,minut);
                timeButton.setText(oraStr);
                medicament.setOra_referinta(oraStr);
                medicament.setOra(oraStr);
            }
        };
        TimePickerDialog timePickerDialog =new TimePickerDialog(this,onTimeSetListener,ora,minut,true);
        timePickerDialog.setTitle("Select time");
        timePickerDialog.show();
    }
    public void update(){

        String nume=getIntent().getStringExtra("nume");
        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("medicament").child(uid);
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    medicament= snapshot.child(nume).getValue(Medicamente.class);
                    if(medicament!=null){
                    Log.d("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!nr_pastile",""+medicament.getNr_pastile());
                    numeET.setText(medicament.getNume());
                    nr_pastile=medicament.getNr_pastile();
                    int spinnerPosition = adapter.getPosition(medicament.getCuloare());
                    spinner.setSelection(spinnerPosition);
                    spinnerPosition = adapter2.getPosition(medicament.getTip_alarma());
                    spinner2.setSelection(spinnerPosition);
                    spinnerPosition = adapter3.getPosition(medicament.getIntervalStringUpd());
                    spinner3.setSelection(spinnerPosition);
                    if(medicament.getIntervalStringUpd().equals("every x h"))
                        x.setText(medicament.getInterval());
                    timeButton.setText(medicament.getOra_referinta());
                    dateButton.setText(medicament.getData_referinta());
                    if(medicament.getData_final()!=null&&!medicament.getData_final().equals(""))
                        dataf.setText(medicament.getData_final());
                    if(!GestionareCutie.getCutie().equals("")){
                        compartimentET.setText(String.valueOf(medicament.getCompartiment()));
                    }
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}