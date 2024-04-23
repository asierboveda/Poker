/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package com.mycompany.poker;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author Usuario
 */
public class Cliente {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("172.18.80.1", 50987);
//            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
//            ObjectInputStream isr = new ObjectInputStream(socket.getInputStream());
            
            
            socket.close();
            
        } catch (IOException e) {
            System.err.println("IOException. Mensaje: " + e.getMessage());
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }
    
}
