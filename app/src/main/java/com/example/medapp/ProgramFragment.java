package com.example.medapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ProgramFragment extends Fragment {

    RecyclerView recyclerView;
    DatabaseReference database;
    MedicamenteAziAdapter medicamenteAziAdapter;
    ArrayList<Istoric> list;
    TextView existaMedicamente;
    Handler mHandler = new Handler();
    public static boolean getBr=false;
    public AlertDialog dialog;
    public static int ok;
    String uid;
    public static int sendMsg=0;
    Button b;
    ImageButton logout;
    public static SetariAlarme24 setariAlarme;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_program,container,false);

        ok=0;
        uid= FirebaseAuth.getInstance().getUid();
        setariAlarme=new SetariAlarme24(getActivity(),getContext(),uid);
        //program azi:
        recyclerView = v.findViewById(R.id.program);
        existaMedicamente=v.findViewById(R.id.textNML);
        String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        database = FirebaseDatabase.getInstance().getReference().child("istoric").child(uid).child(date);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        list = new ArrayList<Istoric>();
        medicamenteAziAdapter = new MedicamenteAziAdapter(getContext(),list);
        recyclerView.setAdapter(medicamenteAziAdapter);
        database.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    Istoric i = dataSnapshot1.getValue(Istoric.class);
                    if(i!=null&&!list.contains(i)){
                        if(i.getOra()!=null)
                         if(i.getOraInt()*60+i.getMinInt()>Calendar.getInstance().get(Calendar.HOUR_OF_DAY)*60+Calendar.getInstance().get(Calendar.MINUTE))
                            list.add(i);
                        Collections.sort(list, Istoric.ordonare);}
                }

                medicamenteAziAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        b=v.findViewById(R.id.teste);
        b.setVisibility(View.GONE);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mesaj_supraveghetor(uid,"da");
            }
        });
        logout=v.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getContext(),MainActivity.class));
            }
        });

        //Calendar:
        CalendarView cv = v.findViewById(R.id.calendar);
        cv.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
              String date=String.format(Locale.getDefault(),"%02d-%02d-%04d",i2,i1+1,i);
              Intent intent=new Intent(getContext(),AfisareIstoricZi.class);
              intent.putExtra("date",date);
              startActivity(intent);
            }
        });


       ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.SEND_SMS}, PackageManager.PERMISSION_GRANTED);


       //istoric update
        mToastRunnable.run();
        //luare pastila
        mToastRunnable1.run();
        mToastRunnable3.run();
        return v;
    }


    //medicament_neluat
    private Runnable mToastRunnable = new Runnable() {
        @Override
        public void run() {
            Calendar calend=Calendar.getInstance();
            int ora_curenta=calend.get(Calendar.HOUR_OF_DAY)*60+ calend.get(Calendar.MINUTE);
            int ora_apel=AlarmeReceiver.getOraApel()*60+AlarmeReceiver.getMinApel();
            if ((ora_curenta==ora_apel+5) && !AlarmeReceiver.luat&&sendMsg==0){
                setariAlarme.oprireAlarma(calend.get(Calendar.HOUR_OF_DAY), calend.get(Calendar.MINUTE)-5);
                if(dialog!=null)
                dialog.dismiss();
                Log.d("dialog","dismiss1");
                String message=AlarmeReceiver.nume+"was not taken at  "+AlarmeReceiver.getOraApel()+":"+AlarmeReceiver.getMinApel();
                String uid= FirebaseAuth.getInstance().getUid();
                mesaj_supraveghetor(uid, message);
                AlarmeReceiver.luat=false;
                if(!GestionareCutie.getCutie().equals(""))
                    setariAlarme.trimiteMedUrmCutie(uid);
                sendMsg=1;
            }
            if(ora_curenta==ora_apel+1&&sendMsg==0&&!GestionareCutie.getCutie().equals("")){
                Log.d("Program fragment","Trimiteeee mesaj suprav. Un min dupa alarma");
                verificarePastile();
                sendMsg=1;
            }

            if(ora_curenta==ora_apel+2&&sendMsg==1&&!GestionareCutie.getCutie().equals("")){
                sendMsg=0;
            }
            if(ora_curenta==ora_apel+6&&sendMsg==1){
                sendMsg=0;
            }
            Log.d("Program fragment",ora_apel+" .........."+ora_curenta);
            mHandler.postDelayed(this, 60000);
            }
        };

    //setare AlarmDialog si oprire alarme
    private Runnable mToastRunnable3 = new Runnable() {
        @Override
        public void run() {

            if(AlarmeReceiver.luat==true&&dialog!=null){
                dialog.dismiss();
            }
            if(!AlarmeReceiver.oprire)
            {
                Log.d("Dialog:","Trebuie deschis pe ecran");
                if(getContext()!=null){
                KeyguardManager myKM = (KeyguardManager) getContext().getSystemService(Context.KEYGUARD_SERVICE);
                if( myKM.inKeyguardRestrictedInputMode()) {
                    Log.d("Screen is locked","da");
                }
                else
                    if(getActivity()!=null&&ok==0){
                        System.out.println("In runnable");
                        AlarmeReceiver.oprire=true;
                        alarmDialog(AlarmeReceiver.getOraApel(),AlarmeReceiver.getMinApel(),AlarmeReceiver.getNume());
                    }
            }}
            Calendar calend=Calendar.getInstance();
            int ora_curenta=calend.get(Calendar.HOUR_OF_DAY)*60+ calend.get(Calendar.MINUTE);
            int ora_apel=AlarmeReceiver.getOraApel()*60+AlarmeReceiver.getMinApel();

            if ((ora_curenta>=ora_apel)&&(ora_curenta<ora_apel+5) && !AlarmeReceiver.luat) {
                if(BluetoothConnection.conectBT==1&&!GestionareCutie.getCutie().equals("")){
                    if(verificare_med_cutie()){
                        getBr=true;
                        String s= BluetoothConnection.bluetoothConnectionService.getBrataraLuat();
                        if(s==null||s.equals(""))
                        Log.d("In program fragment","se preia s null");
                        if(s!=null&&s.equals("1"))
                        {
                            Log.d("In program fragment in if",s);
                            BluetoothConnection.bluetoothConnectionService.setBrataraLuat("0");
                            setariAlarme.oprireAlarma(AlarmeReceiver.getOraApel(),AlarmeReceiver.getMinApel());
                            if(dialog!=null)
                            dialog.dismiss();
                            Log.d("dialog","dismiss3");
                            setTrue(AlarmeReceiver.getOraApel(),AlarmeReceiver.getMinApel(),AlarmeReceiver.getNume());
                            AlarmeReceiver.luat=true;
                            set_luat_cutie();
                            AlarmeReceiver.notificationManager.cancel(100);
                            setariAlarme.trimiteMedUrmCutie(uid);
                            getBr=false;
                        }
                    }
                }
                else
                if(BluetoothConnection.conectBT==1){
                    getBr=true;
                    String s= BluetoothConnection.bluetoothConnectionService.getBrataraLuat();
                    System.out.println("citire bluetooth:"+s);
                    if(s!=null&&s.equals("1"))
                    {
                        BluetoothConnection.bluetoothConnectionService.setBrataraLuat("0");
                        setariAlarme.oprireAlarma(AlarmeReceiver.getOraApel(),AlarmeReceiver.getMinApel());
                        if(dialog!=null)
                        dialog.dismiss();
                        Log.d("dialog","dismiss3");
                        setTrue(AlarmeReceiver.getOraApel(),AlarmeReceiver.getMinApel(),AlarmeReceiver.getNume());
                        AlarmeReceiver.luat=true;
                        AlarmeReceiver.notificationManager.cancel(100);
                        getBr=false;
                    }
                }
                else if(!GestionareCutie.getCutie().equals("")){
                    if(verificare_med_cutie()){
                        setariAlarme.oprireAlarma(AlarmeReceiver.getOraApel(),AlarmeReceiver.getMinApel());
                        setTrue(AlarmeReceiver.getOraApel(),AlarmeReceiver.getMinApel(),AlarmeReceiver.getNume());
                        AlarmeReceiver.luat=true;
                        set_luat_cutie();
                        AlarmeReceiver.notificationManager.cancel(100);
                        if(dialog!=null)
                            dialog.dismiss();
                        setariAlarme.trimiteMedUrmCutie(uid);
                    }
                }
            }
            mHandler.postDelayed(this, 1000);
        }
    };
    public void alarmDialog(int ora,int min, String nume){

        System.out.println("in fct alarmD");
        Button anulare_alarma;
        AlertDialog.Builder dialogBuilder=new AlertDialog.Builder(getContext());
        final View alarmView=getLayoutInflater().inflate(R.layout.oprire_alarma,null);
        anulare_alarma= alarmView.findViewById(R.id.oprireAlarma);
        TextView tv=alarmView.findViewById(R.id.dialogTxt);
        tv.setText("Take the medicine "+AlarmeReceiver.getNume());
        dialogBuilder.setView(alarmView);
        dialog=dialogBuilder.create();
        dialog.show();
        anulare_alarma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setariAlarme.oprireAlarma(ora,min);
                dialog.dismiss();
                setTrue(ora,min,nume);
                AlarmeReceiver.luat=true;
                getBr=false;
                AlarmeReceiver.notificationManager.cancel(100);
                if(!GestionareCutie.getCutie().equals("")){
                    setariAlarme.trimiteMedUrmCutie(uid);
                    Log.d("apel set luat cutie","da");
                    set_luat_cutie();
                }
            }
        });
    }

    private Runnable mToastRunnable1 = new Runnable() {
        @Override
        public void run() {

            String uid= FirebaseAuth.getInstance().getUid();
            if(uid!=null){
                database = FirebaseDatabase.getInstance().getReference().child("medicament").child(uid);
                database.addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                            Medicamente m = dataSnapshot.getValue(Medicamente.class);
                            setariAlarme.adaugare_istoric(m);
                        }
                        medicamenteAziAdapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
                mHandler.postDelayed(this, 10000);
            }
            }

    };
    public void setTrue(int ora, int min, String nume){
        Log.d("SetTrue","da");
        String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        DatabaseReference db= FirebaseDatabase.getInstance().getReference().child("istoric");
        Map<String, Object> map=new HashMap<>();
        map.put("luat",true);
        map.put("ora",String.format(Locale.getDefault(),"%02d:%02d",ora,min));
        map.put("data",date);
        map.put("nume",nume);
        if(uid==null)
            System.out.println("uid null");
        else
            if(nume==null)
                System.out.println("nume null");
            else
        db.child(uid).child(date).child(String.format(Locale.getDefault(),"%02d:%02d",ora,min)).child(nume).updateChildren(map);
    }
    public void trimiteSMS(String telefon, String mesaj){

        SmsManager mySmsManager=SmsManager.getDefault();
        mySmsManager.sendTextMessage(telefon,null, mesaj,null,null);

       }


    public void mesaj_supraveghetor(String uid, String mesaj){
        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("supraveghetori").child(uid);
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                    Log.d("Program fragment","Trimiteeee mesaj suprav. Trimite din mesaj_supraveghetor");
                        Supraveghetori s = dataSnapshot.getValue(Supraveghetori.class);
                        Log.d("ProgramFragment","s-a trimis mesajul catre supraveghetorul "+s.getNume());
                        trimiteSMS(s.getTelefon(),mesaj);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
   long ret=0;
    public boolean verificare_med_cutie(){
        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child(GestionareCutie.getCutie()).child("eliberat");
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue()!=null){
                    ret= (long) snapshot.getValue();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if(ret==1)
            return true;
        else
            return false;
    }
    public static void set_luat_cutie(){
        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child(GestionareCutie.getCutie());
        Map<String, Object> map=new HashMap<>();
        map.put("luat",1);
        Log.d("Luat devine 1","daaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        database.updateChildren(map);
    }


    public void verificarePastile(){

        Log.d("Program fragment","Trimiteeee mesaj suprav. In verificare nr pastile");
        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("medicament").child(uid).child(AlarmeReceiver.getNume()).child("nr_pastile");
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue()!=null){
                    long nrP= (long) snapshot.getValue();
                    if(nrP<=3){

                        Log.d("Program fragment","Trimiteeee mesaj suprav. sunt sub 3 pastile din verificare pastile");
                        mesaj_supraveghetor(uid,"There are "+nrP+" pills left of medicine: "+ AlarmeReceiver.getNume());
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
