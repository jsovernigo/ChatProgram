JC		=javac

all: server client

server: chatserver/ChatServer.java
	$(JC) chatserver/ChatServer.java

client: chatclient/ChatClientDriver.java
	$(JC) chatclient/ChatClientDriver.java

clean:
	rm chatclient/*.class -R
	rm chatserver/*.class -R

