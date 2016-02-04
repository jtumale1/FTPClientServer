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
	private File currentWorkingDir = null;
    private InputStream in;
    private OutputStream out;
    private BufferedReader br;
    
	
	public ClientThread(Socket socket, String cmd){
		super();
		this.socket = socket;
		this.cmd = cmd;
		this.currentWorkingDir =  new File(System.getProperty("user.dir"));
		try{
		    this.in = socket.getInputStream();
		    this.out = socket.getOutputStream();
		    //   this.br = new BufferedReader(new InputStreamReader(System.in));
		}
		catch(Exception e){
		    e.printStackTrace();   
		}
	}
	
	@Override
	public void run(){
		try {
			this.send();
			this.receive();
		}
		catch(FileNotFoundException fnfe){
			fnfe.printStackTrace();
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
			
			//send command the server first
		    PrintWriter out = new PrintWriter(this.socket.getOutputStream());
		    out.println(this.cmd);
		    out.flush();	
		    
		    //stream the file next
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
	
	    if (tokens.length > 1){
  		    	String fileName = tokens[1];
	    	
  		    	
	    	if (cmd.equals("get")){
	    		//case 1, client issued a get file command and server is currently returning file. 
		    
		    //first check to make sure there was no error in getting file
		    //check server's response. If the exist existed then we need to write
		    boolean acceptFile = this.checkServerResponse();
		    if(acceptFile){
			    
			    //if no error then read file
			    InputStream in = this.socket.getInputStream();
			    
			    byte[] bytes = new byte[16*1024];
			    
			    in.read(bytes);
			    
			    //CreateFile
			    FileOutputStream fos = new FileOutputStream(fileName);
			    fos.write(bytes);
			    fos.close();
			    
			    printResponse();
			    
		    }
		    //otherwise print error messag
		    else{
		    	printResponse();
		    }
		}//if
	    	
	    	//case 2 client issue another command, server is returning a string. Receive the string
	    	else{
	    		//Receive the server's response
	    		printResponse();
	    	}//else
	    
	    }
	    
	    else{ //token length < 1
    		//Receive the server's response
	    	printResponse();
	    }//else
	    
	}//receive
	
    private boolean checkServerResponse(){
	BufferedReader 	in = new BufferedReader(
						new InputStreamReader(this.socket.getInputStream()) //bug here...error reading socket.
						//new InputStreamReader(fileDownloader)
						);
<<<<<<< Updated upstream
	StringBuffer response = new StringBuffer;
	String input = null;
	while (((input = in.readLine()) != null) && !input.equals("")){
	    response.append(input);
	}
	boolean acceptFile;
	return acceptFile = (response.toString.equals("Accept")) ? true : false;  
=======
	
	StringBuffer response = new StringBuffer();
	String input = null;
	
//	while (((input = in.readLine()) != null) && !input.equals("")){
//	    response.append(input);
//	}
	
//	if (((input = in.readLine()) != null) && !input.equals("")){
//	    response.append(input);
//	}
	
	input = in.readLine();
	response.append(input);

	boolean acceptFile = (response.toString().equals("Accept")) ? true : false;  
	System.out.println("Acceptingfile : " + acceptFile);
	
	return acceptFile;

    }

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
		
		//get the output stream
		OutputStream out = this.socket.getOutputStream();

		//create an input stream for the file
		FileInputStream fileInputStream = new FileInputStream(file);
		
		//create a byte array
		byte[] bytes = new byte[(int) file.length()];	    	
    	
		//write the bytes to the output stream
    	int count;
    	while ((count = fileInputStream.read(bytes)) > 0){
    		out.write(bytes, 0, count);
    	}

    	//print the file content
//    	for (int i = 0; i < bytes.length; i++){	
//    		System.out.print((char) bytes[i]);
//    	}
    	
    	//close the file input stream
    	fileInputStream.close();
    	//out.close();
		
	}
	
	
	
	//helper method for receive()
	private void receiveByteStreamAndWriteToFile(String fileName, FileOutputStream fileWriter, 
			InputStream fileDownloader) throws IOException{

		try{    
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
	public void printResponse() throws IOException{

		BufferedReader 	in = new BufferedReader(
				       new InputStreamReader(this.socket.getInputStream()) //bug here...error reading socket.
			       //new InputStreamReader(fileDownloader)
				       );

		//Print the response
		String input = null;
	
		while (((input = in.readLine()) != null) && !input.equals("")){
				System.out.println(input);
		}
	}	
	
}