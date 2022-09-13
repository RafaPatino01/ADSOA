package cliente;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Controller {
    @FXML private Label label;
    @FXML private TextField operacionField;

    public void initialize() {
        label.setText("Hello, JavaFX " + ".");
    }

    @FXML
    private void sendString(ActionEvent event) throws UnknownHostException, IOException, ClassNotFoundException, InterruptedException {
        event.consume();
        //Get text from textfield
        String operacion = operacionField.getText();

        //operacion should not contain other symbols than */+- and numbers
        if(!operacion.matches(".*[a-zA-Z].*")){
            //get the localhost IP address
            InetAddress host = InetAddress.getLocalHost();
            Socket socket = null;
            ObjectOutputStream oos = null;
            ObjectInputStream ois = null;

            //create socket connection
            socket = new Socket(host.getHostName(), 9876);

            //write to socket using ObjectOutputStream
            oos = new ObjectOutputStream(socket.getOutputStream());
            System.out.println("Sending request to Socket Server");
            
            //oos.writeObject("exit");
            oos.writeObject(operacion);

            //read the server response message
            ois = new ObjectInputStream(socket.getInputStream());
            String message = (String) ois.readObject();
            System.out.println("respuesta: " + message);

            //close resources
            ois.close();
            oos.close();
            Thread.sleep(100);

            label.setText(message);
        }
        else {
            label.setText("Error en la expresión");
        }
        
    }

}
