package com.faridarbai.tapexchange.networking;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.faridarbai.tapexchange.MeetingActivity;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class BluetoothServer implements BluetoothTask {
	private static final String TAG = "BluetoothServer";
	private static final String SERVICE_NAME = "TapExchange";
	public static final int DISCOVERABLE_DURATION = 300;
	public static final BluetoothTask.Type TYPE = BluetoothTask.Type.SERVER;
	private final BluetoothAdapter ADAPTER = BluetoothAdapter.getDefaultAdapter();
	
	private UUID secret_uuid;
	private MeetingActivity activity;
	private byte[] payload;
	
	public BluetoothServer(String secret_uuid, byte[] payload, MeetingActivity activity){
		this.secret_uuid = UUID.fromString(secret_uuid);
		this.payload = payload;
		this.activity = activity;
	}
	
	public void start(){
		Thread thread = new Thread(this);
		thread.start();
	}
	
	@Override
	public void run(){
		try{
			BluetoothServerSocket hosting_socket =
					this.ADAPTER.listenUsingRfcommWithServiceRecord(BluetoothServer.SERVICE_NAME, this.secret_uuid);
			
			
			Log.d(TAG, "run: SERVER EMPIEZA A ESCUCHAR");
			BluetoothSocket client_socket = hosting_socket.accept();
			Log.d(TAG, "run: SERVER RECIBE CONEXION");
			
			hosting_socket.close();
			
			OutputStream to_client = client_socket.getOutputStream();
			to_client.write(this.payload);
			to_client.close();
			Log.d(TAG, "run: SERVER TERMINA CONEXION");
			
			this.activity.onSendFinished();
			
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}
	
	@Override
	public BluetoothTask.Type getType() {
		return BluetoothServer.TYPE;
	}
}
