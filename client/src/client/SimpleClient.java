package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class SimpleClient {
	public static void main(String[] args) {
		Socket s;
		
		try {
			s = new Socket("localhost", 1270);
			PrintStream w = null;
			try {
				w = new PrintStream(s.getOutputStream());
				w.println("hello world"); //this will be returned in all cam
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

			s.close();
		} catch (UnknownHostException e) {
		} catch (IOException e) {
		}
	}
}
