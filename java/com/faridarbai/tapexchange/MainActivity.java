package com.faridarbai.tapexchange;

import android.app.PendingIntent;
import android.content.IntentFilter;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcF;
import android.os.Bundle;
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
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.TextView;
import android.widget.Toast;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements CreateNdefMessageCallback{
	private static final String TAG = "MainActivity";
	
	NfcAdapter nfc_adapter;
	PendingIntent pending_nfc_intent;
	IntentFilter[] intent_filters_array;
	String[][] tech_lists_array;
	
	ContactsViewAdapter contacts_adapter;
	String username = android.os.Build.MODEL;
	private ArrayList<String> contact_names = new ArrayList<>();
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
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
		
		initNFC();
		initNames();
		initContactsView();
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
		String text = this.username;
		
		byte[] payload = text.getBytes();
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
		String message = new String(ndef_message.getRecords()[0].getPayload());
		
		this.contact_names.add(message);
		this.contacts_adapter.notifyItemInserted(this.contact_names.size()-1);
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private void initNames(){
		this.contact_names.add("Farid Arbai");
		this.contact_names.add("Harvey Specter");
	}
	
	private void initContactsView(){
		RecyclerView contacts_view = findViewById(R.id.contacts_view);
		this.contacts_adapter = new ContactsViewAdapter(this, this.contact_names);
		
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
		if (id == R.id.action_settings) {
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
}
