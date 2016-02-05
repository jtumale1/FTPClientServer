
/**
 * ClientThread.java
 * @author Montana Wong
 * @author Justin Tumale
 * @author Matthew Haneburger
 * @description Represents one connection
 * */
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


/**
 * Creates ClientThread inherited from Java.lang.Thread 
 * Represents one connection
 *
 */
public class ClientThread extends Thread {
	
	private Socket socket;
	private String cmd;
    private InputStream in;
    private BufferedReader br;
    
	/**
	 * Creates new ClientThread with param socket and cmd 
	 * Sets socket and connection
	 * Constructor
	 * @param socket
	 * @param cmd
	 */
	public ClientThread(Socket socket, String cmd){
		super();
		this.socket = socket;
		this.cmd = cmd;
		try{
		    this.in = socket.getInputStream();
		    this.br = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
		}
		catch(Exception e){
		    e.printStackTrace();   
		}
	}
	
	/**
	 * overridden method run to send and receive signals and catches exceptions with reading files and 
	 * handling sockets
	 */
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
	
	/**
	 * Sends signals
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws FileSystemException
	 */
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
	
	/**
	 * Receives signals and checks the command to tokenize them
	 * @throws IOException
	 */
	private void receive() throws IOException{
	    String[] tokens = this.cmd.split(" ");
	    String cmd = tokens[0];
	     	
    	if (tokens.length > 1 && cmd.equals("get")){
    		 String fileName = tokens[1];
    		//case 1, client issued a get file command and server is currently returning file. 
	    
		    //first check to make sure there was no error in getting file
		    //check server's response. 
		    boolean acceptFile = this.checkServerResponse();
		    if(acceptFile){
			    //If the file exists then we need to write to file.
			    byte[] bytes = new byte[16*1024];
			    
			    this.in.read(bytes);
			    
			    //CreateFile
			    FileOutputStream fos = new FileOutputStream(fileName);
			    fos.write(bytes);
			    fos.close();
		    }
    	}
    	//print response is called no matter what
	    printResponse();
	}//receive
	
	/**
	 * Checks the server response and throws IOException
	 * 
	 * @return prompt to user that file is accepted or not accepted
	 * @throws IOException
	 */
    private boolean checkServerResponse() throws IOException{
		StringBuffer response = new StringBuffer();
		String input = null;
		
		input = this.br.readLine();
		response.append(input);
	
		return (response.toString().equals("Accept")) ? true : false;  

    }

	/**
	 * Helper method for send. Reads a file to bytes then outputs it to the output stream
	 * @param fileName String the name of the file to output to the stream
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws FileSystemException
	 */
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
    	
    	//close the file input stream
    	fileInputStream.close();
	}
	
	
	
	/**
	 * Helper method; Receives byte streams in order to read then write to particular file
	 * 
	 * @param fileName
	 * @param fileWriter
	 * @param fileDownloader
	 * @throws IOException
	 */
	@SuppressWarnings("unused")
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

	/**
	 * Prints response by user
	 * @throws IOException
	 */
	public void printResponse() throws IOException{
		//Print the response
		String input = null;
		while (((input = this.br.readLine()) != null) && !input.equals("")){
				System.out.println(input);
		}
	}	
	
}
