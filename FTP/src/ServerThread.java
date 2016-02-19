/* ServerThread.java
 * @author Montana Wong
 * @author Justin Tumale
 * @author Matthew Haneburger
 * @description: Handles connections to the server. Individual thread will listen to client to tell it
 * what commands to run and sends the output of said command. Contains parsing method to generate the 
 * specific output given by the client/user.
 * */
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
/**
 * Implements Runnable to override the inherited run method
 */
public class ServerThread implements Runnable {
	
	private Socket clientSocket;
	private File currentWorkingDir;
	private PrintWriter out;
	private BufferedReader br;
	
	public ServerThread(Socket clientSocket, ServerSocket serverSocket){
		this.clientSocket = clientSocket;
		this.currentWorkingDir = new File(System.getProperty("user.dir"));
		//out is the message buffer to return to the client
		try {
			this.out = new PrintWriter(clientSocket.getOutputStream(), true);
			//br is the incoming message buffer from the client to be read by the server
			this.br = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
		} catch (IOException e) {
			System.out.println("Error connecting to socket.");
		}
		
		
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
			String command = "";
			String response = "";
			while((command = this.br.readLine()) != null){
				if(command.equalsIgnoreCase("quit")) {
					this.out.println("Goodbye, Exiting\n");
					break;
				}
				
				//parse client's request
				response = this.parse(command);
				
				//return server's response
				this.out.println(response + "\n");
			}
			//close reader and writer
			this.out.close();
			this.br.close();
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
				System.out.println("Error closing client socket");
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

	/**
     * method of file transfer
     * @param name of file to remote machine from local machine
     * @return success or failure message
     * */
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
			File f = new File(this.currentWorkingDir, fileName);
	    	FileOutputStream fos = new FileOutputStream(f);
	    	fos.write(bytes);
	    	fos.close();
			
		}
		catch(IOException ioe){
			return "File can not be written";
		}
		
		return fileName + " successfully copied to server";
	}

	/**
	 * This method is intended to transfer a file from remote machine to local machine
	 * @param name of the file to transfer
	 * @return success or failure message of the file transfer
	 * */
	private String get(String fileName) {
	    File f = null;
	    try{
	    	f = new File(this.currentWorkingDir, fileName);
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
	    	} 
	    	catch (IOException e1) {
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
    
/**Notify client whether or not the file exists.
	 * @param boolean flag
	 * @return "Accept" or "Error" based on result
	 * */
	 private void notifyClient(boolean sendingFile){
	//write to stream send some text
    	//System.out.println("Client notified");
    	if(sendingFile == false){
			this.out.println("Error");
    	}
    	else{
		    this.out.println("Accept");
    	}
    	   this.out.flush();
    }
	
    /**
     * Changes the current working directory to the directory specified
     * @param path of the directory as only parameter
     * @return path of the new directory
     */
	private String cd(String newPath){
		//does not actually change the location, just rebuild this.cwd.
		File dir = new File(this.currentWorkingDir, newPath);
		if(dir.isDirectory() == true){
			System.setProperty("user.dir", dir.getAbsolutePath());
			this.currentWorkingDir = dir;
		}
		else{
			return newPath + " is not a directory.";
		}
		return "Changed directory";
	}
	
	/**
	 * Prints the current working directory
	 * @return the current working directory
	 */
   	private String pwd(){
		return System.getProperty("user.dir");
	}
	
   	/**
   	 * Returns a list of all files in the currently working directory on new lines
   	 * @return list of files
   	 */
	private String ls(){
		StringBuffer output = new StringBuffer();
		//File currentDirectory = new File(System.getProperty("user.dir"));
		String childs[] = this.currentWorkingDir.list();
		for(String file: childs){
			output.append(file + "\n");
		}
		return output.toString().substring(0, output.length());
	}
	
	/**
	 * Makes a new directory in the current working directory
	 * @param dirName, name of new directory
	 * @return error message 'success' or 'failure'
	 */
	private String mkdir(String dirName) {
		//Makes file object to check if it exists
		File file = new File(this.currentWorkingDir, dirName);
		if(!file.exists()){
			file.mkdir();
			return "Directory created!";
		}
		return "Directory not created, it already exists!";
		
	}
	/**
	 * Deletes a specified file
	 * @param fileName
	 * @return message indicating whether a file was deleted, not deleted, or error message
	 */
	private String delete(String fileName){
		//Makes file object to check if it exists
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
