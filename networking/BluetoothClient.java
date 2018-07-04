package com.faridarbai.tapexchange.networking;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

import com.faridarbai.tapexchange.MeetingActivity;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BluetoothClient implements BluetoothTask{
	private static final String TAG = "BluetoothClient";
	public static final BluetoothTask.Type TYPE = BluetoothTask.Type.CLIENT;
	
	private static final int MAX_CONNECTION_RETRIES = 3;
	private static final int RETRY_IDLE_TIME = 1000;
	
	private static final int DISCOVERY_TIMEOUT = 20000;
	
	
	
	private final BluetoothAdapter ADAPTER = BluetoothAdapter.getDefaultAdapter();
	private final Lock DISCOVERY_LOCK = new ReentrantLock();
	private final Condition DISCOVERY_CONDITION = DISCOVERY_LOCK.newCondition();
	
	private UUID secret_uuid;
	private final int PAYLOAD_LENGTH;
	private final String SERVER_NAME;
	private MeetingActivity activity;
	
	private boolean server_found;
	private BluetoothDevice server_device;
	
	
	public BluetoothClient(ServerDescriptor server_descriptor, MeetingActivity activity){
		String secret_uuid_str = server_descriptor.getSecretUUID();
		this.secret_uuid = UUID.fromString(secret_uuid_str);
		this.SERVER_NAME = server_descriptor.getName();
		this.PAYLOAD_LENGTH = server_descriptor.getPayloadLength();
		this.activity = activity;
		this.server_found = false;
		
		String log_str = String.format("READY TO READ %d BYTES FROM %s\n[UUID: %s]", this.PAYLOAD_LENGTH, this.SERVER_NAME, secret_uuid_str);
		
		Log.d(TAG, "run : " + log_str);
	}
	
	
	private final BroadcastReceiver discovery_receiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "onReceive: RECEIVED A NEW ACTION");
			BluetoothClient.this.handleBluetoothActions(intent);
		}
	};
	
	private void handleBluetoothActions(Intent intent){
		String action = intent.getAction();
		
		switch(action){
			case (BluetoothDevice.ACTION_FOUND): {
				BluetoothDevice discovered_device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				String device_name = discovered_device.getName();
				String device_mac = discovered_device.getAddress();
				
				if(device_name.equals(SERVER_NAME)){
					this.server_device = discovered_device;
					this.server_found = true;
					this.ADAPTER.cancelDiscovery();
					this.DISCOVERY_LOCK.lock();
					this.DISCOVERY_CONDITION.signalAll();
					Log.d(TAG, "handleBluetoothActions: DISCOVERER SIGNALS THREAD");
					this.DISCOVERY_LOCK.unlock();
				}
				
				String log_str = String.format("DISCOVERED NEW DEVICE : %s:%s", device_name, device_mac);
				Toast.makeText(activity, log_str, Toast.LENGTH_SHORT).show();
				Log.d(BluetoothClient.this.TAG, "handleBluetoothActions : " + log_str);
				
				break;
			}
			case (BluetoothAdapter.ACTION_DISCOVERY_STARTED):{
				String log_str = String.format("DISCOVERY STARTED");
				
				Toast.makeText(activity, log_str, Toast.LENGTH_SHORT).show();
				Log.d(BluetoothClient.this.TAG, "onReceive: " + log_str);
				
				break;
			}
			case (BluetoothAdapter.ACTION_DISCOVERY_FINISHED):{
				String log_str = String.format("DISCOVERY FINISHED");
				
				Toast.makeText(activity, log_str, Toast.LENGTH_SHORT).show();
				Log.d(BluetoothClient.this.TAG, "onReceive: " + log_str);
				
				if(this.server_found==false) {
					this.ADAPTER.startDiscovery();
				};
				break;
			}
			default:{
				String log_str = String.format("STRANGE STRING : %s", action);
				
				Toast.makeText(activity, log_str, Toast.LENGTH_SHORT).show();
				Log.d(BluetoothClient.this.TAG, "onReceive: " + log_str);
				
				break;
			}
		}
	}
	
	public void start(){
		Thread thread = new Thread(this);
		thread.start();
	}
	
	private void setupDiscoveryReceiver(){
		IntentFilter filter = new IntentFilter();
		
		filter.addAction(BluetoothDevice.ACTION_FOUND);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		
    	this.activity.registerReceiver(this.discovery_receiver, filter);
	}
	
	@Override
	public void run() {
		boolean error = false;
		String error_message = null;
		byte[] payload = new byte[this.PAYLOAD_LENGTH];
		
		setupDiscoveryReceiver();
		this.ADAPTER.startDiscovery();
		
		this.DISCOVERY_LOCK.lock();
		try {
			this.DISCOVERY_CONDITION.await(this.DISCOVERY_TIMEOUT, TimeUnit.MILLISECONDS);
		}catch(InterruptedException ex){
			ex.printStackTrace();
		}
		
		if(!this.server_found){
			error = true;
			error_message = "The contact has either refused discoverability or accepted too late.\n\n" +
					"Please try again.";
		}
		else {
			this.activity.onContactsDeviceFound();
			BluetoothSocket server_socket = null;
			boolean socket_created;
			
			try {
				server_socket = this.server_device.createRfcommSocketToServiceRecord(this.secret_uuid);
				socket_created = true;
			} catch (IOException ex) {
				socket_created = false;
			}
			
			if (!socket_created) {
				error = true;
				error_message = "Couldn't create a bluetooth connection, please try again.\n\n" +
						"If the problem persists check your bluetooth status.";
			}
			else {
				boolean connection_error = false;
				int n_retries = 0;
				boolean max_retries_reached = false;
				
				do {
					connection_error = false;
					try {
						server_socket.connect();
					} catch (IOException ex) {
						connection_error = true;
						n_retries++;
						try {
							Thread.sleep(BluetoothClient.RETRY_IDLE_TIME);
						}catch(InterruptedException ex2){
							ex2.printStackTrace();
						}
					}
					max_retries_reached = (n_retries == BluetoothClient.MAX_CONNECTION_RETRIES);
				} while (connection_error & (!max_retries_reached));
				
				if (max_retries_reached) {
					error = true;
					error_message = "Reached maximum number of connection attempts, please try again.\n\n" +
							"If the problem persists your contact should check his bluetooth status.";
				}
				else{
					InputStream from_server = null;
					boolean stream_created;
					try {
						from_server = server_socket.getInputStream();
						stream_created = true;
					}catch(IOException ex){
						stream_created = false;
						ex.printStackTrace();
					}
					
					if(!stream_created){
						error = true;
						error_message = "Unable to create Bluetooth InputStream, please try again.\n\n" +
								"If the problem persists you should check both your bluetooth status " +
								"and your contact's bluetooth status";
					}
					else {
						int read_bytes = 0;
						int max_bytes = this.PAYLOAD_LENGTH - read_bytes;
						float percentage;
						
						boolean finished_reading = false;
						boolean read_error = false;
						
						while ((!finished_reading) && (!read_error)) {
							try {
								read_bytes += from_server.read(payload, read_bytes, max_bytes);
								max_bytes = this.PAYLOAD_LENGTH - read_bytes;
								
								percentage = (((float) read_bytes) / this.PAYLOAD_LENGTH);
								
								this.activity.setProgressTo(percentage, read_bytes, this.PAYLOAD_LENGTH);
								
								finished_reading = (read_bytes == this.PAYLOAD_LENGTH);
							} catch (IOException ex) {
								read_error = true;
							}
						}
						
						if (read_error) {
							error = true;
							error_message = "Bluetooth connection was closed during the transfer, please try again.\n\n" +
									"If the problem persists, both of you should make sure that bluetooth is running up during " +
									"the transfer.";
						} else {
							error = false;
						}
					}
				}
			}
		}
		
		if(error){
			this.activity.onConnectionError(error_message);
		}
		else{
			activity.onReceiveFinished(payload);
		}
		
		this.DISCOVERY_LOCK.unlock();
		this.activity.unregisterReceiver(this.discovery_receiver);
	}
	
	@Override
	public BluetoothTask.Type getType() {
		return BluetoothServer.TYPE;
	}
}




























