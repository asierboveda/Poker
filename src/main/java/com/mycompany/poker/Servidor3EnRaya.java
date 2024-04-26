/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package com.mycompany.poker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Usuario
 */
public class Servidor3EnRaya {

    /**
     * @param args the command line arguments
     */
    private static final int PUERTO = 34450;
    private static int turno = 1;
    private static final char[][] tablero = new char[3][3];

    public static void main(String[] args) {
        try {
            ServerSocket servidor = new ServerSocket(PUERTO);
            System.out.println("Servidor esperando conexiones...");

            //(samu) he quitado el while para que no deje crear mas sockets y solo espere dos jugadores
            //luego los print de que el jugador se ha conectado los he puesto dentro de juego porque sino no funcionaba correctamente. Ponia en el servidor que se habia conectado pero no se comunicaba con el cliente todavia.
            //while (true) {
            Socket clienteX = servidor.accept();
            new Thread(new Juego(clienteX, 'X')).start();

            Socket clienteO = servidor.accept();
            new Thread(new Juego(clienteO, 'O')).start();

            //}
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    private static synchronized int getTurno() {
        return turno++;
    }

    private static synchronized void marcarTablero(int fila, int columna, char marca) {
        tablero[fila][columna] = marca;
    }

    private static synchronized boolean verificarGanador(char marca) {
        // Verificar filas y columnas
        for (int i = 0; i < 3; i++) {
            if ((tablero[i][0] == marca && tablero[i][1] == marca && tablero[i][2] == marca)
                    || (tablero[0][i] == marca && tablero[1][i] == marca && tablero[2][i] == marca)) {
                return true;
            }
        }

        // Verificar diagonales
        if ((tablero[0][0] == marca && tablero[1][1] == marca && tablero[2][2] == marca)
                || (tablero[0][2] == marca && tablero[1][1] == marca && tablero[2][0] == marca)) {
            return true;
        }

        return false;
    }

    private static class Juego implements Runnable {

        private Socket cliente;
        private char marca;

        public Juego(Socket cliente, char marca) {
            this.cliente = cliente;
            this.marca = marca;
        }

        @Override
        public void run() {
            try {
                System.out.println("Jugador " + marca + " conectado desde: " + cliente.getInetAddress().getHostName());

                BufferedReader in = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
                PrintWriter out = new PrintWriter(cliente.getOutputStream(), true);

                out.println(marca); //(samu)para que le diga que jugador es de forma correcta
                if (marca == 'X') {
                    out.println("Esperando a que el otro jugador se una...");
                } else {
                    out.println("SIGUE");
                }

                int miTurno = getTurno();

                while (true) {
                    String mensaje = in.readLine();
                    if (mensaje.equals("FIN")) {
                        break;
                    }

                    String[] coordenadas = mensaje.split(",");
                    int fila = Integer.parseInt(coordenadas[0]);
                    int columna = Integer.parseInt(coordenadas[1]);

                    marcarTablero(fila, columna, marca);

                    if (verificarGanador(marca)) {
                        out.println("GANASTE");
                        break;
                    } else if (miTurno == 9) {
                        out.println("EMPATE");
                        break;
                    } else {
                        out.println("SIGUE");
                    }
                }

                in.close();
                out.close();
                cliente.close();
            } catch (IOException e) {
                e.printStackTrace();

            }
        }
    }

}


