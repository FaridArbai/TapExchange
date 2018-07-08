package com.faridarbai.tapexchange;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import com.faridarbai.tapexchange.serialization.PersonData;
import com.faridarbai.tapexchange.users.Form;
import com.faridarbai.tapexchange.users.Person;
import com.theartofdev.edmodo.cropper.CropImage;
import de.hdodenhof.circleimageview.CircleImageView;

public class FormActivity extends AppCompatActivity {
	private static final String TAG = "FormActivity";
	public static final int REQUEST_CODE = 6512;
	
	private Person user;
	private Form form;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.form_activity);
		
		//Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		//setSupportActionBar(toolbar);
		
		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.new_section_fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				FormActivity.this.openNewSectionDialog();
			}
		});
		
		this.getUserFromIntent();
		this.form = new Form(this.getWindow().getDecorView());
		this.form.initForm(this.user);
		this.configureLayout();
	}
	
	private void getUserFromIntent(){
		Intent intent = getIntent();
		
		PersonData person_data = (PersonData)intent.getSerializableExtra("PersonData");
		this.user = new Person(person_data,this);
	}
	
	private void closeActivity(){
		Intent result_intent = new Intent();
		result_intent.putExtra("PersonData", this.user.serialize());
		setResult(RESULT_OK, result_intent);
		finish();
	}
	
	@Override
	public void onBackPressed() {
		this.form.updateUser(user);
		closeActivity();
	}
	
	private void configureLayout(){
		CircleImageView avatar_view = this.findViewById(R.id.form_avatar);
		avatar_view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Form.openImageHandler(FormActivity.this);
			}
		});
		
		RecyclerView sections_view = this.findViewById(R.id.form_sections);
		sections_view.setAdapter(this.user.getFormSections());
		sections_view.setLayoutManager(new LinearLayoutManager(this));
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
				FormActivity.this.user.addFormSection(section_name);
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
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
