import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;


public class ClientThread extends Thread {
	
	private Socket SOCK;
	private PrintWriter out;
	private String cmd;

	public ClientThread(Socket socket, String cmd){
		super();
		this.SOCK = socket;
		this.cmd = cmd;
		run();
	}
	
	@Override
	public void run(){
	 
		try {
			//Get the output stream to the server
			out = new PrintWriter(this.SOCK.getOutputStream());
			//Send the command to the server
			out.println(this.cmd);
			out.flush();
			
			//Receive the server's response
			BufferedReader in = new BufferedReader(
					new InputStreamReader(this.SOCK.getInputStream())
				);
//			Print the response
//			TODO: the response is printing twice?
			StringBuffer buffer = new StringBuffer("");
			String INPUT = null;
			while ( (INPUT = in.readLine()) != null){
				System.out.println(INPUT);
			}
			in.close();

		
			
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
