package com.faridarbai.tapexchange.profiles;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
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

import com.faridarbai.tapexchange.MainActivity;
import com.faridarbai.tapexchange.R;
import com.faridarbai.tapexchange.serialization.PersonData;
import com.faridarbai.tapexchange.users.Person;
import com.faridarbai.tapexchange.users.User;
import com.faridarbai.tapexchange.serialization.UserData;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URI;

public class UserProfile extends PersonalProfile{
	public int PICK_IMAGE = 100;
	public static int REQUEST_CODE = 101;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		configureDialog();
	}
	
	private void configureDialog(){
		FloatingActionButton section_selection_button = (FloatingActionButton) findViewById(R.id.section_selection_button);
		
		section_selection_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				AlertDialog.Builder builder =
						new AlertDialog.Builder(UserProfile.this);
				
				View dialog_view = getLayoutInflater().inflate(R.layout.fill_data_dialog, null);
				addSectionSpinnerListener(dialog_view);
				addTypeSpinnerListener(dialog_view);
				
				final EditText data_input = (EditText) dialog_view.findViewById(R.id.entry_data);
				final Spinner section_spinner = (Spinner) dialog_view.findViewById(R.id.section_spinner);
				final Spinner type_spinner = (Spinner) dialog_view.findViewById(R.id.type_spinner);
				
				final Button done_button = (Button) dialog_view.findViewById(R.id.dialog_done_button);
				final Button back_button = (Button) dialog_view.findViewById(R.id.dialog_back_button);
				
				builder.setView(dialog_view);
				final AlertDialog dialog = builder.create();
				dialog.show();
				
				back_button.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						dialog.cancel();
					}
				});
				
				done_button.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view){
						String section = section_spinner.getSelectedItem().toString();
						String type = type_spinner.getSelectedItem().toString();
						String data = data_input.getText().toString();
						
						UserProfile.this.user.addEntry(section, type, data);
						
						String notification_message = String.format("%s from %s successfully added",type, section);
						
						Toast notification = Toast.makeText(UserProfile.this,
								notification_message, Toast.LENGTH_SHORT);
						
						notification.setGravity(Gravity.TOP| Gravity.CENTER_HORIZONTAL, 0, 0);
						notification.show();
					}
				});
				
			}
		});
	}
	
	private void addSectionSpinnerListener(View view){
		final Spinner section_spinner = view.findViewById(R.id.section_spinner);
		final Spinner type_spinner = view.findViewById(R.id.type_spinner);
		
		section_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
			@Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					String selected_item = section_spinner.getSelectedItem().toString();
					int new_array_id = 0;
					
					if(selected_item!="Custom") {
						switch (selected_item) {
							case "Work": {
								new_array_id = R.array.work_section_array;
							}
							break;
							case "Contact": {
								new_array_id = R.array.contact_section_array;
							}
							break;
							case "Social Media": {
								new_array_id = R.array.social_media_section_array;
							}
							break;
							case "Address": {
								new_array_id = R.array.address_section_array;
							}
							break;
							default: {
							}
							break;
						}
						
						ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(arg1.getContext(),
								new_array_id, android.R.layout.simple_spinner_item);
						// Specify the layout to use when the list of choices appears
						adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						// Apply the adapter to the spinner
						
						type_spinner.setAdapter(adapter);
					}
					else{
						//delete the spinner and enable the text edit
					}
				}
				
				@Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
		});
	}
	
	private void addTypeSpinnerListener(View view){
		final Spinner type_spinner = view.findViewById(R.id.type_spinner);
		
		type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
			@Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					String selected_item = type_spinner.getSelectedItem().toString();
					
					if(selected_item!="Other") {
						//enable the edit
					}
				}
				
				@Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		getMenuInflater().inflate(R.menu.menu_user, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		boolean caught = true;
		boolean result;
		
		switch(id){
			case R.id.action_change_description:{
			
			}break;
			
			case R.id.action_change_image:{
				changeImage();
			}break;
			
			case R.id.action_back:{
				closeActivity();
			}break;
			
			case android.R.id.home:{
				closeActivity();
			}break;
			
			default:{
				caught = false;
			}break;
		}
		
		Log.d(TAG, "onOptionsItemSelected: " + id);
		
		if(caught){
			result = true;
		}
		else{
			result = super.onOptionsItemSelected(item);
		}
		
		return result;
	}
	
	void changeImage(){
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (requestCode == PICK_IMAGE) {
		 	Uri uri = data.getData();
		 	
		 	try {
				Bitmap image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
				int height = image.getHeight();
				int width = image.getWidth();
				int size = 0;
				int padx = 0;
				int pady = 0;
				
				if(height<width){
					size = height;
					padx = (width-height)/2;
					pady = 0;
				}
				else{
					size = width;
					padx = 0;
					pady = (height-width)/2;
				}
				
				image = Bitmap.createBitmap(image, padx,pady,size, size);
				
				int root_width = this.getWindow().getDecorView().getWidth()/4;
				
				image = Bitmap.createScaledBitmap(image, root_width, root_width, true);
				
				image = this.compress(image);
				
				String image_folder = MainActivity.FILES_PATH;
				String image_name = "user"; //new String(Long.toHexString(Double.doubleToLongBits(Math.random())));
				String image_path = String.format("%s/%s", image_folder, image_name);
				
				Person.writeImage(image, image_path);
				
				this.user.setImagePath(image_path);
				
				updateAvatar();
				
			}catch(Exception ex){
		 		ex.printStackTrace();
			}
			
		}
	}
	
	
	private Bitmap compress(Bitmap orig){
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		orig.compress(Bitmap.CompressFormat.JPEG, 40, out);
		ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		Bitmap compressed = BitmapFactory.decodeStream(in);
		
		return compressed;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
