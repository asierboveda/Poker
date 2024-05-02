/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package AdrianUno;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author alumno
 */
public class Server {

    private static final int SERVER_PORT = 59001;
    public static final Baraja baraja = new Baraja();
    private static Carta cartaArriba = baraja.robarCarta();
    private static int turno = 0;
    public static int sentido = 1;

    public static void main(String[] args) {
        System.out.println("The Uno server is running...");
        ExecutorService pool = Executors.newFixedThreadPool(500);
        ServerSocket server = null;

        try {
            server = new ServerSocket(SERVER_PORT);
            List<HandlerCliente> listaHandlers = new ArrayList<>();

            while (listaHandlers.size() < 4) {
                Socket clientSocket = server.accept();
                HandlerCliente handler = new HandlerCliente(clientSocket, listaHandlers.size(), listaHandlers);
                listaHandlers.add(handler);
                pool.execute(handler);
            }
        } catch (IOException e) {
            System.out.println("Exception: " + e);
            e.printStackTrace();
        } finally {
            if (server != null) {
                try {
                    server.close();
                } catch (IOException e) {
                    System.out.println("Error al cerrar el servidor: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    public static synchronized Carta getCartaArriba() {
        return cartaArriba;
    }

    public static synchronized void setCartaArriba(Carta carta) {
        cartaArriba = carta;
    }

    public static synchronized int getTurno() {
        return turno;
    }

    public static synchronized void avanzarTurno() {
        if (sentido == 1) {
            turno = (turno + 1) % 4;
        } else if (sentido == -1) {
            if (turno == 0) {
                turno = 3;
            } else {
                turno = (turno - 1) % 4;
            }
        }
    }

    public static synchronized void cambioSentido() {
        sentido = sentido * (-1);
    }
}
