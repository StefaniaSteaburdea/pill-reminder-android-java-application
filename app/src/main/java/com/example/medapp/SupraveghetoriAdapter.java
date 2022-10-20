package com.example.medapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class SupraveghetoriAdapter extends RecyclerView.Adapter<SupraveghetoriAdapter.MyViewHolder> {

    Context context;
    ArrayList<Supraveghetori> list;

    public SupraveghetoriAdapter(Context context, ArrayList<Supraveghetori> list) {
        this.context = context;
        this.list = list;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.adapter_view_supraveghetori_afisare,parent,false);
        return  new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Supraveghetori s = list.get(position);
        holder.nume.setText(s.getNume());
        holder.telefon.setText(s.getTelefon());
        holder.sterge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder=new AlertDialog.Builder(holder.nume.getContext());
                builder.setTitle("Are you sure you want to delete the supervisor?");
                builder.setMessage("Deleted data cannot be recovered!");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String uid= FirebaseAuth.getInstance().getUid();
                        //delete from realtime db
                        int x=holder.getAdapterPosition();
                        FirebaseDatabase.getInstance().getReference().child("supraveghetori").child(uid).child(list.get(x).getNume()).removeValue();
                        //remove from app
                        list.remove(x);
                        notifyDataSetChanged();
                        Toast.makeText(holder.nume.getContext(),"Deleted",Toast.LENGTH_SHORT).show();

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(holder.nume.getContext(),"Canceled",Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
            }

        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView nume;
        TextView telefon;
        LinearLayout sterge;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nume = itemView.findViewById(R.id.numeS);
            telefon=itemView.findViewById(R.id.tel);
            sterge=itemView.findViewById(R.id.layout);
        }
    }

}