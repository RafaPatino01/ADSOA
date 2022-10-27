package com.mycompany.nodo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author rafaelpatino
 */
public class Nodo {
    
    public static InetAddress host;
    private static ServerSocket nodo_socket;
    private static int port = 3332;
    
    public static ArrayList<Socket> active_connections = new ArrayList<Socket>();
    public static ArrayList<ObjectOutputStream> active_output_streams = new ArrayList<ObjectOutputStream>();
    
    public static int port_min = 3000;
    public static int port_max = 3100;
    
    public static void main(String[] args) throws ClassNotFoundException
    {
        
        ServerSocket nodo_server_socket = null;
        
        // Find other NODES that are already running
        for (int i = port_min; i < port_max; i++) {
            try {
                //Create socket
                host = InetAddress.getLocalHost();
                Socket s = new Socket(host.getHostName(), i);
                
                
                //Indicate that this is a node searching for a free port
                ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());  
                oos.writeObject("tipo-nodo");
                
                ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
                System.out.println("Conexión establecida con Nodo: " + i + " | (Mensaje): " + ois.readObject());
                
                // create handler
                NodeHandler nodoSock = new NodeHandler(s, oos, ois);
                new Thread(nodoSock).start();
                
                //oos.close();??????????
                //ois.close();
                //s.close();
                
            } catch(ConnectException e) {
                //If connection was not possible the port is available
                System.out.println("Puerto disponible encontrado: " + i);
                try {
                    //Create a server socket to listen this port
                   nodo_server_socket = new ServerSocket(i);
                   port = i;
                   break;
                } catch (IOException ex) {
                    System.out.println(ex);
                }
                break;
            } catch (Exception e) {
                System.out.println(e);
            }
        
        }
        
        try {
  
            // nodo_socket = new ServerSocket(port); 
            // cuando encontró un socket disponible para ofertar conexión
            nodo_socket = nodo_server_socket;
            nodo_socket.setReuseAddress(true);
  
            // ---- HANDLE Connections to this node ----
            while (true) {
                
                if(active_connections.isEmpty()){
                    System.out.println("[nodo] Esperando conexiones en el puerto: " + Integer.toString(port));
                }
                else {
                    System.out.println("[nodo] Conexiones actuales: " + "("+ Integer.toString(active_connections.size()) +") " + active_connections);
                }
                
                // socket object to receive incoming client requests
                System.out.println("[nodo] Esperando conexiones en el puerto: " + Integer.toString(port));
                Socket connection = nodo_socket.accept();
                
                // Respond to this connection
                ObjectOutputStream temp_oos = new ObjectOutputStream(connection.getOutputStream());
                ObjectInputStream temp_ois = new ObjectInputStream(connection.getInputStream());
                
                String recieved_msg = (String) temp_ois.readObject();
                
                if(recieved_msg.equals("tipo-nodo")){
                    temp_oos.writeObject("quehubo bro");
                    System.out.println("[nodo] Nueva conexion nodo: " + connection.getRemoteSocketAddress());

                    // Handle connection with another node
                    NodeHandler nodoSock = new NodeHandler(connection, temp_oos, temp_ois);
                    
                    new Thread(nodoSock).start();
                } 
                else{
                    temp_oos.writeObject("quehubo bro");
                    System.out.println("[nodo] Nueva conexion cliente/server: " + connection.getRemoteSocketAddress());
                    // create a new thread object
                    NodeHandler clientSock = new NodeHandler(connection, temp_oos, temp_ois);

                    // This thread will handle the client
                    // separately
                    new Thread(clientSock).start();
                }
                
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
}