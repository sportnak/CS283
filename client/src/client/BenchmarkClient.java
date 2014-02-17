package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class BenchmarkClient {
	public static void main(String[] args) throws UnknownHostException, IOException {
		final long startTime = System.currentTimeMillis();
		final Socket s = new Socket("localhost", 1270);
		int users = 0;
		while(System.currentTimeMillis() < startTime + 500){
			for(int i = 0; i < 2; i++){
				new Thread(new Runnable(){
					public void run(){
						int users = 0;
						try {
							PrintStream w = null;
							try {
								w = new PrintStream(s.getOutputStream());
								w.println("hello world"); //this will be returned in all cam
							} catch (IOException e) {
							}
							BufferedReader r = new BufferedReader(new InputStreamReader(s.getInputStream()));
							while(true){
								String line = r.readLine();
								if(line == null){
									break;
								}
								else{
									System.out.println("We have received " + line + " back in the client");
									System.out.println("After having sent it to the server.");
									break;
								}
							}

							
						
						} catch (UnknownHostException e) {
							System.out.println("uhe");
						} catch (IOException e) {
							System.out.println("ioe");
						}
	
					}
				
				}).start();
				users++;
			}
		}
		//s.close();
	}
}
