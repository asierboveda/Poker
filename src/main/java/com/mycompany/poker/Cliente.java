/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package com.mycompany.poker;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author Usuario
 */

public class Cliente {

    public static void main(String[] args) {
        MarcoCliente mc = new MarcoCliente();
        mc.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}

class MarcoCliente extends JFrame {

    public MarcoCliente() {
        setBounds(600, 300, 280, 350);
        LaminaMarcoCliente lmc = new LaminaMarcoCliente();
        add(lmc);
        setVisible(true);
    }
}

class LaminaMarcoCliente extends JPanel {

    private JTextField campo1;
    private JButton miboton;

    public LaminaMarcoCliente() {
        JLabel texto = new JLabel("Introduce tu nombre");
        add(texto);
        campo1 = new JTextField(20);
        add(campo1);
        miboton = new JButton("Enviar");
        EnviarTexto evento = new EnviarTexto();
        miboton.addActionListener(evento);
        add(miboton);
    }

    class EnviarTexto implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                Socket s = new Socket("172.19.86.201", 4444); 
                try (DataOutputStream salida = new DataOutputStream(s.getOutputStream())) {
                    salida.writeUTF(campo1.getText());
                }
                s.close(); 
            } catch (IOException excepcion) {
                System.err.println("error");
            }
        }
    }
}
    

