package Server;

import java.awt.List;
import java.io.*;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;

public class ChatServerThread extends Thread {

    private Server server = null;
    private Socket socket = null;
    private SocketAddress ID = null;
    private BufferedInputStream bis = null;
    private DataInputStream dis = null;
    private BufferedOutputStream bos = null;
    private DataOutputStream dos = null;
    private DefaultListModel<String> lsmLog;
    private String user = null;

    public ChatServerThread(Server _server, Socket _socket, DefaultListModel<String> lsmLog) {
        super();
        try {
            server = _server;
            socket = _socket;
            ID = socket.getRemoteSocketAddress();
            this.lsmLog = lsmLog;

            bis = new BufferedInputStream(socket.getInputStream());
            dis = new DataInputStream(bis);

            bos = new BufferedOutputStream(socket.getOutputStream());
            dos = new DataOutputStream(bos);

            user = dis.readUTF();
            server.addUser(user);

            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(server.getStoredMessages());
            oos.flush();

        } catch (IOException ex) {
            Logger.getLogger(ChatServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public SocketAddress getID() {
        return ID;
    }

    public void send(String message) {
        try {
            dos.writeUTF(message);
            dos.flush();
        } catch (IOException e) {
            lsmLog.addElement("Client " + socket.getRemoteSocketAddress() + " error sending : " + e.getMessage());
            server.remove(ID);
        }
    }

    @Override
    public void run() {
        try {
            lsmLog.addElement("Client " + socket.getRemoteSocketAddress() + " connected to server...");

            while (true) {
                server.handle(ID, user, dis.readUTF());
            }
        } catch (IOException e) {
            //lsmLog.addElement("Client " + socket.getRemoteSocketAddress() + " error reading : " + e.getMessage());
            server.remove(ID);
        }
    }

    public void close() throws IOException {
        lsmLog.addElement("Client " + socket.getRemoteSocketAddress() + " disconnect from server...");
        socket.close();
        dis.close();
        dos.close();
    }
}
