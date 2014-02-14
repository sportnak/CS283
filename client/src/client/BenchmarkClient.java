package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class BenchmarkClient {
	public static void main(String[] args) {
		Socket s;
		
		try {
			s = new Socket("localhost", 1231);
			PrintStream w = null;
			final long startTime = System.currentTimeMillis();
			try {
				w = new PrintStream(s.getOutputStream());
				w.println("hello world \n"); //this will be returned in all cam
			} catch (IOException e) {
			}
			BufferedReader r = new BufferedReader(new InputStreamReader(s.getInputStream()));
			while(true){
				String line = r.readLine();
				w.close();
				if(line == null){
					break;
				}
				else{
					System.out.println("We have received " + line + " back in the client");
					System.out.println("After having sent it to the server.");
					break;
				}
				
			}
			final long endTime = System.currentTimeMillis();
			System.out.println("We took: " + (endTime - startTime) + " milli-seconds to send one message and get it back.");
			s.close();
		} catch (UnknownHostException e) {
		} catch (IOException e) {
		}
		try{
			final ServerSocket ss = new ServerSocket(1231);
			ss.close();
			while(true) {
				
				final Socket cs = ss.accept();
				WorkerThread.WorkerThread(cs);
				ss.close();
				break;
				
			}
		}catch(IOException e){
			
		}
	}
}
