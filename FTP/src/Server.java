import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.IOException;
import java.net.*;

public class Server {

	private ExecutorService threadPool = Executors.newCachedThreadPool();
	private final int BACKLOG = 20;
	private int port;
	private String address = "localhost";
	private ServerSocket socket;
	private volatile Boolean active = true;
	
	
	public Server(String address, int port){
		this.address = address;
		this.port = port;
	}
	
	public Server(int port){
		this.port = port;
	}
	
	public void run() throws IOException{
		//create a socket
		try {
			this.socket = new ServerSocket(
					this.port, 
					this.BACKLOG, 
					InetAddress.getLocalHost()
					);
			//bind port
			this.socket.bind(new InetSocketAddress(this.address, this.port));
			
			do{
				Socket client = this.socket.accept();
			
				this.threadPool.execute(new ServerThread(client, this.active));
			
			}while(active);
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			this.socket.close();
		}
		
	}

	
	public static void main(String[] args) {
		
	}

}
