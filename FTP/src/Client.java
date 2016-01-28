import java.io.IOException;
import java.io.PrintStream;
import java.net.*;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Client {
	
	private ExecutorService threadPool = Executors.newCachedThreadPool();
	private Socket clientSocket = null;
	private Scanner scanner = new Scanner(System.in);
	private PrintStream output = null;
	private int connectPort;
	private String hostName = null;
	
	public Client(int port){
		this.connectPort = port;
		this.hostName = "localhost";
	}
	public Client(String hostName, int port){
		this.connectPort = port;
		this.hostName = hostName;
	}
	
	
	public void run() throws UnknownHostException, IOException, InterruptedException{
		//create socket
		this.clientSocket = new Socket(this.hostName, this.connectPort);
		
		//read commands from sys.in
		String input = null;
		while(true){
			System.out.print("ftpclient> ");
			input = this.scanner.nextLine();
			if(input.equalsIgnoreCase("quit")){
				//close connection 
				break;
				
			}
			else{
				ClientThread clientThread = new ClientThread(this.clientSocket, input);
				this.threadPool.execute(clientThread);
				
				//this forces our client to be synchronous for now, program blocks until thread dies
				clientThread.join();
			}
			
		}
	}
	
	
	
	public static void main(String[] args){
		
		try{
			Client client = new Client("localhost", 9000);
			System.out.println("Running client!");
			client.run();
		}catch(Exception ex){
			System.out.println(ex.getStackTrace());
		}
	}
}
