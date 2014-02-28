package bananabank.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Collection;
import java.util.Iterator;

import bananabank.client.BananaBankBenchmark;

public class ServerWorkerThread extends Thread {

	Socket cs;
	BananaBank bb;
	
	public ServerWorkerThread(Socket cs2, BananaBank bs){
		cs = cs2;
		bb = bs;
	}
	
	public void run(){
		try{
			System.out.println("Client connected");
			PrintStream ps = new PrintStream(cs.getOutputStream());
			BufferedReader r = new BufferedReader(new InputStreamReader(cs.getInputStream()));
			String line;
			while(true){
				line = r.readLine();
				if (line == null){
					break;
				}
				else if(line.equalsIgnoreCase("SHUTDOWN")){
					
					for(Thread t : Server.threads){
						if(t != Thread.currentThread()){
							try {
								t.join();
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					
					
					bb.save("myAccounts.txt");
					Collection<Account> cc = bb.getAllAccounts();
					Iterator<Account> iterator = cc.iterator();
					int i = 0;
					while(iterator.hasNext()){
						Account acc = iterator.next();
						i = i + acc.getBalance();
					}
					ps.println(i);
					System.out.println("Disconnected");
					ps.close();
					cs.close();
					break;
				}
				else{
					//splitting the string
					Account acc1 = null, acc2 = null;
					int breakone = line.indexOf(" ");
					String amount = line.substring(0,breakone).trim();
					int breaktwo = line.substring(breakone+1).indexOf(" ");
					String src = line.substring(breakone, breakone+breaktwo+1).trim();
					String dest = line.substring(breakone+breaktwo+1).trim();
					
					//getting the accounts
					Account srAcc = bb.getAccount(Integer.parseInt(src));
					Account destAcc = bb.getAccount(Integer.parseInt(dest));
					if(Integer.parseInt(src) > Integer.parseInt(dest)){
						acc1 = destAcc;
						acc2 = srAcc;
					}
					else{
						acc1 = srAcc;
						acc2 = destAcc;
					}
					if(srAcc != null && destAcc != null){
					//locking access to the accounts.
						synchronized(acc1){
							synchronized(acc2){
								srAcc.transferTo(Integer.parseInt(amount), destAcc);
								ps.println("Success");
							}
						}
					}
					else{
						ps.println("Invalid Account");
					}
				}
			}
			System.out.println("Disconnected");
			cs.close();
		} catch (IOException e){
			System.out.println("Hello");
		}
	}

}
