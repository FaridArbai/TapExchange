package com.faridarbai.tapexchange.serialization;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;

import com.faridarbai.tapexchange.MainActivity;
import com.faridarbai.tapexchange.users.Person;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class ProtocolMessage implements Serializable{
	PersonData person;
	String image_b64;
	
	public ProtocolMessage(Person person){
		this.person = new PersonData(person);
		String image_path = person.getImagePath();
		Bitmap image = ProtocolMessage.loadImage(image_path);
		String image_b64 = ProtocolMessage.imageToBase64(image);
		this.image_b64 = image_b64;
	}
	
	
	public byte[] toByteArray(){
		byte[] payload = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = null;
		
		try{
			out = new ObjectOutputStream(bos);
			out.writeObject(this);
			out.flush();
			payload = bos.toByteArray();
		}catch(IOException ex){
			ex.printStackTrace();
		}finally{
			try{
				bos.close();
			}catch(IOException ex){
				ex.printStackTrace();
			}
		}
		
		return payload;
	}
	
	
	static public Person fromByteArray(byte[] payload, MainActivity activity){
		ByteArrayInputStream bis = new ByteArrayInputStream(payload);
		ObjectInput in = null;
		Person person= null;
		ProtocolMessage pm = null;
		
		try{
			in = new ObjectInputStream(bis);
			Object o = in.readObject();
			
			pm = (ProtocolMessage)o;
			person = pm.toPerson(activity);
		}catch(Exception ex){
			ex.printStackTrace();
		}finally {
			try{
				if(in!=null){
					in.close();
				}
			}catch(IOException ex){
				ex.printStackTrace();
			}
		}
		
		return person;
	}
	
	
	public Person toPerson(MainActivity activity){
		Person person = new Person(this.person, activity);
		Bitmap image = ProtocolMessage.base64ToImage(this.image_b64);
		String image_folder = activity.FILES_PATH;
		String image_name = new String(Long.toHexString(Double.doubleToLongBits(Math.random())));
		String image_path = String.format("%s/%s", image_folder, image_name);
		
		Person.writeImage(image, image_path);
		person.setImagePath(image_path);
		
		return person;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static Bitmap loadImage(String image_path){
		Bitmap image = null;
		
		try{
			File file = new File(image_path);
			BitmapFactory.Options options = new BitmapFactory.Options();
			image = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		return image;
	}
	
	public static String imageToBase64(Bitmap image){
		String encoded;
		
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
		byte[] byteArray = byteArrayOutputStream .toByteArray();
		encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
		
		return encoded;
	}
	
	public static Bitmap base64ToImage(String image_b64){
		Bitmap image;
		byte[] decoded = Base64.decode(image_b64, Base64.DEFAULT);
		image = BitmapFactory.decodeByteArray(decoded, 0, decoded.length);
		
		return image;
	}
	
	
	
	
	
	
	
	
	
	
	
}
