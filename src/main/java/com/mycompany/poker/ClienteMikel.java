package com.mycompany.poker;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClienteMikel {

    public static void main(String[] args) {
        try (Socket cliente = new Socket("127.0.0.1", 50678); 
             Scanner in = new Scanner(cliente.getInputStream());
             PrintWriter pw = new PrintWriter(cliente.getOutputStream(), true)) {
            
            Scanner teclado = new Scanner(System.in);
            System.out.print("Teclea pa continuar cuando todos se conecten: ");
            teclado.nextLine();
            
            // Leer cartas comunes (cartas del medio)
            System.out.print("Cartas comunes recibidas: ");
            for (int i = 0; i < 5; i++) {
                if (in.hasNextInt()) {
                    int carta = in.nextInt();
                    System.out.print(carta + " ");
                }
            }
            System.out.println(); // Salto de línea para formato

            // Leer cartas propias (mano)
            System.out.print("Cartas propias recibidas: ");
            for (int i = 0; i < 2; i++) {
                if (in.hasNextInt()) {
                    int carta = in.nextInt();
                    System.out.print(carta + " ");
                }
            }
            System.out.println(); // Salto de línea para formato

            // Pedir apuesta al cliente
            
            System.out.print("Cuál es tu apuesta?: ");
            int apuesta = teclado.nextInt();

            // Enviar la apuesta al servidor
            pw.println(apuesta);

        } catch (IOException excepcion) {
            System.err.println("Error de conexión:");
            excepcion.printStackTrace(System.err);
        }
    }
}
