package com.example.medapp;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.UUID;

public class BluetoothConnectionService {

    private BluetoothDevice mmDevice;
    private static final UUID MY_UUID_INSECURE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private UUID deviceUUID;
    private static final String TAG = "BluetoothConnectionService";
    private final BluetoothAdapter mBluetoothAdapter;
    Context mContext;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    ProgressDialog mProgressDialog;
    public BluetoothConnectionService(Context context) {
        mContext = context;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        start();
    }
    public String getBrataraLuat(){

        Log.d(TAG, "a fost preluat luat"+mConnectedThread.getBrataraLuat());
        return mConnectedThread.getBrataraLuat();
    }
    public void setBrataraLuat(String s){
        mConnectedThread.setBrataraLuat(s);
    }

    public void cancel(){
        mConnectedThread.cancel();
    }

    public void startClient(BluetoothDevice device,UUID uuid){
        Log.d(TAG, "startClient: Started.");

        mProgressDialog = ProgressDialog.show(mContext,"Connecting Bluetooth"
                ,"Vă rugăm așteptați...",true);

        mConnectThread = new ConnectThread(device, uuid);
        mConnectThread.start();
    }

    @SuppressLint("MissingPermission")
    private void connected(BluetoothSocket mmSocket, BluetoothDevice mmDevice) {
        Log.d(TAG, "connected: Starting.");
        BluetoothConnection.conDev=mmDevice.getName();
        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(mmSocket);
        mConnectedThread.start();
    }

    //thread->ruleaza pana cand se realizeaza o conexiune
    private class ConnectThread extends Thread {
        private BluetoothSocket mmSocket;

        public ConnectThread(BluetoothDevice device, UUID uuid) {
            Log.d(TAG, "ConnectThread: started.");
            mmDevice = device;
            deviceUUID = uuid;
        }

        @SuppressLint("MissingPermission")
        public void run(){
            BluetoothSocket tmp = null;
            try {
                Log.d(TAG, "ConnectThread: Trying to create InsecureRfcommSocket using UUID: " +MY_UUID_INSECURE );
                tmp = mmDevice.createRfcommSocketToServiceRecord(deviceUUID);
            } catch (IOException e) {
                Log.e(TAG, "ConnectThread: Could not create InsecureRfcommSocket " + e.getMessage());
            }

            mmSocket = tmp;

            // oprirea cautarii de noi dispozitive deoarece incetineste conexiunea
            mBluetoothAdapter.cancelDiscovery();

            try {
                mmSocket.connect();
                Log.d(TAG, "run: ConnectThread connected.");
                connected(mmSocket,mmDevice);
            } catch (IOException e) {
                Log.d(TAG, "run: exception!!!"+ e);
            }
        }
        public void cancel() {
            try {
                Log.d(TAG, "cancel: Closing Client Socket.");
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "cancel: close() of mmSocket in Connectthread failed. " + e.getMessage());
            }
        }
    }


    public synchronized void start() {
        Log.d(TAG, "start");

        // Cancel any thread attempting to make a connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

    }

    /**
     Pt trimitere si acceptare date
     **/
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private String brataraLuat;
        public ConnectedThread(BluetoothSocket socket) {
            Log.d(TAG, "ConnectedThread: Starting.");

            //dismiss the progressdialog when connection is established
            try{
                mProgressDialog.dismiss();
                BluetoothConnection.conectBT=1;

            }catch (NullPointerException e){
                e.printStackTrace();
            }

            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = mmSocket.getInputStream();
                tmpOut = mmSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public String getBrataraLuat(){

            Log.d(TAG,"in Thread a fost trimis"+brataraLuat);
            return brataraLuat;
        }
        public void setBrataraLuat(String s){
            brataraLuat=s;
        }

        public void run(){
            byte[] buffer = new byte[1024];
            int bytes;
            while (true) {
                // Read from the InputStream
                try {
                    bytes = mmInStream.read(buffer);
                    String incomingMessage = new String(buffer, 0, bytes);
                    Log.d(TAG, "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1InputStream: " + incomingMessage);
                    if(incomingMessage.equals("1")&& ProgramFragment.getBr){
                        brataraLuat=incomingMessage;
                        Log.d(TAG,"A fost ridicata mana!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"+brataraLuat);
                    }
                } catch (IOException e) {
                    BluetoothConnection.conectBT=0;
                    Log.e(TAG, "write: Error reading Input Stream. " + e.getMessage() );
                    break;
                }
            }
        }

        //Call this from the main activity to send data to the remote device
        public void write(byte[] bytes) {
            String text = new String(bytes, Charset.defaultCharset());
            Log.d(TAG, "write: Writing to outputstream: " + text);
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                Log.e(TAG, "write: Error writing to output stream. " + e.getMessage() );
                BluetoothConnection.conectBT=0;
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {

                Log.d(TAG,"Exceptie");
            }
        }
    }

    public void write(byte[] out) {
        // Synchronize a copy of the ConnectedThread
        Log.d(TAG, "write: Write Called.");
        //perform the write
        mConnectedThread.write(out);
    }

}

