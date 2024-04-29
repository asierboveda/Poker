/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.poker.Uno;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author asier
 */
public class ClientHandler implements Runnable{
        public static ArrayList<ClientHandler> clientHandler  = new ArrayList<>();
        
        private Socket socket;
        private ObjectInputStream in;
        private ObjectOutputStream out;
        private String username;
        
       
        public ClientHandler(Socket socket) {
                try{
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        this.socket = socket;
                        this.in = new ObjectInputStream(socket.getInputStream());             
                        this.out = new ObjectOutputStream(socket.getOutputStream());
                        this.username = bufferedReader.readLine();
                        clientHandler.add(this);
                }catch(IOException e){
                        e.getMessage();
                }
        }
        
//        int turno  = (valor_ jugador;+sentido)% NUM_JUGADORES
//        int sentido = 1;
//        sentido *-1
        
        @Override
        public void run() {
                PaqueteEnvio envio;
                while(socket.isConnected()){
                        try{
                                envio =(PaqueteEnvio) in.readObject();
                        }catch (ClassNotFoundException ex) {
                                        Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
                        }catch(IOException e){
                                  e.getMessage();
                        }
                }
        }
        public void broadcastMessage(String msg ){
                for(ClientHandler ch: clientHandler){
                        try{
                                if(! ch.username.equals(username)){
                                        ch.out.writeObject(out);
                                }
                        }catch(IOException e){
                                e.getMessage();
                        }
                }
        }
        
}
