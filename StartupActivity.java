package com.faridarbai.tapexchange;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.faridarbai.tapexchange.graphical.form.SectionFormAdapter;
import com.faridarbai.tapexchange.users.Form;
import com.faridarbai.tapexchange.users.Person;
import com.faridarbai.tapexchange.users.User;
import com.theartofdev.edmodo.cropper.CropImage;

import de.hdodenhof.circleimageview.CircleImageView;

public class StartupActivity extends AppCompatActivity {
	private static final String TAG = "StartupActivity";
	private static final int NUM_PAGES = 3;
	private static final int WELCOME_NUM_PAGE 	= 1;
	private static final int FORM_NUM_PAGE 		= 2;
	private static final int TUTORIAL_NUM_PAGE 	= 3;
	public static final int REQUEST_CODE = 3011;
	
	private SectionsPagerAdapter pager_adapter;
	private ViewPager view_pager;
	
	private User user;
	
	
	private SectionFormAdapter form_adapter;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.startup_activity);
		
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		
		PlaceholderFragment.activity = this;
		pager_adapter = new SectionsPagerAdapter(getSupportFragmentManager());
		view_pager = (ViewPager) findViewById(R.id.container);
		view_pager.setAdapter(pager_adapter);
		
		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.startup_fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view){
				StartupActivity.this.onNextClicked();
			}
		});
		
		this.initUser();
		this.initGUIListener();
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
		Log.d(TAG, "initGUIConstants: " + Person.AVATAR_SIZE);
	}
	
	
	private void initUser(){
		Bitmap default_image = BitmapFactory.decodeResource(this.getResources(), MainActivity.DEFAULT_RESOURCE_IMAGE);
		User.writeImage(default_image, MainActivity.DEFAULT_IMAGE_PATH);
		this.user = new User(MainActivity.DEFAULT_USERNAME, MainActivity.DEFAULT_IMAGE_PATH, this);
	}
	
	private void onNextClicked(){
		int current = view_pager.getCurrentItem();
		int next = current+1;
		boolean end_reached = (next==StartupActivity.NUM_PAGES);
		
		
		if(current == (FORM_NUM_PAGE-1)){
			View form_view = this.view_pager.getRootView();
			Form form = new Form(form_view);
			boolean mandatory_fields_are_filled = form.mandatoryFieldsAreFilled();
			
			if(mandatory_fields_are_filled){
				form.updateUser(this.user);
				this.user.save(MainActivity.USER_FILENAME);
				view_pager.setCurrentItem(next);
			}
			else{
				Snackbar.make(findViewById(R.id.startup_fab), "All mandatory fields must be filled", Snackbar.LENGTH_LONG).show();
			}
		}
		else {
			if (!end_reached) {
				view_pager.setCurrentItem(next);
			} else {
				Intent result_intent = new Intent();
				result_intent.putExtra("UserData", this.user.serialize());
				setResult(1, result_intent);
				finish();
			}
		}
	}
	
	@Override
	public void onBackPressed() {
		int current = view_pager.getCurrentItem();
		
		if(current!=0){
			this.view_pager.setCurrentItem(current-1);
		}
		else{
			this.moveTaskToBack(true);
		}
	}
	
	public static class PlaceholderFragment extends Fragment {
		private static final String ARG_SECTION_NUMBER = "section_number";
		public static AppCompatActivity activity;
		
		public PlaceholderFragment() {
		}
		
		
		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View root_view = null;
			int fragment_number = getArguments().getInt(ARG_SECTION_NUMBER);
			
			switch(fragment_number){
				case(StartupActivity.WELCOME_NUM_PAGE):{
					root_view = inflater.inflate(R.layout.welcome_fragment, container, false);
					break;
				}
				case(StartupActivity.FORM_NUM_PAGE):{
					root_view = inflater.inflate(R.layout.form_fragment, container, false);
					CircleImageView avatar_view = root_view.findViewById(R.id.form_avatar);
					avatar_view.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							Form.openImageHandler(activity);
						}
					});
					break;
				}
				case(StartupActivity.TUTORIAL_NUM_PAGE):{
					root_view = inflater.inflate(R.layout.tutorial_fragment, container, false);
				}
				default:{
					break;
				}
			}
			
			return root_view;
		}
	}
	
	public class SectionsPagerAdapter extends FragmentPagerAdapter {
		
		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}
		
		@Override
		public Fragment getItem(int position) {
			return PlaceholderFragment.newInstance(position + 1);
		}
		
		@Override
		public int getCount() {
			return StartupActivity.NUM_PAGES;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		switch(requestCode){
			case(CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE):{
				CropImage.ActivityResult result = CropImage.getActivityResult(data);
				
				if(resultCode==RESULT_OK) {
					Uri cropped_uri = result.getUri();
					Form.handleNewAvatar(cropped_uri, this.user, this);
				}
				break;
			}
			default:{
				break;
			}
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
