package bananabank.server;

import bananabank.client.BananaBankBenchmark;
import bananabank.server.ServerWorkerThread;
import bananabank.server.BananaBank;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	static Thread threads[] = new Thread[BananaBankBenchmark.WORKER_THREAD_NUM + 2];
	//we use 2 extra. one for the single client test and one for the shutdown.

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		final ServerSocket ss;
		BananaBank bb = new BananaBank("accounts.txt");
		ss = new ServerSocket(2000);
		int i = 0;
		try{
			System.out.println("Server connected");
			while(true) {
				final Socket cs = ss.accept();
				threads[i] = new ServerWorkerThread(cs, bb);
				threads[i].start();
				i++;
				if(i >= BananaBankBenchmark.WORKER_THREAD_NUM + 2){
					// if we've used up all the threads then the server host will close itself.
					i = 0;
					break;
				}
			}
		}catch(IOException e){
		}
		ss.close();
		System.out.println("We have closed the server");
		bb.save("myAccounts.txt");
		//need to save the bb after the threads have terminated.
		
	}

}
