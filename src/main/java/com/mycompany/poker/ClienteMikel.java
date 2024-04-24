package com.mycompany.poker;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClienteMikel {

    public static void main(String[] args) {
        /*abro el cliente que apostará y un pw pa mandar apuesta. 
        Al ponerlo en el try, no hace falta cerrar.
        Al poner el true, no hace falta hacer pw.flush*/
        
        try (Socket cliente = new Socket("127.0.0.1", 50678); 
            PrintWriter pw = new PrintWriter(cliente.getOutputStream(), true)) {
            
            Scanner teclado = new Scanner(System.in);
            System.out.println("Espera a que se conecten los 5 y luego: ");//pongo esto pa q primero ejecutemos 5 ClienteMikel, y luego apostar en cada uno
            System.out.print("Cuál es tu apuesta?: ");
            int ap = teclado.nextInt();
            pw.println(ap);

        } catch (IOException excepcion) {
            System.err.println("error");
            excepcion.printStackTrace(System.err);
        }
    }

}
