package Server;

import java.io.*;
import java.net.Socket;
import java.net.SocketAddress;
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
    private DefaultListModel listModel;
    private String idClient = null;

    public ChatServerThread(Server _server, Socket _socket, DefaultListModel _listModel) {
        super();
        try {
            server = _server;
            socket = _socket;
            ID = socket.getRemoteSocketAddress();
            listModel = _listModel;

            bis = new BufferedInputStream(socket.getInputStream());
            dis = new DataInputStream(bis);
            bos = new BufferedOutputStream(socket.getOutputStream());
            dos = new DataOutputStream(bos);
            idClient = dis.readUTF();
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
            listModel.addElement("Client " + socket.getRemoteSocketAddress() + " error sending : " + e.getMessage());
            server.remove(ID);
        }
    }

    @Override
    public void run() {
        try {
            listModel.addElement("Client " + socket.getRemoteSocketAddress() + " connected to server...");

            while (true) {
                server.handle(ID, idClient, dis.readUTF());
            }
        } catch (IOException e) {
            //listModel.addElement("Client " + socket.getRemoteSocketAddress() + " error reading : " + e.getMessage());
            server.remove(ID);
        }
    }

    public void close() throws IOException {
        listModel.addElement("Client " + socket.getRemoteSocketAddress() + " disconnect from server...");
        socket.close();
        dis.close();
        dos.close();
    }
}
