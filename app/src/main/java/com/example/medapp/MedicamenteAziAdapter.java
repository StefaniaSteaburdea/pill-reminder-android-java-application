package com.example.medapp;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MedicamenteAziAdapter extends RecyclerView.Adapter<MedicamenteAziAdapter.MyViewHolder> {

    Context context;
    ArrayList<Istoric> list;

    public MedicamenteAziAdapter(Context context, ArrayList<Istoric> list) {
        this.context = context;
        this.list = list;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.medicamente_azi_afisare,parent,false);
        return  new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Istoric i = list.get(position);
        holder.name.setText(i.getNume());
        holder.oraRef.setText(i.getOra());
        String uid= FirebaseAuth.getInstance().getUid();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("medicament").child(uid).child(i.getNume());
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               String c= (String) snapshot.child("culoare").getValue();
               System.out.println(c);
               if(c!=null)
               {
                   if(c.equals("alb"))
                       holder.culoare_pastila.setColorFilter(Color.rgb(255,255,255));
                   if(c.equals("rosu"))
                       holder.culoare_pastila.setColorFilter(Color.rgb(255,0,0));
                   if(c.equals("portocaliu"))
                       holder.culoare_pastila.setColorFilter(Color.rgb(234,85,31));
                   if(c.equals("galben"))
                       holder.culoare_pastila.setColorFilter(Color.rgb(245,245,0));
                   if(c.equals("albastru"))
                       holder.culoare_pastila.setColorFilter(Color.rgb(38,7,240));
                   if(c.equals("verde"))
                       holder.culoare_pastila.setColorFilter(Color.rgb(13,212,0));
                   if(c.equals("mov"))
                       holder.culoare_pastila.setColorFilter(Color.rgb(85,15,165));
                   if(c.equals("roz"))
                       holder.culoare_pastila.setColorFilter(Color.rgb(255,0,213));

               }
                }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView name;
        TextView oraRef;
        ImageView culoare_pastila;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.numeM);
            oraRef=itemView.findViewById(R.id.ora);
            culoare_pastila=itemView.findViewById(R.id.pill_color);
        }
    }

}