package com.example.medapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MedicamenteAdapter extends RecyclerView.Adapter<MedicamenteAdapter.MyViewHolder> {

    Context context;
    ArrayList<Medicamente> list;

    public MedicamenteAdapter(Context context, ArrayList<Medicamente> list) {
        this.context = context;
        this.list = list;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.medicamente_afisare,parent,false);
        return  new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Medicamente m = list.get(position);
        holder.name.setText(m.getNume());
        holder.oraRef.setText(m.getOra_referinta());
        holder.interval.setText(m.getIntervalString());
        holder.nrPastile.setText(String.valueOf(m.getNr_pastile()));
        if(GestionareCutie.getCutie().equals("")){
            holder.afisareCompartiment.setVisibility(View.GONE);
            holder.pastileramase.setVisibility(View.GONE);
        }
        else{
            holder.afisareCompartiment.setVisibility(View.VISIBLE);
            holder.compartiment.setText(String.valueOf(m.getCompartiment()));
        }

        String uid= FirebaseAuth.getInstance().getUid();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("medicament").child(uid).child(m.getNume());
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
        holder.delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder=new AlertDialog.Builder(holder.name.getContext());
                builder.setTitle("Are you sure you want to delete the medicine?");
                builder.setMessage("Deleted data cannot be recovered!");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String uid= FirebaseAuth.getInstance().getUid();
                        //delete from realtime db
                        int x=holder.getAdapterPosition();
                        FirebaseDatabase.getInstance().getReference().child("medicament").child(uid).child(list.get(x).getNume()).removeValue();
                        //remove from app
                        list.remove(x);
                        notifyDataSetChanged();
                        Toast.makeText(holder.name.getContext(),"Deleted",Toast.LENGTH_SHORT).show();

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(holder.name.getContext(),"Canceled",Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
            }

        });

        holder.box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context,AdaugareMedicament.class);
                intent.putExtra("nume",m.getNume());
                intent.putExtra("update","da");
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        LinearLayout box;
        TextView nrPastile;
        TextView compartiment;
        LinearLayout afisareCompartiment;
        TextView name;
        TextView oraRef;
        TextView interval;
        ImageButton delBtn;
        ImageView culoare_pastila;
        LinearLayout pastileramase;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.numeM);
            oraRef=itemView.findViewById(R.id.oraRef);
            interval=itemView.findViewById(R.id.Interval);
            delBtn=itemView.findViewById(R.id.delBtn);
            culoare_pastila=itemView.findViewById(R.id.pill_col);
            box= itemView.findViewById(R.id.box);
            nrPastile=itemView.findViewById(R.id.nrPastile);
            compartiment=itemView.findViewById(R.id.compartiment);
            afisareCompartiment=itemView.findViewById(R.id.compartimentLayout);
            pastileramase=itemView.findViewById(R.id.pastileRamase);
        }
    }

}