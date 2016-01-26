import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;

public class ServerThread implements Runnable {
	
	Socket SOCK;
	volatile Boolean active;

	
	public ServerThread(Socket SOCK, Boolean active){
		this.SOCK = SOCK;
		this.active = active;
	}
	
	@Override
	public void run(){
		//do tasks until no more, then let thread die
		/*System.out.println("Running thread!");
		
		try {
			mkdir(".");
			ls();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		System.out.println("running");
		try {
			//simulate action for 5 seconds
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("work processed, Killing thread");
		
	}

	public void cd(){
		
	}
	
	public void pwd(){
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
