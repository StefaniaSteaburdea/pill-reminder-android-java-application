package com.example.medapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.nio.charset.Charset;
import java.util.Calendar;

public class AlarmeReceiver extends BroadcastReceiver {
    private Context c;
    private static Ringtone ringtone;
    public static String nume;
    String tip_alarma;
    private static int oraApel;
    private static int minApel;
    public static boolean oprire=true;
    public static boolean luat=true;
    public static NotificationManagerCompat notificationManager;
    @Override
    public void onReceive(Context context, Intent intent) {
        String uid=intent.getStringExtra("uid");
        c=context;
        nume=intent.getStringExtra("nume");
        System.out.println(nume);
        Calendar calendar=Calendar.getInstance();
        oraApel=calendar.get(Calendar.HOUR_OF_DAY);
        minApel=calendar.get(Calendar.MINUTE);
        tip_alarma=intent.getStringExtra("tip");
        System.out.println("test1"+tip_alarma);
        luat=false;
        if(tip_alarma.equals("alarm")){
            Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            ringtone = RingtoneManager.getRingtone(context, alarmUri);
            ringtone.play();
            System.out.println("test2");
            oprire=false;
        }
        notificare(nume,oraApel,minApel,uid);
        if(BluetoothConnection.conectBT==1){

                String s=nume;
                byte[] bytes = s.getBytes(Charset.defaultCharset());
                BluetoothConnection.bluetoothConnectionService.write(bytes);
        }
        Intent i = new Intent(context, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);

    }

    public static int getOraApel(){
        return oraApel;
    }
    public static int getMinApel(){
        return minApel;
    }
    public static String getNume(){ return nume;}

    public static Ringtone getRingtone() {
        return ringtone;
    }

    public void notificare(String n, int oraApel, int minApel, String uid){

        //creez canalul pt notificari
        createNotificationChannel();

        //creez un intent pentru ca at cand apasa pe notificare sa il duca in aplicatie
        Intent intent = new Intent(c, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(c, 0, intent, 0);

        //creez intent pt buton
        Intent intent2 = new Intent(c,com.example.medapp.NotificariReceiver.class);
        Bundle b = new Bundle();
        b.putString("nume",nume);
        b.putInt("ora", oraApel);
        b.putInt("min", minApel);
        b.putString("uid",uid);
        intent2.putExtras(b);
        Log.d("AlarmeReceiver","in notificari receiver"+nume+"  "+oraApel+"  "+minApel+"  "+uid+"  ");
        PendingIntent actionIntent = PendingIntent.getBroadcast(c, 0, intent2, PendingIntent.FLAG_UPDATE_CURRENT);

        //creare notificare
        NotificationCompat.Builder builder= new NotificationCompat.Builder(c, "canal1")
                .setSmallIcon(R.drawable.lic1)
                .setContentTitle("Pill reminder")
                .setContentText("Take your pill: "+ n)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(contentIntent)
                .addAction(R.mipmap.ic_launcher,"Taken",actionIntent)
                .setTimeoutAfter(1000*60*5);
        notificationManager= NotificationManagerCompat.from(c);
        notificationManager.notify(100, builder.build());
        Toast.makeText(c, "Take your pill: "+n, Toast.LENGTH_LONG).show();
    }
    private void createNotificationChannel(){
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            CharSequence name= "canal";
            String description= "canal pentru notificari medicamente";
            int importance= NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel=new NotificationChannel("canal1", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager=c.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
