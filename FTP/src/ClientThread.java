import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.FileSystemException;
import java.nio.file.Files;



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
			this.send();
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
	
	private void send() throws FileNotFoundException, IOException, FileSystemException{
	    String[] tokens = this.cmd.split(" ");		
		//sending a file to server
		if(tokens[0].equals("put") && tokens.length == 2){
			String fileName = tokens[1];
	
			readBytesAndOutputToStream(fileName);			
		}
		//sending string to server
		else{
		    //Get the output stream to the server
		    PrintWriter out = null;
		    out = new PrintWriter(this.socket.getOutputStream());
		    //Send the command to the server
		    out.println(this.cmd);
		    out.flush();	
		}
	}
	
	private void receive() throws IOException{
	    String[] tokens = this.cmd.split(" ");
	    String cmd = tokens[0];
	
	    if (tokens.length >1){
  		    	String fileName = tokens[1];
	    	//case 1, client issued a get file command and server is currently returning file. 
	    	//We need to receive this incoming byte stream as file
	    	if (cmd.equals("get")){
	    		
	    		receiveByteStreamAndWriteToFile(fileName);
		    		
	    	}//if
	    	//case 2 client issue another command, server is returning a string. Receive the string
	    	else{
	    		//Receive the server's response
	    		printResponse(this.socket);
	    	}//else
	    
	    }else{ //token length > 1
    		//Receive the server's response
	    	printResponse(this.socket);
	    }//else
	    
	}//receive
	
	//helper method for send()
	private void readBytesAndOutputToStream(String fileName) 
			throws FileNotFoundException, IOException, FileSystemException{
		
		File file = new File(fileName);
		if (!file.exists()){
		    throw new FileNotFoundException();
		}
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
	
	
	
	//helper method for receive()
	private void receiveByteStreamAndWriteToFile(String fileName) throws IOException{
		
		FileOutputStream fileWriter = null;
		InputStream fileDownloader = null;
		try{
			//TODO code to get file goes here
			fileWriter = new FileOutputStream(fileName);
			fileDownloader = this.socket.getInputStream();
    
			//write file to client system
			byte bytes[] = new byte[16*1024];
			int count;
			while ((count = fileDownloader.read(bytes)) > 0) {
				fileWriter.write(bytes, 0, count);
			}//while
		}//try
		finally{
			if (fileWriter != null) fileWriter.close();
			if (fileDownloader != null) fileDownloader.close();
		}//finally
		
	}

	//helper method for receive()
	public void printResponse(Socket socket){
		socket = this.socket;
		BufferedReader in = null;
		try {
			in = new BufferedReader(
				       new InputStreamReader(socket.getInputStream())
				       );
		} catch (IOException e) {
			e.printStackTrace();
		}

		//Print the response
		String input = null;
		try {
			while ( (input = in.readLine()) != null){
				System.out.println(input);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}//while
		
	}	
	
}
