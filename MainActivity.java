package com.faridarbai.tapexchange;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import android.content.Intent;

import com.faridarbai.tapexchange.graphical.ContactsViewAdapter;
import com.faridarbai.tapexchange.profiles.UserProfile;
import com.faridarbai.tapexchange.serialization.PersonData;
import com.faridarbai.tapexchange.serialization.UserData;
import com.faridarbai.tapexchange.users.Person;
import com.faridarbai.tapexchange.users.User;

import java.io.File;

public class MainActivity extends AppCompatActivity{
	private static final String TAG = "MainActivity";
	
	ContactsViewAdapter contacts_adapter;
	
	int DEFAULT_RESOURCE_IMAGE = R.drawable.executive;
	
	static public String FILES_PATH;
	
	String DEFAULT_IMAGE_PATH;
	String DEFAULT_USERNAME;
	String USER_FILENAME;
	
	private User user;
	
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
				MainActivity.this.openMeetingActivity();
			}
		});
		
		this.initConstants();
		this.checkFirstLaunch();
		
		this.loadUser();
		
		this.contacts_adapter = new ContactsViewAdapter(this.user, this);
		initContactsView();
	}
	
	private void openMeetingActivity(){
		MeetingActivity.setCalledFromMain(true);
		this.openActivity(this.user, MeetingActivity.class, MeetingActivity.REQUEST_CODE);
	}
	
	private void loadUser(){
		this.user = User.load(USER_FILENAME, this);
	}
	
	private void initConstants(){
		FILES_PATH = getFilesDir().getAbsolutePath();
		DEFAULT_IMAGE_PATH = FILES_PATH + "/default.png";
		DEFAULT_USERNAME = "Unknown Name";
		USER_FILENAME = FILES_PATH + "/data.dat";
	}
	
	private void checkFirstLaunch(){
		File user_file = new File(USER_FILENAME);
    	boolean first_launch = (user_file.exists()==false);
		
		if (first_launch) {
			// Write default image into files/ and serialize the user file
      	Bitmap default_image = BitmapFactory.decodeResource(this.getResources(), DEFAULT_RESOURCE_IMAGE);
      	
      	User.writeImage(default_image, DEFAULT_IMAGE_PATH);
      	
      	User new_user = new User(DEFAULT_USERNAME, DEFAULT_IMAGE_PATH, this);
			
			Log.d(TAG, "checkFirstLaunch: " + new_user.getName());
			Log.d(TAG, "checkFirstLaunch: " + new_user.getImagePath());
      	
      	
      	new_user.save(USER_FILENAME);
      	
    	}
	}
	
	
	
	
	
	private void initContactsView(){
		RecyclerView contacts_view = (RecyclerView) findViewById(R.id.contacts_view);
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
			Person user_person = (Person)this.user;
			this.openActivity(user_person, UserProfile.class, UserProfile.REQUEST_CODE);
			
			return true;
		}
		
		if (id == R.id.action_about) {
			
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	public void openActivity(Person person, Class activity_class, int request_code){
		Intent intent = new Intent(this, activity_class);
		
		PersonData person_data = person.serialize();
		intent.putExtra("PersonData", person_data);
		startActivityForResult(intent, request_code);
	}
	
	public void openActivity(User user, Class activity_class, int request_code){
		Intent intent = new Intent(this, activity_class);
		
		UserData user_data = user.serialize();
		intent.putExtra("UserData", user_data);
		startActivityForResult(intent, request_code);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		PersonData person_data = (PersonData)data.getExtras().getSerializable("PersonData");
		Person person = new Person(person_data,this);
		
		switch(requestCode){
			case(UserProfile.REQUEST_CODE):{
				this.user = User.merge(person, this.user);
				this.user.save(USER_FILENAME);
				break;
			}
			case(MeetingActivity.REQUEST_CODE):{
				
				/**
					this.user.getContacts().add(person);
					int n_contacts = this.user.getContacts().size();
					this.contacts_adapter.notifyItemInserted(n_contacts-1);
				**/
				break;
			}
			default:{
				
				break;
			}
		}
		
	}
}



















































