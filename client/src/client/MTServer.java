package client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import client.WorkerThread;

public class MTServer {
	
	public void main(String[] args) {
		try{
			final ServerSocket ss = new ServerSocket(1238);
			ss.close();
			while(true) {
				
				final Socket cs = ss.accept();
				WorkerThread.WorkerThread(cs);
				ss.close();
				
			}
		}catch(IOException e){
			
		}
	}
}
