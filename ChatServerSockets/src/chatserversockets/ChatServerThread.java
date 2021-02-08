package chatserversockets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class ChatServerThread extends Thread{
    
    private int id;
    private final Socket servidor;
    private DataInputStream flujoE;
    private DataOutputStream flujoS;
    private boolean run = true;
    private String nombre ="";
    private ChatServerSockets server;

    public ChatServerThread(ChatServerSockets server, Socket servidor) {
        this.servidor = servidor;
        this.server = server;
        
        try {
            flujoE = new DataInputStream(servidor.getInputStream());
            flujoS = new DataOutputStream(servidor.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(ChatServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void run(){
        String text;
        nombre = JOptionPane.showInputDialog("Introduce tu nombre de usuario");
        server.listClients();
        while(run){
            try {
                text = flujoE.readUTF();
                if(nombre.equals("")){
                    setNombre(text);
                }else{
                    if(text.contains("::")){
                        String[] parts = text.split("::");
                        String user = parts[0]; 
                        String msj = parts[1]; 
                        server.broadcastPrivate(msj+"\n", user, nombre);
                    }else{
                        
                        if(text.equals("XYZXY")){
                            for (int i = 0; i < server.getServerThreads().size(); i++) {
                                if(server.getServerThreads().get(i).getNombre().equals(nombre)){
                                    server.getServerThreads().remove(server.getServerThreads().get(i));
                                }
                                if(server.getServerThreads().isEmpty()){
                                    server.getServerThreads().removeAll(server.getServerThreads());
                                }
                            }
                            break;
                        }
                        server.broadcast(nombre+"-> " + text+ "\n");
                        
                    }
                    
                }
                
            } catch (IOException ex) {
                System.out.println(ex.getLocalizedMessage());
            }
        }
        
        try {
            flujoE.close();
            flujoS.close();
            servidor.close();
            server.listClients();
        } catch (IOException ex1) {
        }
        
        
    }
    
    public void send(String text){
        try {
                flujoS.writeUTF(text);
                flujoS.flush();//siempre despues de un write para limpiar el buffer
        } catch (IOException ex) {
        }
    }
    
    public void setId(int id){
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }
    
    
    
    
}
