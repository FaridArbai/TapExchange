package com.faridarbai.tapexchange;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactsViewAdapter extends RecyclerView.Adapter<ContactsViewAdapter.ViewHolder>{
	private static final String TAG = "ContactsViewAdapter";
	
	private ArrayList<String> contact_names = new ArrayList<>();
	private Context context;
	
	public ContactsViewAdapter(Context context, ArrayList<String> contact_names) {
		this.contact_names = contact_names;
		this.context = context;
	}
	
	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contacts_item, parent, false);
		ViewHolder holder = new ViewHolder(view);
		
		return holder;
	}
	
	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
		final String contact_name = this.contact_names.get(position);
		Log.d(TAG, "onBindViewHolder: Included item at position " + position);
		
		//put the whole object data into the item place
		holder.contact_name.setText(contact_name);
		
		holder.contact_item_layout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(TAG, "onClick: Clicked on" + contact_name);
				Toast.makeText(context, contact_name, Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	@Override
	public int getItemCount() {
		return contact_names.size();
	}
	
	public class ViewHolder extends RecyclerView.ViewHolder{
		CircleImageView contact_image;
		TextView contact_name;
		ConstraintLayout contact_item_layout;
		
		public ViewHolder(View itemView) {
			super(itemView);
			
			this.contact_image = itemView.findViewById(R.id.contact_image);
			this.contact_name = itemView.findViewById(R.id.contact_id);
			this.contact_item_layout = itemView.findViewById(R.id.contact_item_layout);
			
			
			
			
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
