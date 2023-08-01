package Server;

import Client.Models.Message;

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

            registerUser();
            sendFirstMessages();

        } catch (IOException ex) {
            Logger.getLogger(ChatServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void registerUser() throws IOException {
        user = dis.readUTF();
        server.addUser(user);
    }

    private void sendFirstMessages() throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(server.getStoredMessages());
        oos.flush();
    }

    public SocketAddress getID() {
        return ID;
    }

    public void sendText(String message) {
        try {
            dos.writeUTF(Message.TYPE_TEXT);
            dos.flush();
            dos.writeUTF(message);
            dos.flush();
        } catch (IOException e) {
            lsmLog.addElement("Client " + socket.getRemoteSocketAddress() + " error sending : " + e.getMessage());
            server.remove(ID);
        }
    }

    public synchronized void sendFile(String input, String name, int size) {

        try {

            System.out.println("Getting file from server...");
            int FILE_SIZE = 6022386;
            byte[] myByteArray = new byte[FILE_SIZE];

            String projectPath = System.getProperty("user.dir");
            String imagePath = projectPath + "/gambarClient/" + name;

            InputStream inputStream = socket.getInputStream();
            int bytesRead = inputStream.read(myByteArray, 0, myByteArray.length);
            int current = bytesRead;
//            do {
//                bytesRead = inputStream.read(myByteArray, current, (myByteArray.length - current));
//                if (bytesRead >= 0) {
//                    current += bytesRead;
//                }
//            } while (bytesRead > -1);

            FileOutputStream fileOutputStream  = new FileOutputStream(imagePath);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
            bufferedOutputStream.write(myByteArray, 0, bytesRead);
            bufferedOutputStream.flush();

            System.out.println("Get file success...");

//            dos.writeUTF(Message.TYPE_FILE);
//            dos.flush();
//            dos.writeUTF(input);
//            dos.flush();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() throws IOException {
        lsmLog.addElement("Client " + socket.getRemoteSocketAddress() + " disconnect from server...");
        socket.close();
        dis.close();
        dos.close();
    }

    @Override
    public void run() {
        try {
            lsmLog.addElement("Client " + socket.getRemoteSocketAddress() + " connected to server...");

//            InputStream inputStream = socket.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());

            while (true) {

                String messageType = dis.readUTF();
                String messageType2 = dis.readUTF();

                System.out.println("Getting file from server...");
                String[] dataFile = messageType2.split(",");

                String nameFile = dataFile[0];
                int sizeFile = Integer.parseInt(dataFile[1]);

                int FILE_SIZE = sizeFile;
                byte[] myByteArray = new byte[FILE_SIZE];
                String projectPath = System.getProperty("user.dir");
                String imagePath = projectPath + "/gambarClient/" + nameFile;


                Thread.sleep(2000);
//                int bytesRead = inputStream.read(myByteArray, 0, myByteArray.length);
                bos = new BufferedOutputStream(new FileOutputStream(imagePath));

                int in;
                while ((in = bis.read(myByteArray)) != -1){
                    bos.write(myByteArray,0,in);
                }
//                bos.close();
//                do {
//                    bytesRead = inputStream.read(myByteArray, current, (myByteArray.length - current));
//                    if (bytesRead >= 0) {
//                        current += bytesRead;
//                    }
//                } while (bytesRead > -1);

//                FileOutputStream fileOutputStream = new FileOutputStream(imagePath);
//                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
//                bufferedOutputStream.write(myByteArray, 0, current);
                bos.flush();
//
                System.out.println("Get file success...");

//                String messageType = dis.readUTF();
//                switch (messageType) {
//                    case Message.TYPE_TEXT:
//                        server.handleText(ID, user, dis.readUTF());
//                        break;
//
//                    case Message.TYPE_FILE:
//                        server.handleFile(ID, user, dis.readUTF(), socket);
//                        break;
//
//                    default:
//                        break;
//                }
            }
        } catch (Exception e) {
            System.out.println("Client " + socket.getRemoteSocketAddress() + " error reading : " + e.getMessage());
            server.remove(ID);
        }
    }

}
