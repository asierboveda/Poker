/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package UnoLunes;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Cliente {

    /**
     * @param args the command line arguments
     */
    private Socket socket;
    static private ObjectInputStream in; //no se si poner estatic aqui esta bien
    static private ObjectOutputStream out;
    private String username;

    public Cliente(Socket socket, String username) {
        try {
            this.socket = socket;
            this.in = new ObjectInputStream(socket.getInputStream());
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.username = username;
        } catch (IOException e) {
            e.getStackTrace();
        }
    }

    /*
        public void sendMessage(){
                try{
                        
                }
        }
     */
 /*
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
     */
    public static void main(String[] args) throws IOException {

        // TODO code application logic hereç
        Scanner scan = new Scanner(System.in);
        String username = scan.nextLine();
        Socket socket = new Socket("172.18.83.40", 50019);
        Cliente client = new Cliente(socket, username);

        try {
            //recibe bienvenida

            String bienvenida = (String) in.readObject();
            System.out.println(bienvenida);

            //recibe mano
            ArrayList<Carta> mano = (ArrayList<Carta>) in.readObject();
            for (int i = 0; i < 7; i++) {
                System.out.println(mano.get(i).toString());
            }

            //empieza a jugar
            /*while (true){
                    
                }*/
            //client.listen();
            //client.sendMessage();
        } catch (IOException | ClassNotFoundException ex) {
            System.out.println("erorr");
        }

    }

}
