package com.faridarbai.tapexchange.graphical.form;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.faridarbai.tapexchange.R;

import java.util.ArrayList;

import at.markushi.ui.CircleButton;

public class SectionFormAdapter extends RecyclerView.Adapter<SectionFormAdapter.ViewHolder>{
	private static final String TAG = "SectionFormAdapter";
	
	private ArrayList<SectionForm> sections;
	private Context context;
	
	private SectionFormAdapter(ArrayList<SectionForm> sections, Context context){
		this.sections = sections;
		this.context = context;
	}
	
	public ArrayList<SectionForm> getSections() {
		return sections;
	}
	
	public boolean addSection(String title){
		boolean found = false;
		boolean success;
		
		for(SectionForm section : this.sections){
			if(section.matchesTitle(title)){
				found = true;
			}
		}
		
		if(found){
			success = false;
		}
		else{
			success = true;
			ArrayList<String> fields = new ArrayList<>();
			SectionForm section = new SectionForm(title, fields, this.context);
			this.sections.add(section);
			int n_items = this.sections.size();
			this.notifyItemInserted(n_items-1);
		}
		
		return success;
	}
	
	
	public void addField(SectionForm section, String field_name){
		section.addField(field_name);
	}
	
	public void openNewFieldDialog(SectionForm section){
	
	}
	
	@NonNull
	@Override
	public SectionFormAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.section_form_item, parent, false);
		SectionFormAdapter.ViewHolder holder = new SectionFormAdapter.ViewHolder(view);
		
		return null;
	}
	
	@Override
	public void onBindViewHolder(@NonNull SectionFormAdapter.ViewHolder holder, int position) {
		final SectionForm section = this.sections.get(position);
		TextView title_view = holder.section_title;
		RecyclerView fields_view = holder.fields_view;
		CircleButton new_field_button = holder.new_field_button;
		
		title_view.setText(section.getTitle());
		section.inflateFieldsIntoRecycler(fields_view, this.context);
		
		new_field_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SectionFormAdapter.this.openNewFieldDialog(section);
			}
		});
	}
	
	@Override
	public int getItemCount() {
		return sections.size();
	}
	
	
	public class ViewHolder extends RecyclerView.ViewHolder{
		TextView section_title;
		RecyclerView fields_view;
		CircleButton new_field_button;
		
		public ViewHolder(View itemView) {
			super(itemView);
			
			this.section_title = (TextView)itemView.findViewById(R.id.form_section_title);
			this.fields_view = (RecyclerView) itemView.findViewById(R.id.form_section_fields);
			this.new_field_button = (CircleButton) itemView.findViewById(R.id.new_field_button);
		}
	}
	
	
}
