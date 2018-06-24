package com.faridarbai.tapexchange.graphical;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.faridarbai.tapexchange.graphical.Data;
import com.faridarbai.tapexchange.graphical.DataViewAdapter;

import java.util.ArrayList;

public class Section {
	private String title;
	private DataViewAdapter data_adapter;
	
	public Section(String title, ArrayList<Data> data_list, Context context) {
		this.data_adapter = new DataViewAdapter(context, data_list);
		this.setTitle(title);
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public ArrayList<Data> getDataList(){
		return this.data_adapter.getDataList();
	}
	
	public void inflateDataIntoRecycler(RecyclerView data_view, Context context) {
		data_view.setAdapter(this.data_adapter);
		data_view.setLayoutManager(new LinearLayoutManager(context));
	}
	
	public boolean matchesTitle(String section_title) {
		boolean same_title;
		same_title = (this.title == section_title);
		
		return same_title;
	}
	
	public void appendData(String type, String value){
		Data new_data = new Data(type, value);
		this.data_adapter.add(new_data);
		
		int count = this.data_adapter.getItemCount();
		
		data_adapter.notifyItemInserted(count-1);
	}
}
