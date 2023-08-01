package Client;

import Client.Models.Message;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

// Client for Server4
public class Client {
    private Socket socket = null;
    private DataOutputStream dos = null;
    private ChatClientThread client = null;
    private final DefaultListModel<String> lsmChat;
    private String user = null;
    private String server = null;
    private int serverPort = 0;

    private JProgressBar proBarSendFile;

    public Client(String user, String server, int serverPort, DefaultListModel<String> lsmChat, JProgressBar proBarSendFile) {
        this.lsmChat = lsmChat;
        this.user = user;
        this.server = server;
        this.serverPort = serverPort;
        this.proBarSendFile = proBarSendFile;

        try {
            socket = new Socket(server, serverPort);
            dos = new DataOutputStream(socket.getOutputStream());

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
        dos.flush();
    }

    private void getFirstMessages() throws Exception {
        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
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

    public void handleText(String message) {
        lsmChat.addElement(message);
    }


    public void handleFile(String input) throws IOException {
        String[] fileInfo = input.split(",");
        String fileName = fileInfo[0];
        int fileSize = Integer.parseInt(fileInfo[1]);
        lsmChat.addElement("Server: received " + fileName + " size: " + fileSize);

        System.out.println("Getting file from server...");

        BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());

        byte[] buffer = new byte[4098];
        String projectPath = System.getProperty("user.dir");
        String filePath = projectPath + "/fileClient/" + fileName;

        FileOutputStream fileOutputStream = new FileOutputStream(filePath);
        BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);
        long totalBytesSent = 0;
        int bytesRead;

        while ((bytesRead = bis.read(buffer)) != -1) {
            bos.write(buffer, 0, bytesRead);
            bos.flush();

            totalBytesSent += bytesRead;

            int progress = (int) ((totalBytesSent * 100) / fileSize);
            proBarSendFile.setValue(progress);
        }

        bos.flush();
        System.out.println("Get file success...");
    }

    public void sendMessage(String message) {
        try {
            lsmChat.addElement("me : " + message);
            dos.writeUTF(Message.TYPE_TEXT);
            dos.flush();
            dos.writeUTF(message);
            dos.flush();
        } catch (IOException e) {
            System.out.println("Sending error: " + e.getMessage());
            stop();
        }
    }

    public void sendFile(File file) {
        int fileSize = (int) file.length();
        byte[] myByteArray = new byte[(int) 4098];
        lsmChat.addElement("Server:  sended file" + file.getName() + " size: " + fileSize);

        int in = 0;
        try {
            dos.writeUTF(Message.TYPE_FILE);
            dos.flush();
            dos.writeUTF(file.getName() + "," + fileSize);
            dos.flush();

            Thread.sleep(2000);

            FileInputStream fileInputStream = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fileInputStream);
            long totalBytesSent = 0;
            BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());

            while ((in = bis.read(myByteArray)) != -1)
            {
                bos.write(myByteArray,0,in);
                totalBytesSent += in;

                int progress = (int) ((totalBytesSent * 100) / fileSize);
                proBarSendFile.setValue(progress);
            }

            bos.flush();
            bos.close();
            System.out.println("Send file success...  " + myByteArray.length);
        } catch (Exception ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void stop() {
        try {
            dos.close();
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
