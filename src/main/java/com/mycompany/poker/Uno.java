/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Uno;

/**
 *
 * @author asier
 */
// Código del cliente

import java.io.*;
import java.net.*;

public class Uno {
    public static void main(String[] args) {
        
        

        try (
            Socket socket = new Socket( "172.19.86.201", 55555);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))
        ) {
            String fromServer;
            String fromUser;

            // Recibe el ID del jugador del servidor
            fromServer = in.readLine();
            System.out.println("ID del jugador: " + fromServer);

            while ((fromServer = in.readLine()) != null) {
                if (fromServer.startsWith("TURN:")) {
                    // Turno del jugador
                    int nextPlayerId = Integer.parseInt(fromServer.substring(5));
                    if (nextPlayerId == 1) {
                        System.out.println("Es tu turno. Juega una carta:");
                        fromUser = stdIn.readLine();
                        out.println(fromUser);
                    } else {
                        System.out.println("Turno del jugador " + nextPlayerId + ".");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

