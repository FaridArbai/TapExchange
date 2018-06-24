package com.faridarbai.tapexchange.serialization;

import android.support.v7.app.AppCompatActivity;

import com.faridarbai.tapexchange.users.Person;
import com.faridarbai.tapexchange.graphical.Section;
import com.faridarbai.tapexchange.graphical.SectionViewAdapter;

import java.io.Serializable;
import java.util.ArrayList;

public class PersonData implements Serializable{
	private static final String TAG = "PersonData";
	String username;
	String image_path;
	ArrayList<SectionData> sections;
	
	public PersonData(Person person){
		this.username = person.getName();
		this.image_path = person.getImagePath();
		
		SectionViewAdapter sections = person.getSections();
		ArrayList<Section> section_list = sections.getSectionList();
		
		int n_sections = section_list.size();
		Section current_section = null;
		SectionData current_section_data = null;
		
		this.sections = new ArrayList<SectionData>();
		
		for(int i=0; i<n_sections; i++){
			current_section = section_list.get(i);
			current_section_data = new SectionData(current_section);
			this.sections.add(current_section_data);
		}
		
	}
	
	public String getUsername(){
		return username;
	}
	
	public String getImagePath(){
		return image_path;
	}
	
	public SectionViewAdapter getAdapter(AppCompatActivity activity){
		SectionViewAdapter adapter;
		ArrayList<Section> section_list = new ArrayList<>();
		Section current_section;
		SectionData current_section_data;
		int n_sections = this.sections.size();
		
		for(int i=0; i<n_sections; i++){
			current_section_data = this.sections.get(i);
			current_section = current_section_data.toSection(activity);
			section_list.add(current_section);
		}
		
		adapter = new SectionViewAdapter(activity, section_list);
		
		return adapter;
	}
	
	
	/*
	public Bitmap getAvatar(){
		Bitmap avatar;
		byte[] decodedString = Base64.decode(this.avatar64, Base64.DEFAULT);
		avatar = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
		
		return avatar;
	}
	
	public String toBase64(Bitmap bitmap){
		Log.d(TAG, "getAvatar: Se llega a la IMAGEN");
		String encoded;
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
		byte[] byteArray = byteArrayOutputStream .toByteArray();
		encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
		Log.d(TAG, "getAvatar: SE SALE DE LA IMAGEN");
		
		return encoded;
	}
	
	static public Bitmap getImageFromResource(int id, AppCompatActivity activity){
		Bitmap image;
		Resources resources = activity.getResources();
		image = BitmapFactory.decodeResource(resources, id);
		
		return image;
	}
	*/
}


































