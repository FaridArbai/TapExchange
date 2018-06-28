package com.faridarbai.tapexchange.networking;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

import com.faridarbai.tapexchange.graphical.MeetingActivity;
import com.faridarbai.tapexchange.serialization.ProtocolMessage;
import com.faridarbai.tapexchange.users.Person;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BluetoothClient implements BluetoothTask{
	private static final String TAG = "BluetoothClient";
	public static final BluetoothTask.Type TYPE = BluetoothTask.Type.CLIENT;
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
					this.DISCOVERY_CONDITION.signal();
					this.DISCOVERY_LOCK.unlock();
				}
				
				String log_str = String.format("DISCOVERED NEW DEVICE : %s:%s", device_name, device_mac);
				Toast.makeText(activity, log_str, Toast.LENGTH_SHORT).show();
				Log.d(BluetoothClient.this.TAG, "onReceive: " + log_str);
				
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
		Log.d(TAG, "run: CLIENTE SE EJECUTA");
		setupDiscoveryReceiver();
		this.ADAPTER.startDiscovery();
		
		try{
			this.DISCOVERY_LOCK.lock();
			this.DISCOVERY_CONDITION.await();
			
			BluetoothSocket server_socket = this.server_device.createRfcommSocketToServiceRecord(this.secret_uuid);
			InputStream from_server = server_socket.getInputStream();
			byte[] payload = new byte[this.PAYLOAD_LENGTH];
			int read_bytes = from_server.read(payload);
			
			activity.onReceiveFinished(payload);
			
		}catch(IOException ex){
			ex.printStackTrace();
		}
		catch(InterruptedException ex){
			ex.printStackTrace();
		}
		finally {
			this.DISCOVERY_LOCK.unlock();
			this.activity.unregisterReceiver(this.discovery_receiver);
		}
	}
	
	@Override
	public BluetoothTask.Type getType() {
		return BluetoothServer.TYPE;
	}
}




























