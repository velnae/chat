/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Server;

import Server.Ui.FrmServer;
import javax.swing.DefaultListModel;
import javax.swing.JList;

/**
 *
 * @author emerson
 */
public class MainServer {
    
    private JList<String> lstLog;
    private DefaultListModel<String> listModel;
    private FrmServer frmServer;
    private Server server;

    public MainServer(){
        frmServer = new FrmServer();
        listModel = new DefaultListModel<>();
        lstLog = frmServer.getLstLog();
        lstLog.setModel(listModel);   
        server = new Server(listModel);
        
        frmServer.setVisible(true);
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        MainServer mainServer = new MainServer();
                
    }
    
}
