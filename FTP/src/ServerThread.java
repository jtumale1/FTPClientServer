import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.Scanner;



public class ServerThread implements Runnable {
	
	private ServerSocket serverSocket;
	private Socket clientSocket;
	private File currentWorkingDir;
	private volatile Boolean active;
	
	public ServerThread(Socket clientSocket, ServerSocket serverSocket, Boolean active){
		this.serverSocket = serverSocket;
		this.clientSocket = clientSocket;
		this.active = active;
		this.currentWorkingDir = new File(System.getProperty("user.dir"));
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
			String response = "";
			while((command = in.readLine()) != null){
				if(command.equalsIgnoreCase("quit")) {
					out.println("Goodbye, Exiting\n");
					break;
				}
				
				//parse client's request
				response = this.parse(command);
				//return server's response
				out.println(response + "\n");
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
    private String parse(String cmd){
		//break command into an array of each word
		// e.g. mkdir files -> {"mkdir", "files"}
		String[] tokens = cmd.split(" ");
		
		if (tokens.length == 1){
			switch(cmd){
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
		return "Command not supported.";
	}

	private String put(String fileName) {
	    InputStream in = null;
		try {
			in = this.clientSocket.getInputStream();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		byte bytes[] = new byte[16*1024];
		try{		
			//read the bytes into the input stream
			in.read(bytes);
			
			//print this. dont remove.
			System.out.println("");
						
			//Output into a file
	    	FileOutputStream fos = new FileOutputStream(fileName);
	    	fos.write(bytes);
	    	fos.close();
			
		}
		catch(IOException ioe){
			return "File can not be written";
		}
		
		return fileName + " successfully copied to server";
	}

	private String get(String fileName) {
	    /*OutputStream out = null;
	    try {
		out = this.clientSocket.getOutputStream();
	    } catch (IOException e1) {
		e1.printStackTrace();
		}*/
	    
	    File f = null;
	    try{
	    	f = new File(fileName);
	    	if (!f.exists()){
	    		throw new FileNotFoundException();
	    	}
	    }
	    catch(FileNotFoundException e){
	    	this.notifyClient(false);
	    	return "File does not exist";   
	    }
	    
	    try{
	    	if(f.isDirectory() == true){
	    		this.notifyClient(false);
	    		return "This is a directory, you can only move files.";
	    	}
		
	    	this.notifyClient(true);
	    	//move code here
	    	OutputStream out = null;
	    	try {
	    		out = this.clientSocket.getOutputStream();
	    	} catch (IOException e1) {
		    e1.printStackTrace();
	    	}
	    	
	    	//create an input stream for the file
	    	FileInputStream fileInputStream = new FileInputStream(f);
		    //create a byte array
	    	byte[] bytes = new byte[(int) f.length()];	    	
		
	    	int count;
		    //write the bytes to the output stream
	    	while ((count = fileInputStream.read(bytes)) > 0){
	    		out.write(bytes, 0, count);
	    	}
		    
	    	fileInputStream.close();
	    	out.flush();  
		
	    }
	    
	    catch(IOException e){
		    this.notifyClient(false);
		    return "Error reading file";
	    }
	    
	    return "Download successful."; 
	}
    
	//Notify client whether or not the file exists.
    private void notifyClient(boolean sendingFile){
	//write to stream send some text
    	System.out.println("Client notified");
    	if(sendingFile == false){
    		try {
    			PrintWriter notify = new PrintWriter(clientSocket.getOutputStream());
    			notify.println("Error");
    			notify.flush();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
    	else{
    		PrintWriter notify = null;
	    try {
	    	notify = new PrintWriter(clientSocket.getOutputStream());
	    } catch (IOException e) {
	    	e.printStackTrace();
	    }
	    notify.println("Accept");
	    notify.flush();
    	}
    }
	
	private String cd(String newPath){
		//does not actually change the location.
		File dir = new File(this.currentWorkingDir, newPath);
		if(dir.isDirectory() == true){
			System.setProperty("user.dir", dir.getAbsolutePath());
			this.currentWorkingDir = dir;
		}
		else{
			return newPath + " is not a directory.";
		}
		return "Changed directory"; //dir.getAbsolutePath();
	}
	
   	private String pwd(){
		return System.getProperty("user.dir");
	}
	
	private String ls(){
		StringBuffer output = new StringBuffer();
		//File currentDirectory = new File(System.getProperty("user.dir"));
		String childs[] = this.currentWorkingDir.list();
		for(String file: childs){
			output.append(file + "\n");
		}
		return output.toString();
	}
	
	private String mkdir(String dirName) {
		//Makes file object to check if it exists
		File file = new File(this.currentWorkingDir, dirName);
		if(!file.exists()){
			file.mkdir();
			return "Directory created!";
		}
		return "Directory not created, it already exists!";
		
	}
	
	private String delete(String fileName){
		//Makes file object to check if it exists
		//File file = new File(filename);
		File file = new File(this.currentWorkingDir, fileName);
		if(file.exists()){
			file.delete();
			if (!file.exists()){
				return "File Deleted!";
			}
			else{
				return "There was an error deleting the file";
			}
		}
		return "File not deleted. File does not exist!";	
	}
}
