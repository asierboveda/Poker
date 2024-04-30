/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package UnoSamuActualizado;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author alumno
 */
public class Server {

    /**
     * @param args the command line arguments
     */
    private static final String SERVER_ADREES = "127.0.0.1";
    private static final int SERVER_PORT = 59001;

    public static void main(String[] args) {
        //Crear baraja del juego
        Baraja baraja = new Baraja();

        System.out.println("The chat server is running...");
        ExecutorService pool = Executors.newFixedThreadPool(500);

        try (ServerSocket server = new ServerSocket(59001)) {
            int clientesConectados = 0;
            
            while (clientesConectados < 4) {
                
                new Thread(new HandlerCliente(server.accept(), clientesConectados, baraja)).start();
                clientesConectados++;
                
            }
        } catch (IOException e) {
            System.out.println("Exception: " + e);
            System.out.println("Message: " + e.getMessage());
            e.printStackTrace(System.err);
        }
    }

}
