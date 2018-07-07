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
import android.view.ViewTreeObserver;

import com.faridarbai.tapexchange.graphical.ContactsViewAdapter;
import com.faridarbai.tapexchange.profiles.UserProfile;
import com.faridarbai.tapexchange.serialization.PersonData;
import com.faridarbai.tapexchange.serialization.UserData;
import com.faridarbai.tapexchange.users.Person;
import com.faridarbai.tapexchange.users.User;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity{
	private static final String TAG = "MainActivity";
	
	ContactsViewAdapter contacts_adapter;
	
	public static int DEFAULT_RESOURCE_IMAGE = R.drawable.executive;
	
	public static String FILES_PATH;
	
	public static String DEFAULT_IMAGE_PATH;
	public static String DEFAULT_USERNAME;
	public static String USER_FILENAME;
	
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
		
		this.initGUIListener();
		this.initDataConstants();
		this.checkFirstLaunch();
	}
	
	private void openMeetingActivity(){
		this.openActivity((Person)this.user, MeetingActivity.class, MeetingActivity.REQUEST_CODE);
	}
	
	private void loadUser(){
		this.user = User.load(USER_FILENAME, this);
	}
	
	private void initDataConstants(){
		FILES_PATH = getFilesDir().getAbsolutePath();
		DEFAULT_IMAGE_PATH = FILES_PATH + "/default.png";
		DEFAULT_USERNAME = "Unknown Name";
		USER_FILENAME = FILES_PATH + "/data.dat";
	}
	
	private void initGUIListener(){
		ViewTreeObserver vto = this.getWindow().getDecorView().getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				initGUIConstants();
			}
		});
	}
	
	private void initGUIConstants(){
		int width = this.getWindow().getDecorView().getWidth();
		Person.AVATAR_SIZE = width/4;
		Person.BACKGROUND_SIZE = width;
	}
	
	private void checkFirstLaunch(){
		File user_file = new File(USER_FILENAME);
    	boolean first_launch = (user_file.exists()==false);
		
		if (first_launch) {
      	Intent intent = new Intent(this, StartupActivity.class);
			startActivityForResult(intent, StartupActivity.REQUEST_CODE);
    	}
    	else{
			this.loadUser();
			initContactsView();
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
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		switch(requestCode){
			case(UserProfile.REQUEST_CODE):{
				PersonData person_data = (PersonData)data.getExtras().getSerializable("PersonData");
				Person person = new Person(person_data,this);
				this.user = User.merge(person, this.user);
				this.user.save(USER_FILENAME);
				break;
			}
			case(MeetingActivity.REQUEST_CODE):{
				if(resultCode==RESULT_OK){
					ArrayList<PersonData> new_contacts_data = (ArrayList<PersonData>)data.getExtras().getSerializable("NewContactsData");
					this.addNewContacts(new_contacts_data);
				}
				break;
			}
			case(StartupActivity.REQUEST_CODE):{
				UserData user_data = (UserData)data.getExtras().getSerializable("UserData");
				this.user = new User(user_data,this);
				initContactsView();
			}
			default:{
				break;
			}
		}
	}
	
	
	private void addNewContacts(ArrayList<PersonData> new_contacts_data){
		Person current_contact;
		
		for(PersonData contact_data : new_contacts_data){
			current_contact = new Person(contact_data, this);
			this.user.addContact(current_contact);
			this.contacts_adapter.notifyItemInserted(0);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}



















































