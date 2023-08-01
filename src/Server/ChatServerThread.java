package Server;

import Client.Models.Message;

import java.awt.List;
import java.io.*;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
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

    public String getFile(String input, String name, int size) {
        try {
            System.out.println("Getting file from server...");
            BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());

            byte[] buffer = new byte[4098];
            String projectPath = System.getProperty("user.dir");
            String filePath = projectPath + "/fileServer/" + name;

            bos = new BufferedOutputStream(Files.newOutputStream(Paths.get(filePath)));

            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);

            int bytesRead;
            while ((bytesRead = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
                bos.flush();
            }

            bos.flush();
            System.out.println("Get file success...");
            return filePath;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public void sendFile(String input, String filePath) {
        File file = new File(filePath);
        int fileSize = (int) file.length();
        byte[] myByteArray = new byte[(int) 4098];

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

            int in;
            while ((in = bis.read(myByteArray)) != -1) {
                bos.write(myByteArray, 0, in);
                totalBytesSent += in;
            }

            bos.flush();
            bos.close();
        } catch (Exception e) {
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

            while (true) {

                String messageType = dis.readUTF();
                switch (messageType) {
                    case Message.TYPE_TEXT:
                        server.handleText(ID, user, dis.readUTF());
                        break;

                    case Message.TYPE_FILE:
                        server.handleFile(ID, user, dis.readUTF());
                        break;

                    default:
                        break;
                }
            }
        } catch (Exception e) {
            System.out.println("Client " + socket.getRemoteSocketAddress() + " error reading : " + e.getMessage());
            server.remove(ID);
        }
    }

}
