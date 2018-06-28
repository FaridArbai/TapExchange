package com.faridarbai.tapexchange.graphical;

import android.Manifest;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.nfc.tech.Ndef;
import android.os.Build;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.faridarbai.tapexchange.MainActivity;
import com.faridarbai.tapexchange.R;
import com.faridarbai.tapexchange.networking.BluetoothClient;
import com.faridarbai.tapexchange.networking.BluetoothServer;
import com.faridarbai.tapexchange.networking.BluetoothTask;
import com.faridarbai.tapexchange.networking.ServerDescriptor;
import com.faridarbai.tapexchange.serialization.PersonData;
import com.faridarbai.tapexchange.serialization.ProtocolMessage;
import com.faridarbai.tapexchange.users.Person;

import java.util.UUID;

public class MeetingActivity extends AppCompatActivity implements NfcAdapter.CreateNdefMessageCallback {
	private static final String TAG = "MeetingActivity";
	public static final int REQUEST_CODE = 1012;
	private static final int PERMISSIONS_REQUEST_CODE = 1013;
	private static final int DISCOVERABLE_REQUEST_CODE = 1014;
	private static final String[] RUNTIME_PERMISSIONS = new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
																							Manifest.permission.ACCESS_COARSE_LOCATION};
	private BluetoothServer server;
	private byte[] user_payload;
	
	NfcAdapter nfc_adapter;
	PendingIntent pending_nfc_intent;
	IntentFilter[] intent_filters_array;
	String[][] tech_lists_array;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.meeting_activity);
		
		Log.d(TAG, "onCreate: CREATED");
		
		this.getUserPayloadFromIntent();
		this.initNFC();
	}
	
	private void initNFC(){
		this.nfc_adapter = NfcAdapter.getDefaultAdapter(this);
		this.nfc_adapter.setNdefPushMessageCallback(this, this);
		
		this.pending_nfc_intent = PendingIntent.getActivity(
    this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
	
		IntentFilter ndef_filter = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
		
		try{
			ndef_filter.addDataType("*/*");
		}
		catch(IntentFilter.MalformedMimeTypeException ex){
			ex.printStackTrace();
		}
		
		this.intent_filters_array = new IntentFilter[]{ndef_filter};
		this.tech_lists_array = new String[][]{new String[]{Ndef.class.getName()}};
	}
	
	@Override
	public NdefMessage createNdefMessage(NfcEvent event){
		int payload_length = this.user_payload.length;
		ServerDescriptor secret_descriptor = new ServerDescriptor(payload_length);
		byte[] payload = secret_descriptor.toByteArray();
		
		String domain = "com.faridarbai";
		String type = "faridtype";
		
		NdefRecord record = NdefRecord.createExternal(domain, type, payload);
		NdefMessage message = new NdefMessage(record);
		
		System.out.printf("Ndef creation method was called\n");
		
		String secret_uuid = secret_descriptor.getSecretUUID();
		this.server = new BluetoothServer(secret_uuid, this.user_payload, this);
		requestDiscoverability();
		this.server.start();
		
		return message;
	}
	
	@Override
	public void onNewIntent(Intent intent){
		setIntent(intent);
		String action = intent.getAction();
		String type = intent.getType();
		String pack = intent.getPackage();
		
		String log_str = String.format("RECEIVED INTENT\nACTION: %s\nTYPE: %s\nPACK: %s", action, type, pack);
		Log.d(TAG, log_str);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		this.nfc_adapter.disableForegroundDispatch(this);
	}
	
	@Override
	public void onResume(){
		super.onResume();
		this.nfc_adapter.enableForegroundDispatch(this, this.pending_nfc_intent, this.intent_filters_array,this.tech_lists_array);
		
		String received_data_action = NfcAdapter.ACTION_TECH_DISCOVERED;
		Intent last_intent = getIntent();
		String last_intent_action;
		boolean actions_match;
		
		if(last_intent!=null) {
			last_intent_action = last_intent.getAction();
			if(last_intent_action!=null) {
				actions_match = last_intent_action.equals(received_data_action);
				
				if (actions_match) {
					this.handleReceivedNdef(last_intent);
					setIntent(null);
				}
			}
		}
	}
	
	public void handleReceivedNdef(Intent intent){
		Parcelable[] raw_messages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
		NdefMessage ndef_message = (NdefMessage)raw_messages[0];
		byte[] payload = ndef_message.getRecords()[0].getPayload();
		
		ServerDescriptor secret_descriptor = ServerDescriptor.fromByteArray(payload, this);
		
		BluetoothClient client = new BluetoothClient(secret_descriptor, this);
		client.start();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	protected void getUserPayloadFromIntent(){
		//Intent intent = getIntent();
		//PersonData user_data = (PersonData)intent.getSerializableExtra("PersonData");
		//Person user = new Person(user_data,this);
		
		Person user = MainActivity.getUser();
		ProtocolMessage pm = new ProtocolMessage(user);
		this.user_payload = pm.toByteArray();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public void onSendFinished(){
		Toast.makeText(this,"SEND HAS FINISHED", Toast.LENGTH_SHORT).show();
	}
	
	public void onReceiveFinished(byte[] payload){
		Person user = ProtocolMessage.fromByteArray(payload, this);
		
		String log_str = String.format("Received username {%s}", user.getName());
		Toast.makeText(this, log_str, Toast.LENGTH_SHORT).show();
	}
	
	private void requestDiscoverability(){
		Intent discoverableIntent =
        new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		
		discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, BluetoothServer.DISCOVERABLE_DURATION);
		startActivityForResult(discoverableIntent, MeetingActivity.DISCOVERABLE_REQUEST_CODE);
	}
	
	
	private void requestBluetoothPermissions(){
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
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		switch(requestCode){
			case(PERMISSIONS_REQUEST_CODE):{
				
				break;
			}
			case(DISCOVERABLE_REQUEST_CODE):{
				break;
			}
			default:{
				
				break;
			}
		}
		
	}
}