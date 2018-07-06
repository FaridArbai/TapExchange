package com.faridarbai.tapexchange.users;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.faridarbai.tapexchange.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class Form {
	private static final String TAG = "Form";
	public static final int AVATAR_REQUEST_CODE = 4012;
	
	private View form_view;
	
	private static int NAME_ID = R.id.form_name_edittext;
	private static int JOB_ID = R.id.form_job_edittext;
	private static int COMPANY_ID = R.id.form_company_edittext;
	private static int CITY_ID = R.id.form_city_edittext;
	private static int COUNTRY_ID = R.id.form_country_edittext;
	
	
	public Form(View form_view){
		this.form_view = form_view;
	}
	
	public boolean mandatoryFieldsAreFilled(){
		boolean name_is_empty = this.isEmpty(NAME_ID);
		boolean job_is_empty = this.isEmpty(JOB_ID);
		boolean city_is_empty = this.isEmpty(CITY_ID);
		boolean country_is_empty = this.isEmpty(COUNTRY_ID);
		boolean filled = (!(name_is_empty || job_is_empty || city_is_empty || country_is_empty));
		
		return filled;
	}
	
	private boolean isEmpty(int id){
		View view = this.form_view.findViewById(id);
		boolean is_empty = ((EditText)view).getText().toString().equals("");
		return is_empty;
	}
	
	public void updateUser(User user){
		String name = ((EditText)this.form_view.findViewById(NAME_ID)).getText().toString();
		String job = ((EditText)this.form_view.findViewById(JOB_ID)).getText().toString();
		String company = ((EditText)this.form_view.findViewById(COMPANY_ID)).getText().toString();
		String city = ((EditText)this.form_view.findViewById(CITY_ID)).getText().toString();
		String country = ((EditText)this.form_view.findViewById(COUNTRY_ID)).getText().toString();
		
		Log.d(TAG, "updateUser: " + job);
		
		user.setName(name);
		user.setJob(job);
		user.setCompany(company);
		user.setCity(city);
		user.setCountry(country);
	}
	
	public static void openImageChooser(AppCompatActivity activity){
		Intent intent = new Intent();
		Intent chooser = Intent.createChooser(intent,"Select new image");
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		
		activity.startActivityForResult(chooser, Form.AVATAR_REQUEST_CODE);
	}
	
	public static void handleImageChooserCallback(Intent data, CircleImageView avatar_view, User user){
		
	
	
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
