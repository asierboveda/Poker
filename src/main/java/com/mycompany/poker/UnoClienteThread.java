/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Uno;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author asier
 */
public class UnoClienteThread extends Thread {

        private Socket client;
        private int id;

        public UnoClienteThread(Socket client, int id) {
                this.client = client;
                this.id = id;
        }

        public void run() {
                try (PrintWriter out = new PrintWriter(client.getOutputStream()); BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()))) {
                        String inputLine;
                        System.out.println("klk");
                        out.println("ID"+id);
                        in.readLine();
                        while((inputLine = in.readLine())!=null){
                                System.out.println("Jugador"+id+"jugo la carta"+inputLine);
                                int nextPlayerId  = (id%4)+1;
                                out.println("Turn"+nextPlayerId);
                        }
                } catch (IOException ex) {
                        Logger.getLogger(UnoClienteThread.class.getName()).log(Level.SEVERE, null, ex);
                }

        }
}
