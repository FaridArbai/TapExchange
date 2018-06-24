package com.faridarbai.tapexchange.users;

import android.support.v7.app.AppCompatActivity;

import com.faridarbai.tapexchange.serialization.PersonData;
import com.faridarbai.tapexchange.serialization.UserData;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class User extends Person {
	ArrayList<Person> contacts;
	
	public User(String default_name, String default_path, AppCompatActivity activity){
		super(default_name, default_path, activity);
		this.contacts = new ArrayList<>();
	}
	
	public User(UserData user_data, AppCompatActivity activity){
		super((PersonData)user_data, activity);
		this.contacts = new ArrayList<>();
		ArrayList<PersonData> contacts_data = user_data.getContactsData();
		int n_contacts = contacts_data.size();
		PersonData current_contact_data;
		Person current_contact;
		
		for(int i=0; i<n_contacts; i++){
			current_contact_data = contacts_data.get(i);
			current_contact = new Person(current_contact_data,activity);
			this.contacts.add(current_contact);
		}
	}
	
	private User(Person person){
		super(person);
	}
	
	public ArrayList<Person> getContacts(){
		return contacts;
	}
	
	public void addContact(Person person){
		this.contacts.add(person);
	}
	
	
	public UserData serialize(){
		UserData data = new UserData(this);
		return data;
	}
	
	public void save(String user_path){
		FileOutputStream out = null;
		ObjectOutputStream ostream = null;
		
		try{
			out = new FileOutputStream(user_path);
			ostream = new ObjectOutputStream(out);
			ostream.writeObject(this.serialize());
			
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			try{
				if(out!=null){
					out.close();
					ostream.close();
				}
			}catch(IOException ex){
				ex.printStackTrace();
			}
		}
	}
	
	static public User load(String user_path, AppCompatActivity activity){
		FileInputStream in = null;
		ObjectInputStream istream = null;
		UserData data = null;
		User user = null;
		
		try{
			in = new FileInputStream(user_path);
			istream = new ObjectInputStream(in);
			data = (UserData)istream.readObject();
			user = new User(data, activity);
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			try{
				if(in!=null){
					in.close();
					istream.close();
				}
			}catch(IOException ex){
				ex.printStackTrace();
			}
		}
		
		return user;
	}
	
	
	public static User merge(Person person, User user){
		User merged = new User(person);
		merged.contacts = user.contacts;
		
		return merged;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
