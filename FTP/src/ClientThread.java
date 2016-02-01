import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.FileSystemException;
import java.util.Scanner;


public class ClientThread extends Thread {
	
	private Socket socket;
	private String cmd;
	
	public ClientThread(Socket socket, String cmd){
		super();
		this.socket = socket;
		this.cmd = cmd;
	}
	
	@Override
	public void run(){
		try {
			this.send(this.cmd);
			this.receive();
		}
		catch(FileNotFoundException fnfe){
			System.out.println("File not found.");
		}
		catch(FileSystemException fse){
			System.out.println("File size too large.");
		}
		catch(IOException ioe){
			System.out.println("Error reading file or socket");
		}
	}
	
	private void send(String cmd) throws FileNotFoundException, IOException, FileSystemException{
		String[] tokens = cmd.split(" ");
		
		//sending a file to server
		if(tokens[0].equals("put") && tokens.length == 2){
			String fileName = tokens[1];
			File file = new File(fileName);
			if (file.length() > Long.MAX_VALUE){
				throw new FileSystemException("File size too large");
			}
			
			byte bytes[] = new byte[16*1024];
			InputStream fileReader = new FileInputStream(file);
			OutputStream fileUploader = this.socket.getOutputStream();
				
			int count;
			while ((count = fileReader.read(bytes)) > 0) {
			    fileUploader.write(bytes, 0, count);
			}
			fileReader.close();
			fileUploader.close();
		}
		//sending string to server
		else{
			//Get the output stream to the server
			PrintWriter out = null;
			out = new PrintWriter(this.socket.getOutputStream());
			
			//Send the command to the server
			out.println(this.cmd);
			out.flush();
			
			//Receive the server's response
			BufferedReader in = new BufferedReader(
					new InputStreamReader(this.SOCK.getInputStream())
				);
//			Print the response
			String INPUT = null;
			Object INPUTobject = null;
//			while ( (INPUT = in.readLine()) != null){
//				System.out.println(INPUT);
//			}			
			
			
			switch(this.cmd){
			case "ls":
				while ( (INPUT = in.readLine()) != null){
					System.out.println(INPUT);
				}
			case "mkdir":
				while ( (INPUT = in.readLine()) != null){
					System.out.println(INPUT);
				}
			case "delete":
				while ( (INPUT = in.readLine()) != null){
					System.out.println(INPUT);
				}
			case "get":
			
			case "put":
				
			case "cd":
				while ( (INPUT = in.readLine()) != null){
					System.out.println(INPUT);
				}
				
			}
			
		
			
		} 
		catch (IOException e) {
			e.printStackTrace();		}
		
	}
	
	private void receive() throws IOException{
		//Receive the server's response
		BufferedReader in = new BufferedReader(
				new InputStreamReader(this.socket.getInputStream())
			);
		
//		Print the response
		String input = null;
		while ( (input = in.readLine()) != null){
			System.out.println(input);
		}
	}
}
