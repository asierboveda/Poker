/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package AdrianUno;

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
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 59001;

    public static void main(String[] args) {

        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT); ObjectInputStream in = new ObjectInputStream(socket.getInputStream()); ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {
            
            Scanner scanner = new Scanner(System.in);
            
            String mensaje = (String) in.readObject();
            System.out.println(mensaje);

            System.out.println("Sus cartas son:");
            ArrayList<Carta> mano = (ArrayList<Carta>) in.readObject();
            for (Carta c : mano) {
                System.out.println(c);
            }

            while (true) {
                System.out.println("\n");
                String instruccionesComplementarias = (String) in.readObject();
                if(instruccionesComplementarias!=null){
                    System.out.println(instruccionesComplementarias);
                    if(instruccionesComplementarias.startsWith("Te debes chupar 4")){
                        mano.add((Carta)in.readObject());
                        mano.add((Carta)in.readObject());
                        mano.add((Carta)in.readObject());
                        mano.add((Carta)in.readObject());
                    }else if(instruccionesComplementarias.startsWith("Te debes chupar 2")){
                        mano.add((Carta)in.readObject());
                        mano.add((Carta)in.readObject());
                    }
                }
                
                String instrucciones = (String) in.readObject();
                System.out.println(instrucciones); 

                System.out.println("Sus cartas son: ");
                for (int i = 0; i < mano.size(); i++) {
                    System.out.println(mano.get(i) + "(" + (i+1) + ")");
                }
                System.out.print("Elija el número de la carta: ");                
                
                int numeroCarta = Integer.parseInt(scanner.nextLine());
                
                out.writeInt(numeroCarta);
                
                if (numeroCarta != 0) {
                    if(mano.get(numeroCarta-1).getValor().equals(Valor.CAMBIO_COLOR)){
                        System.out.print("Elige el color(en mayusculas) a cambiar: ");
                        String colorcambioStr = scanner.next();
                        for(Color c : Color.values()){
                            
                            if(colorcambioStr.equals(c.toString())){
                                out.writeObject(c);
                                
                            }
                        }
                        
                    }else if(mano.get(numeroCarta-1).getValor().equals(Valor.MAS_CUATRO)){
                        System.out.print("Elige el color(en mayusculas) a cambiar: ");
                        String colorcambioStr = scanner.next();
                        for(Color c : Color.values()){
                            
                            if(colorcambioStr.equals(c.toString())){
                                out.writeObject(c);
                                
                            }
                        }
                    mano.remove(numeroCarta - 1);//para que se vayan eliminando las cartas tiradas de la mano
                }
                out.flush();

                String resultado = (String) in.readObject();
                System.out.println(resultado);

                if (resultado.equals("he ganado")) {
                    break;
                }
                }
            }
                    
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();

        }

    }

}
