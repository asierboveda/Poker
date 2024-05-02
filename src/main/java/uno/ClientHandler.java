/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package uno;


import uno.Carta.Valor;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author asier
 */
public class ClientHandler implements Runnable {

        public static List<ClientHandler> clientHandler = Collections.synchronizedList(new ArrayList<>());
        private Socket socket;
        private BufferedReader br;
        private BufferedWriter bw;
        private String username;
        
        private ArrayList<Carta> mano = new ArrayList<>();
        private static final CyclicBarrier barrera = new CyclicBarrier(4);
        public ClientHandler(Socket socket) {

                try {
                        this.socket = socket;
                        this.br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        this.bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                        this.username = br.readLine();
                        
                        for (int i = 0; i < 7; i++) {
                                synchronized (Server.baraja) {
                                        mano.add(Server.baraja.robarCarta());
                                }
                        }
                        System.out.println("se ha unido " + username);
                        clientHandler.add(this);

                } catch (IOException e) {
                        e.getMessage();
                }
        }
        public synchronized void cambioTurno(){
                Server.turno  = Server.turno +Server.sentido;
        }
        public synchronized void cambioSentido(){
                Server.sentido = Server.sentido*(-1);
        }
        public synchronized void chupate(int n){
                Server.chupate = n;
        }
        public synchronized void borrarChupate(){
                Server.chupate = 0;
        }
        
        @Override
        public void run() {
                System.out.println("Esperando a los 4 jugadores");
                while (clientHandler.size() < 4) {
                }
                System.out.println("Ya estan los 4 jugadores");
                System.out.println(mano);
                mandarMano();

                while (socket.isConnected()) {
                        try {

                                // Verificar si es el turno de este hilo
                                this.bw.newLine();
                                this.bw.write("EL TURNO ES DE: " + clientHandler.get(Server.turno).username);
                                this.bw.newLine();
                                
                                if (clientHandler.get(Server.turno).equals(this)) {
                                        if(Server.chupate != 0){
                                                this.bw.write("Tienes que cojer " + Server.chupate+" cartas");
                                                for(int i = 0; i<Server.chupate; i++){
                                                         Carta c = Server.baraja.robarCarta();
                                                         this.bw.write(c.toString());
                                                         this.mano.add(c);
                                                }
                                        }
                                        this.bw.write("La carta del centro es: " + Server.centro);
                                        this.bw.newLine();
                                       this.bw.write("si quieres saltar elija una cualquier carta incorrecta");
                                       this.bw.newLine();
                                        mandarMano();
                                        int numeroCarta = this.br.read() - 1;
                                        Carta cartaValida = null;
                                        if (this.mano.get(numeroCarta).cartaValida(Server.centro)) {
                                                synchronized (Server.centro) {   
                                                        cartaValida = mano.remove(numeroCarta);
                                                        if(cartaValida.getValor()==Valor.CAMBIO_COLOR){
                                                                this.bw.write("Tienes que elegir el color:  ");
                                                                this.bw.newLine();
                                                                this.bw.write("1 -Rojo  ");
                                                                this.bw.newLine();
                                                                this.bw.write("2-Verde ");
                                                                this.bw.newLine();
                                                                this.bw.write("3-Azul ");
                                                                this.bw.newLine();
                                                                this.bw.write("4-Amarillo ");
                                                                this.bw.newLine();
                                                                int n_color = this.br.read();
                                                                switch(n_color){
                                                                        case 1:
                                                                                cartaValida = new Carta(Carta.Color.ROJO,Carta.Valor.CAMBIO_COLOR);
                                                                                break;
                                                                         case 2:
                                                                                cartaValida = new Carta(Carta.Color.VERDE,Carta.Valor.CAMBIO_COLOR);
                                                                                break;
                                                                         case 3:
                                                                                 cartaValida = new Carta(Carta.Color.AZUL,Carta.Valor.CAMBIO_COLOR);
                                                                                 break;
                                                                         case 4: 
                                                                                 cartaValida = new Carta(Carta.Color.AMARILLO,Carta.Valor.CAMBIO_COLOR);
                                                                                 break;
                                                                }
                                                        }else if (cartaValida.getValor()==Valor.MAS_CUATRO){
                                                                this.bw.write("Tienes que elegir el color:  ");
                                                                this.bw.newLine();
                                                                this.bw.write("1 -Rojo  ");
                                                                this.bw.newLine();
                                                                this.bw.write("2-Verde ");
                                                                this.bw.newLine();
                                                                this.bw.write("3-Azul ");
                                                                this.bw.newLine();
                                                                this.bw.write("4-Amarillo ");
                                                                this.bw.newLine();
                                                                int n_color = this.br.read();
                                                                switch(n_color){
                                                                        case 1:
                                                                                cartaValida = new Carta(Carta.Color.ROJO,Carta.Valor.MAS_CUATRO);
                                                                                break;
                                                                         case 2:
                                                                                cartaValida = new Carta(Carta.Color.VERDE,Carta.Valor.MAS_CUATRO);
                                                                                break;
                                                                         case 3:
                                                                                 cartaValida = new Carta(Carta.Color.AZUL,Carta.Valor.MAS_CUATRO);
                                                                                 break;
                                                                         case 4: 
                                                                                 cartaValida = new Carta(Carta.Color.AMARILLO,Carta.Valor.MAS_CUATRO);
                                                                                 break;
                                                                }
                                                         }
                                                                Server.centro = cartaValida;
                                                        
                                                }

                                        } else {
                                                this.bw.write("Robas carta");
                                                this.mano.add(Server.baraja.robarCarta());
                                        }
                                        if(cartaValida !=null){
                                                switch(cartaValida.getValor()){
                                                        case CAMBIO_SENTIDO:
                                                                cambioSentido();
                                                                break;
                                                        case SALTO:
                                                                cambioTurno();
                                                                break;
                                                        case MAS_CUATRO:
                                                                chupate(4);
                                                                break;
                                                        case MAS_DOS:
                                                                chupate(2);
                                                                break;
                                                }
                                        }
                                        cambioTurno();
                                        
                                        
                                }
                                //todos deben de cambiar el centro
                                
                                barrera.await();

                        } catch (IOException e) {
                                e.getMessage();
                        } catch (InterruptedException ex) {
                                Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (BrokenBarrierException ex) {
                                Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
                        }
                }

        }

        public void mandarMano() {

                try {
                        this.bw.write("TU MANO ES:\n ");
                        this.bw.newLine();
                        int i = 1;
                        for (Carta c : this.mano) {
                                this.bw.write(c.toString()+"("+i+")");
                                this.bw.newLine();
                                i++;
                        }
                        this.bw.flush();
                } catch (IOException e) {
                        e.getMessage();
                }

        }

         }

