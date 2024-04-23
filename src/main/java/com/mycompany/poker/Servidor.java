/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package com.mycompany.poker;

import java.awt.BorderLayout;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 *
 * @author Usuario
 */

public class Servidor {

    public static void main(String[] args) {
        MarcoServidor mimarco = new MarcoServidor();
        mimarco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}

class MarcoServidor extends JFrame implements Runnable {

    private JTextArea areatexto;

    public MarcoServidor() {
        setBounds(1200, 300, 280, 350);
        JPanel milamina = new JPanel();
        milamina.setLayout(new BorderLayout());
        areatexto = new JTextArea();
        milamina.add(areatexto, BorderLayout.CENTER);
        add(milamina);
        setVisible(true);
        Thread mihilo = new Thread(this);
        mihilo.start();
    }

    @Override
    public void run() {
        try {
             final int NUM_CLIENTES =6;
             int clientes = 0;
            ServerSocket servidor = new ServerSocket(4444);
           
           
            while (clientes<=NUM_CLIENTES) {
                        
                        clientes++;
                        DataInputStream entrada = new DataInputStream(s.getInputStream());
                        String mensaje = entrada.readUTF();
                        System.out.println(mensaje);
        //            areatexto.append("\n" + mensaje);
                        areatexto.append("Se conencta el cliente  "+mensaje+ "\n");
                        
                        
            }
            
        } catch (IOException ex) {
            Logger.getLogger(MarcoServidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
