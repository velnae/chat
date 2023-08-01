package Client;

import Client.Models.Message;

import java.io.*;
import java.net.Socket;
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

    private JProgressBar proBarSendFile;

    public Client(String user, String server, int serverPort, DefaultListModel<String> lsmChat, JProgressBar proBarSendFile) {
        this.lsmChat = lsmChat;
        this.user = user;
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
        lsmChat.addElement("File name: " + fileName + " size: " + fileSize);


        System.out.println("Getting file from server...");
        byte[] myByteArray = new byte[fileSize];

        String projectPath = System.getProperty("user.dir");
        String imagePath = projectPath + "/gambarClient/" + fileName;

        InputStream inputStream = socket.getInputStream();
        int bytesRead = inputStream.read(myByteArray, 0, myByteArray.length);
        int current = bytesRead;
//
            do {
                bytesRead = inputStream.read(myByteArray, current, (myByteArray.length - current));
                if (bytesRead >= 0) {
                    current += bytesRead;
                }
            } while (bytesRead > -1);

        FileOutputStream fileOutputStream  = new FileOutputStream(imagePath);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
        bufferedOutputStream.write(myByteArray, 0, current);
        bufferedOutputStream.flush();

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
        int fileSize = 8192;
        byte[] myByteArray = new byte[(int) file.length()];

        int in = 0;
        try {
            dos.writeUTF(Message.TYPE_FILE);
            dos.flush();
            dos.writeUTF(file.getName() + "," + myByteArray.length);
            dos.flush();
            Thread.sleep(2000);
            FileInputStream fileInputStream = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fileInputStream);
//            bis.read(myByteArray, 0, myByteArray.length);
            long totalBytesSent = 0;
            BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
            while ((in = bis.read(myByteArray)) != -1)
            {
                bos.write(myByteArray,0,in);
                totalBytesSent += in;

                int progress = (int) ((totalBytesSent * 100) / fileSize);
                proBarSendFile.setValue(progress);
            }


//            OutputStream outputStream = socket.getOutputStream();
//            outputStream.write(myByteArray, 0, myByteArray.length);
            bos.flush();
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
