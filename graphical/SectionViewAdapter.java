package com.faridarbai.tapexchange.graphical;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.faridarbai.tapexchange.R;

import java.util.ArrayList;

public class SectionViewAdapter extends RecyclerView.Adapter<SectionViewAdapter.ViewHolder>{
	private static final String TAG = "SectionViewAdapter";
	
	private ArrayList<Section> section_list;
	private Context context;
	
	public SectionViewAdapter(Context context, ArrayList<Section> section_list) {
		this.section_list = section_list;
		this.context = context;
	}
	
	public ArrayList<Section> getSectionList(){
		return section_list;
	}
	
	public void refreshContext(Context context){
		this.context = context;
	}
	
	public void addEntry(String section_title, String type, String field){
		int n_sections = section_list.size();
		boolean found = false;
		int pos;
		Section current_section = null;
		
		for(pos=0;((pos<n_sections)&&(!found)); pos++){
			current_section = this.section_list.get(pos);
			found = current_section.matchesTitle(section_title);
		}
		
		if(found){
			Log.d(TAG, "addEntry: FOUND IT");
			current_section.appendData(type, field);
		}
		else{
			Log.d(TAG, "addEntry: DIDNT FOUND IT");
			ArrayList<Data> new_data_list = new ArrayList<>();
			new_data_list.add(new Data(type, field));
			Section section = new Section(section_title, new_data_list, this.context);
			section_list.add(section);
			
			this.notifyDataSetChanged();
		}
		
	}
	
	
	@NonNull
	@Override
	public SectionViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.section_item, parent, false);
		SectionViewAdapter.ViewHolder holder = new SectionViewAdapter.ViewHolder(view);
		
		return holder;
	}
	
	@Override
	public void onBindViewHolder(@NonNull SectionViewAdapter.ViewHolder holder, int position) {
		Section section = this.section_list.get(position);
		final String section_title = section.getTitle();
		
		Log.d(TAG, "onBindViewHolder: Included item at position " + position);
		
		//put the whole object data into the item place
		holder.section_title.setText(section_title);
		
		section.inflateDataIntoRecycler(holder.section_data_view, context);
		
	}
	
	@Override
	public int getItemCount() {
		return section_list.size();
	}
	
	public class ViewHolder extends RecyclerView.ViewHolder{
		TextView section_title;
		RecyclerView section_data_view;
		ConstraintLayout section_item_layout;
		
		public ViewHolder(View itemView) {
			super(itemView);
			
			this.section_title = itemView.findViewById(R.id.section_title);
			this.section_data_view = itemView.findViewById(R.id.section_data_view);
			this.section_item_layout = itemView.findViewById(R.id.section_item_layout);
		}
	}
	
}
