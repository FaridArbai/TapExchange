package com.faridarbai.tapexchange.profiles;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.os.AsyncTask;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.faridarbai.tapexchange.R;
import com.faridarbai.tapexchange.graphical.PersonalTitlesController;
import com.faridarbai.tapexchange.serialization.PersonData;
import com.faridarbai.tapexchange.users.Person;

import java.io.File;

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
		this.updateBackground();
		this.updateAvatar();
	}
	
	private void initTitlesController(){
		LinearLayout status = (LinearLayout) findViewById(R.id.personal_status);
		TextView title = (TextView) findViewById(R.id.personal_title);
		AppBarLayout appbar = (AppBarLayout) findViewById(R.id.personal_appbar);
		
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
	}
	
	protected void updateBackground(){
		Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.profile_background);
		ImageView background = findViewById(R.id.personal_image_background);
		
		background.setImageBitmap(bitmap);
		
		new ImageProcessingTask(bitmap).execute();
	}
	
	
	 private class ImageProcessingTask extends AsyncTask<Void, Void, Void>{
		Bitmap bitmap;
		int status_color, action_color, buttons_color;
		
		public ImageProcessingTask(Bitmap bitmap){
			this.bitmap = bitmap;
		}
		
		 @Override
		 protected Void doInBackground(Void... voids) {
			Palette palette = (new Palette.Builder(bitmap)).generate();
		
			final RenderScript rs = RenderScript.create( PersonalProfile.this );
			final Allocation input = Allocation.createFromBitmap( rs, bitmap, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT );
			final Allocation output = Allocation.createTyped( rs, input.getType() );
			final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create( rs, Element.U8_4( rs ) );
			script.setRadius(11.f);
			script.setInput( input );
			script.forEach( output );
			output.copyTo( bitmap );
			
			int muted = palette.getMutedColor(0);
		
			int dark_muted = palette.getDarkMutedColor(muted);
			int dominant = palette.getDominantColor(0);
		
			int dark_dominant = PersonalProfile.this.darken(dominant);
		
			this.status_color = dark_dominant;
			this.action_color = dominant;
			this.buttons_color = palette.getVibrantColor(palette.getLightMutedColor(dominant));
			
			return null;
		 }
		
		 @Override
		 protected void onPostExecute(Void aVoid) {
			 super.onPostExecute(aVoid);
			 ImageView background = findViewById(R.id.personal_image_background);
			 
			 background.setImageBitmap(bitmap);
			 PersonalProfile.this.setStatusColor(status_color);
			 PersonalProfile.this.setActionColor(action_color);
			 PersonalProfile.this.setFloatingButtonColor(buttons_color);
		 }
	 }
	
	protected void setFloatingButtonColor(int color){
		FloatingActionButton bottom_button = (FloatingActionButton) findViewById(R.id.section_selection_button);
		bottom_button.setBackgroundTintList(ColorStateList.valueOf(color));
	}
	
	protected int darken(int color){
		float factor = 0.8f;
		int a = Color.alpha(color);
		int r = Math.round(Color.red(color) * factor);
		int g = Math.round(Color.green(color) * factor);
		int b = Math.round(Color.blue(color) * factor);
		int dark_color = Color.argb(a, r, g, b);
		
		return dark_color;
	}
	
	
	protected void adjustAvatarHeight(){
		AppBarLayout personal_appbar = (AppBarLayout) findViewById(R.id.personal_appbar);
		float screen_width = getResources().getDisplayMetrics().widthPixels;
		CoordinatorLayout.LayoutParams appbar_params = (CoordinatorLayout.LayoutParams)personal_appbar.getLayoutParams();
		//appbar_params.height = (int)screen_width;
	}
	
	public void initDataView(){
		RecyclerView sections_view = (RecyclerView) findViewById(R.id.personal_sections_view);
		
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



























