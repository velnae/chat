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
    private DefaultListModel<String> lsmLog;
    private JList<String> lstUsers;
    private DefaultListModel<String> lsmUsers;
    private FrmServer frmServer;
    private Server server;

    public MainServer(){
        frmServer = new FrmServer();
        
        lsmLog = new DefaultListModel<>();
        lsmUsers = new DefaultListModel<>();
        
        lstLog = frmServer.getLstLog();
        lstUsers = frmServer.getLstUsers();
        
        lstLog.setModel(lsmLog);   
        lstUsers.setModel(lsmUsers);  
        
        server = new Server(lsmLog, lsmUsers);
        
        frmServer.setVisible(true);
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        MainServer mainServer = new MainServer();
                
    }
    
}
