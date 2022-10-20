package com.example.medapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MedicamenteIstoricZiAdapter extends RecyclerView.Adapter<MedicamenteIstoricZiAdapter.MyViewHolder> {

    Context context;
    ArrayList<Istoric> list;

    public MedicamenteIstoricZiAdapter(Context context, ArrayList<Istoric> list) {
        this.context = context;
        this.list = list;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.medicamente_istoric_afisare,parent,false);
        return  new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Istoric i = list.get(position);
        holder.name.setText(i.getNume());
        holder.oraRef.setText(i.getOra());
        @SuppressLint("SimpleDateFormat")
        String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        if(i.getData()!=null)
        if(i.getData().equals(date)&&i.getOraInt()*60+i.getMinInt()>Calendar.getInstance().get(Calendar.HOUR_OF_DAY)*60+Calendar.getInstance().get(Calendar.MINUTE) ){
            holder.v1.setVisibility(View.INVISIBLE);
            holder.v2.setVisibility(View.INVISIBLE);
        }
        else
        {
            if (!i.isLuat()){
                holder.v2.setVisibility(View.VISIBLE);
                holder.v1.setVisibility(View.INVISIBLE);
            }
            else{
                holder.v1.setVisibility(View.VISIBLE);
                holder.v2.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView name;
        TextView oraRef;
        ImageView v1;
        ImageView v2;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.numeM);
            oraRef=itemView.findViewById(R.id.ora);
            v1=itemView.findViewById(R.id.verif1);
            v2=itemView.findViewById(R.id.verif2);
        }
    }

}