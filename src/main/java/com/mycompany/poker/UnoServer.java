/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Uno;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author asier
 */
public class UnoServer {

        /**
         * @param args the command line arguments
         */
        public static void main(String[] args) {
                 
                try(ServerSocket s = new ServerSocket(55555)){
                        System.out.println("Servidor esperando conexiones");
                        
                        for (int i = 0; i < 4; i++) {
                                Socket cliente = s.accept();
                                System.out.println("Cliente"+(i+1)+"conectado.");
                                
                              
                                new UnoClienteThread(cliente,i).start();
                        }
                } catch (IOException ex) {
                        Logger.getLogger(UnoServer.class.getName()).log(Level.SEVERE, null, ex);
                }
        }
        
}
