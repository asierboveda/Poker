/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package com.mycompany.poker.Uno;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author asier
 */
public class Cliente {

        /**
         * @param args the command line arguments
         */
        private Socket socket;
        private ObjectInputStream in;
        private ObjectOutputStream out;
        private String username;

        public Cliente(Socket socket,String username) {
                try{
                        this.socket = socket;
                        this.in = new ObjectInputStream(socket.getInputStream());
                        this.out = new ObjectOutputStream(socket.getOutputStream()) ;
                        this.username = username;
                }catch(IOException e){
                        e.getStackTrace();
                }
        }
        public void sendMessage(){
//                try{
//                        
//                }
        }
        public void listen(){
                new Thread(() -> {
                        String msg;
                        while(socket.isConnected()){
                                try{
                                        msg = in.readUTF();
                                        System.out.println(msg);
                                }catch(IOException e){
                                        e.getMessage();
                                }
                        }
                }).start();
        }
        public static void main(String[] args) throws IOException {
                // TODO code application logic hereç
                Scanner scan = new Scanner(System.in);
                String username =  scan.nextLine();
                Socket socket  = new Socket("192.168.1.136",55555);
                Cliente client = new Cliente(socket,username);
                client.listen();
                client.sendMessage();
        }
        
}
