/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package UnoSamuActualizado;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author alumno
 */
public class Cliente {

    /**
     * @param args the command line arguments
     */
    private static final String SERVER_ADREES = "127.0.0.1";
    private static final int SERVER_PORT = 59001;

    public static void main(String[] args) {

        try {

            Socket s = new Socket(SERVER_ADREES, SERVER_PORT);
            ObjectInputStream in = new ObjectInputStream(s.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());

            //leer el mensage
            String mensaje = (String) in.readObject();
            System.out.println(mensaje);

            //recibir su mano
            System.out.println("Sus cartas son:");
            ArrayList<Carta> mano = (ArrayList) in.readObject();
            for (Carta cartas : mano) {

                System.out.println(cartas);
            }
            //Turnos
            while (true) {
                //recibe instrucciones
                String instrucciones = (String) in.readObject();
                //Se muestran las cratas y sus posiciones
                System.out.println("Sus cartas son: ");
                
                int cont = 1;
                for (Carta cartas : mano) {
                    System.out.println( cont +": "+cartas);
                    cont++;
                }
                System.out.println("Elija la carta para tirar (recuerde que tiene que ser valida)"); 
                Scanner sc = new Scanner(System.in);
                int numeroCarta = sc.nextInt();
                Carta cartaElejida = mano.remove(numeroCarta-1);
                
                out.writeObject(cartaElejida);
                //ver si ha ganado
                
                if (mano.isEmpty()){
                    System.out.println("Usted ha ganado!!!!");
                    String ganado = "he ganado";
                    out.writeObject(ganado);
                    
                }else{
                    System.out.println("Ha usted le quedan "+mano.size()+" cartas.");
                    String noGanado = "no he ganado";
                    out.writeObject(noGanado);
                }
                
                

            }

        } catch (ClassNotFoundException | IOException e) {
            System.out.println("Exception: " + e);
            System.out.println("Message: " + e.getMessage());
            e.printStackTrace(System.err);
        }

    }

}
