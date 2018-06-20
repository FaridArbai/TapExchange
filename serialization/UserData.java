package com.faridarbai.tapexchange.serialization;

import com.faridarbai.tapexchange.users.Person;
import com.faridarbai.tapexchange.users.User;

import java.io.Serializable;
import java.util.ArrayList;

public class UserData extends PersonData implements Serializable{
	PersonData personal_data;
	ArrayList<PersonData> contacts_data;
	
	public UserData(User user){
		super((Person)user);
		
		ArrayList<Person> contacts = user.getContacts();
		int n_contacts = contacts.size();
		Person current_person;
		PersonData current_data;
		this.contacts_data = new ArrayList<>();
		
		for(int i=0; i<n_contacts; i++){
			current_person = contacts.get(i);
			current_data = new PersonData(current_person);
			contacts_data.add(current_data);
		}
	}
	
	public ArrayList<PersonData> getContactsData(){
		return this.contacts_data;
	}
	
	
}
