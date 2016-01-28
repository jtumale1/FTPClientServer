all: FTP/src/Client.java FTP/src/ClientThread.java FTP/src/Server.java FTP/src/ServerThread.java
	javac -d ../bin/ FTP/src/Client.java
	javac -d ../bin/ FTP/src/ClientThread.java
	javac -d ../bin/ FTP/src/Server.java
	javac -d ../bin/ FTP/src/ServerThread.java
server: FTP/bin/Server.class FTP/bin/ServerThread.class
	java FTP/bin/Server
client: FTP/bin/Client.class FTP/bin/ClientThread.class
	java FTP/bin/Client
clean:
	rm -f FTP/bin/*.class