package com.faridarbai.tapexchange.users;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;

import com.faridarbai.tapexchange.graphical.Section;
import com.faridarbai.tapexchange.graphical.SectionViewAdapter;
import com.faridarbai.tapexchange.serialization.PersonData;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class Person implements Serializable{
	private static final String TAG = "Person";
	private String name;
	private String image_path;
	private SectionViewAdapter sections;
	
	public Person(String default_name, String default_path, AppCompatActivity activity){
		this.setName(default_name);
		this.setImagePath(default_path);
		
		ArrayList<Section> section_list = new ArrayList<>();
		this.sections = new SectionViewAdapter(activity, section_list);
	}
	
	public Person(PersonData data, AppCompatActivity activity){
		String username = data.getUsername();
		String image_path = data.getImagePath();
		SectionViewAdapter adapter = data.getAdapter(activity);
		
		this.setName(username);
		this.setImagePath(image_path);
		this.setSections(adapter);
	}
	
	public Person(Person person){
		this.name = person.name;
		this.image_path = person.image_path;
		this.sections = person.sections;
	}
	
	public void refreshContext(AppCompatActivity activity){
		this.sections.refreshContext(activity);
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getImagePath(){
		return image_path;
	}
	
	public void setImagePath(String image_path) {
		this.image_path = image_path;
	}
	
	public SectionViewAdapter getSections() {
		return sections;
	}
	
	public void setSections(SectionViewAdapter sections) {
		this.sections = sections;
	}
	
	
	public static void writeImage(Bitmap image, String image_path){
		FileOutputStream out = null;
		
		try {
		 out = new FileOutputStream(image_path);
		 image.compress(Bitmap.CompressFormat.JPEG, 60, out);
		}catch (Exception e) {
		 	e.printStackTrace();
		}finally {
			 try{
			 	if(out != null){
						out.close();
			 	}
			 } catch (IOException e) {
				  e.printStackTrace();
			 }
		}
	}
	
	public void addEntry(String section_title, String type, String field){
		this.sections.addEntry(section_title, type, field);
	}
	
	public PersonData serialize(){
		PersonData data = new PersonData(this);
		return data;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
