/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.poker.Uno;

/**
 *
 * @author asier
 */
public class PaqueteEnvio {
        public Carta carta;
        public int cartas_restantes;
        public int turno;

        public PaqueteEnvio(Carta carta, int cartas_restantes, int turno) {
                this.carta = carta;
                this.cartas_restantes = cartas_restantes;
                this.turno = turno;
        }
        
}
