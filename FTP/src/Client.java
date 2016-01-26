import java.net.*;


public class Client {
	public static void main(String[] args){
		try{
		Socket SOCK = new Socket("localhost", 9000);
		}catch(Exception ex){
			System.out.println(ex.getStackTrace());
		}
	}
}
