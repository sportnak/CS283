package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class WorkerThread extends Thread {
	
	static Socket cs;
	
	public static void WorkerThread(Socket ds){
		cs = ds;
	}

	
	public void run() {
		try{
			System.out.println("Client connected");
			BufferedReader r = new BufferedReader(new InputStreamReader(cs.getInputStream()));
			String line;
			line = r.readLine();
			while(true){
				line = r.readLine();
				System.out.println("Received: " + line);
				if(line == null){
					break;
				}
			}
			System.out.println("Disconnected");
			cs.close();
		} catch (IOException e){
			System.out.println("Hello");
		}
	}
	

}
