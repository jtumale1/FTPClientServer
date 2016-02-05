
JFLAGS = -g
JC = javac
JVM= java 
BIN_DIR = FTP/bin/
SRC_DIR = FTP/src/
CLASSPATH = -cp FTP/src/


.SUFFIXES: .java .class


.java.class:
	$(JC) $(CLASSPATH) -d $(BIN_DIR) $(JFLAGS) $*.java


#our classes
CLASSES = \
        $(SRC_DIR)ServerThread.java \
	$(SRC_DIR)ClientThread.java \
	$(SRC_DIR)Server.java \
	$(SRC_DIR)Client.java


MAIN = Server 

#
# the default make target entry
# for this example it is the target classes

default: classes


#handle dependencies

classes: $(CLASSES:.java=.class)



run: $(MAIN).class
	$(JVM) $(MAIN)


# Clean up binary files

clean:
	$(RM) $(BIN_DIR)*.class
