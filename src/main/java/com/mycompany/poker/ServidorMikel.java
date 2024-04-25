package com.mycompany.poker;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;

public class ServidorMikel {

    private static final int NUM_JUGADORES = 5;

    public static void main(String[] args) {
        try (ServerSocket servidor = new ServerSocket(50678)) {

            Socket[] clientes = new Socket[NUM_JUGADORES];
            
            for (int i = 0; i < NUM_JUGADORES; i++) {
                System.out.println("Servidor esperando al siguiente jugador...");
                System.out.println("Conectados " + i + " / " + NUM_JUGADORES);

                // Acepta la conexión del cliente
                clientes[i] = servidor.accept();
                System.out.println("Jugador " + (i + 1) + " conectado al servidor");
            }

            Random random = new Random();

            // Genera las cartas comunes
            int[] cartasMedio = {
                random.nextInt(1, 14),
                random.nextInt(1, 14),
                random.nextInt(1, 14),
                random.nextInt(1, 14),
                random.nextInt(1, 14)
            };

            // Envía las cartas comunes y propias a cada jugador
            for (Socket cliente : clientes) {
                try (PrintWriter out = new PrintWriter(cliente.getOutputStream(), true)) {
                    
                    for (int carta : cartasMedio) {
                        out.println(carta);
                    }
                    
                    out.println(random.nextInt(1, 14)); // Primera carta propia
                    out.println(random.nextInt(1, 14)); // Segunda carta propia
                }
            }
            
            Scanner teclado = new Scanner(System.in);
            System.out.print("Estan todas las apuestas?: ");
            String resp=teclado.nextLine();
            
            System.out.print("Teclea pa continuar cuando todos los clientes hayan apostado: ");
            teclado.nextLine();
            
            // Lee las apuestas de cada jugador
            for (int j = 0; j < NUM_JUGADORES; j++) {
                try (Scanner in = new Scanner(clientes[j].getInputStream())) {
                    if (in.hasNextInt()) { // Verifica si hay un entero para leer
                        int apuesta = in.nextInt();
                        System.out.println("El jugador " + (j + 1) + " apostó: " + apuesta);
                    }
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
