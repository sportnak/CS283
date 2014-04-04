package com.example.cs_tic_tac;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

public class WorkerService extends IntentService {
	public static final String MESSENGER = "messenger";
	
	public WorkerService(){
		super("WorkserService");
	}

	private final int MAX_PACKET_SIZE = 512;
	DatagramSocket socket;
	
	public void onHandleIntent(Intent intent){
		Log.d("Hello", "Let's start");
		Bundle extras = intent.getExtras();
		String x = intent.getStringExtra(PlayGame.TAG_X);
		String y = intent.getStringExtra(PlayGame.TAG_Y);
	    Messenger messenger=(Messenger)extras.get(MESSENGER);
	    Message msg=Message.obtain();

	    InetSocketAddress serverSocketAddress = MainActivity.serverSocketAddress;
	    msg.arg1=Activity.RESULT_OK;
	    try {
			socket = new DatagramSocket();
		} catch (SocketException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
		String myload = MainActivity.user + " SEND " + x + " " + y;
		Log.d("HelloWorld", myload);
		DatagramPacket txPacket;
		try {
			txPacket = new DatagramPacket(myload.getBytes(),
					myload.length(), serverSocketAddress);
			socket.send(txPacket);
		} catch (SocketException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ListUsers.turn = false;
		System.out.println("Successful send");
		byte[] buf = new byte[MAX_PACKET_SIZE];
		DatagramPacket rxPacket = new DatagramPacket(buf, buf.length);
		try {
			socket.receive(rxPacket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String payload = new String(rxPacket.getData(), 0,
				rxPacket.getLength());
		Log.d("HelloWelp", payload);
		try {
			Bundle b = new Bundle();
			b.putString("msg", payload);
			msg.setData(b);
	        messenger.send(msg);
	    }
	    catch (android.os.RemoteException e1) {

	    }
	
	}
	
	public void onDestroy(){
		
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
