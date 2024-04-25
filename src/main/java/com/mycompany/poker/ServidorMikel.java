package com.mycompany.poker;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;

public class ServidorMikel {

    private static final int NUM_JUGADORES = 5;
    private static final int RONDAS = 3;

    public static void main(String[] args) {
        try (ServerSocket servidor = new ServerSocket(50678)) {

            Socket[] clientes = new Socket[NUM_JUGADORES];
            Thread[] hiloClientes = new Thread[NUM_JUGADORES];
            
            for (int i = 0; i < NUM_JUGADORES; i++) {
                System.out.println("Servidor esperando al siguiente jugador...");
                System.out.println("Conectados " + i + " / " + NUM_JUGADORES);

                // Acepta la conexión del cliente
                clientes[i] = servidor.accept();
                hiloClientes[i] = new Thread(new HiloCliente(clientes[i], i));
                System.out.println("Jugador " + (i + 1) + " conectado al servidor");
            }

            Random random = new Random();
            for (int ronda = 1; ronda <= RONDAS; ronda++) {
                System.out.println("Ronda "+ronda);
                
                // Envía las cartas comunes y propias a cada jugador
                for (Socket cliente : clientes) {
                    try (PrintWriter out = new PrintWriter(cliente.getOutputStream(), true)) {
                        out.println(random.nextInt(1, 14)); 
                        out.println(random.nextInt(1, 14)); 
                        out.println(random.nextInt(1, 14)); 
                        out.println(random.nextInt(1, 14)); 
                        out.println(random.nextInt(1, 14)); 
                    }
                }
                
                //turnos
                int turno=1;
                while(turno<=NUM_JUGADORES){
                    switch(turno){
                        case 1:
                           hiloClientes[0].start();
                           //recibir su apuesta y guardarla en algun lado
                        case 2:
                            //mandarle al Socket 2 la apuesta del 1
                            hiloClientes[1].start();
                            //recibir su apuesta y guardarla en algun lado
                        case 3:
                            //mandarle al Socket 3 las apuestas del 1 y del 2
                            hiloClientes[2].start();
                            //recibir su apuesta y guardarla en algyn lado
                        case 4:
                            //mandarle al Socket 2 la apuesta del 1, del 2 y del 3
                            hiloClientes[3].start();
                            //recibir su apuesta y  guardarla en algyn lado
                        case 5:
                            //mandarle al Socket 2 la apuesta del 1, del 2 y del 3
                            hiloClientes[4].start();
                            //recibir su apuesta y  guardarla en algyn lado
                    }
                    turno++;
                    //comu
                }
                
            }

            // Cierra los sockets al final
            for (Socket cliente : clientes) {
                cliente.close(); // Cierra cada socket
            }

        } catch (IOException excepcion) {
            excepcion.printStackTrace(); // Muestra información del error
        }
    }
}
