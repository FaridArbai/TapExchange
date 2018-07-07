package com.faridarbai.tapexchange;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.os.Build;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.faridarbai.tapexchange.networking.BluetoothClient;
import com.faridarbai.tapexchange.networking.BluetoothServer;
import com.faridarbai.tapexchange.networking.ServerDescriptor;
import com.faridarbai.tapexchange.serialization.PersonData;
import com.faridarbai.tapexchange.serialization.ProtocolMessage;
import com.faridarbai.tapexchange.serialization.UserData;
import com.faridarbai.tapexchange.users.Person;
import com.faridarbai.tapexchange.users.User;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MeetingActivity extends AppCompatActivity implements NfcAdapter.CreateNdefMessageCallback {
	private static final String TAG = "MeetingActivity";
	public static final int REQUEST_CODE = 1012;
	private static final int PERMISSIONS_REQUEST_CODE 			= 1013;
	private static final int DISCOVERABLE_REQUEST_CODE 		= 1014;
	private static final int BLUETOOTH_ENABLE_REQUEST_CODE	= 1015;
	private static final int NFC_ENABLE_REQUEST_CODE			= 1016;
	
	
	private static final String[] RUNTIME_PERMISSIONS = new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
			Manifest.permission.ACCESS_COARSE_LOCATION};
	
	private BluetoothServer server;
	private BluetoothClient client;
	
	private ProgressDialog progress_dialog;
	
	private byte[] user_payload;
	
	private Person user;
	private ArrayList<PersonData> new_contacts_data;
	
	private BluetoothAdapter bluetooth_adapter;
	
	private NfcAdapter nfc_adapter;
	private PendingIntent pending_nfc_intent;
	private IntentFilter[] intent_filters_array;
	private String[][] tech_lists_array;
	
	private ServerDescriptor last_secret_descriptor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.meeting_activity);
		
		this.new_contacts_data = new ArrayList<PersonData>();
		
		this.getUserPayloadFromIntent();
		
		this.initNFC();
		
		this.initBluetooth();
		
		this.requestLocationPermissions();
		this.enableBluetooth();
	}
	
	private void initBluetooth(){
		this.bluetooth_adapter = BluetoothAdapter.getDefaultAdapter();
	}
	
	private void enableBluetooth(){
		if(!this.bluetooth_adapter.isEnabled()){
			Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(intent, MeetingActivity.BLUETOOTH_ENABLE_REQUEST_CODE);
		}
	}
	
	private void initNFC(){
		this.nfc_adapter = NfcAdapter.getDefaultAdapter(this);
		
		if(!this.nfc_adapter.isEnabled()){
			this.onNfcDisabled();
		}
		
		this.nfc_adapter.setNdefPushMessageCallback(this, this);
		
		Intent intent = new Intent(this, getClass());
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		this.pending_nfc_intent = PendingIntent.getActivity(this, 0, intent, 0);
		
		IntentFilter ndef_filter = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
		
		try{
			ndef_filter.addDataType("*/*");
		}
		catch(IntentFilter.MalformedMimeTypeException ex){
			ex.printStackTrace();
		}
		
		this.intent_filters_array = new IntentFilter[]{ndef_filter};
		
		this.tech_lists_array = new String[][]{
				new String[]{Ndef.class.getName()},
				new String[]{NfcA.class.getName()},
				new String[]{NfcB.class.getName()},
				new String[]{NfcV.class.getName()},
				new String[]{NfcF.class.getName()}};
		
		this.nfc_adapter.setOnNdefPushCompleteCallback(new NfcAdapter.OnNdefPushCompleteCallback() {
			@Override
			public void onNdefPushComplete(NfcEvent event) {
				MeetingActivity.this.launchServer();
			}
		},this);
	}
	
	private void launchServer(){
		String secret_uuid = this.last_secret_descriptor.getSecretUUID();
		this.server = new BluetoothServer(secret_uuid, this.user_payload, this);
		this.server.start();
		requestDiscoverability();
	}
	
	
	@Override
	public NdefMessage createNdefMessage(NfcEvent event){
		int payload_length = this.user_payload.length;
		this.last_secret_descriptor = new ServerDescriptor(payload_length);
		byte[] payload = this.last_secret_descriptor.toByteArray();
		
		String domain = "com.faridarbai";
		String type = "faridtype";
		
		NdefRecord record = NdefRecord.createExternal(domain, type, payload);
		NdefMessage message = new NdefMessage(record);
		
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
		
		String TARGET_ACTION = NfcAdapter.ACTION_TECH_DISCOVERED;
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
					setIntent(null);
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
		
		this.progress_dialog = new ProgressDialog(this);
		this.progress_dialog.setTitle("Scanning");
		this.progress_dialog.setIcon(R.drawable.bluetooth_scan_icon);
		this.progress_dialog.setMessage("Searching device");
		this.progress_dialog.setCanceledOnTouchOutside(false);
		this.progress_dialog.setCancelable(false);
		
		this.progress_dialog.show();
	}
	
	public void onContactsDeviceFound(){
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				MeetingActivity.this.progress_dialog.dismiss();
		
				MeetingActivity.this.progress_dialog = new ProgressDialog(MeetingActivity.this);
				MeetingActivity.this.progress_dialog.setTitle("Downloading");
				MeetingActivity.this.progress_dialog.setIcon(R.drawable.bluetooth_download_icon);
				MeetingActivity.this.progress_dialog.setMessage("\nLoading contact information");
				MeetingActivity.this.progress_dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				MeetingActivity.this.progress_dialog.setMax(100);
				MeetingActivity.this.progress_dialog.setCanceledOnTouchOutside(false);
				MeetingActivity.this.progress_dialog.setCancelable(false);
				
				MeetingActivity.this.progress_dialog.show();
			}
		});
	}
	
	public void setProgressTo(float percentage, final int downloaded, final int total_size){
		final int percentage_100 = (int)(percentage*100.0);
		
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				MeetingActivity.this.progress_dialog.setProgress(percentage_100);
				String number_format = String.format("%s / %s", bytesToString(downloaded), bytesToString(total_size));
				MeetingActivity.this.progress_dialog.setProgressNumberFormat(number_format);
			}
		});
	}
	
	public static final double SPACE_KB = 1024;
	public static final double SPACE_MB = 1024 * SPACE_KB;
	public static final double SPACE_GB = 1024 * SPACE_MB;
	public static final double SPACE_TB = 1024 * SPACE_GB;
	
	private String bytesToString(int bytes){
		NumberFormat nf = new DecimalFormat();
		String format;
		
		nf.setMaximumFractionDigits(2);
		
		try {
			if ( bytes < SPACE_KB ) {
				format =  nf.format(bytes) + " Byte(s)";
			} else if ( bytes < SPACE_MB ) {
				format =  nf.format(bytes/SPACE_KB) + " KB";
			} else if ( bytes < SPACE_GB ) {
				format = nf.format(bytes/SPACE_MB) + " MB";
			} else if ( bytes < SPACE_TB ) {
				format = nf.format(bytes/SPACE_GB) + " GB";
			} else {
				format = nf.format(bytes/SPACE_TB) + " TB";
			} 
		} catch (Exception e) {
			format = String.format("%d B", bytes);
		}
		
		return format;
	}
	
	protected void getUserPayloadFromIntent(){
		Intent intent = getIntent();;
		
		PersonData person_data = (PersonData)intent.getSerializableExtra("PersonData");
		Person user = new Person(person_data,this);
		ProtocolMessage pm = new ProtocolMessage((Person)user);
		
		this.user_payload = pm.toByteArray();
		this.user = user;
		
		Log.d(TAG, "getUserPayloadFromIntent: USER IS CREATED FROM MAIN INTENT");
	}
	
	
	
	
	protected void closeActivity(){
		Intent result_intent = new Intent();
		int result;
		
		if(this.new_contacts_data.size()==0){
			result = RESULT_CANCELED;
		}
		else{
			result = RESULT_OK;
			result_intent.putExtra("NewContactsData", this.new_contacts_data);
		}
		
		setResult(result, result_intent);
		finish();
	}
	
	@Override
	public void onBackPressed(){
		closeActivity();
	}
	
	public void onReceiveFinished(byte[] payload){
		final Person new_contact = ProtocolMessage.fromByteArray(payload, this);
		PersonData new_contact_data = new_contact.serialize();
		this.new_contacts_data.add(new_contact_data);
		
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				MeetingActivity.this.progress_dialog.dismiss();
				MeetingActivity.this.showNewContactDialog(new_contact);
			}
		});
		
		Log.d(TAG, "onReceiveFinished: FINISHED ALL SENDING");
	}
	
	public void showNewContactDialog(Person new_contact){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View dialog_view = this.getLayoutInflater().inflate(R.layout.new_contact_dialog, null);
		
		final CircleImageView contact_image_view = dialog_view.findViewById(R.id.new_contact_image);
		final TextView contact_username = dialog_view.findViewById(R.id.new_contact_username);
		final TextView contact_job = dialog_view.findViewById(R.id.new_contact_job);
		final TextView contact_location = dialog_view.findViewById(R.id.new_contact_location);
		
		String image_path = new_contact.getImagePath();
		Bitmap contact_image = BitmapFactory.decodeFile(image_path);
		contact_image_view.setImageBitmap(contact_image);
		
		contact_username.setText(new_contact.getName());
		contact_job.setText(new_contact.getJobStatus());
		contact_location.setText(new_contact.getLocationStatus());
		
		builder.setView(dialog_view);
		final AlertDialog dialog = builder.create();
		
		dialog.setCanceledOnTouchOutside(false);
		
		dialog.show();
	}
	
	public void onConnectionError(final String error_message){
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				final AlertDialog.Builder builder = new AlertDialog.Builder(MeetingActivity.this);
			
				builder.setIcon(R.drawable.error_icon);
				builder.setTitle("Connection error");
				builder.setMessage(error_message);
				builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
				
				if(MeetingActivity.this.progress_dialog!=null) {
					if (MeetingActivity.this.progress_dialog.isShowing()) {
						MeetingActivity.this.progress_dialog.dismiss();
					}
				}
				
				builder.show();
			}
		});
	}
	
	
	private void requestDiscoverability(){
		Intent discoverableIntent =
        new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		
		discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, BluetoothServer.DISCOVERABLE_DURATION);
		startActivityForResult(discoverableIntent, MeetingActivity.DISCOVERABLE_REQUEST_CODE);
	}
	
	
	private void requestLocationPermissions(){
		if(true){
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
	
	private void onUserCancelledBluetooth(){
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		String message = "Bluetooth must be set in order to exchange contact information.";
		
		builder.setIcon(R.drawable.bluetooth_cancel_icon);
		builder.setTitle("Settings error");
		builder.setMessage(message);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				MeetingActivity.this.enableBluetooth();
			}
		});
		
		builder.setNegativeButton("EXIT", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				MeetingActivity.this.closeActivity();
			}
		});
		
		AlertDialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}
	
	private void onUserCancelledLocation(){
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		String message = "Location must be enabled to register the meeting place.";
		
		builder.setIcon(R.drawable.location_cancel_icon);
		builder.setTitle("Settings error");
		builder.setMessage(message);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				MeetingActivity.this.requestLocationPermissions();
			}
		});
		
		builder.setNegativeButton("EXIT", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				MeetingActivity.this.closeActivity();
			}
		});
		
		AlertDialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}
	
	private void onUserCancelledDiscoverability(){
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		String message = "Discoverability must be set for the bluetooth transfer to take place.\n\n" +
				"Note: When enabling discoverability your device will be visible to other local devices " +
				"for 10 seconds. Even though, only the device you have tapped will be able to download " +
				"your contact profile since secret connection information has been exchanged during the NFC tap.";
		
		builder.setIcon(R.drawable.bluetooth_cancel_icon);
		builder.setTitle("Settings error");
		builder.setMessage(message);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				MeetingActivity.this.requestDiscoverability();
			}
		});
		
		builder.setNegativeButton("EXIT", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				MeetingActivity.this.closeActivity();
			}
		});
		
		AlertDialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}
	
	private void onNfcDisabled(){
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		String message = "NFC must be enabled in order to exchange profile information.";
		
		builder.setIcon(R.drawable.nfc_icon);
		builder.setTitle("Settings error");
		builder.setMessage(message);
		builder.setPositiveButton("NFC SETTINGS", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
				startActivityForResult(intent, MeetingActivity.NFC_ENABLE_REQUEST_CODE);
			}
		});
		
		builder.setNegativeButton("EXIT", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				MeetingActivity.this.closeActivity();
			}
		});
		
		AlertDialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		switch(requestCode){
			case(BLUETOOTH_ENABLE_REQUEST_CODE):{
				if(resultCode!=RESULT_OK){
					this.onUserCancelledBluetooth();
				}
				break;
			}
			case(DISCOVERABLE_REQUEST_CODE):{
				if(resultCode<=0){
					this.onUserCancelledDiscoverability();
				}
				break;
			}
			case(NFC_ENABLE_REQUEST_CODE):{
				if(!this.nfc_adapter.isEnabled()){
					this.onNfcDisabled();
				}
			}
			default:{
				break;
			}
		}
	}
	
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		
		switch(requestCode){
			case(PERMISSIONS_REQUEST_CODE):{
				boolean retry = false;
				
				for(int result : grantResults){
					if(result == PackageManager.PERMISSION_DENIED){
						retry = true;
					}
				}
				
				if(retry){
					this.onUserCancelledLocation();
				}
				
				
				break;
			}
			default:{
				
				break;
			}
		}
	}
}