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

    private static final int PUERTO = 4554;
    private static int turno = 1;
    private static char[][] tablero = new char[3][3];

    public static void main(String[] args) {
        try {
            ServerSocket servidor = new ServerSocket(PUERTO);
            System.out.println("Servidor esperando conexiones...");

            while (true) {
                Socket clienteX = servidor.accept();
                System.out.println("Jugador X conectado desde: " + clienteX.getInetAddress().getHostName());

                Socket clienteO = servidor.accept();
                System.out.println("Jugador O conectado desde: " + clienteO.getInetAddress().getHostName());

                new Thread(new Juego(clienteX, 'X')).start();
                new Thread(new Juego(clienteO, 'O')).start();
            }
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
                BufferedReader in = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
                PrintWriter out = new PrintWriter(cliente.getOutputStream(), true);

                out.println("Esperando a que el otro jugador se una...");

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


