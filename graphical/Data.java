package com.faridarbai.tapexchange.graphical;

import android.app.Notification;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.Serializable;

public class Data implements Serializable{
	private String data_field;
	private String data_value;
	
	public Data(String data_field, String data_value) {
		this.data_field = data_field;
		this.data_value = data_value;
	}
	
	public Data(String data_field, String data_value, Resources resources) {
		this.data_field = data_field;
		this.data_value = data_value;
		
	}
	
	
	public String getDataField() {
		return data_field;
	}
	
	public void setDataField(String data_field) {
		this.data_field = data_field;
	}
	
	public String getDataValue() {
		return data_value;
	}
	
	public void setDataValue(String data_value) {
		this.data_value = data_value;
	}
}
