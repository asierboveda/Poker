/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package com.mycompany.poker;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Usuario
 */
public class Servidor {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        final int NUM_CLIENTES_ESPERADOS = 6;
        int clientesConectados = 0;
        try{
            ServerSocket servidor = new ServerSocket(50987);
            while(clientesConectados < NUM_CLIENTES_ESPERADOS){
                Socket cliente = servidor.accept(); 
                clientesConectados++;
                System.out.println("Cliente " + clientesConectados + "conectado");
                
            }
            System.out.println("La partida va a comenzar. Estan todos los jugadores.");
        }catch(IOException e){
            System.err.println("IOException. Mensaje: " + e.getMessage());
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }
    
}
