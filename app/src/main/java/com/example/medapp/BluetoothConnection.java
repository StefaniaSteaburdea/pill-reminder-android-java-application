package com.example.medapp;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.UUID;


public class BluetoothConnection extends AppCompatActivity implements AdapterView.OnItemClickListener{

    Button y;
    Button x;
    ArrayList<BluetoothDevice> list = new ArrayList<>();
    BluetoothManager bluetoothManager;
    BluetoothAdapter bluetoothAdapter;
    MyBluetoothAdapter myBluetoothAdapter;
    private static final String TAG = "BluetoothConnection";
    public static int conectBT=0;
    public static String conDev;
    ListView listView;
    public static BluetoothConnectionService bluetoothConnectionService;
    private static final UUID MY_UUID_INSECURE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static BluetoothDevice bluetoothDevice;

    LinearLayout l1;
    LinearLayout l2;
    TextView txt;
    Button stergere;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_connection);

        l1=findViewById(R.id.bt_con1);
        l2=findViewById(R.id.bt_con2);
                if(conectBT==0){
                    l1.setVisibility(View.VISIBLE);
                    l2.setVisibility(View.GONE);
                    //asocierea a doua dispozitive
                    IntentFilter f=new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
                    registerReceiver(receiver2,f);
                    listView= findViewById(R.id.dispozitive);
                    listView.setOnItemClickListener(BluetoothConnection.this);

                    y = findViewById(R.id.buton);
                    y.setOnClickListener(new View.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void onClick(View view) {
                            enableBluetooth();
                            discoverDevices();
                        }
                    });
                    x = findViewById(R.id.buton2);
                    x.setOnClickListener(new View.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void onClick(View view) {
                            String s="1";
                            byte[] bytes = s.getBytes(Charset.defaultCharset());
                            bluetoothConnectionService.write(bytes);
                        }
                    });
                }
                else{
                    l1.setVisibility(View.GONE);
                    l2.setVisibility(View.VISIBLE);
                    txt=findViewById(R.id.txt1);
                    txt.setText(conDev);
                    stergere=findViewById(R.id.stergere);
                    stergere.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            conectBT=0;
                            conDev="";
                            bluetoothConnectionService.cancel();
                            Intent i=new Intent(BluetoothConnection.this,BluetoothConnection.class);
                            startActivity(i);
                            finish();
                        }
                    });
                }

    }
    public void enableBluetooth() {
        bluetoothManager = getSystemService(BluetoothManager.class);
        bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null) {
            System.out.println("Dispozitivul nu are bluetooth");
        } else {
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    Log.d("Activare Bluetooth","deconectat");
                    checkBtPermissions();
                } else {
                    Log.d("Activare Bluetooth","conectat");
                }
            } else {
                Log.d("Activare Bluetooth","conectat");
            }
        }
    }


    //BroadcastReceiver pentru conexiune
    private final BroadcastReceiver receiver2= new BroadcastReceiver() {
        @SuppressLint({"NotifyDataSetChanged", "MissingPermission"})
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
                BluetoothDevice mDevice=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(mDevice.getBondState()==BluetoothDevice.BOND_NONE){
                    Log.d(TAG, "Receiver2: NONE");
                }
                if(mDevice.getBondState()==BluetoothDevice.BOND_BONDING){

                    Log.d(TAG, "Receiver2: BONDING");
                }
                if(mDevice.getBondState()==BluetoothDevice.BOND_BONDED){
                    Log.d(TAG, "Receiver2: BONDED");
                    bluetoothDevice=mDevice;
                }
            }
        }};

    // Crearea unui BroadcastReceiver pt gasirea dispozitivelor Bluetooth
    @SuppressLint("MissingPermission")
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(device!=null&&device.getName()!=null)
                    if(!list.contains(device))
                    {
                        list.add(device);
                        Log.d("Device found","in receiver: " + device.getName());
                        myBluetoothAdapter = new MyBluetoothAdapter(context, R.layout.bluetooth_dev_adapter, list);
                        listView.setAdapter(myBluetoothAdapter);
                    }
            }
        }
    };
    public void discoverDevices() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("Nu sunt permisiunile in manifest");
            checkBtPermissions();
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            checkPermissions();}
        if(bluetoothAdapter.isDiscovering()){
            bluetoothAdapter.cancelDiscovery();
            System.out.println("cancel discovery");
        }

        bluetoothAdapter.startDiscovery();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);
        listView = findViewById(R.id.dispozitive);

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {

            unregisterReceiver(receiver);
            unregisterReceiver(receiver2);
        }catch(Exception e){
           Log.d(TAG,"Exceptie");
        }
    }

    public void checkBtPermissions(){
        int permissionCheck=this.checkSelfPermission("Manifest.permission.BLUETOOTH_SCAN");
        permissionCheck+=this.checkSelfPermission("Manifest..permission.BLUETOOTH_CONNECT");
        if(permissionCheck!=0){
            System.out.println("checkPermissions !!!");
            this.requestPermissions(new String[]{Manifest.permission.BLUETOOTH_SCAN,Manifest.permission.BLUETOOTH_CONNECT},1001);
        }

    }
    public void checkPermissions(){
        int permissionCheck=this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
        permissionCheck+=this.checkSelfPermission("Manifest..permission.ACCESS_COARSE_LOCATION");
        if(permissionCheck!=0){
            System.out.println("checkPermissions !!!");
            this.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION},1001);
        }

    }


    @SuppressLint("MissingPermission")
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        //bluetoothAdapter.cancelDiscovery();
        bluetoothDevice=list.get(i);
        String nume = bluetoothDevice.getName();
        String adresa = bluetoothDevice.getAddress();

        Log.d(TAG, "Se incearca conectarea cu " + nume +"Adr:"+adresa);
        bluetoothDevice.createBond();
        bluetoothConnectionService = new BluetoothConnectionService(BluetoothConnection.this);
        bluetoothConnectionService.startClient(bluetoothDevice,MY_UUID_INSECURE);
        while (conectBT==0){
            Log.d(TAG,"in while");
        }
        if(conectBT==1){
            Intent intent=new Intent(BluetoothConnection.this,BluetoothConnection.class);
            startActivity(intent);
            finish();
        }
        }
}