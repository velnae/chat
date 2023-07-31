/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Client;

import Client.Ui.FrmClient;
import Server.Server;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 *
 * @author emerson
 */
public class MainClient {

    private JList<String> lstChat;
    private DefaultListModel<String> listModel;
    private FrmClient frmClient;
    private Client client;
    private JTextField txtMessage;
    private JTextField txtidClient;

    private JButton btnConnect;
    private JButton btnClose;

    public MainClient() {
        frmClient = new FrmClient();
        frmClient.setVisible(true);

        listModel = new DefaultListModel<>();
        lstChat = frmClient.getLstChat();
        lstChat.setModel(listModel);

        txtidClient = frmClient.getTxtIdClient();
        btnConnect = frmClient.getBtnConnect();
        btnClose = frmClient.getBtnClose();

        btnConnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (txtidClient.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "User es required", "Warning", JOptionPane.WARNING_MESSAGE);
                } else {
                    client = new Client(listModel, txtidClient);
                    btnClose.setEnabled(true);
                    btnConnect.setEnabled(false);
                }

            }
        });

        btnClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listModel.clear();
                client.stop();

                btnClose.setEnabled(false);
                btnConnect.setEnabled(true);
            }
        });

        txtMessage = frmClient.getTxtMessage();

        txtMessage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String inputText = txtMessage.getText();
                client.sendMessage(inputText);
                inputText = "";
                txtMessage.setText(inputText);

            }
        });

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        MainClient mainClient = new MainClient();
    }

}
