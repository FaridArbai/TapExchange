package com.faridarbai.tapexchange.graphical;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.faridarbai.tapexchange.R;

import java.util.ArrayList;

public class DataViewAdapter  extends RecyclerView.Adapter<DataViewAdapter.ViewHolder>{
	private static final String TAG = "DataViewAdapter";
	
	private ArrayList<Data> data_list = new ArrayList<>();
	private Context context;
	
	public DataViewAdapter(Context context, ArrayList<Data> data_list) {
		this.data_list = data_list;
		this.context = context;
	}
	
	public void add(Data data){
		this.data_list.add(data);
	}
	
	public ArrayList<Data> getDataList() {
		return data_list;
	}
	
	@NonNull
	@Override
	public DataViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.data_item, parent, false);
		DataViewAdapter.ViewHolder holder = new DataViewAdapter.ViewHolder(view);
		
		return holder;
	}
	
	@Override
	public void onBindViewHolder(@NonNull DataViewAdapter.ViewHolder holder, int position) {
		Data  data = this.data_list.get(position);
		final String data_field = data.getDataField();
		String data_value = data.getDataValue();
		
		Log.d(TAG, "onBindViewHolder: Included item at position " + position);
		
		//put the whole object data into the item place
		holder.data_field.setText(data_field);
		holder.data_value.setText(data_value);
		//holder.data_icon.setImageBitmap(data_icon);
	}
	
	@Override
	public int getItemCount() {
		return data_list.size();
	}
	
	public class ViewHolder extends RecyclerView.ViewHolder{
		ImageView data_icon;
		TextView data_field;
		TextView data_value;
		ConstraintLayout data_item_layout;
		
		public ViewHolder(View itemView) {
			super(itemView);
			
			this.data_icon = (ImageView) itemView.findViewById(R.id.data_icon);
			this.data_field = (TextView) itemView.findViewById(R.id.data_field);
			this.data_value = (TextView) itemView.findViewById(R.id.data_value);
			data_item_layout = (ConstraintLayout) itemView.findViewById(R.id.data_item_layout);
		}
	}
	
}
