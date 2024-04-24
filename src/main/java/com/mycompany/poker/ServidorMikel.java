
package com.mycompany.poker;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ServidorMikel {
    
    private static final int NUM_JUGADORES=5;
    
    public static void main(String[] args) {
        //al poenrlo en el try no hace falta cerrar
        try(ServerSocket servidor = new ServerSocket(50678);){
            
            //se guardan los clientes pa un futuro hacer cosas cn ellos
            Socket clientes [] = new Socket[NUM_JUGADORES];
            //los inicializo pa poder hacer close al final
            Socket c1=null;Socket c2=null;Socket c3=null;Socket c4=null;Socket c5=null;
            
            int i=1;
            while(i<=NUM_JUGADORES){
                System.out.println("Servidor esperando al siguiente jugador...");
                System.out.println("Conectados "+(i-1)+" / "+ 5+"....");
                
                switch(i){
                    case 1:
                        c1=servidor.accept();
                        System.out.println("Jugador 1 conectado al servidor");//aviso de que se ha conectado
                        clientes[i-1]=c1;//añado
                        break;
                    case 2:
                        c2=servidor.accept();
                        clientes[i-1]=c2;
                        System.out.println("Jugador 2 conectado al servidor");
                        break;
                    case 3:
                        c3=servidor.accept();
                        clientes[i-1]=c3;
                        System.out.println("Jugador 3 conectado al servidor");
                        break;
                    case 4:
                        c4=servidor.accept();
                        clientes[i-1]=c4;
                        System.out.println("Jugador 4 conectado al servidor");
                        break;
                    case 5:
                        c5=servidor.accept();
                        clientes[i-1]=c5;
                        System.out.println("Jugador 5 conectado al servidor");
                        break;
                }
                
                i++;
                
            }
            
            //leo lo que han mandado los clientes
            for(int j=1; j<=NUM_JUGADORES;j++){
                try(Scanner in = new Scanner(clientes[j-1].getInputStream())){
                    if(in.hasNext()){
                        int apuestLeida = in.nextInt();
                        System.out.println("El jugador "+j+" apostó "+apuestLeida);
                    }
                }
            }
            
            //cierro xq los clientes ya han terminado
            c1.close();c2.close();c3.close();c4.close();c5.close();
            
            
        }catch (IOException excepcion) {
                System.err.println("error");
        }
    }
    
}
