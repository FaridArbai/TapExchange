package com.faridarbai.tapexchange.profiles;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.faridarbai.tapexchange.R;
import com.faridarbai.tapexchange.serialization.PersonData;
import com.faridarbai.tapexchange.users.Person;
import com.faridarbai.tapexchange.users.User;
import com.faridarbai.tapexchange.serialization.UserData;

public class PersonalProfile extends AppCompatActivity {
	protected static final String TAG = "PersonalProfile";
	
	protected Person user;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.personal_profile_activity);
		
		configureToolbar();
		
		getUserFromIntent();
		initDataView();
		
		adjustAvatarHeight();
		this.updateAvatar();
	}
	
	protected void getUserFromIntent(){
		Intent intent = getIntent();
		PersonData user_data = (PersonData)intent.getSerializableExtra("PersonData");
		this.user = new Person(user_data,this);
	}
	
	protected void configureToolbar() {
		Toolbar toolbar = (Toolbar) findViewById(R.id.personal_toolbar);
		setSupportActionBar(toolbar);
		ActionBar actionbar = getSupportActionBar();
		
		if (actionbar != null) {
			actionbar.setDisplayHomeAsUpEnabled(true);
			actionbar.setDisplayShowTitleEnabled(false);
		}
	}
	
	protected void updateAvatar(){
		ImageView personal_image = (ImageView)findViewById(R.id.personal_image_foreground);
		String image_path = this.user.getImagePath();
		Bitmap bitmap = BitmapFactory.decodeFile(image_path);
		personal_image.setImageBitmap(bitmap);
	}
	
	
	protected void adjustAvatarHeight(){
		AppBarLayout personal_appbar = findViewById(R.id.personal_appbar);
		float screen_width = getResources().getDisplayMetrics().widthPixels;
		CoordinatorLayout.LayoutParams appbar_params = (CoordinatorLayout.LayoutParams)personal_appbar.getLayoutParams();
		//appbar_params.height = (int)screen_width;
	}
	
	public void initDataView(){
		RecyclerView sections_view = findViewById(R.id.personal_sections_view);
		
		sections_view.setAdapter(this.user.getSections());
		sections_view.setLayoutManager(new LinearLayoutManager(this));
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
	
}
