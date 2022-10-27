package com.mycompany.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

/**
 *
 * @author Rafa
 *
 */
public class Server {
    
    //get the localhost IP address 
    public static InetAddress host;
    public static Socket socket = null;
    public static ObjectOutputStream oos = null;
    public static ObjectInputStream ois = null;
    
    //socket server port on which it will listen
    private static int nodo_port = 3332;
    
    public static void main(String args[]) throws IOException, ClassNotFoundException, InterruptedException{
        
        host = InetAddress.getLocalHost();
        System.out.println("[server] Buscando conexión con nodo... ");
        
        boolean socket_connection = false;
        while(!socket_connection){
            try {
                nodo_port = getRandomNumber(3000,3100);
                socket = new Socket(host.getHostName(), nodo_port);
                System.out.println("[server] Conexion establecida con nodo: " + Integer.toString(nodo_port));
                socket_connection = true;
                
                //Objects OI Stream
                oos = new ObjectOutputStream(socket.getOutputStream());
                ois = new ObjectInputStream(socket.getInputStream());

                //Indicate that this is a server
                oos.writeObject("tipo-server");
            }
            catch(Exception e){
                //nothing happens
            }
        }
        
        //keep listens indefinitely until receives 'exit' call or program terminates
        while(true){
            
            System.out.println("[server] Esperando un request");
            
            //convert ObjectInputStream object to String
            String message = (String) ois.readObject();
            
            System.out.println("[server] Mensaje recibido: " + message);

            String parts[] = message.split(","); // {type of message},{content}

            if(parts[0].equals("operacion")){
                System.out.println("[server] Evaluando expresión recibida: " + parts[1]);
                
                // solve expression from message
                Expression expression = new ExpressionBuilder(parts[1]).build();
                double result = expression.evaluate();

                //write object to Socket
                // {type of message},{content},{flag},{id_hash}
                oos.writeObject("resultado,"+Double.toString(result)+",0,"+parts[3]); 
                System.out.println("[server] Resultado enviado: " + result);
            }
            
            //terminate the server if client sends exit request
            if(message.equalsIgnoreCase("exit")) break;
        }
        System.out.println("Shutting down Socket server!!");
        //close resources
        ois.close();
        oos.close();
        socket.close();
        
    }
    
    public static int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
}