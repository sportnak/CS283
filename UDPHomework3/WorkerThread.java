package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

public class WorkerThread extends Thread {

	private DatagramPacket rxPacket;
	private DatagramSocket socket;
	protected String name;
	InetAddress address;
	int port;
	int ACKNUM = -1;
	Timer timer;
	Boolean acked = false;

	public WorkerThread(DatagramPacket packet, DatagramSocket socket) {
		this.rxPacket = packet;
		this.socket = socket;
		
	}

	@Override
	public void run() {
		// convert the rxPacket's payload to a string
		String payload = new String(rxPacket.getData(), 0, rxPacket.getLength())
				.trim();

		address = this.rxPacket.getAddress();
		port = this.rxPacket.getPort();

		// dispatch request handler functions based on the payload's prefix
		if(payload.startsWith("HELP")){
			onHelpRequested();
			return;
		}


		if (payload.startsWith("REGISTER")) {
			onRegisterRequested(payload);
			return;
		}

		for (ClientEndPoint clientEndPoint : Server.clientEndPoints) {
			if(clientEndPoint.address.equals(address) && clientEndPoint.port == port){
				name = clientEndPoint.name;
			}
		}
		
		if (payload.startsWith("UNREGISTER")) {
			onUnregisterRequested(payload);
			return;
		}

		if (payload.startsWith("SEND")) {
			onSendRequested(payload, name);
			return;
		}
		
		if (payload.startsWith("GROUP")){
			onGroupRequested(payload);
			return;
		}
		
		if (payload.startsWith("LEAVE GROUP")){
			onLeaveRequested(payload);
			return;
		}
		
		if (payload.startsWith("SHUTDOWN")){
			onSendRequested("SEND SERVER SHUTDOWN", name);
			Server.shutdown = true;
			
			return;
		}
		if(payload.startsWith("MY GROUPS")){
			onGroupsRequested(payload);
			return;
		}
		
		if (payload.startsWith("POLL")){
			onPollRequested();
			return;
		}
		if(payload.startsWith("ACK")){
			onACK(payload);
			return;
		}

		//
		// implement other request handlers here...
		//

		// if we got here, it must have been a bad request, so we tell the
		// client about it
		onBadRequest(payload);
	}
	
	public void onGroupsRequested(String payload){
		for (ClientEndPoint clientEndPoint : Server.clientEndPoints) {
			if(clientEndPoint.address.equals(address) && clientEndPoint.port == port && clientEndPoint.name.equals(name)){
				for(String group : clientEndPoint.groups){
					try {
						send(group + "\n", address, port);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}

			
	// send a string, wrapped in a UDP packet, to the specified remote endpoint
	public void send(String payload, InetAddress address, int port)throws IOException {
		DatagramPacket txPacket = new DatagramPacket(payload.getBytes(),
				payload.length(), address, port);
		this.socket.send(txPacket);
		System.out.println("Successful send");
	}
	//The user wants to see the commands.
	public void onHelpRequested(){
		try {
			send("You can enter HELP,\nREGISTER + NAME + PASS,\nUNGREGISTER,\nSEND + [TO] + [GROUP] + [groupname],\nGROUP,\nPOLL,\nSHUTDOWN", address, port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void onACK(String payload){
		String number = payload.substring("ACK".length(), payload.length());
		for (ClientEndPoint clientEndPoint : Server.clientEndPoints) {
			if(clientEndPoint.address.equals(address) && clientEndPoint.port == port && clientEndPoint.name.equals(name)){
				if(Integer.parseInt(number) == clientEndPoint.getNum()){
					try {
						send("Acknowledged\n\n", address, port);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					clientEndPoint.acked = true;
				}
			}
		}
		
	}
	public void scheduleTimer(String mess){
		final String mymess = mess;
		timer = new Timer();
		timer.schedule(new TimerTask(){
			public void run(){
				try {
					for (ClientEndPoint clientEndPoint : Server.clientEndPoints) {
						if(clientEndPoint.address.equals(address) && clientEndPoint.port == port && clientEndPoint.name.equals(name)){
							if(clientEndPoint.acked){
								timer.cancel();
								onPollRequested();
							}
							else{
								send(mymess, address, port);
								scheduleTimer(mymess);
							}
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}, 3000);
	}
	public void onPollRequested(){
		for (ClientEndPoint clientEndPoint : Server.clientEndPoints) {
			if(clientEndPoint.address.equals(address) && clientEndPoint.port == port && clientEndPoint.name.equals(name)){
				if(!clientEndPoint.messages.isEmpty()){
					try {
						send(clientEndPoint.messages.peek() + "Please send ACK" + clientEndPoint.messages.size()+ " when you have received this message\n", address, port);
						final String mess = clientEndPoint.messages.poll()+"Please send ACK" + (clientEndPoint.messages.size() + 1) + " when you have received this message\n";
						clientEndPoint.ackNum = clientEndPoint.messages.size() + 1;
						clientEndPoint.acked = false;
						scheduleTimer(mess);
					
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	public void onGroupRequested(String payload){
		//get the groupName
		String message = payload.substring("GROUP".length() + 1,
				payload.length()).trim();
		
		//find the client with the appropriate name and continue.
		for (ClientEndPoint clientEndPoint : Server.clientEndPoints) {
			if(clientEndPoint.address.equals(address) && clientEndPoint.port == port && clientEndPoint.name.equals(name)){
				clientEndPoint.addGroup(message);
				Server.groups.add(message);
				//Registers them for a group
			}
		}
		System.out.println("CLIENT " + name + " ADDED TO " + message);
	}
	public void onNameRequested(String message){
		//get the name
		
		//looks through the clients for the appropriate one and assigns name
		for (ClientEndPoint clientEndPoint : Server.clientEndPoints) {
			if(clientEndPoint.address.equals(address) && clientEndPoint.port == port){
				clientEndPoint.setName(message);
				clientEndPoint.logon();
				System.out.println("Success");
			}
		}
		
		System.out.println("CLIENT: " + message + " ADDED");
		
		try {
			send("Hi " + message + "!", address, port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void onLeaveRequested(String payload){
		String group = payload.substring("LEAVE GROUP".length() +1).trim();
		for(ClientEndPoint clientEndPoint : Server.clientEndPoints){
			if(clientEndPoint.address.equals(address) && clientEndPoint.port == port && clientEndPoint.name.equals(name)){
				if(clientEndPoint.groups.contains(group)){
					clientEndPoint.groups.remove(group);
				}
			}
		}
	}
	

	private void onRegisterRequested(String payload) {
		
		String userName = payload.substring("REGISTER".length() + 1).trim();
		String password = userName.substring(userName.indexOf(" ")+1, userName.length()).trim();
		userName = userName.substring(0, userName.indexOf(" "));
		System.out.println(userName + " " + payload.indexOf(" "));
		int c = 0;
		for (ClientEndPoint clientEndPoint : Server.clientEndPoints) {
			if(clientEndPoint.address.equals(address) && clientEndPoint.port == port){
				c = clientEndPoint.status;
			}
		}
		if(c == 0){
			if(Server.users.contains(userName)){
				for (Entry<String, String> entry : Server.userToPass.entrySet())
				{
					if(entry.getKey().equals(userName) && entry.getValue().equals(password)){
						for(ClientEndPoint clientEndPoint : Server.clientEndPoints){
							if(clientEndPoint.name.equals(userName)){
								clientEndPoint.address = address;
								clientEndPoint.port = port;
								clientEndPoint.logon();
								try {
									send("Welcome back " + userName + "!\n", address, port);
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
					} else if (entry.getKey().equals(userName) && !entry.getValue().equals(password)){
						try {
							send("Wrong Password.\n", address, port);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}else {
				Server.clientEndPoints.add(new ClientEndPoint(address, port));
				Server.userToPass.put(userName, password);
				onNameRequested(userName);

				try {
					send("REGISTERED\n", this.rxPacket.getAddress(),
							this.rxPacket.getPort());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		else{
			try {
				send("You are already logged on...\n", address, port);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//}
		
		// create a client object, and put it in the map that assigns names
		// to client objects
		// note that calling clientEndPoints.add() with the same endpoint info
		// (address and port)
		// multiple times will not add multiple instances of ClientEndPoint to
		// the set, because ClientEndPoint.hashCode() is overridden. See
		// http://docs.oracle.com/javase/7/docs/api/java/util/Set.html for
		// details.

		// tell client we're OK
	}

	private void onUnregisterRequested(String payload) {
	// check if client is in the set of regi
		for(ClientEndPoint clientEndPoints : Server.clientEndPoints){
			System.out.println(clientEndPoints.name);
			if(clientEndPoints.name != null && clientEndPoints.name.equals(name)){
				clientEndPoints.logoff();
				try {
					send("UNREGISTERED " + clientEndPoints.name + "\n", this.rxPacket.getAddress(),
							this.rxPacket.getPort());
					Server.users.add(name);
					return;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		// no, send back a message
		try {
			send("CLIENT NOT REGISTERED\n", this.rxPacket.getAddress(),
					this.rxPacket.getPort());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void onSendRequested(String payload, String name) {
		// the message is comes after "SEND" in the payload
		ClientEndPoint client = null;
		int stat = 0;
		for (ClientEndPoint clientEndPoint : Server.clientEndPoints) {
			if(clientEndPoint.address.equals(address) && clientEndPoint.port == port && clientEndPoint.name.equals(name)){
				client = clientEndPoint;
				stat = clientEndPoint.status;
			}
		}
		if(stat == 0){
			try {
				send("You are not logged in.\n", address, port);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else{
			String message = payload.substring("SEND".length() + 1,
					payload.length()).trim();
			if(message.startsWith("TO")){
				String rest = message.substring("TO".length()+1, message.length()).trim();
				if(rest.startsWith("GROUP")){
					rest = rest.substring("GROUP".length()+1, rest.length()).trim();
					String groupName = rest.substring(0,rest.indexOf(" ")).trim();
					if(Server.groups.contains(groupName) && client.groups.contains(groupName)){
						String mymess = rest.substring(rest.indexOf(" "), rest.length()).trim();
						for (ClientEndPoint clientEndPoint : Server.clientEndPoints) {
							if(clientEndPoint.groups != null && clientEndPoint.groups.contains(groupName) && clientEndPoint.status == 1){
								try {
									send("MESSAGE TO " + groupName + " FROM " + name +" : " + mymess + "\n", clientEndPoint.address, clientEndPoint.port);
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								//send("MESSAGE TO " + groupName + " FROM " + name +": " + mymess, clientEndPoint.address,
								//		clientEndPoint.port);
								System.out.println("CLIENT: MSG TO " + groupName + " " + mymess);
							}
							else if(clientEndPoint.groups != null && clientEndPoint.groups.contains(groupName)) {
								clientEndPoint.messages.add("MESSAGE TO " + groupName + " FROM " + name +" : " + mymess + "\n");
							}
						}
					}
					else {
						try {
							send("Error, either the group doesn't exist, or you are not a part of the group: " + groupName, address,
									port);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				else{
					String picked = rest.substring(0,rest.indexOf(" ")).trim();
					String mymess = rest.substring(rest.indexOf(" "), rest.length()).trim();
					for (ClientEndPoint clientEndPoint : Server.clientEndPoints) {
						if(clientEndPoint.name != null && clientEndPoint.name.equals(picked) && clientEndPoint.status == 1){
							try {
								send("MESSAGE TO " + picked + " FROM " + name + ": " + mymess +"\n", clientEndPoint.address, clientEndPoint.port);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							System.out.println("CLIENT: MSG TO " + name + ": " + mymess);
						}else if(clientEndPoint.name != null && clientEndPoint.name.equals(picked)){
							clientEndPoint.messages.add("MESSAGE TO " + picked + " FROM " + name + ": " + mymess + "\n");
						}
					}
				}
			}
			else{
				for (ClientEndPoint clientEndPoint : Server.clientEndPoints) {
						//the initial response if there is a client who hasn't registered their name yet.
					if(clientEndPoint.status == 1){
						try {
							send("MESSAGE FROM " + name + " TO " + clientEndPoint.name + " : " + message + "\n", clientEndPoint.address, clientEndPoint.port);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					else {
						clientEndPoint.addMess("MESSAGE FROM " + name + " TO " + clientEndPoint.name + " : " + message + "\n");
					}
				}
				System.out.println("CLIENT: MSG TO ALL:" + message);
			}
		}
	}

	private void onBadRequest(String payload) {
		try {
			send("BAD REQUEST\n", this.rxPacket.getAddress(),
					this.rxPacket.getPort());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
