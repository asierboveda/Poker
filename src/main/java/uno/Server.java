        /*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package uno;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author asier
 */
public class Server {

        

        /**
         * @param args the command line arguments
         */
        private ServerSocket sc;
        public static final Baraja baraja = new Baraja();
        public static  Carta centro = baraja.robarCarta();
        public static int turno = 0;
        public static int sentido = 1;
        public static int chupate = 0;
        
        
        
        public Server(ServerSocket sc) {
                this.sc = sc;
        }
        public void comenzarJuego(){
                try{
                        while(!sc.isClosed()){
                                
                                Socket s =  sc.accept();
                                System.out.println("Jugador conectado");
                                ClientHandler ch = new ClientHandler(s);
                                
                                Thread thread  = new Thread((Runnable) ch);
                                thread.start();
                                
                        }
                
                }catch(IOException e){
                               e.getMessage();
                }
        }
        public static void main(String[] args) throws IOException {
                // TODO code application logic here
                ServerSocket sc =  new ServerSocket(55500);
                Server server = new Server(sc);
                server.comenzarJuego();
        }
        
}
