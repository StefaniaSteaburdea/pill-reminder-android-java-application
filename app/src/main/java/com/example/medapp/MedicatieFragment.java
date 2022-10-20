package com.example.medapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MedicatieFragment extends Fragment {
    RecyclerView recyclerView;
    DatabaseReference database;
    MedicamenteAdapter medicamentAdapter;
    ArrayList<Medicamente> list;
    Handler mHandler = new Handler();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_medicatie,container,false);


        ProgramFragment.ok=1;
        recyclerView = v.findViewById(R.id.myBoxMedicament);
        list = new ArrayList<>();
        medicamentAdapter = new MedicamenteAdapter(getContext(),list);
        String uid= FirebaseAuth.getInstance().getUid();
        database = FirebaseDatabase.getInstance().getReference().child("medicament").child(uid);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(medicamentAdapter);
        database.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                    Medicamente m = dataSnapshot.getValue(Medicamente.class);
                    add(list,m);

                }
                medicamentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        ImageButton adauga = v.findViewById(R.id.adaugare);
        adauga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getActivity(),AdaugareMedicament.class);
                startActivity(i);

            }
        });
        mToastRunnable.run();
        return v;
    }
    private Runnable mToastRunnable = new Runnable() {
        @Override
        public void run() {
            if(ProgramFragment.ok==1){
                if(!AlarmeReceiver.oprire)
                {
                    Intent i=new Intent(getActivity(),PaginaPrincipalaUtilizator.class);
                    startActivity(i);
                }
                mHandler.postDelayed(this, 10000);
            }

        }
    };

    private void add(ArrayList<Medicamente> l, Medicamente i){
        for (Medicamente med: l){
            if (med.getNume().equals(i.getNume())){
                l.remove(med);
                list.add(i);
                return;
            }
        }
        list.add(i);
    }
}
