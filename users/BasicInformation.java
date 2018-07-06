package com.faridarbai.tapexchange.users;

import java.io.Serializable;

public class BasicInformation implements Serializable {
	private String job;
	private String company;
	private String city;
	private String country;
	
	public BasicInformation(){
		this.setJob("");
		this.setCompany("");
		this.setCity("");
		this.setCountry("");
	}
	
	public String getJob() {
		return job;
	}
	
	public String getCompany() {
		return company;
	}
	
	public void setCompany(String company) {
		this.company = company;
	}
	
	public String getCity() {
		return city;
	}
	
	public void setCity(String city) {
		this.city = city;
	}
	
	public String getCountry() {
		return country;
	}
	
	public void setCountry(String country) {
		this.country = country;
	}
	
	
	public void setJob(String job) {
		this.job = job;
	}
}
