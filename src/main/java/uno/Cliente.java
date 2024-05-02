/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package uno;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
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
        private BufferedReader br;
        private BufferedWriter bw       ;
        private String username;

        public Cliente(Socket socket,String username) {
                try{
                        this.socket = socket;
                        this.br= new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        this.bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())) ;
                        this.username = username;
                }catch(IOException e){
                        e.getStackTrace();
                }
        }
        public void sendMessage(){
                try{
                        bw.write(username);
                        bw.newLine();
                        bw.flush();
                        Scanner scan = new Scanner(System.in);
                        while(socket.isConnected()){
                                int numeroCarta = scan.nextInt();
                                bw.write(numeroCarta);
                                bw.newLine();
                                bw.flush();
                        }
                }catch(IOException e){
                        e.getMessage();
                }
        }
        public void listen(){
                new Thread(() -> {
                        String msg;
                        while(socket.isConnected()){
                                try{
                                        msg =br.readLine();
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
                System.out.println("Escriba su nombre: ");
                String username =  scan.nextLine();
                Socket socket  = new Socket("192.168.1.136",55500);
                Cliente client = new Cliente(socket,username);

                client.listen();
                client.sendMessage();
        }
        
}
