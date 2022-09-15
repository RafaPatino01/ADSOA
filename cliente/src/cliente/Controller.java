package cliente;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.application.Platform;
import javafx.event.ActionEvent;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Controller {
    @FXML private Label label_resultado;
    @FXML private TextField operacionField;

    //get the localhost IP address 
    public InetAddress host;
    public Socket socket = null;
    public ObjectOutputStream oos = null;
    public ObjectInputStream ois = null;

    public int nodo_port = 3332;

    public void initialize() throws IOException, ClassNotFoundException {
        label_resultado.setText("");

        //get the localhost IP address
        host = InetAddress.getLocalHost();
        
        // Create socket
        socket = new Socket(host.getHostName(), nodo_port);
        System.out.println("[cliente] Conexion establecida con nodo: " + Integer.toString(nodo_port));
        
        //Objects OI Stream
        oos = new ObjectOutputStream(socket.getOutputStream());
        ois = new ObjectInputStream(socket.getInputStream());

        //Listening thread ObjectInputStream
        t.start();
    }

    @FXML
    private void sendString(ActionEvent event) throws UnknownHostException, IOException, ClassNotFoundException, InterruptedException {
        event.consume();
        //Get text from textfield
        String operacion = operacionField.getText();

        //operacion should not contain other symbols than */+- and numbers
        if(!operacion.matches(".*[a-zA-Z].*")){
            
            // write to socket using ObjectOutputStream
            System.out.println("[cliente] Enviando datos al nodo: " + "operacion,"+operacion);
            oos.writeObject("operacion,"+operacion);
            
        }
        else {
            label_resultado.setText("Error en la expresión");
        }
        
    }


    Thread t = new Thread(() -> {
        //Here write all actions that you want execute on background
        while(true){
            
            String message;
            try {
                System.out.println("[cliente] esperando respuesta... ");

                message = (String) ois.readObject();
                System.out.println("[cliente] Respuesta recibida: " + message);
    
                String parts[] = message.split(","); // {type of message},{content}
        
                if(parts[0].equals("resultado")){
                    Platform.runLater(() -> {
                        label_resultado.setText(parts[1]);
                    });
                }
                
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }
            
        }
        
    });
}
