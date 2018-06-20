package com.faridarbai.tapexchange;

import android.app.PendingIntent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcEvent;
import android.os.Parcelable;

import com.faridarbai.tapexchange.graphical.ContactsViewAdapter;
import com.faridarbai.tapexchange.profiles.ContactProfile;
import com.faridarbai.tapexchange.profiles.PersonalProfile;
import com.faridarbai.tapexchange.profiles.UserProfile;
import com.faridarbai.tapexchange.serialization.PersonData;
import com.faridarbai.tapexchange.serialization.ProtocolMessage;
import com.faridarbai.tapexchange.serialization.UserData;
import com.faridarbai.tapexchange.users.Person;
import com.faridarbai.tapexchange.users.User;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements CreateNdefMessageCallback{
	private static final String TAG = "MainActivity";
	
	NfcAdapter nfc_adapter;
	PendingIntent pending_nfc_intent;
	IntentFilter[] intent_filters_array;
	String[][] tech_lists_array;
	
	ContactsViewAdapter contacts_adapter;
	
	
	int DEFAULT_RESOURCE_IMAGE = R.drawable.default_avatar;
	
	static public String FILES_PATH;
	
	String DEFAULT_IMAGE_PATH;
	String DEFAULT_USERNAME;
	String USER_FILENAME;
	
	User user;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		
		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
						.setAction("Action", null).show();
			}
		});
		
		this.initConstants();
		this.checkFirstLaunch();
		this.initNFC();
		this.loadUser();
		
		this.contacts_adapter = new ContactsViewAdapter(this.user, this);
		initContactsView();
	}
	
	private void loadUser(){
		this.user = User.load(USER_FILENAME, this);
	}
	
	private void initConstants(){
		FILES_PATH = getFilesDir().getAbsolutePath();
		DEFAULT_IMAGE_PATH = FILES_PATH + "default.png";
		DEFAULT_USERNAME = "Unknown Name";
		USER_FILENAME = FILES_PATH + "data.dat";
	}
	
	private void checkFirstLaunch(){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    	boolean first_launch = (prefs.getBoolean("firstTime", false)==false);
		
		if (true) {
			// Write default image into files/ and serialize the user file
      	Bitmap default_image = BitmapFactory.decodeResource(this.getResources(), DEFAULT_RESOURCE_IMAGE);
      	
      	User.writeImage(default_image, DEFAULT_IMAGE_PATH);
      	
      	User new_user = new User(DEFAULT_USERNAME, DEFAULT_IMAGE_PATH, this);
			
			Log.d(TAG, "checkFirstLaunch: " + new_user.getName());
			Log.d(TAG, "checkFirstLaunch: " + new_user.getImagePath());
      	
      	
      	new_user.save(USER_FILENAME);
      	
			SharedPreferences.Editor editor = prefs.edit();
			editor.putBoolean("firstTime", true);
			editor.commit();
    	}
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
		ProtocolMessage pm = new ProtocolMessage((Person)this.user);
		
		byte[] payload = pm.toByteArray();
		String domain = "com.faridarbai";
		String type = "faridtype";
		
		NdefRecord record = NdefRecord.createExternal(domain, type, payload);
		NdefMessage message = new NdefMessage(record);
		
		System.out.printf("Ndef creation method was called\n");
		
		return message;
	}
	
	@Override
	public void onNewIntent(Intent intent){
		setIntent(intent);
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
			actions_match = last_intent_action.equals(received_data_action);
			
			if(actions_match) {
				this.handleReceivedNdef(last_intent);
				setIntent(null);
			}
		}
	}
	
	public void handleReceivedNdef(Intent intent){
		Parcelable[] raw_messages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
		NdefMessage ndef_message = (NdefMessage)raw_messages[0];
		byte[] payload = ndef_message.getRecords()[0].getPayload();
		Person person = ProtocolMessage.fromByteArray(payload, this);
		
		
		Log.d(TAG, "handleReceivedNdef: THIS ->" + person.getName());
		
		this.user.getContacts().add(person);
		
		int n_contacts = this.user.getContacts().size();
		
		this.contacts_adapter.notifyItemInserted(n_contacts-1);
		
	}
	
	private void initContactsView(){
		RecyclerView contacts_view = findViewById(R.id.contacts_view);
		this.contacts_adapter = new ContactsViewAdapter(this.user, this);
		
		contacts_view.setAdapter(contacts_adapter);
		contacts_view.setLayoutManager(new LinearLayoutManager(this));
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		
		//noinspection SimplifiableIfStatement
		if (id == R.id.action_profile) {
			this.openProfile(this.user, UserProfile.class, UserProfile.REQUEST_CODE);
			
			return true;
		}
		
		if (id == R.id.action_about) {
			
			return true;
		}
		
		
		return super.onOptionsItemSelected(item);
	}
	
	public void openProfile(Person person, Class profile_class, int request_code){
		Intent intent = new Intent(this, profile_class);
		
		PersonData person_data = person.serialize();
		intent.putExtra("PersonData", person_data);
		startActivityForResult(intent, request_code);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		PersonData person_data = (PersonData)data.getExtras().getSerializable("PersonData");
		Person person = new Person(person_data,this);
		
		if(requestCode == UserProfile.REQUEST_CODE) {
			this.user = User.merge(person, this.user);
		}
	}
}



















































