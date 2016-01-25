import java.io.File;

public class ServerThread implements Runnable {
	
	@Override
	public void run(){
		
	}

	public void cd(){
		
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
	
}
