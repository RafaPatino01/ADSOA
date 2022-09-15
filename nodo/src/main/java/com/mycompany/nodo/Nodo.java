/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */

package com.mycompany.nodo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rafaelpatino
 */
public class Nodo {
    
    private static ServerSocket nodo_socket;
    private static int port = 3332;
    
    public static ArrayList<Socket> activeClients = new ArrayList<Socket>();
    public static ArrayList<ObjectOutputStream> activeOutputStreams = new ArrayList<ObjectOutputStream>();
    
    public static void main(String[] args)
    {
        ServerSocket nodo_socket = null;
        // Lista de contratos
        
        try {
  
            nodo_socket = new ServerSocket(port);
            nodo_socket.setReuseAddress(true);
  
            // running infinite loop for getting
            // client request
            while (true) {
  
                if(activeClients == null){
                    System.out.println("[nodo] Esperando conexiones en el puerto: " + Integer.toString(port));
                }
                else {
                    System.out.println("[nodo] Conexiones actuales: ");
                    System.out.println(activeClients);
                }
                
                // socket object to receive incoming client
                // requests
                Socket client = nodo_socket.accept();
  
                // Displaying that new client is connected
                // to server
                System.out.println("[nodo] Nueva conexion: " + client.getRemoteSocketAddress());
  
                
                // create a new thread object
                ClientHandler clientSock = new ClientHandler(client);
  
                // This thread will handle the client
                // separately
                
                activeClients.add(client);
                
                new Thread(clientSock).start();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (nodo_socket != null) {
                try {
                    nodo_socket.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private final ObjectOutputStream oos;
        private final ObjectInputStream ois;
        // Constructor
        public ClientHandler(Socket socket) throws IOException
        {
            this.clientSocket = socket;
            this.oos = new ObjectOutputStream(socket.getOutputStream());
            this.ois = new ObjectInputStream(socket.getInputStream());
            activeOutputStreams.add(oos); // add oos a la lista
        }
  
        public void run()
        {
            try {
                
                while(true){
                    
                    //convert ObjectInputStream object to String
                    String message = (String) ois.readObject();
                    System.out.println("[nodo] (" + clientSocket.getRemoteSocketAddress() + ") Mensaje recibido: " + message);
                    
                    // Broadcast to all active clients
                    for (int i = 0; i < activeOutputStreams.size(); i++) 
                    {
                        ObjectOutputStream temp_oos = activeOutputStreams.get(i);
                        
                        if(temp_oos != oos){
                            temp_oos.writeObject(message);
                            System.out.println("[nodo] Enviando mensaje: " + message + " a " + activeClients.get(i));
                        }
                    }

                }
                
            }
            catch (IOException e) {
                System.out.println("*[nodo] Conexion finalizada con: " + clientSocket.getRemoteSocketAddress());
                
                activeClients.remove(clientSocket);
                activeOutputStreams.remove(oos);
                
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Nodo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
