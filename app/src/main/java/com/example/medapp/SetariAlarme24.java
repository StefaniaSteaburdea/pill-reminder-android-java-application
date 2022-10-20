package com.example.medapp;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SetariAlarme24 {

    private Activity activity;
    private Context context;
    private String uid;


    public SetariAlarme24(Activity activity, Context context, String uid) {
        this.activity = activity;
        this.context = context;
        this.uid = uid;
    }

    public void adaugare_istoric(Medicamente m)
    {

        Log.d("Adaugare istoric:","Apel");
        int interv=m.getInterval();
        String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date()); //data curenta

        if(m.getData_final()!=null&&!m.getData_final().equals("")&&m.getData_final().compareTo(date)<0){ //daca a terminat tratamentul nu mai ia medicamentul
           Log.d("Adaugare istoric:","Finalizat"+m.getNume());
            return;
        }

        try {
            Log.d("Adaugare istoric:","Nefinalizat");
            String dt = m.getData_referinta();  // data la care se ia medicamentul
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Calendar c = Calendar.getInstance();
            c.setTime(sdf.parse(dt));
            dt = sdf.format(c.getTime());  // dt e data med
            if(m.getData()==null&&dt.equals(date)) //daca medicamentul trebuie luat azi prima data
            {
                m.setData(m.getData_referinta());
                m.setOra(m.getOra_referinta());
            }
            if(m.getData()!=null)
            {
                if(interv>24){

                    int ora_r=m.getOraInt();
                    int min_r=m.getMinInt();
                    int nrZile=interv/24;
                    int o=ora_r+interv-nrZile*24; //ora la care iau medicamentul
                    if(o>=24){
                        o=o-24;
                        nrZile=nrZile+1;
                    }
                    c.add(c.DATE, nrZile);  // urmatoarea data lac acre iau medicamentul
                    dt = sdf.format(c.getTime());

                    if(dt.equals(date)){
                        m.setData(dt);
                        String x=String.format(Locale.getDefault(),"%02d:%02d",o,min_r);
                        Istoric i=new Istoric(dt,x,m.getNume(),false);
                        adaugareIstoricBD(i,uid);
                        setareAlarma(m.getNume(),m.getTip_alarma(),i.getOraInt(),i.getMinInt(),m.getNr_pastile());
                        m.setOra(i.getOra());
                    }
                    if(m.getData().equals(date)){
                        String x=String.format(Locale.getDefault(),"%02d:%02d",o,min_r);
                        Istoric i=new Istoric(m.getData(),x,m.getNume(),false);
                        adaugareIstoricBD(i,uid);
                        setareAlarma(m.getNume(),m.getTip_alarma(),i.getOraInt(),i.getMinInt(),m.getNr_pastile());
                        m.setOra(i.getOra());
                    }
                }
                else{

                    Log.d("Adaugare istoric:","Adaugare"+m.getNume()+"max24h");
                    int ora_r=m.getOraInt();
                    int min_r=m.getMinInt();
                    c = Calendar.getInstance();
                    dt = sdf.format(c.getTime());
                    int o=ora_r;
                    while(o<24){
                        Log.d("Adaugare istoric:","Adaugare"+o+":"+min_r);
                        Istoric i=new Istoric(dt,String.format(Locale.getDefault(),"%02d:%02d",o,min_r),m.getNume(),false);
                        adaugareIstoricBD(i,uid);
                        setareAlarma(m.getNume(),m.getTip_alarma(),i.getOraInt(),i.getMinInt(),m.getNr_pastile());
                        o=i.getOraInt()+interv;
                    }
                    o=o-24;
                    m.setOra(String.format(Locale.getDefault(),"%02d:%02d",o,min_r));
                    m.setData(dt);
                }
                modificareMedicamentBD(m,uid);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public void  stergeIstoricAzi(Medicamente m){

            String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date()); //data curenta
            Calendar calend=Calendar.getInstance();
            String hour=calend.get(Calendar.HOUR_OF_DAY)+":"+calend.get(Calendar.MINUTE);
            DatabaseReference dbRef= FirebaseDatabase.getInstance().getReference().child("istoric").child(uid).child(date);
            ValueEventListener listener=new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        for (DataSnapshot dataSnapshot2 : dataSnapshot.getChildren()){
                            Istoric i = dataSnapshot2.getValue(Istoric.class);
                            Log.d("!!!!StergeIstoricAzi:","istoric:"+i.getNume()+i.getData()+i.getOra()+"!!!!!!!!!!!!!!!!!!!");
                            if(m.getNume().equals(i.getNume())&&i.getOra().compareTo(hour)>0){
                                oprireAlarma(i.getOraInt(),i.getMinInt());
                                dbRef.child(i.getOra()+"/"+i.getNume()).removeValue();
                            }
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
            dbRef.addListenerForSingleValueEvent(listener);
        }


    public void modificareMedicamentBD(Medicamente m,String uid){
        DatabaseReference db= FirebaseDatabase.getInstance().getReference().child("medicament");
        Map<String, Object> map=new HashMap<>();
        map.put("ora",m.getOra());
        map.put("data",m.getData());
        db.child(uid).child(m.getNume()).updateChildren(map);
    }

    public void adaugareIstoricBD(Istoric i, String uid){

        DatabaseReference dbRef= FirebaseDatabase.getInstance().getReference().child("istoric").child(uid).child(i.getData()+"/"+i.getOra());
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int min1=0;
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    Istoric istoric=ds.getValue(Istoric.class);
                    if(istoric!=null){
                        int ora_db=istoric.getOraInt()*60+istoric.getMinInt();
                        int ora_add=i.getOraInt()*60+i.getMinInt();
                        if(ora_add<=ora_db&&ora_db<=ora_add+5){
                            min1=ora_add-5+ora_db-ora_add;//se va decala cu minimul posibil astfel incat sa fie 5 min intre ele
                        }
                        if(ora_db<=ora_add&&ora_add<=ora_db+5){
                            min1=ora_add+5+ora_add-ora_db;
                        }
                    }
                }
                if(!dataSnapshot.exists()) {
                    if(min1==0){
                        Log.d("Adaugare istoricDB:","Adaugare in onDC");
                        dbRef.child(i.getNume()).setValue(i);
                    }
                    else{

                        Log.d("Adaugare istoricDB:","Adaugare in onDC decalat");
                        i.setOraaa(min1);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        dbRef.addListenerForSingleValueEvent(eventListener);
    }
    private void setareAlarma(String nume,String tip,int ora,int min,int pastile) {
        Calendar cal = Calendar.getInstance();
        int h=cal.get(Calendar.HOUR_OF_DAY);
        int m= cal.get(Calendar.MINUTE);
        System.out.println("inSetAlarma1:"+tip+" "+ora+":"+min+"!!!!!"+h+":"+m);
        cal.set(cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH),
                ora,
                min,
                0);
        if (cal.getTime().after(Calendar.getInstance().getTime()) && h+m<ora+min+5) {
            System.out.println("inSetAlarma2!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            if(activity!=null){
                AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(context, AlarmeReceiver.class);
                Bundle b = new Bundle();
                b.putString("nume", nume);
                b.putString("tip", tip);
                b.putString("uid",uid);
                b.putString("pastile",pastile+"");
                intent.putExtras(b);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, ora+min, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);

            }
            else
                System.out.println("Activity null");
        }
    }

    public void oprireAlarma(int ora,int min) {
        if(activity!=null){
            AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, AlarmeReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, ora+min, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            if (pendingIntent != null) {

                if(AlarmeReceiver.getRingtone()!=null)
                {
                AlarmeReceiver.getRingtone().stop();
                }
                alarmManager.cancel(pendingIntent);

            }
        }else
            System.out.println("Null activity");
    }

    public void trimiteMedUrmCutie(String uid){
        String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        Calendar cal = Calendar.getInstance();
        int o=cal.get(Calendar.HOUR_OF_DAY);
        int m= cal.get(Calendar.MINUTE);
        DatabaseReference dbRef= FirebaseDatabase.getInstance().getReference().child("istoric").child(uid).child(date);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int br=0;
                for(DataSnapshot snapshot3: snapshot.getChildren())
                for(DataSnapshot snapshot1:snapshot3.getChildren()){
                    Istoric i=snapshot1.getValue(Istoric.class);
                    if(i!=null&&br==0)
                    if(i.getOraInt()*60+i.getMinInt()>o*60+m){
                        trimiteCutie(i.getNume(),i.getOraInt(),i.getMinInt());
                        br=1;
                    }
                }
                if(br==0){
                    trimiteCutie(" ",0,0);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    public void trimiteCutie(String nume, int oraApel,int minApel){
        if(!GestionareCutie.getCutie().equals("")){
            DatabaseReference database = FirebaseDatabase.getInstance().getReference().child(GestionareCutie.getCutie());
            Map<String, Object> map=new HashMap<>();
            map.put("ora_urm",String.format(Locale.getDefault(),"%02d:%02d",oraApel,minApel));
            map.put("nume_medicament",nume);
            map.put("eliberat",0);
            Log.d("eliberat e 0","daaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
            database.updateChildren(map);
        }
    }

}
