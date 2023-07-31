package Server;

import java.awt.List;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.text.Element;

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
    private DefaultListModel lsmLog;
    private DefaultListModel lsmUsers;
    private ArrayList<String> storedMessages = new ArrayList<>();

    public Server(DefaultListModel lsmLog, DefaultListModel lsmUsers) {
        this.lsmLog = lsmLog;
        this.lsmUsers = lsmUsers;

        try {
            serverSocket = new ServerSocket(port);

            lsmLog.addElement("Server started on port " + serverSocket.getLocalPort() + "...");
            lsmLog.addElement("Waiting for client...");
            thread = new Thread(this);
            thread.start();
        } catch (IOException e) {
            lsmLog.addElement("Can not bind to port : " + e);
        }
    }

    @Override
    public void run() {
        while (thread != null) {
            try {
                // wait until client socket connecting, then add new thread
                Socket socket = serverSocket.accept();
                addThreadClient(socket);
            } catch (IOException e) {
                lsmLog.addElement("Server accept error : " + e);
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
            String messageToSend = idClient + " says : " + input;
            storedMessages.add(messageToSend);
            for (int i = 0; i < clientCount; i++) {
                if (clients[i].getID() == ID) {
                    // if this client ID is the sender, just skip it
                    continue;
                }
                clients[i].send(messageToSend);
            }
        }
    }

    public synchronized void remove(SocketAddress ID) {
        int index = findClient(ID);
        if (index >= 0) {

            ChatServerThread threadToTerminate = clients[index];
            lsmLog.addElement("Removing client thread " + ID + " at " + index);
            lsmUsers.remove(index);

            if (index < clientCount - 1) {
                for (int i = index + 1; i < clientCount; i++) {
                    clients[i - 1] = clients[i];
                }
            }
            clientCount--;
            try {
                threadToTerminate.close();
            } catch (IOException e) {
                lsmLog.addElement("Error closing thread : " + e.getMessage());
            }
        }
    }

    private void addThreadClient(Socket socket) {
        if (clientCount < clients.length) {
            clients[clientCount] = new ChatServerThread(this, socket, lsmLog);
            clients[clientCount].start();
            clientCount++;
        } else {
            lsmLog.addElement("Client refused : maximum " + clients.length + " reached.");
        }
    }

    public synchronized ArrayList<String> getStoredMessages() {
        return new ArrayList<>(storedMessages); // Return a copy of the stored messages to avoid direct modification
    }

    public void addUser(String user) {
        lsmUsers.addElement(user);
    }

    public static void main(String[] args) {
//        Server server = new Server();
    }
}
