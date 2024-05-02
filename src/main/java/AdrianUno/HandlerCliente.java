/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package AdrianUno;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author alumno
 */
public class HandlerCliente implements Runnable {

    private int id;
    private Socket socket;

    private List<HandlerCliente> listaHandlers;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public HandlerCliente(Socket socket, int id, List<HandlerCliente> listaHandlers) {
        this.id = id;
        this.socket = socket;
        this.listaHandlers = listaHandlers;
    }

    @Override
    public void run() {

        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            String saludoInicioPartida = "Usted se ha conectado al servidor. Es el jugador numero " + (id + 1);
            out.writeObject(saludoInicioPartida);

            ArrayList<Carta> mano = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                synchronized (Server.baraja) {
                    mano.add(Server.baraja.robarCarta());
                }
            }
            out.writeObject(mano);

            while (Server.getTurno() != id) {
                Thread.sleep(1000); // Esperar hasta que sea el turno del cliente
            }

            while (true) {
                synchronized (Server.baraja) {
                    Carta cartaArriba = Server.getCartaArriba();

                    //Gestionar los casos en los que la cartaArriba que nos llega es extraña (chúpates, saltos...)
                    String instruccionesComplementarias = null;
                    ArrayList<Carta> robar = new ArrayList<>();
                    //nos puede llegar un null, si al anterior le han saltado 
                    if (cartaArriba.getValor() != null) {
                        switch (cartaArriba.getValor()) {
                            case SALTO:
                                instruccionesComplementarias = "Te han saltado el turno. Teclea el 0, obligatoriamente";
                                Server.setCartaArriba(new Carta(cartaArriba.getColor(), null));//para que el siguiente jugador tenga turno
                                break;
                            case MAS_CUATRO:
                                instruccionesComplementarias = "Te debes chupar 4 cartas";
                                robar.add(Server.baraja.robarCarta());
                                robar.add(Server.baraja.robarCarta());
                                robar.add(Server.baraja.robarCarta());
                                robar.add(Server.baraja.robarCarta());
                                break;
                            case MAS_DOS:
                                instruccionesComplementarias = "Te debes chupar 2 cartas";
                                robar.add(Server.baraja.robarCarta());
                                robar.add(Server.baraja.robarCarta());
                                break;
                            case CAMBIO_SENTIDO:
                                Server.cambioSentido();
                            default:
                                break;
                        }
                    }

                    //Momentos excpecionales, en los que nos han lllegado chúpates
                    out.writeObject(instruccionesComplementarias);
                    if (!robar.isEmpty()) {
                        for (Carta c : robar) {
                            out.writeObject(c);
                            mano.add(c); //para que el handler tmbn la tenga en cuenta
                        }
                    }

                    //Inicio turno
                    String instrucciones = "Es su turno. Tiene que elegir una de sus cartas para tirar ó el 0 para pasar. La carta de arriba es: " + cartaArriba;
                    out.writeObject(instrucciones);

                    //recibimos la carta del jugador y, si no ha pasado, la ponemos hacia arriba
                    int numeroCarta = in.readInt();

                    if (numeroCarta != 0) {
                        if (mano.get(numeroCarta - 1).getValor().equals(Valor.CAMBIO_COLOR)) {
                            Color cambioColor = (Color) in.readObject();
                            Server.setCartaArriba(new Carta(cambioColor, null));
                        } else {
                            Carta cartaElegida = mano.remove(numeroCarta - 1);
                            Server.setCartaArriba(cartaElegida);
                        }

                    }

                    //Comprobación de si hemos ganado
                    if (mano.isEmpty()) {
                        out.writeObject("he ganado");
                        for (HandlerCliente handler : listaHandlers) {
                            if (handler != this) {
                                handler.out.writeObject("El jugador " + (id + 1) + " ha ganado.");
                            }
                        }
                        break;
                    } else {
                        Server.avanzarTurno();
                        out.writeObject("no he ganado");
                    }
                }
                // Esperar un momento antes de avanzar al siguiente jugador
                Thread.sleep(100);
                // Verificar si es el turno de este jugador
                while (Server.getTurno() != id) {
                    Thread.sleep(100);
                }

            }
        } catch (IOException | InterruptedException | ClassNotFoundException e) {
            System.out.println("Exception: " + e);
            e.printStackTrace();
        }
    }

}
