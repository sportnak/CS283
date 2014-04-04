package com.example.cs_tic_tac;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ListUsers extends ListActivity {
	public static Boolean turn;
	ArrayList<HashMap<String, String>> userMap = new ArrayList<HashMap<String, String>>();
	ListView lv;
	private ProgressDialog pDialog;
	private final String serverAddress = "54.186.196.225";
	private final int serverPort = 20000;
	private final int MAX_PACKET_SIZE = 512;
	String TAG_TAG = "TAG";
	static DatagramSocket socket;
	String payload;
	String oppName;
	Boolean RESPONSE_FLAG;
	public static InetSocketAddress serverSocketAddress;
	private Handler handler=new Handler() {
	    @Override
	  public void handleMessage(Message msg) {	      
	      Toast.makeText(getApplicationContext(), "Message!",Toast.LENGTH_LONG)
	        .show();
	      Bundle b = msg.getData();
	      String payload = (String)b.get("msg");
	      Log.d("HelloWorld", payload);
	      if(payload.startsWith("JOIN")){
	    	  final String rest = payload.substring("JOIN".length() + 1).trim();
	    	  runOnUiThread(new Runnable() {
					@SuppressLint("NewApi")
					public void run() {
						FragmentTransaction ft = getFragmentManager().beginTransaction();
						DialogFragment newFragment = new JoinRequest();
						Bundle bundle = new Bundle();
						bundle.putString("OPP" , rest);
						newFragment.setArguments(bundle);	
						newFragment.show(ft, "dialog");
					}
	    	  });
	    	  
	      } 
	      
	    	  
	    }
	  };
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_users);
		serverSocketAddress = MainActivity.serverSocketAddress;
		try {
			socket = new DatagramSocket();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		new LoadUsers().execute();
		final ListView lv = getListView();

		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				View v = parent.getChildAt(position);
				oppName = ((TextView) v.findViewById(R.id.listIt)).getText().toString();
				new StartGame().execute();
			}
		});
	}
	protected void onStop(){
		super.onStop();
	}
	
	protected void onRestart(){
		super.onRestart();
		new LoadUsers().execute();
	}
	
	public void onDestroy(){
		super.onDestroy();
		
		// send the packet through the socket to the server
		new Thread(new Runnable(){
			public void run(){
				try {
					String command = null;
					command = MainActivity.user + " UNREGISTER";
					DatagramPacket txPacket = new DatagramPacket(command.getBytes(), command.length(), serverSocketAddress);
					socket.send(txPacket);
				} catch (IOException e){
					e.printStackTrace();
				}
			}
		}).start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list_users, menu);
		return true;
	}
	
	class LoadUsers extends AsyncTask<String, Void, String>{
		protected void onPreExecute(){
			pDialog = new ProgressDialog(ListUsers.this);
			pDialog.setMessage("Getting Users...");
			pDialog.show();
		}
		@Override
		protected String doInBackground(String... params) {
			String rval = null;
			BufferedReader reader = null;
			
			// send the packet through the socket to the server
			try {
				String command = null;
				command = MainActivity.user + " LIST ";
				DatagramPacket txPacket = new DatagramPacket(command.getBytes(), command.length(), serverSocketAddress);
				socket.send(txPacket);
				byte[] buf = new byte[MAX_PACKET_SIZE];
				DatagramPacket rxPacket = new DatagramPacket(buf, buf.length);
				// call receive (this will poulate the packet with the received
				// data, and the other endpoint's info)
				socket.receive(rxPacket);
				payload = new String(rxPacket.getData(), 0, rxPacket.getLength())
				.trim();
				Log.d("Hello", payload);
				Scanner scanner = new Scanner(payload);
				while(scanner.hasNext()){
					HashMap<String, String> users = new HashMap<String, String>();
					users.put(TAG_TAG, scanner.next());
					userMap.add(users);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
		protected void onPostExecute(String name){
			pDialog.dismiss();
			runOnUiThread(new Runnable() {
				public void run() {
					ListAdapter adapter = new SimpleAdapter(
							ListUsers.this, userMap,
							R.layout.list_item1, new String[] { TAG_TAG},
							new int[] { R.id.listIt });
					// updating listview
					setListAdapter(adapter);
				}
			});
			startListening();
			
		}
	
	}
	
	public void startListening(){
		Intent i = new Intent(this, WorkerService.class);
		i.putExtra(WorkerService.MESSENGER, new Messenger(handler));
		i.putExtra(PlayGame.TAG_X, "no");
		i.putExtra(PlayGame.TAG_Y, "no");
		startService(i);
	}
	class StartGame extends AsyncTask<String, String, String> {

		protected void onPreExecute(){
			Intent i = new Intent(getApplicationContext(), WorkerService.class);
			stopService(i);
			pDialog = new ProgressDialog(ListUsers.this);
			pDialog.setMessage("Waiting on Response...");
			pDialog.show();
			RESPONSE_FLAG = false;
		}
		
		@Override
		protected String doInBackground(String... arg0) {
			String command = MainActivity.user + " JOIN " + oppName;
			Log.d("HelloWhy", command);
			try {
				DatagramPacket txPacket = new DatagramPacket(command.getBytes(), command.length(), serverSocketAddress);
				socket.send(txPacket);
				byte[] buf = new byte[MAX_PACKET_SIZE];
				DatagramPacket rxPacket = new DatagramPacket(buf, buf.length);
				socket.receive(rxPacket);
				String payload = new String(rxPacket.getData(), 0,
						rxPacket.getLength());
				if(payload.startsWith("FAIL")){
					RESPONSE_FLAG = false;
					return null;
				} else if(payload.startsWith("ACCEPT")){
					Log.d("Hello", "Made it here");
					RESPONSE_FLAG = true;
					return null;
				}
			} catch (SocketException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
		protected void onPostExecute(String name){
			pDialog.dismiss();
			if(RESPONSE_FLAG){
				Log.d("Hello", "Challenge was accepted");
				turn = true;
				Intent i = new Intent(getApplicationContext(), PlayGame.class);
				i.putExtra("OPP", oppName);
				startActivity(i);
			} else {
				new LoadUsers().execute();
			}
		}
		
	}


}
