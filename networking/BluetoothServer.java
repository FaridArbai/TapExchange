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
	public static final int DISCOVERABLE_DURATION = 10;
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
		boolean important_error = false;
		boolean error;
		String error_message = null;
		
		BluetoothServerSocket hosting_socket = null;
		boolean hosting_created;
		
		try {
			hosting_socket = this.ADAPTER.listenUsingRfcommWithServiceRecord(this.secret_uuid.toString(), this.secret_uuid);
			hosting_created = true;
		}catch(IOException ex){
			hosting_created = false;
			ex.printStackTrace();
		}
		
		if(!hosting_created){
			important_error = true;
			error = true;
			error_message = "Bluetooth connection could not be opened\n\n. Please check your bluetooth status.";
		}
		else {
			boolean socket_created;
			BluetoothSocket client_socket = null;
			
			try {
				client_socket = hosting_socket.accept();
				hosting_socket.close();
				socket_created = true;
			}catch(IOException ex){
				socket_created = false;
				ex.printStackTrace();
			}
			
			if(!socket_created){
				error = true;
				error_message = "Bluetooth connection could not be accepted.\n\n" +
						"Please check your bluetooth status.";
			}
			else{
				OutputStream to_client = null;
				boolean stream_created;
				
				try {
					to_client = client_socket.getOutputStream();
					stream_created = true;
				}catch(IOException ex){
					stream_created = false;
					ex.printStackTrace();
				}
				
				if(!stream_created){
					error = true;
					error_message = "Bluetooth output stream could not be created.\n\n" +
						"Please check your bluetooth status.";
				}
				else{
					boolean payload_sent;
					
					try {
						to_client.write(this.payload);
						payload_sent = true;
					}catch(IOException ex){
						payload_sent = false;
						ex.printStackTrace();
					}
					
					if(!payload_sent){
						error = true;
						error_message = "Transfer was interrupted.\n\n" +
								"Please check your bluetooth status.";
					}
				}
			}
		}
		
		if(important_error){
			this.activity.onConnectionError(error_message);
		}
	}
	
	@Override
	public BluetoothTask.Type getType() {
		return BluetoothServer.TYPE;
	}
}
