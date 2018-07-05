package com.faridarbai.tapexchange;

import android.support.design.widget.FloatingActionButton;
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

public class StartupActivity extends AppCompatActivity {
	private static final String TAG = "StartupActivity";
	private static final int NUM_PAGES = 2;
	
	
	private SectionsPagerAdapter pager_adapter;
	private ViewPager view_pager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.startup_activity);
		
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		pager_adapter = new SectionsPagerAdapter(getSupportFragmentManager());
		
		// Set up the ViewPager with the sections adapter.
		view_pager = (ViewPager) findViewById(R.id.container);
		view_pager.setAdapter(pager_adapter);
		
		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				int current = view_pager.getCurrentItem();
				int next = current+1;
				
				if(next<StartupActivity.NUM_PAGES){
					view_pager.setCurrentItem(next);
				}
				else{
					//Save user and push main_activity
				}
			}
		});
	}
	
	@Override
	public void onBackPressed() {
		int current = view_pager.getCurrentItem();
		
		if(current!=0){
			this.view_pager.setCurrentItem(current-1);
		}
		else{
			super.onBackPressed();
		}
	}
	
	public static class PlaceholderFragment extends Fragment {
		private static final String ARG_SECTION_NUMBER = "section_number";
		
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
				case 1:{
					root_view = inflater.inflate(R.layout.welcome_fragment, container, false);
					break;
				}
				case 2:{
					root_view = inflater.inflate(R.layout.form_fragment, container, false);
					break;
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
			return 2;
		}
	}
}
