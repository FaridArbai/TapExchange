package com.faridarbai.tapexchange.graphical.form;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public class SectionForm {
	private String title;
	private DataFormAdapter fields_adapter;
	
	public SectionForm(String title, ArrayList<String> fields, Context context){
		this.fields_adapter = new DataFormAdapter(context, fields);
		this.setTitle(title);
	}
	
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public ArrayList<String> getFields(){
		return this.fields_adapter.getFields();
	}
	
	public void inflateFieldsIntoRecycler(RecyclerView fields_view, Context context){
		fields_view.setAdapter(this.fields_adapter);
		fields_view.setLayoutManager(new LinearLayoutManager(context));
	}
	
	public boolean matchesTitle(String section_title) {
		boolean same_title;
		same_title = (this.title.equals(section_title));
		
		return same_title;
	}
	
	public void addField(String name){
		this.fields_adapter.add(name);
		int n_fields = this.fields_adapter.getItemCount();
		this.fields_adapter.notifyItemInserted(n_fields+1);
	}
	
	
}
