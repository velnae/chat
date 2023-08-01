import Client.MainClient;
import Server.MainServer;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        MainServer serverMain = new MainServer();
        serverMain.startAction();
        Thread.sleep(1000);
        MainClient mainClient = new MainClient("Emerson");
        mainClient.connectAction();
        Thread.sleep(1000);
        MainClient mainClient2 = new MainClient("Juan");
        mainClient2.connectAction();
    }
}
