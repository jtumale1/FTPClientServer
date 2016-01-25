import java.net.*;


public class Client {
	public static void main(String[] args){
		try{
		Socket SOCK = new Socket("localhost", 1024);
		}catch(Exception ex){
			System.out.println(ex.getStackTrace());
		}
	}
}
