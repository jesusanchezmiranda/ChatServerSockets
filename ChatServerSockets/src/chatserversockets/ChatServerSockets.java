package chatserversockets;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;


public class ChatServerSockets {

    private ServerSocket servicio;
    private boolean run = true;
    private List<ChatServerThread> serverThreads = new ArrayList<>();
    private static ClientList clientList;

    public ChatServerSockets(int port) {

        try {
            servicio = new ServerSocket(port);

        } catch (IOException e) {
            
        }

    }

    public List<ChatServerThread> getServerThreads() {
        return serverThreads;
    }

    public void setServerThreads(List<ChatServerThread> serverThreads) {
        this.serverThreads = serverThreads;
    }

    public void startService() {

        Thread hebraprincipal = new Thread() {
            @Override
            public void run() {
                Socket servidor;
                ChatServerThread serverthread;

                while (run) {
                    try {
                        servidor = servicio.accept();
                        serverthread = new ChatServerThread(ChatServerSockets.this, servidor);
                        serverThreads.add(serverthread);
                        serverthread.setId(serverThreads.indexOf(serverthread));
                        serverthread.start();
                        listClients();

                    } catch (IOException ex) {        
                    }
                }
            }
        };

        hebraprincipal.start();

    }

    public void listClients() {
        if(serverThreads.isEmpty()){
            serverThreads.clear();
            clientList.writeOnList(serverThreads);
            JOptionPane.showMessageDialog(null, "There aren`t users");
        }else{
            clientList.writeOnList(serverThreads);
        }
        
    }

    public void broadcast(String text) {
        for (ChatServerThread client : serverThreads) {
            client.send(text);
        }
    }

    public void broadcastPrivate(String text, String user, String nombre) {
        for (ChatServerThread client : serverThreads) {
            if (client.getNombre().equalsIgnoreCase(user)) {
                client.send(nombre + "(private)" + "-> " + text);
            }
        }
    }

    public static void main(String[] args) {
        ChatServerSockets chatServer = new ChatServerSockets(5000);
        clientList = new ClientList();
        clientList.setVisible(true);
        chatServer.startService();

    }

}
