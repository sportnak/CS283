package server;

import java.net.InetAddress;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class ClientEndPoint {
	protected InetAddress address;
	protected int port;
	protected String name;
	protected List<String> groups = new ArrayList<String>();
	protected Queue<String> messages = new ArrayDeque<String>();
	protected int status;
	protected int ackNum = -1;
	protected Boolean acked = false;
	
	public ClientEndPoint(InetAddress addr, int port) {
		this.address = addr;
		this.port = port;
		status = 1;
		this.name = null;
	}
//	public ClientEndPoint(InetAddress addr, int port, String name){
//		this.address = addr;
//		this.port = port;
//		this.name = name;
//	}
//	
//	public ClientEndPoint(InetAddress addr, int port, String name, String group){
//		this.address = addr;
//		this.port = port;
//		if(group != null){
//			this.groups.add(group);
//		}
//	}
	public void addGroup(String input){
		this.groups.add(input);
	}
	public ArrayList<String> getGroups(){
		ArrayList<String> myGroups = (ArrayList<String>) this.groups;
		return myGroups;
	}
	public void setName(String input){
		this.name = input;
	}
	public String getName(){
		return this.name;
	}
	public void logoff(){
		this.status = 0;
	}
	public void addMess(String input){
		this.messages.add(input);
	}
	public void setNum(int number){
		this.ackNum = number;
	}
	public int getNum(){
		return this.ackNum;
	}

	public void logon(){
		this.status = 1;
	}
	@Override
	public int hashCode() {
		// the hashcode is the exclusive or (XOR) of the port number and the hashcode of the address object
		return this.port ^ this.address.hashCode();
	}
	
	
}
