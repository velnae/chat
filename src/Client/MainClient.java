/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Client;

import Client.Ui.FrmClient;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.*;

/**
 * @author emerson
 */
public class MainClient {

    private JList<String> lstChat;
    private DefaultListModel<String> lsmChat;
    private FrmClient frmClient;
    private Client client;
    private JTextField txtMessage;
    private JTextField txtUser;
    private JTextField txtServer;
    private JTextField txtServerPort;

    private JButton btnConnect;
    private JButton btnClose;
    private JButton btnAttach;
    private JButton btnSend;

    private JProgressBar proBarSendFile;

    public MainClient() {
        frmClient = new FrmClient();
        frmClient.setVisible(true);

        initComponents();

        initEvents();
    }

    public MainClient(String user) {
        frmClient = new FrmClient();
        frmClient.setVisible(true);

        initComponents();

        initEvents();

        txtUser.setText(user);
    }

    private void initEvents() {
        btnConnect.addActionListener(e -> connectAction());

        btnClose.addActionListener(e -> closeAction());

        txtMessage.addActionListener(e -> messageAction());

        btnAttach.addActionListener(e -> frmClient.showFileChooser());

        btnSend.addActionListener(e -> sendAction());
    }


    public void connectAction() {

        String user = txtUser.getText();
        String server = txtServer.getText();
        int serverPort = Integer.parseInt(txtServerPort.getText());


        if (user.isEmpty() || server.isEmpty() || serverPort < 1) {
            JOptionPane.showMessageDialog(null, "User, server and port is required", "Warning", JOptionPane.WARNING_MESSAGE);
        } else {
            client = new Client(user, server, serverPort, lsmChat, proBarSendFile);
            setEditableConnectionComponents(false);
        }
    }

    private void messageAction() {
        String inputText = txtMessage.getText();
        if (!inputText.isEmpty()) {
            client.sendMessage(inputText);
            inputText = "";
            txtMessage.setText(inputText);
        }
    }

    private void closeAction() {
        lsmChat.clear();
        client.stop();
        proBarSendFile.setValue(0);

        setEditableConnectionComponents(true);
    }

    private void sendAction(){
        if (btnAttach.getText().equals(FrmClient.ATTACH)) {
            messageAction();
        } else {
            client.sendFile(frmClient.getSelectedFile());
            frmClient.appearanceToSendMessage();
        }
    }

    private void setEditableConnectionComponents(Boolean editable) {
        txtUser.setEditable(editable);
        txtServer.setEditable(editable);
        txtServerPort.setEditable(editable);
        btnClose.setEnabled(!editable);
        btnConnect.setEnabled(editable);
    }

    private void initComponents() {
        lsmChat = new DefaultListModel<>();
        lstChat = frmClient.getLstChat();
        lstChat.setModel(lsmChat);

        txtUser = frmClient.getTxtUser();
        txtServer = frmClient.getTxtServer();
        txtServerPort = frmClient.getTxtServerPort();
        txtMessage = frmClient.getTxtMessage();

        btnConnect = frmClient.getBtnConnect();
        btnClose = frmClient.getBtnClose();
        btnAttach = frmClient.getBtnAttach();
        btnSend = frmClient.getBtnSend();

        proBarSendFile = frmClient.getProBarSendFile();
        proBarSendFile.setStringPainted(true);
        proBarSendFile.setValue(0);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        MainClient mainClient = new MainClient();
    }

}
