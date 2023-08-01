/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Server;

import Server.Ui.FrmServer;

import javax.swing.*;

/**
 *
 * @author emerson
 */
public class MainServer {
    
    private JList<String> lstLog;
    private DefaultListModel<String> lsmLog;
    private JList<String> lstUsers;
    private DefaultListModel<String> lsmUsers;
    private final FrmServer frmServer;

    private JButton btnStart;
    private JButton btnDisconnect;
    private JButton btnClear;
    private Server server;

    public MainServer(){
        frmServer = new FrmServer();
        frmServer.setVisible(true);

        initComponents();

        initEvents();
    }

    private void initComponents(){
        lsmLog = new DefaultListModel<>();
        lsmUsers = new DefaultListModel<>();

        lstLog = frmServer.getLstLog();
        lstUsers = frmServer.getLstUsers();
        btnStart = frmServer.getBtnStart();
        btnDisconnect = frmServer.getBtnDisconnect();
        btnClear = frmServer.getBtnClear();

        lstLog.setModel(lsmLog);
        lstUsers.setModel(lsmUsers);
    }

    private void initEvents() {
        btnStart.addActionListener(e -> startAction());

        btnDisconnect.addActionListener(e -> disconnectAction());

        btnClear.addActionListener(e -> clearAction());
    }

    public void startAction(){
        server = new Server(lsmLog, lsmUsers);
        steEnableButtonsOnConnect(true);
    }

    private void disconnectAction(){
        server.stop();
        lsmUsers.clear();
        steEnableButtonsOnConnect(false);
    }

    private void clearAction(){
        lsmLog.clear();
    }

    private void steEnableButtonsOnConnect(Boolean  enable){
        btnStart.setEnabled(!enable);
        btnDisconnect.setEnabled(enable);
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        MainServer mainServer = new MainServer();
    }
}
