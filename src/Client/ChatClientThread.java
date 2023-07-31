package Client;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import javax.swing.DefaultListModel;

public class ChatClientThread extends Thread {

    private Socket socket = null;
    private Client client = null;
    private DataInputStream dis = null;
    private DefaultListModel listModel;

    public ChatClientThread(Client _client, Socket _socket, DefaultListModel _listModel) {
        client = _client;
        socket = _socket;
        listModel = _listModel;
        open();
        start();
    }

    public void open() {
        try {
            dis = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.out.println("Error getting input stream : " + e.getMessage());
            client.stop();
        }
    }

    public void close() {
        try {
            dis.close();
        } catch (IOException e) {
            System.out.println("Error closing input stream : " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                client.handleMessage(dis.readUTF());
            }
        } catch (IOException e) {
            client.stop();
        }
    }
}
