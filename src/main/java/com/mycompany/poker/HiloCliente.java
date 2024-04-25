/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.poker;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author alumno
 */
public class HiloCliente implements Runnable{
    private Socket cliente;
    private int id;

    public HiloCliente(Socket cliente, int id) {
        this.cliente = cliente;
        this.id=id;
    }

    @Override
    public void run() {
        try(PrintWriter pw = new PrintWriter(cliente.getOutputStream(), true)){
            
            System.out.println("Te ha llegado el turno, jugador "+id);
            
            
        }catch(IOException e){
            System.err.println("Excepcion "+e.getMessage());
        }
    }
    
    
}
