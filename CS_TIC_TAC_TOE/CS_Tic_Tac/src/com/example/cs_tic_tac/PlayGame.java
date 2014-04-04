package com.example.cs_tic_tac;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PlayGame extends Activity {
	EditText xVal;
	EditText yVal;
	TextView board;
	public static String TAG_X = "X";
	Game game;
	public static String TAG_Y = "Y";
	
	
	private Handler handler=new Handler() {
	    @Override
	  public void handleMessage(Message msg) {	      
	      Toast.makeText(getApplicationContext(), "Download complete!",Toast.LENGTH_LONG)
	        .show();
	      Bundle b = msg.getData();
	      String payload = (String)b.get("msg");
	      Log.d("HelloWorld", payload);
	      if(payload.startsWith("MOVE")){
	    	  ListUsers.turn = true;
	    	  String rest = payload.substring("MOVE".length() + 1).trim();
	    	  String x = rest.substring(0, rest.indexOf(" "));
	    	  String y = rest.substring(rest.indexOf(" "));
	    	  final String other = rest.substring(rest.indexOf(" ")+ 3).trim();
	    	  
	    	  runOnUiThread(new Runnable() {
					public void run() {
			    	  	board.setText(other);
					}
	    	  });
	      } else if(payload.startsWith("GAME WON")){
	    	  runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(getApplicationContext(), "You won!", Toast.LENGTH_LONG).show();
					}
	    	  });
	    	  onDestroy();
	      } else if(payload.startsWith("FORFEIT")){
	    	  runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(getApplicationContext(), "Your opponent forfeited.", Toast.LENGTH_LONG).show();
					}
	    	  });
	    	  onDestroy();
	      } else if(payload.startsWith("GAME LOST")){
	    	  runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(getApplicationContext(), "You lost...", Toast.LENGTH_LONG).show();
					}
	    	  });
	    	  onDestroy();
	      } else if(payload.startsWith("GAME OVER")){
	    	  runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(getApplicationContext(), "Tie game... try harder", Toast.LENGTH_LONG).show();
					}
	    	  });
	    	  onDestroy();
	      } else if(payload.startsWith("INVALID")){
	    	  ListUsers.turn = true;
	    	  runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(getApplicationContext(), "That move doesn't work try again.", Toast.LENGTH_LONG).show();
					}
	    	  });
	      }
	      
	    	  
	    }
	  };
	  
	public void onDestroy(){
	  super.onDestroy();
  	  finish();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play_game);
		board = (TextView)findViewById(R.id.board);
		xVal = (EditText)findViewById(R.id.xcoor);
		yVal = (EditText)findViewById(R.id.ycoor);
		
		
		if(!ListUsers.turn){
			Intent i = new Intent(this, WorkerService.class);
			i.putExtra(WorkerService.MESSENGER, new Messenger(handler));
			i.putExtra(TAG_X, "no");
			i.putExtra(TAG_Y, "no");
			startService(i);
			Toast.makeText(getApplicationContext(), "It is your opponent's turn", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(getApplicationContext(), "It is your turn first", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void onStop(){
		super.onStop();
		new Thread(new Runnable(){
			public void run(){
				DatagramSocket socket = null;
				// send the packet through the socket to the server
				try {
					socket = new DatagramSocket();
					String command = null;
					command = MainActivity.user + " STOP";
					DatagramPacket txPacket = new DatagramPacket(command.getBytes(), command.length(), MainActivity.serverSocketAddress);
					socket.send(txPacket);
				} catch (IOException e){
					e.printStackTrace();
				}	
			}
		}).start();
		onDestroy();
		
	}
	
	public void sendMove(View v){
		if(ListUsers.turn){
			String x = xVal.getText().toString();
			String y = yVal.getText().toString();
			int xVal = Integer.parseInt(x);
			int yVal = Integer.parseInt(y);
			if(xVal < 0 || xVal > 2 || yVal < 0 || yVal > 2){
				Toast.makeText(getApplicationContext(), "Invalid, try again (0-2)(0-2)", Toast.LENGTH_LONG).show();
			} else {
				Intent i = new Intent(this, WorkerService.class);
				i.putExtra(WorkerService.MESSENGER, new Messenger(handler));
				i.putExtra(TAG_X, x);
				i.putExtra(TAG_Y, y);
				Log.d("Hello", "What");
				startService(i);
			}
			//
		} else {
			Toast.makeText(getApplicationContext(), "It's not your turn", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.play_game, menu);
		return true;
	}

}
