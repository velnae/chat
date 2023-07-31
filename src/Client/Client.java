package Client;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JTextField;

// Client for Server4
public class Client {
    private Socket socket = null;
    private DataOutputStream dos = null;
    private ObjectInputStream ois = null;
    private ChatClientThread client = null;
    private final DefaultListModel<String> lsmChat;

    private String user = null;

    public Client(String user, String server, int serverPort, DefaultListModel<String> lsmChat) {
        this.lsmChat = lsmChat;
        this.user = user;

        try {
            socket = new Socket(server, serverPort);
            dos = new DataOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());

            lsmChat.addElement("Client started on port " + socket.getLocalPort());
            lsmChat.addElement("Connected to server " + socket.getRemoteSocketAddress());

            identifyUser();
            getFirstMessages();

            client = new ChatClientThread(this, socket);

        } catch (Exception e) {
            lsmChat.addElement("Error : " + e.getMessage());
        }
    }

    private void identifyUser() throws IOException {
        dos.writeUTF(user);
    }

    private void getFirstMessages() throws Exception {
        Object receivedObject = ois.readObject();

        if (receivedObject instanceof ArrayList<?>) {
            ArrayList<String> messages = (ArrayList<String>) receivedObject;
            for (String message : messages) {
                message = message.replaceAll(user, "me");
                lsmChat.addElement(message);
            }
        } else {
            throw new Exception("Server send wrong initial messages");
        }
    }

    public void handleMessage(String message) {
        lsmChat.addElement(message);
    }

    public void sendMessage(String message) {
        try {
            lsmChat.addElement("me : " + message);
            dos.writeUTF(message); // Enviar el mensaje al servidor
            dos.flush();
        } catch (IOException e) {
            System.out.println("Sending error: " + e.getMessage());
            stop();
        }
    }

    public void sendFile(String filePath) {
        File myFile = new File(filePath);
        byte[] myByteArray = new byte[(int) myFile.length()];

        try {
            dos.writeUTF("message:type:file");
            FileInputStream fileInputStream = new FileInputStream(myFile);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            bufferedInputStream.read(myByteArray, 0, myByteArray.length);

            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(myByteArray, 0, myByteArray.length);
            outputStream.flush();
            System.out.println("Send file success...");
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void stop() {
        try {
            dos.close();
            ois.close();
            socket.close();
        } catch (IOException e) {
            lsmChat.addElement("Error closing : " + e.getMessage());
        }
        client.close();
    }

    public static void main(String args[]) {
//        Client client = new Client();
    }
}
