import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.Scanner;



public class ServerThread implements Runnable {
	
	private ServerSocket serverSocket;
	private Socket clientSocket;
	private volatile Boolean active;
	
	public ServerThread(Socket clientSocket, ServerSocket serverSocket, Boolean active){
		this.serverSocket = serverSocket;
		this.clientSocket = clientSocket;
		this.active = active;
	}
	
	/**
	 * Automatically called when the thread is created. This method handles the communication between
	 * client and server until the client enters the quit command. The thread dies soonthereafter. 
	 */
	@Override
	public void run(){
		//do tasks until no more, then let thread die
		System.out.println("Running thread!");
		
		try {
			//out is the message buffer to return to the client
			PrintWriter out = new PrintWriter(this.clientSocket.getOutputStream(), true);
			//in is the incoming message buffer from the client to be read by the server
			BufferedReader in = new BufferedReader( 
					new InputStreamReader(this.clientSocket.getInputStream())
					);
			String command = "";
			Object response = "";
			
			while((command = in.readLine()) != null){
				if(command.equalsIgnoreCase("quit")) {
					out.println("Goodbye, Exiting");
					break;
				}
				//parse client's request
				response = this.parse(command);
				//return server's response
				out.println(response);
			}
			//close reader and writer
			out.close();
			in.close();
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			try {
				//close client conn.
				this.clientSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	/**
	 * Parses the client's command and returns the response string
	 * @param cmd String the command to be parsed.
	 * @return response String the response to return to the client.
	 */
	private Object parse(String cmd){
		//break command into an array of each word
		// e.g. mkdir files -> {"mkdir", "files"}
		String[] tokens = cmd.split(" ");
		
		if (tokens.length == 1){
			switch(cmd){
				//todo
				case "ls":
					return this.ls();
				case "pwd":
					return this.pwd();
			}
		}
		else if(tokens.length == 2){
			switch(tokens[0]){
				case "mkdir":
					return this.mkdir(tokens[1]);
				case "delete":
					return this.delete(tokens[1]);
				case "get":
					return this.get(tokens[1]);
				case "put":
					return this.put(tokens[1]);
				case "cd":
					return this.cd(tokens[1]);
			}	
		}
		return "ERROR MESSAGE HERE";
	}

	private String put(String fileName) {
		// TODO Auto-generated method stub
		return null;
	}

	private File get(String fileName) {

		File curDir = new File(".");
		File[] filesList = curDir.listFiles();
		
	    for(File f : filesList){
	    	 System.out.println("filename: " + f.getName().trim() + " input: " + fileName.trim());
	          if (f.getName().toString().trim().equals(fileName.trim())){
	        	  System.out.println("MATCH");
	        	  return f; 
	          }
	    }
	    return null;
	    
		
	}
	
	private String cd(String newPath){
		File dir = new File(newPath);
		if(dir.isDirectory() == true){
			System.setProperty("user.dir", dir.getAbsolutePath());
			}
		else{
			return newPath + " is not a directory.\n";
		}
		return dir.getAbsolutePath();
	}
	
	
	
	private String pwd(){
		String currentDirectory = System.getProperty("user.dir");
		return currentDirectory;
				
	}
	
	private String ls(){
		StringBuffer output = new StringBuffer();
		File currentDirectory = new File(System.getProperty("user.dir"));
		String childs[] = currentDirectory.list();
		for(String file: childs){
			output.append(file + "\n");
		}
		return output.toString();
	}
	
	private String mkdir(String dirName) {
		String directory = "";
		//Makes file object to check if it exists
		File file = new File(dirName);
		if(!file.exists()){
			file.mkdir();
			directory = "Directory created!\n";
		}
		else if(file.exists()){
			directory =  "Directory not created. File already exists!\n";
		}
		return directory;
	}
	
	private String delete(String filename){
		String status = "";
		//Makes file object to check if it exists
		File file = new File(filename);
		if(file.exists()){
			file.delete();
			status = "Directory Deleted!\n";
		}
		else if(!file.exists()){
			status =  "Directory not deleted. File does not exist!\n";
		}
		return status;
		
	}
	
}
