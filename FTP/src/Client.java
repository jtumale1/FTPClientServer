import java.net.*;


public class Client {
	
	private Socket clientSocket = null;
	private Scanner scanner = new Scanner(System.in);
	private PrintStream output = null;
	
	public Client(int port){
		clientSocket = new Socket("localhost", port);
	}
	public Client(String hostname, int port){
		clientSocket = new Socket(hostname, port);
	}
	
	
	public void run(){
		String input = null;
		while(true){
			System.out.print("ftpclient> ");
			input = this.scanner.nextLine();
			if(input.equalsIgnoreCase("quit") 
				break;
			else if this.parse(input) == true(){
				//TODO use threadpool
				ClientThread clientThread = new ClientThread(clientSocket, input);
			}
			else{
				//print error message
			}
		}
	}
	
	private boolean parse(){
		//todo
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
