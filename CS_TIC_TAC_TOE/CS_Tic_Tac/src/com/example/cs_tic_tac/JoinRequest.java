package com.example.cs_tic_tac;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;


@SuppressLint("NewApi")
public class JoinRequest extends DialogFragment {
	
	final int REQUEST_CHECK_IN = 1;
	
	public Dialog onCreateDialog(Bundle SavedInstance){
		final Bundle bundle = this.getArguments();
		final String opponent = bundle.getString("OPP");
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage(opponent + " has challenged you! Respond in the next 10 seconds...")
			.setPositiveButton("Accept", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int id){
					//accept the challenge
					new Thread(new Runnable() {
						public void run() {
							String rval = null;
							DatagramSocket socket = null;
							BufferedReader reader = null;
							
							// send the packet through the socket to the server
							try {
								socket = new DatagramSocket();
								String command = null;
								command = MainActivity.user + " ACCEPT " + opponent;
								DatagramPacket txPacket = new DatagramPacket(command.getBytes(), command.length(), MainActivity.serverSocketAddress);
								socket.send(txPacket);
							}catch (IOException e){
								e.printStackTrace();
							}
							ListUsers.turn = false;
						}
					}).start();


					Log.d("Hello", "Challenge was accepted");
					Intent i = new Intent(getActivity(), PlayGame.class);
					i.putExtra("OPP", opponent);
					startActivity(i);
			    	  
					
				}
			})
			.setNegativeButton("Decline", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int id){
					//decline
				}
			});
		
		return builder.create();
	}
	
}

