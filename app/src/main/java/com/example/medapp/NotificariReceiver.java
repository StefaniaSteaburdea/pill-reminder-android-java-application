package com.example.medapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class NotificariReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("NotificariReceiver","in notificari receiver");

        //setare luat true pt med+ora la care a fost trimisa notificarea
        String nume=intent.getStringExtra("nume");
        int ora=intent.getIntExtra("ora",0);
        int min=intent.getIntExtra("min",0);
        String uid=intent.getStringExtra("uid");
        String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());

        Log.d("NotificariReceiver","in notificari receiver"+nume+"  "+ora+"  "+min+"  "+uid+"  "+date);
        DatabaseReference db= FirebaseDatabase.getInstance().getReference().child("istoric");
        Map<String, Object> map=new HashMap<>();
        map.put("luat",true);
        if(uid==null)
            System.out.println("uid null");
        else
        if(nume==null)
            System.out.println("nume null");
        else
        {
            db.child(uid).child(date).child(String.format(Locale.getDefault(),"%02d:%02d",ora,min)).child(nume).updateChildren(map);
            AlarmeReceiver.luat=true;
            AlarmeReceiver.oprire=true;
        }
        AlarmeReceiver.notificationManager.cancel(100);
        if(AlarmeReceiver.getRingtone()!=null)
        AlarmeReceiver.getRingtone().stop();
        if(!GestionareCutie.getCutie().equals("")){
            ProgramFragment.setariAlarme.trimiteMedUrmCutie(uid);
            ProgramFragment.set_luat_cutie();
        }
    }

}
