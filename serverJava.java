import java.io.*;
import java.net.*;
import java.util.Set;
import java.util.ArrayList;


/*Note: For multiple clients, create a thread. 
i.e., each time a client connects to server, 
server starts a new thread, each independently 
responsible to connect to the client.*/

public class serverJava{
	//incoming client request array
	public static ArrayList<Socket> clientManagerList = new  ArrayList<Socket>();
	public static int count = 0;
	public static void main(String args[]) throws Exception{
		
		// server socket initializations
		String msgFromClient;
		String msgFromServer;
		int portNum = 8880;

		// create socket 		
		ServerSocket serverSock = new ServerSocket(portNum);
		System.out.println("Server socket running. You may start the client to begin communication.");

		boolean toContinue = true;

		// Keep waiting for connections, accept and "chat"
		try{
			while(toContinue) {
				try{
					Socket clientSock = serverSock.accept();
					count = count+1;
					clientManagerList.add(clientSock);
					System.out.println("Accepted: Incoming connection from client "+clientSock.getInetAddress()+" with port "+clientSock.getPort());
					Thread manager = new ClientManager(clientSock);
					manager.start();

					for(Socket s: clientManagerList){

						if(s.isClosed()){
							System.out.println("Below socket is closed");
							System.out.println(s);
						}
					}
					
				}
				catch(Exception e){
					if(serverSock != null)
						serverSock.close();
				}
			} //end of loop for while true
		}
		catch(Exception e){
			if(serverSock != null)
				serverSock.close();
		}
		finally{
			serverSock.close();
		}
	}
}

class ClientManager extends Thread{
	private Socket sock;
	BufferedReader brIn;
	BufferedWriter dout;
	BufferedWriter allClientdout;
	// private OutputStreamReader dout;
	private boolean isDisconnect = false;

	// constructor initialization to create  a thread each time new connection comes in
	public ClientManager(Socket sock){
		this.sock = sock;
	}

	public void run(){
		int i = 0;

		try{
			while(true){
				brIn = new BufferedReader(new InputStreamReader(sock.getInputStream()));
				dout = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
				String msgFromClient = brIn.readLine();
				System.out.println("<CLIENT" +String.valueOf(sock.getPort())+"> : "+msgFromClient);
				// dout.write("Message recieved. :)");
				if(msgFromClient.equalsIgnoreCase("exit")){
					System.out.println("Client terminated");
					dout.write("Chat ending. Bye :)");	
					dout.flush();				
					break;
				} else{
					String curClient = sock.getInetAddress().toString();
						if(serverJava.clientManagerList.size() > 0){
							for (Socket tempSock : serverJava.clientManagerList) {
									if(!tempSock.equals(sock)){
									allClientdout = new BufferedWriter(new OutputStreamWriter(tempSock.getOutputStream()));
									allClientdout.write("<CLIENT" +String.valueOf(sock.getPort())+"> : "+msgFromClient);
									allClientdout.flush();
								}
							 }
						}
					// System.out.println(curClient);
					// System.out.println(sock.getPort());
					// System.out.println()

				}
				dout.flush();
				// dout.close();
			}
		} catch (IOException e) {
			e.printStackTrace();			
		} finally {
			if (brIn != null)
				try {
					brIn.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			if (sock != null)
				try {
					sock.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

}