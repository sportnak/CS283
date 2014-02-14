package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class SimpleServer {
	public static void main(String[] args) {
		try{
			ServerSocket ss = new ServerSocket(1231);
			System.out.println("ServerSocket Created");
			while(true) {
				Socket cs = ss.accept();
				System.out.println("Client connected");
				BufferedReader r = new BufferedReader(new InputStreamReader(cs.getInputStream()));
				String line = "hello";
				while(true){
					line = r.readLine();
					if(line == null){
						break;
					}
					else{
						System.out.println("Received: " + line);
						PrintStream out = new PrintStream(cs.getOutputStream());
						out.println(line.toUpperCase() +'\n' );
					}
					
				}
				System.out.println("Disconnected");
				cs.close();
				ss.close();
			}
		}
		catch(IOException e){
		}
		

	}
}
