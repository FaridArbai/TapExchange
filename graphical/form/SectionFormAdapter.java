package com.faridarbai.tapexchange.graphical.form;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.faridarbai.tapexchange.R;
import com.faridarbai.tapexchange.graphical.Data;
import com.faridarbai.tapexchange.graphical.Section;

import java.lang.reflect.Array;
import java.util.ArrayList;

import at.markushi.ui.CircleButton;

public class SectionFormAdapter extends RecyclerView.Adapter<SectionFormAdapter.ViewHolder>{
	private static final String TAG = "SectionFormAdapter";
	
	private ArrayList<Section> sections;
	private AppCompatActivity context;
	
	public SectionFormAdapter(AppCompatActivity context, ArrayList<Section> sections){
		this.sections = sections;
		this.context = context;
	}
	
	public ArrayList<Section> getSections() {
		return sections;
	}
	
	public boolean addSection(String title){
		boolean found = false;
		boolean added;
		
		for(Section section : this.sections){
			if(section.matchesTitle(title)){
				found = true;
			}
		}
		
		if(found){
			added = false;
		}
		else{
			added = true;
			ArrayList<Data> fields = new ArrayList<>();
			
			Section section = new Section(title, fields, this.context);
			this.sections.add(section);
			int n_sections = this.getItemCount();
			this.notifyItemInserted(n_sections-1);
		}
		
		return added;
	}
	
	public void addField(Section section, String field_name){
		section.appendFormData(field_name,"");
	}
	
	public void openNewFieldDialog(final Section section){
		final AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
		
		builder.setIcon(R.drawable.new_list_icon);
		builder.setTitle(String.format("New %s field", section.getTitle()));
		
		View dialog_view = this.context.getLayoutInflater().inflate(R.layout.input_dialog, null);
		final EditText input = dialog_view.findViewById(R.id.input);
		
		builder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String field_name = input.getText().toString();
				section.appendFormData(field_name, "");
			}
		});
		
		builder.setNegativeButton("EXIT", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		
		builder.setView(dialog_view);
		AlertDialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}
	
	@NonNull
	@Override
	public SectionFormAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.section_form_item, parent, false);
		SectionFormAdapter.ViewHolder holder = new SectionFormAdapter.ViewHolder(view);
		
		return holder;
	}
	
	@Override
	public void onBindViewHolder(@NonNull SectionFormAdapter.ViewHolder holder, int position) {
		final Section section = this.sections.get(position);
		TextView title_view = holder.section_title;
		RecyclerView fields_view = holder.fields_view;
		Button new_field_button = holder.new_field_button;
		
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
		Button new_field_button;
		
		public ViewHolder(View itemView) {
			super(itemView);
			
			this.section_title = (TextView)itemView.findViewById(R.id.form_section_title);
			this.fields_view = (RecyclerView) itemView.findViewById(R.id.form_section_fields);
			this.new_field_button = (Button) itemView.findViewById(R.id.new_field_button);
		}
	}
	
	
}
