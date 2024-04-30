/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package UnoSamuActualizado;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author alumno
 */
public class HandlerCliente implements Runnable {

    private int id;
    private Socket socket;
    private Baraja baraja;
    private ArrayList<HandlerCliente> listaHandlers = new ArrayList<>();
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public HandlerCliente(Socket socket, int id, Baraja baraja) {
        this.socket = socket;
        this.id = id;
        this.baraja = baraja;
        listaHandlers.add(this);
        
    }

    @Override
    public void run() {

        try {
            //in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());

            //Se da la bienvenida
            String saludoInicioPartida = "Usted se ha conectado al servidor. Es el jugador numero " + (id + 1);
            out.writeObject(saludoInicioPartida);

            //Se reparten cartas
            ArrayList<Carta> mano = new ArrayList<Carta>();
            for (int i = 0; i < 7; i++) {
                mano.add(baraja.robarCarta());
            }
            out.writeObject(mano);
            
            while(listaHandlers.size()<3){
                //Los demas hilos no se añaden a la lista no se porque. Hay que buscar una form de que se añadan lo hilos a la lista 
            }

            //Empieza la partida
            int turno = 0; //turno ira del 0 al 3 y volvera a empezar del 0.
            int inicioPartida = 1; //Para sacar la primera carta de la baraja
            Carta cartaArriba = null;

            while (true) {
                if (turno == id) {
                    //Falta ver cuantas cartas le quedan para ver si ha ganado
                    if (inicioPartida == 1) {
                        cartaArriba = baraja.robarCarta();
                        baraja.tirarCarta(cartaArriba);
                        inicioPartida = 0;
                    }
                    String instrucciones = "Es su turno. Tiene que elegir una de sus cartas para tirar. La carta de arriba es: " + baraja.mostrarCartaArriba();
                    out.writeObject(instrucciones);
                    Carta cartaElejida = (Carta) in.readObject();
                    //Tirar la carta enviada por el cliente
                    baraja.tirarCarta(cartaElejida);
                    System.out.println("La carta que ha tirado el cliente " + id + " es: " + cartaElejida);

                    //Ver si ha ganado
                    String textoClienteGanado = (String) in.readObject();
                    if (textoClienteGanado.equals("he ganado")) {
                        //se acaba el juego
                        System.out.println("El jugador " + id + " es el ganador");
                        //reventar programa
                    } else {
                        if (turno == 3) { //No se tiene en cuanta la carta de cambio de sentido
                            turno = 0;
                        } else {
                            turno++;
                        }
                    }

                }

            }

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Exception: " + e);
            System.out.println("Message: " + e.getMessage());
            e.printStackTrace(System.err);
        }
    }

}
