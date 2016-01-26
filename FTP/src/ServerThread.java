import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;


public class ServerThread implements Runnable {
	
	private Socket SOCK;
	private volatile Boolean active;
	private Scanner scanner;
	
	public ServerThread(Socket SOCK, Boolean active){
		this.SOCK = SOCK;
		this.active = active;
	}
	
	@Override
	public void run(){
		//do tasks until no more, then let thread die
		System.out.println("Running thread!");
		
		try {
		
			scanner = new Scanner(this.SOCK.getInputStream());
			String command = "";
			while(true){
				command = scanner.nextLine();
				if(command.equalsIgnoreCase("quit")) break;
				this.parse(command);
			}
				
			
				
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void parse(String cmd){
		switch(cmd){
			//todo
			case "ls":
				break;
			default:
				//TODO print error message
				
			
			
		}
	}

	private void cd(){
		
	}
	
	private void pwd(){
		System.out.println(System.getProperty("user.dir"));
		
	}
	
	public void ls(){
		File curDir = new File(".");
		File[] filesList = curDir.listFiles();
	     for(File f : filesList){
	    
	            if(f.isFile()){
	                System.out.println(f.getName());
	            }
	     }
	}
	
	public void ls(String path){
		File curDir = new File(path);
		File[] filesList = curDir.listFiles();
	     for(File f : filesList){
	    
	            if(f.isFile()){
	                System.out.println(f.getName());
	            }
	        }
		
	}
	
	public void mkdir(String path) throws IOException{
//		File directory = new File("New folder");
//		Path p1 = Paths.get(path);
//		Files.createDirectory(p1);
	}
	
	public void delete(String filename){
		File curDir = new File(".");
		File[] filesList = curDir.listFiles();
	     for(File f : filesList){
	    
	            if(f.isFile()){
	                System.out.println(f.getName());
	            }
	     }
		
	}
	
}
