package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import javax.swing.DefaultListModel;

// SERVER : Multi Server
// TIPE : Two-Way Communication (Client to Server, Server to Client)
// DESCRIPTION : 
// A simple server that will accept multi client connection and display everything the client says on the screen. 
// The Server can handle multiple clients simultaneously.
// The Server can sends all text received from any of the connected clients to all clients, 
// this means the Server has to receive and send, and the client has to send as well as receive.
// If the client user types "exit", the client will quit.
public class Server implements Runnable {

    private int port = 8081;
    private ServerSocket serverSocket = null;
    private Thread thread = null;
    private ChatServerThread clients[] = new ChatServerThread[50];
    private int clientCount = 0;
    private DefaultListModel listModel;

    public Server(DefaultListModel listModel) {
        this.listModel = listModel;

        try {
            serverSocket = new ServerSocket(port);

            listModel.addElement("Server started on port " + serverSocket.getLocalPort() + "...");
            listModel.addElement("Waiting for client...");
            thread = new Thread(this);
            thread.start();
        } catch (IOException e) {
            listModel.addElement("Can not bind to port : " + e);
        }
    }    
    
    
    @Override
    public void run() {
        while (thread != null) {
            try {
                // wait until client socket connecting, then add new thread
                addThreadClient(serverSocket.accept());
            } catch (IOException e) {
                listModel.addElement("Server accept error : " + e);
                stop();
            }
        }
    }

    public void stop() {
        if (thread != null) {
            thread = null;
        }
    }

    private int findClient(SocketAddress ID) {
        for (int i = 0; i < clientCount; i++) {
            if (clients[i].getID() == ID) {
                return i;
            }
        }
        return -1;
    }

    public synchronized void handle(SocketAddress ID, String idClient, String input) {
        if (input.equals("exit")) {
            clients[findClient(ID)].send("exit");
            remove(ID);
        } else {
            for (int i = 0; i < clientCount; i++) {
                if (clients[i].getID() == ID) {
                    // if this client ID is the sender, just skip it
                    continue;
                }
                clients[i].send("\n" + idClient + " says : " + input);
            }
        }
    }

    public synchronized void remove(SocketAddress ID) {
        int index = findClient(ID);
        if (index >= 0) {
            ChatServerThread threadToTerminate = clients[index];
            listModel.addElement("Removing client thread " + ID + " at " + index);
            if (index < clientCount - 1) {
                for (int i = index + 1; i < clientCount; i++) {
                    clients[i - 1] = clients[i];
                }
            }
            clientCount--;
            try {
                threadToTerminate.close();
            } catch (IOException e) {
                listModel.addElement("Error closing thread : " + e.getMessage());
            }
        }
    }

    private void addThreadClient(Socket socket) {
        if (clientCount < clients.length) {
            clients[clientCount] = new ChatServerThread(this, socket, listModel);
            clients[clientCount].start();
            clientCount++;
        } else {
            listModel.addElement("Client refused : maximum " + clients.length + " reached.");
        }
    }

    public static void main(String[] args) {
//        Server server = new Server();
    }
}
