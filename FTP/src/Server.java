import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.net.Socket;

public class Server {

	private ExecutorService threadPool = Executors.newCachedThreadPool();
	private int port;
	private String address;
	private Socket socket;
	
	public Server(String address, int port){
		this.address = address;
		this.port = port;
	}
	
	public void run(){
		//listen at a port
		
	}

	
	public static void main(String[] args) {
		
	}

}
