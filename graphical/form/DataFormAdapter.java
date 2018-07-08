package com.faridarbai.tapexchange.graphical.form;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputEditText;
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
import com.faridarbai.tapexchange.graphical.Data;

import java.util.ArrayList;

public class DataFormAdapter  extends RecyclerView.Adapter<DataFormAdapter.ViewHolder>{
	private static final String TAG = "DataFormAdapter";
	
	private ArrayList<Data> fields;
	private Context context;
	
	public DataFormAdapter(Context context, ArrayList<Data> fields) {
		this.fields = fields;
		this.context = context;
	}
	
	public void add(Data data){
		this.fields.add(data);
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
		final Data data = this.fields.get(position);
		String field_name = data.getDataField();
		String field_value = data.getDataValue();
		
		final TextInputEditText field = holder.field;
		TextInputLayout field_layout = holder.field_layout;
		
		field_layout.setHint(field_name);
		field.setText(field_value);
		
		holder.field.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				data.setDataValue(field.getText().toString());
			}
		});
	}
	
	@Override
	public int getItemCount() {
		return fields.size();
	}
	
	public class ViewHolder extends RecyclerView.ViewHolder{
		TextInputEditText field;
		TextInputLayout field_layout;
		
		public ViewHolder(View itemView) {
			super(itemView);
			
			this.field_layout = (TextInputLayout)itemView.findViewById(R.id.form_field_layout);
			this.field = (TextInputEditText)itemView.findViewById(R.id.form_field);
		}
	}
	
}