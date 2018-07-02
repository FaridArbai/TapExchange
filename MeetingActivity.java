package com.faridarbai.tapexchange;

import android.Manifest;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
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

import com.faridarbai.tapexchange.networking.BluetoothClient;
import com.faridarbai.tapexchange.networking.BluetoothServer;
import com.faridarbai.tapexchange.networking.ServerDescriptor;
import com.faridarbai.tapexchange.serialization.PersonData;
import com.faridarbai.tapexchange.serialization.ProtocolMessage;
import com.faridarbai.tapexchange.serialization.UserData;
import com.faridarbai.tapexchange.users.Person;
import com.faridarbai.tapexchange.users.User;

public class MeetingActivity extends AppCompatActivity implements NfcAdapter.CreateNdefMessageCallback {
	private static final String TAG = "MeetingActivity";
	public static final int REQUEST_CODE = 1012;
	private static final int PERMISSIONS_REQUEST_CODE = 1013;
	private static final int DISCOVERABLE_REQUEST_CODE = 1014;
	private static final String[] RUNTIME_PERMISSIONS = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
	
	private static boolean called_from_main;
	private static Intent last_main_intent;
	
	public static void setCalledFromMain(boolean called_from_main){
		MeetingActivity.called_from_main = called_from_main;
	}
	
	private static void setLastMainIntent(Intent intent){
		MeetingActivity.last_main_intent = intent;
	}
	
	private BluetoothServer server;
	private BluetoothClient client;
	private byte[] user_payload;
	
	private User user;
	
	private NfcAdapter nfc_adapter;
	private PendingIntent pending_nfc_intent;
	private IntentFilter[] intent_filters_array;
	private String[][] tech_lists_array;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.meeting_activity);
		
		Log.d(TAG, "onCreate: CREATED");
		
		this.initNFC();
		this.getUserPayloadFromIntent();
	}
	
	private void initNFC(){
		this.nfc_adapter = NfcAdapter.getDefaultAdapter(this);
		this.nfc_adapter.setNdefPushMessageCallback(this, this);
		
		Intent intent = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		this.pending_nfc_intent = PendingIntent.getActivity(this, 0, intent, 0);
	
		IntentFilter ndef_filter = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
		
		try{
			ndef_filter.addDataType("*/*");
		}
		catch(IntentFilter.MalformedMimeTypeException ex){
			ex.printStackTrace();
		}
		
		this.intent_filters_array = new IntentFilter[]{ndef_filter,};
		
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
		
		String log_str = String.format("RECEIVED INTENT [ACTION: %s] [TYPE: %s] [PACK: %s]", action, type, pack);
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
		this.nfc_adapter.enableForegroundDispatch(this, this.pending_nfc_intent, this.intent_filters_array, this.tech_lists_array);
		
		String TARGET_ACTION = NfcAdapter.ACTION_NDEF_DISCOVERED;
		Intent last_intent = getIntent();
		String last_intent_action;
		boolean actions_match;
		
		Log.d(TAG, "onResume: SE ENTRA EN EL ONRESUME PARA HANDLEAR");
		
		if(last_intent!=null) {
			last_intent_action = last_intent.getAction();
			Log.d(TAG, "onResume: LAST INTENT IS NOT NULL");
			
			if(last_intent_action!=null) {
				actions_match = last_intent_action.equals(TARGET_ACTION);
				Log.d(TAG, "onResume: LAST INTENT ACTION IS NOT NULL");
				
				if (actions_match) {
					Log.d(TAG, "onResume: THE ACTIONS MATCH");
					
					this.handleReceivedNdef(last_intent);
				}
				else{
					Log.d(TAG, "onResume: ACTIONS MISMATCH : " + last_intent_action);
				}
			}
		}
	}
	
	public void handleReceivedNdef(Intent intent){
		Parcelable[] raw_messages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
		NdefMessage ndef_message = (NdefMessage)raw_messages[0];
		byte[] payload = ndef_message.getRecords()[0].getPayload();
		
		ServerDescriptor secret_descriptor = ServerDescriptor.fromByteArray(payload, this);
		
		this.client = new BluetoothClient(secret_descriptor, this);
		this.client.start();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	protected void getUserPayloadFromIntent(){
		Intent intent;
		
		if(MeetingActivity.called_from_main) {
			intent = getIntent();
			MeetingActivity.setLastMainIntent(intent);
			MeetingActivity.setCalledFromMain(false);
			Log.d(TAG, "getUserPayloadFromIntent: USER IS TAKEN FROM THE NEW MAIN INTENT");
		}
		else{
			intent = MeetingActivity.last_main_intent;
			Log.d(TAG, "getUserPayloadFromIntent: USER IS TAKEN FROM THE OLD MAIN INTENT");
		}
		
		UserData user_data = (UserData)intent.getSerializableExtra("UserData");
		User user = new User(user_data,this);
		ProtocolMessage pm = new ProtocolMessage((Person)user);
		
		this.user_payload = pm.toByteArray();
		this.user = user;
	}
	
	
	
	
	protected void closeActivity(){
		Intent result_intent = new Intent();
		result_intent.putExtra("PersonData", this.user.serialize());
		setResult(1, result_intent);
		
		finish();
	}
	
	@Override
	public void onBackPressed(){
		closeActivity();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public void onSendFinished(){
		String log_str = "SEND HAS FINISHED";
		
		Log.d(TAG, "onSendFinished: " + log_str);
	}
	
	public void onReceiveFinished(byte[] payload){
		Person new_contact = ProtocolMessage.fromByteArray(payload, this);
		
		this.user.addContact(new_contact);
		
		String log_str = String.format("RECEIVE HAS FINISHED : Received username {%s}", user.getName());
		Log.d(TAG, "onReceiveFinished: " + log_str);
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