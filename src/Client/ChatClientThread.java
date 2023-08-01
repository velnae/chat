package Client;

import Client.Models.Message;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import javax.swing.DefaultListModel;

public class ChatClientThread extends Thread {

    private Socket socket = null;
    private Client client = null;
    private DataInputStream dis = null;

    public ChatClientThread(Client _client, Socket _socket) {
        client = _client;
        socket = _socket;
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
                String messageType = dis.readUTF();
                switch (messageType) {
                    case Message.TYPE_TEXT:
                        client.handleText(dis.readUTF());
                        break;
                    case Message.TYPE_FILE:
                        client.handleFile(dis.readUTF());
                        break;

                    default:
                        break;
                }

            }
        } catch (IOException e) {
            client.stop();
        }
    }
}
