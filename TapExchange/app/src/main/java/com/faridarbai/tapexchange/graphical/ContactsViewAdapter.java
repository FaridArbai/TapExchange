package com.faridarbai.tapexchange.graphical;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.faridarbai.tapexchange.MainActivity;
import com.faridarbai.tapexchange.R;
import com.faridarbai.tapexchange.profiles.ContactProfile;
import com.faridarbai.tapexchange.users.Person;
import com.faridarbai.tapexchange.users.User;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactsViewAdapter extends RecyclerView.Adapter<ContactsViewAdapter.ViewHolder>{
	private static final String TAG = "ContactsViewAdapter";
	
	private User user;
	private Context context;
	private MainActivity main_activity;
	
	public ContactsViewAdapter(User user, MainActivity main_activity) {
		this.user = user;
		this.context = main_activity;
		this.main_activity = main_activity;
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
		final Person contact = this.user.getContacts().get(position);
		String contact_name = contact.getName();
		String image_path = contact.getImagePath();
		
		holder.contact_name.setText(contact_name);
		
		Bitmap image = BitmapFactory.decodeFile(contact.getImagePath());
		holder.contact_image.setImageBitmap(image);
		
		holder.contact_item_layout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				main_activity.openProfile(contact,ContactProfile.class, ContactProfile.REQUEST_CODE);
			}
		});
	}
	
	@Override
	public int getItemCount() {
		return this.user.getContacts().size();
	}
	
	public class ViewHolder extends RecyclerView.ViewHolder{
		CircleImageView contact_image;
		TextView contact_name;
		ConstraintLayout contact_item_layout;
		
		public ViewHolder(View itemView) {
			super(itemView);
			
			this.contact_image = (CircleImageView) itemView.findViewById(R.id.contact_image);
			this.contact_name = (TextView) itemView.findViewById(R.id.contact_id);
			this.contact_item_layout = (ConstraintLayout) itemView.findViewById(R.id.contact_item_layout);
			
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
