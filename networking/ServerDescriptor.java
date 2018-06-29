package com.faridarbai.tapexchange.networking;

import android.bluetooth.BluetoothAdapter;
import android.support.v7.app.AppCompatActivity;

import com.faridarbai.tapexchange.serialization.ProtocolMessage;
import com.faridarbai.tapexchange.users.Person;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.UUID;

public class ServerDescriptor implements Serializable{
	private String secret_uuid;
	private String name;
	private int payload_length;
	
	public ServerDescriptor(int payload_length){
		this.secret_uuid = UUID.randomUUID().toString();
		this.name = BluetoothAdapter.getDefaultAdapter().getName();
		this.payload_length = payload_length;
	}
	
	public String getSecretUUID(){
		return this.secret_uuid;
	}
	
	public String getName(){
		return this.name;
	}
	
	public int getPayloadLength(){
		return this.payload_length;
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
	
	static public ServerDescriptor fromByteArray(byte[] payload, AppCompatActivity activity){
		ByteArrayInputStream bis = new ByteArrayInputStream(payload);
		ObjectInput in = null;
		ServerDescriptor descriptor = null;
		
		try{
			in = new ObjectInputStream(bis);
			Object o = in.readObject();
			
			descriptor = (ServerDescriptor)o;
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
		
		return descriptor;
	}
}
