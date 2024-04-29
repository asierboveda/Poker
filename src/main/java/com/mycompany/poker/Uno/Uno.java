/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.poker.Uno;

/**
 *
 * @author asier
 */
public class Uno {

    public static void main(String[] args) {
        Baraja baraja = new Baraja();
        while(!baraja.barajaVacia()){
                System.out.println(baraja.robarCarta());
        }
    }
}
