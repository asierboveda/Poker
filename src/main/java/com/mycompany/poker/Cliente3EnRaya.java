/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package com.mycompany.poker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author Usuario
 */
public class Cliente3EnRaya {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("172.18.83.10", 34450);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            Scanner scanner = new Scanner(System.in);
            char marca = in.readLine().charAt(0);
            System.out.println("Eres el jugador " + marca);

            while (true) {
                String mensajeServidor = in.readLine();
                if (mensajeServidor.equals("Esperando a que el otro jugador se una...")) {
                    System.out.println(mensajeServidor);

                } else if (mensajeServidor.equals("GANASTE")) {
                    System.out.println("¡Felicidades! ¡Ganaste!");
                    break;
                } else if (mensajeServidor.equals("EMPATE")) {
                    System.out.println("¡Empate!");
                    break;
                } else if (mensajeServidor.equals("SIGUE")) {
                    System.out.println("Es tu turno. Ingresa las coordenadas (fila,columna): ");
                    String coordenadas = scanner.nextLine();
                    out.println(coordenadas);
                } else {
                    System.out.println("Error: " + mensajeServidor);
                    break;
                }
            }

            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

}
