package com.faridarbai.tapexchange.graphical.form;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.faridarbai.tapexchange.R;

import java.util.ArrayList;

public class DataFormAdapter  extends RecyclerView.Adapter<DataFormAdapter.ViewHolder>{
	private static final String TAG = "DataFormAdapter";
	
	private ArrayList<String> fields = new ArrayList<>();
	private Context context;
	
	public DataFormAdapter(Context context, ArrayList<String> fields) {
		this.fields = fields;
		this.context = context;
	}
	
	public void add(String data){
		this.fields.add(data);
	}
	
	public ArrayList<String> getFields() {
		return fields;
	}
	
	@NonNull
	@Override
	public DataFormAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.data_form_item, parent, false);
		DataFormAdapter.ViewHolder holder = new DataFormAdapter.ViewHolder(view);
		
		return holder;
	}
	
	@Override
	public void onBindViewHolder(@NonNull DataFormAdapter.ViewHolder holder, int position) {
		String  field_name = this.fields.get(position);
		
		holder.field.setHint(field_name);
	}
	
	@Override
	public int getItemCount() {
		return fields.size();
	}
	
	public class ViewHolder extends RecyclerView.ViewHolder{
		EditText field;
		TextInputLayout field_layout;
		
		public ViewHolder(View itemView) {
			super(itemView);
			
			this.field_layout = (TextInputLayout)itemView.findViewById(R.id.form_field_layout);
			this.field = (EditText)itemView.findViewById(R.id.form_field);
		}
	}
	
}