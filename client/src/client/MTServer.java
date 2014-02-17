package client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import client.WorkerThread;

public class MTServer {
	
	public static void main(String[] args) {
		final ServerSocket ss;
		try{
			ss = new ServerSocket(1270);
			System.out.println("Server connected");
			while(true) {
				
				final Socket cs = ss.accept();
				new WorkerThread(cs).start();
			}

		}catch(IOException e){
		}
		
	}
}
