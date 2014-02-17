package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class WorkerThread extends Thread {
	
	Socket cs;
	
	public WorkerThread(Socket cs2){
		cs = cs2;
	}
	
	public void run() {
		try{
			System.out.println("Client connected");
			BufferedReader r = new BufferedReader(new InputStreamReader(cs.getInputStream()));
			String line;
			while(true){
				line = r.readLine();
			
				if(line == null){
					break;
				}
				else{
					PrintStream out = new PrintStream(cs.getOutputStream());
					Thread.sleep(10);
					out.println(line.toUpperCase() );
				}
			}
			System.out.println("Disconnected");
			//cs.close();
		} catch (IOException e){
			System.out.println("Hello");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
