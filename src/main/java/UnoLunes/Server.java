/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package UnoLunes;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Baraja baraja;
    
    private ServerSocket sc;

    public Server(ServerSocket sc) {
        this.sc = sc;
    }

    public void comenzarJuego() {
        int contador = 0;
        ArrayList<ClientHandler> listaHandlers = new ArrayList<ClientHandler>();
        ArrayList<String> listaUsernames = new ArrayList<String>();

        try {
            //Conexion de los jugadores
            while (!sc.isClosed() && contador < 4) {
                Socket s = sc.accept();
                System.out.println("Cliente conectado");
                ClientHandler ch = new ClientHandler(s);

                Thread thread = new Thread((Runnable) ch);

                thread.start();

                listaHandlers.add(ch);
                listaUsernames.add(listaHandlers.get(contador).getUsername());

                contador++;

            }

            //Dar bienvenida
            String bienvenida = "Ya se han conectado todso los jugadores. Ahora se repartiran las cartas.";
            out.writeObject(bienvenida);

            //Dar cartas
            for (int i = 0; i < 4; i++) {

                ArrayList<Carta> mano = new ArrayList<Carta>();
                for (int j = 0; i < 7; j++) {
                    mano.add(baraja.robarCarta());

                }
                out.writeObject(listaUsernames.get(i)); 
                out.writeObject(mano);

            }
            

            
            //loop del juego
            while (true ) {

            }
             

        } catch (IOException e) {
            e.getMessage();
        }
    }

    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        ServerSocket sc = new ServerSocket(50019);
        Server server = new Server(sc);
        server.comenzarJuego();
    }

}
