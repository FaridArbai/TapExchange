package com.faridarbai.tapexchange.profiles;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.drm.DrmStore;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.faridarbai.tapexchange.R;
import com.faridarbai.tapexchange.graphical.PersonalTitlesController;
import com.faridarbai.tapexchange.serialization.PersonData;
import com.faridarbai.tapexchange.users.Person;
import com.faridarbai.tapexchange.users.User;
import com.faridarbai.tapexchange.serialization.UserData;

import java.io.File;
import java.io.IOException;

public class PersonalProfile extends AppCompatActivity {
	protected static final String TAG = "PersonalProfile";
	
	protected Person user;
	protected PersonalTitlesController titles_controller;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.personal_profile_activity);
		
		configureActionbar();
		
		initTitlesController();
		
		getUserFromIntent();
		initDataView();
		
		adjustAvatarHeight();
		this.updateAvatar();
	}
	
	private void initTitlesController(){
		LinearLayout status = findViewById(R.id.personal_status);
		TextView title = findViewById(R.id.personal_title);
		AppBarLayout appbar = findViewById(R.id.personal_appbar);
		
		this.titles_controller = new PersonalTitlesController(status, title);
		appbar.addOnOffsetChangedListener(titles_controller);
	}
	
	protected void getUserFromIntent(){
		Intent intent = getIntent();
		PersonData user_data = (PersonData)intent.getSerializableExtra("PersonData");
		this.user = new Person(user_data,this);
	}
	
	protected void configureActionbar() {
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
		
		Palette palette = (new Palette.Builder(bitmap)).generate();
		
		int muted = palette.getMutedColor(0);
		
		int dark_muted = palette.getDarkMutedColor(muted);
		int dominant = palette.getDominantColor(0);
		
		int status_color = palette.getDarkVibrantColor(dark_muted);
		int action_color = palette.getVibrantColor(dominant);
		
		int buttons_color = palette.getLightVibrantColor(palette.getLightMutedColor(dominant));
		
		this.setStatusColor(status_color);
		this.setActionColor(action_color);
		
		FloatingActionButton bottom_button = findViewById(R.id.section_selection_button);
		
		bottom_button.setBackgroundTintList(ColorStateList.valueOf(buttons_color));
		
		//ImageView background_image = (ImageView)findViewById(R.id.personal_image_background);
		//background_image.setColorFilter(dominant, PorterDuff.Mode.MULTIPLY);
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
	
	
	public static Bitmap loadImage(String path){
		File file = new File(path);
		BitmapFactory.Options options = new BitmapFactory.Options();
		Bitmap image = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
		
		return image;
	}
	
	
	protected void setStatusColor(int color){
		Window window = getWindow();
		window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
		
		window.setStatusBarColor(color);
	}
	
	protected void setActionColor(int color){
		//ActionBar actionbar = getSupportActionBar();
		
		CollapsingToolbarLayout collapsing = (CollapsingToolbarLayout) findViewById(R.id.personal_collapsing);
		
		collapsing.setBackgroundColor(color);
		collapsing.setContentScrimColor(color);
		collapsing.setStatusBarScrimColor(color);
		
		//actionbar.setBackgroundDrawable(new ColorDrawable(color));
		//actionbar.setDisplayShowTitleEnabled(false);
		//actionbar.setDisplayShowTitleEnabled(true);
	}
}



























