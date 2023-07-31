package Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import javax.swing.DefaultListModel;
import javax.swing.JTextField;

// Client for Server4
public class Client {

    private String serverName = "localhost";
    private int serverPort = 8081;
    private Socket socket = null;
    private Thread thread = null;
    private DataInputStream dis = null;
    private DataOutputStream dos = null;
    private ChatClientThread client = null;
    private DefaultListModel listModel;
    private JTextField txtidClient;
    
    public Client(DefaultListModel listModel, JTextField txtidClient) {
        this.listModel = listModel;
        this.txtidClient = txtidClient;
        
        try {
            socket = new Socket(serverName, serverPort);
            listModel.addElement("Client started on port " + socket.getLocalPort() + "...");
            listModel.addElement("Connected to server " + socket.getRemoteSocketAddress());

            dis = new DataInputStream(System.in);
            dos = new DataOutputStream(socket.getOutputStream());
            dos.writeUTF(this.txtidClient.getText());
            client = new ChatClientThread(this, socket, this.listModel);
        } catch (IOException e) {
            listModel.addElement("Error : " + e.getMessage());
        }
    }

    public void handleMessage(String message) {
        if (message.equals("exit")) {
            stop();
        } else {
            listModel.addElement(message);
            System.out.print("Message to server : ");
        }
    }

    public void sendMessage(String message) {
        try {
            listModel.addElement("me : " + message);
            dos.writeUTF(message); // Enviar el mensaje al servidor
            dos.flush();
        } catch (IOException e) {
            System.out.println("Sending error: " + e.getMessage());
            stop();
        }
    }
    
    public void stop() {
        try {
            thread = null;
            dis.close();
            dos.close();
            socket.close();
        } catch (IOException e) {
            listModel.addElement("Error closing : " + e.getMessage());
        }
        client.close();
    }

    public static void main(String args[]) {
//        Client client = new Client();
    }
}
