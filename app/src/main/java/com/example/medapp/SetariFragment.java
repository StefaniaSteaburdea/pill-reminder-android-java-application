package com.example.medapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SetariFragment extends Fragment {

    Handler mHandler = new Handler();
    LinearLayout conexiuneBratara;
    LinearLayout conexiuneCutie;
    LinearLayout supraveghetor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_setari,container,false);

        ProgramFragment.ok=2;

        conexiuneBratara=v.findViewById(R.id.l1);
        conexiuneBratara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getActivity(),BluetoothConnection.class);
                startActivity(i);
            }
        });

        conexiuneCutie=v.findViewById(R.id.l2);
        conexiuneCutie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getActivity(),GestionareCutie.class);
                startActivity(i);
            }
        });

        supraveghetor=v.findViewById(R.id.l_supraveghetor);
        supraveghetor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getActivity(),AfisareSupraveghetori.class);
                startActivity(i);
            }
        });
        mToastRunnable.run();
        return v;
    }
    private Runnable mToastRunnable = new Runnable() {
        @Override
        public void run() {
            if(ProgramFragment.ok==2){
                //daca porneste alarma ma intorc la fragmentul 1 ->pun in runnable
                if(!AlarmeReceiver.oprire)
                {
                    Intent i=new Intent(getActivity(),PaginaPrincipalaUtilizator.class);
                    startActivity(i);
                }
                mHandler.postDelayed(this, 10000);
            }

        }
    };
}
