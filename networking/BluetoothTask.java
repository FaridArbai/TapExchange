package com.faridarbai.tapexchange.networking;

public interface BluetoothTask extends Runnable{
	public enum Type{
		SERVER,
		CLIENT;
	};
	
	public Type getType();
}
