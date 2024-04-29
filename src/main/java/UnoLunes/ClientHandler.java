/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package UnoLunes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {

    public static ArrayList<ClientHandler> clientHandler = new ArrayList<>();

    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private String username;

    public ClientHandler(Socket socket) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.socket = socket;
            this.in = new ObjectInputStream(socket.getInputStream());
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.username = bufferedReader.readLine();
            clientHandler.add(this);
        } catch (IOException e) {
            e.getMessage();
        }
    }

    public static ArrayList<ClientHandler> getClientHandler() {
        return clientHandler;
    }

    public static void setClientHandler(ArrayList<ClientHandler> clientHandler) {
        ClientHandler.clientHandler = clientHandler;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public ObjectInputStream getIn() {
        return in;
    }

    public void setIn(ObjectInputStream in) {
        this.in = in;
    }

    public ObjectOutputStream getOut() {
        return out;
    }

    public void setOut(ObjectOutputStream out) {
        this.out = out;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

//        int turno  = (valor_ jugador;+sentido)% NUM_JUGADORES
//        int sentido = 1;
//        sentido *-1
    @Override
    public void run() {
        //PaqueteEncio envio;
        while (socket.isConnected()) {
            try {
                String bienvenida = (String) in.readObject();
                out.writeObject(bienvenida);

                if (in.readObject().equals(username)) {
                    ArrayList<Carta> mano = (ArrayList) in.readObject(); //Igual cambiar a dar carta una por una
                    out.writeObject(mano); //le envi la mano al cliente
                }
                

                
            } catch (IOException | ClassNotFoundException ex) {
                System.err.println("error");
            }
        }
    }
}

/*
public void broadcastMessage(String msg) {
        for (ClientHandler ch : clientHandler) {
            try {
                if (!ch.username.equals(username)) {
                    ch.out.writeObject(out);
                }
            } catch (IOException e) {
                e.getMessage();
            }
        }
    }
*/
