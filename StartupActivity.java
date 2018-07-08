package com.faridarbai.tapexchange;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;

import com.faridarbai.tapexchange.graphical.Data;
import com.faridarbai.tapexchange.graphical.Section;
import com.faridarbai.tapexchange.graphical.form.SectionFormAdapter;
import com.faridarbai.tapexchange.users.Form;
import com.faridarbai.tapexchange.users.Person;
import com.faridarbai.tapexchange.users.User;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import at.markushi.ui.CircleButton;
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.startup_activity);
		
		//Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		//setSupportActionBar(toolbar);
		
		this.initUser();
		this.initGUIListener();
		
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
		
		FloatingActionButton new_section_button = findViewById(R.id.new_section_button);
		new_section_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				StartupActivity.this.openNewSectionDialog();
			}
		});
		
		new_section_button.setVisibility(View.INVISIBLE);
		
		this.view_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			public void onPageScrollStateChanged(int state) {}
    		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    		public void onPageSelected(int position) {
        		if(position==(StartupActivity.this.FORM_NUM_PAGE-1)){
        			StartupActivity.this.findViewById(R.id.new_section_button).setVisibility(View.VISIBLE);
				}
				else{
        			StartupActivity.this.findViewById(R.id.new_section_button).setVisibility(View.INVISIBLE);
				}
    		}
		});
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
		if(Person.AVATAR_SIZE==0){
			int width = this.getWindow().getDecorView().getWidth();
			Person.AVATAR_SIZE = width/4;
			Person.BACKGROUND_SIZE = width;
			Log.d(TAG, "initGUIConstants: " + Person.AVATAR_SIZE);
		}
	}
	
	
	private void initUser(){
		Bitmap default_image = BitmapFactory.decodeResource(this.getResources(), MainActivity.DEFAULT_RESOURCE_IMAGE);
		User.writeImage(default_image, MainActivity.DEFAULT_IMAGE_PATH);
		ArrayList<Section> initial_sections = new ArrayList<>();
		int n_sections = StartupActivity.initial_section_titles.size();
		Section section;
		String section_title;
		ArrayList<Data> data;
		ArrayList<String> data_fields;
		
		for(int i=0; i<n_sections; i++){
			section_title = StartupActivity.initial_section_titles.get(i);
			data_fields = StartupActivity.initial_section_fields.get(i);
			data = new ArrayList<>();
			
			for (String data_field : data_fields){
				data.add(new Data(data_field,""));
			}
			
			initial_sections.add(new Section(section_title, data, this));
		}
		
		
		this.user = new User(MainActivity.DEFAULT_USERNAME, MainActivity.DEFAULT_IMAGE_PATH, initial_sections,this);
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
		public static StartupActivity activity;
		
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
					
					RecyclerView sections_recycler = root_view.findViewById(R.id.form_sections);
					
					sections_recycler.setAdapter(activity.user.getFormSections());
					sections_recycler.setLayoutManager(new LinearLayoutManager(activity));
					
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
	
	public void openNewSectionDialog(){
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		builder.setIcon(R.drawable.new_list_icon);
		builder.setTitle("New section");
		
		View dialog_view = this.getLayoutInflater().inflate(R.layout.input_dialog, null);
		final EditText input = dialog_view.findViewById(R.id.input);
		input.setHint("e.g. Education");
		
		builder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String section_name = input.getText().toString();
				StartupActivity.this.user.addFormSection(section_name);
			}
		});
		
		builder.setNegativeButton("EXIT", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		
		builder.setView(dialog_view);
		AlertDialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}
	
	private final static ArrayList<String> initial_section_titles = new ArrayList<>(Arrays.asList(
			"Work",
			"Contact",
			"Social Media",
			"Address"
	));
	
	private final static ArrayList<String> initial_work_fields = new ArrayList<>(Arrays.asList(
			"Job Position",
			"Company Name",
			"Headquarters"
	));
	
	private final static ArrayList<String> initial_contact_fields = new ArrayList<>(Arrays.asList(
			"Phone Number",
			"Home Number",
			"Email"
	));
	
	private final static ArrayList<String> initial_social_fields = new ArrayList<>(Arrays.asList(
			"LinkedIn",
			"Facebook",
			"Twitter",
			"Telegram",
			"Github",
			"Instagram"
	));
	
	private final static ArrayList<String> initial_address_fields = new ArrayList<>(Arrays.asList(
			"Country",
			"City",
			"Postal Code",
			"Street",
			"Number",
			"Flat ID"
	));
	
	private final static ArrayList<ArrayList<String>> initial_section_fields = new ArrayList<>(Arrays.asList(
			initial_work_fields,
			initial_contact_fields,
			initial_social_fields,
			initial_address_fields
	));
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
