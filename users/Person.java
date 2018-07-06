package com.faridarbai.tapexchange.users;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;

import com.faridarbai.tapexchange.graphical.Section;
import com.faridarbai.tapexchange.graphical.SectionViewAdapter;
import com.faridarbai.tapexchange.serialization.PersonData;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class Person implements Serializable{
	private static final String TAG = "Person";
	public static int AVATAR_SIZE;
	public static int BACKGROUND_SIZE;
	public static int IMAGE_COMPRESSION_QUALITY = 60;
	
	private String name;
	private BasicInformation basic_information;
	private String image_path;
	private SectionViewAdapter sections;
	
	public Person(String default_name, String default_path, AppCompatActivity activity){
		this.setName(default_name);
		this.setImagePath(default_path);
		this.basic_information = new BasicInformation();
		
		ArrayList<Section> section_list = new ArrayList<>();
		this.sections = new SectionViewAdapter(activity, section_list);
	}
	
	public Person(PersonData data, AppCompatActivity activity){
		String username = data.getUsername();
		String image_path = data.getImagePath();
		BasicInformation basic_information = data.getBasicInformation();
		SectionViewAdapter adapter = data.getAdapter(activity);
		
		this.setName(username);
		this.setImagePath(image_path);
		this.setBasicInformation(basic_information);
		this.setSections(adapter);
	}
	
	public Person(Person person){
		this.name = person.name;
		this.image_path = person.image_path;
		this.basic_information = person.basic_information;
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
	
	public String getJob() {
		return basic_information.getJob();
	}
	
	public String getCompany() {
		return basic_information.getCompany();
	}
	
	public String getCity() {
		return basic_information.getCity();
	}
	
	public String getCountry() {
		return basic_information.getCountry();
	}
	
	public BasicInformation getBasicInformation(){
		return this.basic_information;
	}
	
	
	public void setCompany(String company){
		this.basic_information.setCompany(company);
	}
	
	public void setCity(String city) {
		this.basic_information.setCity(city);
	}
	
	public void setCountry(String country) {
		this.basic_information.setCountry(country);
	}
	
	public void setJob(String job) {
		this.basic_information.setJob(job);
	}
	
	public void setBasicInformation(BasicInformation basic_information){
		this.basic_information = basic_information;
	}
	
	public String getJobStatus(){
		String job_status;
		
		if(this.getCompany().equals("")){
			job_status = this.getJob();
		}
		else{
			job_status = String.format("%s, %s", this.getJob(), this.getCompany());
		}
		
		return job_status;
	}
	
	public String getLocationStatus(){
		String location_status = String.format("%s, %s", this.getCity(), this.getCountry());
		
		return location_status;
	}
	
	public static void writeImage(Bitmap image, String image_path){
		FileOutputStream out = null;
		
		try {
		 out = new FileOutputStream(image_path);
		 image.compress(Bitmap.CompressFormat.JPEG, 100, out);
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
	
	public static Bitmap compress(Bitmap orig){
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		orig.compress(Bitmap.CompressFormat.JPEG, Person.IMAGE_COMPRESSION_QUALITY, out);
		ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		Bitmap compressed = BitmapFactory.decodeStream(in);
		
		return compressed;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
