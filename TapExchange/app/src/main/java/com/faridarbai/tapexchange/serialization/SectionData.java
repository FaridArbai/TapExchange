package com.faridarbai.tapexchange.serialization;

import android.support.v7.app.AppCompatActivity;

import com.faridarbai.tapexchange.graphical.Data;
import com.faridarbai.tapexchange.graphical.Section;

import java.io.Serializable;
import java.util.ArrayList;

public class SectionData implements Serializable{
	public String title;
	public ArrayList<Data> fields;
	
	public SectionData(Section section){
		this.title = section.getTitle();
		fields = section.getDataList();
	}
	
	public Section toSection(AppCompatActivity activity){
		Section section = new Section(title, fields, activity);
		return section;
	}
	
}
