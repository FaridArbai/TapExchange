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

public class ContactProfile extends PersonalProfile{
	public static int REQUEST_CODE = 102;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		deleteFloatingButton();
	}
	
	private void deleteFloatingButton() {
		FloatingActionButton section_selection_button = (FloatingActionButton) findViewById(R.id.section_selection_button);
		section_selection_button.hide();
	}
	
	
}
























































































































