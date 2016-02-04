import java.io.IOException;
import java.io.PrintStream;
import java.net.*;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Client {
	
	//private ExecutorService threadPool = Executors.newCachedThreadPool();
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
		
			ClientThread clientThread = new ClientThread(this.clientSocket, input);
			clientThread.start();
			//this.threadPool.execute(clientThread);
			
			try{
				//this forces our client to be synchronous for now, program blocks until thread dies
				clientThread.join();
			}
			catch(InterruptedException ie){
				ie.printStackTrace();
			}
			if(input.equals("quit")) break;
			
		}
		if(this.clientSocket != null){
			this.clientSocket.close();
		}
	}
	
	
	
	public static void main(String[] args){
		boolean DEVELOPMENT = true;
		if(DEVELOPMENT){
			Client client = new Client("localhost", 60000);
			System.out.println("Running client!");
			try {
				client.run();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Client shutdown!");
		}
		else{
		
			try{
				if(args.length == 2 && Integer.valueOf(args[1]) >= 49152 && Integer.valueOf(args[1]) <= 65535){
					Client client = new Client(args[0], Integer.valueOf(args[1]));
					System.out.println("Running client!");
					client.run();
					System.out.println("Client shutdown!");
				}
				else{
					throw new NumberFormatException();
				}
			}
			catch (UnknownHostException e) {
				System.out.println("Count not find host");
			}
			catch(NumberFormatException nfe){
				System.out.println("Client must be run with this syntax: java Client hostname port_number" + 
						"\n e.g. java Client localhost 60000");
			}
			catch(Exception ex){
				ex.printStackTrace();
			}
		}
	}
}
