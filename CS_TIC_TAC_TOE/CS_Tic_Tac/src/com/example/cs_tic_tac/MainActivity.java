package com.example.cs_tic_tac;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

	private ProgressDialog pDialog;
	private final String serverAddress = "54.186.196.225";
	private final int serverPort = 20000;
	private final int MAX_PACKET_SIZE = 512;
	protected DatagramSocket socket;
	static String TAG_NAME = "NAME";
	//
	EditText userName;
	EditText password;
	public static String user;
	String pass;
	String payload;
	protected final String TAG_USERS = "users";
	public static InetSocketAddress serverSocketAddress;
	Boolean failed;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		failed = false;
		userName = (EditText) findViewById(R.id.username);
		password = (EditText) findViewById(R.id.password);
		serverSocketAddress = new InetSocketAddress(serverAddress, serverPort);
		try {
			socket = new DatagramSocket();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void newGame(View view){
		user = userName.getText().toString();
		pass = password.getText().toString();
		new ConnectToUser().execute();
		
	}
	public void send(String payload, InetAddress address, int port)throws IOException {
		DatagramPacket txPacket = new DatagramPacket(payload.getBytes(),
				payload.length(), address, port);
		socket.send(txPacket);
		System.out.println("Successful send");
	}
	class ConnectToUser extends AsyncTask<String, Void, String>{
		protected void onPreExecute(){
			pDialog = new ProgressDialog(MainActivity.this);
			pDialog.setMessage("Connecting to Server...");
			pDialog.show();
		}
		@Override
		protected String doInBackground(String... params) {
			try {
				String command = user + " REGISTER " + user + " " + pass;
				DatagramPacket txPacket = new DatagramPacket(command.getBytes(),
						command.length(), serverSocketAddress);
				// send the packet through the socket to the server
				try {
					socket.send(txPacket);
					Log.d("Hello", "packet sent");
					byte[] buf = new byte[MAX_PACKET_SIZE];
					DatagramPacket rxPacket = new DatagramPacket(buf, buf.length);
					// call receive (this will poulate the packet with the received
					// data, and the other endpoint's info)
					socket.receive(rxPacket);
					payload = new String(rxPacket.getData(), 0, rxPacket.getLength()).trim();
					if(payload.startsWith("Wrong") || payload.startsWith("You")){
						Toast.makeText(getApplicationContext(), "Wrong Password or You are already logged on", Toast.LENGTH_SHORT).show();
						failed = true;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
		protected void onPostExecute(String name){
			pDialog.dismiss();
			if(!failed){
				Intent i = new Intent(getApplicationContext(), ListUsers.class);
				startActivity(i);
			}
		}
	
}

}
