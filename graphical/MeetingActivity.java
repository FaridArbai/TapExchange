package com.faridarbai.tapexchange.graphical;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.faridarbai.tapexchange.MainActivity;
import com.faridarbai.tapexchange.R;

public class MeetingActivity extends AppCompatActivity {
	private static final String TAG = "MeetingActivity";
	private static final int REQUEST_CODE = 2101;
	private static final int PERMISSIONS_REQUEST_CODE = 1010;
	private static final String[] RUNTIME_PERMISSIONS = new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
																							Manifest.permission.ACCESS_COARSE_LOCATION};
	
	private BluetoothAdapter bluetooth_adapter;
	private final BroadcastReceiver bluetooth_receiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "onReceive: RECEIVED A NEW ACTION");
			MeetingActivity.this.handleBluetoothActions(intent);
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.meeting_activity);
		
		initBluetooth();
	}
	
	private void initBluetooth(){
		checkRuntimePermissions();
		this.bluetooth_adapter = BluetoothAdapter.getDefaultAdapter();
		
		Intent discoverableIntent =
        new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		
		discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
		startActivity(discoverableIntent);
		
		IntentFilter filter = new IntentFilter();
		
		filter.addAction(BluetoothDevice.ACTION_FOUND);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		
    	registerReceiver(this.bluetooth_receiver, filter);
    	
    	boolean has_started = this.bluetooth_adapter.startDiscovery();
    	Log.d(TAG, "initBluetooth: " + has_started);
	}
	
	
	private void checkRuntimePermissions(){
		if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
			boolean permissions_enabled;
			
			if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
				permissions_enabled = ((this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) &&
				(this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED));
			}
			else{
				permissions_enabled = ((ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) &&
				(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED));
			}
			
			if(!permissions_enabled){
				
				if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
					this.requestPermissions(MeetingActivity.RUNTIME_PERMISSIONS, MeetingActivity.PERMISSIONS_REQUEST_CODE);
				}
				else{
					ActivityCompat.requestPermissions(this, MeetingActivity.RUNTIME_PERMISSIONS, MeetingActivity.PERMISSIONS_REQUEST_CODE);
				}
			}
		}
	}
	
	private void handleBluetoothActions(Intent intent){
		String action = intent.getAction();
		
		switch(action){
			case (BluetoothDevice.ACTION_FOUND): {
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				String deviceName = device.getName();
				String deviceHardwareAddress = device.getAddress();
				
				String log_str = String.format("DISCOVERED NEW DEVICE : %s:%s", deviceName, deviceHardwareAddress);
				
				Toast.makeText(this, log_str, Toast.LENGTH_SHORT).show();
				Log.d(MeetingActivity.this.TAG, "onReceive: " + log_str);
				
				break;
			}
			case (BluetoothAdapter.ACTION_DISCOVERY_STARTED):{
				String log_str = String.format("DISCOVERY STARTED");
				
				Toast.makeText(this, log_str, Toast.LENGTH_SHORT).show();
				Log.d(MeetingActivity.this.TAG, "onReceive: " + log_str);
				
				break;
			}
			case (BluetoothAdapter.ACTION_DISCOVERY_FINISHED):{
				String log_str = String.format("DISCOVERY FINISHED");
				
				Toast.makeText(this, log_str, Toast.LENGTH_SHORT).show();
				Log.d(MeetingActivity.this.TAG, "onReceive: " + log_str);
				this.bluetooth_adapter.startDiscovery();
				break;
			}
			default:{
				String log_str = String.format("STRANGE STRING : %s", action);
				
				Toast.makeText(this, log_str, Toast.LENGTH_SHORT).show();
				Log.d(MeetingActivity.this.TAG, "onReceive: " + log_str);
				
				break;
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(this.bluetooth_receiver);
	}
	
	
}