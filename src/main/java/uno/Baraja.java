/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package uno;

import uno.Carta.Color;
import uno.Carta.Valor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author asier
 */
public class Baraja{
        private final List <Carta> cartas;
        
        public Baraja(){
                cartas = new ArrayList<>();
                crearBaraja();
        }
        private void crearBaraja(){
                //Creo todas las cartas menos los comodines(cartas de color negro)
                for(int i = 0; i<2; i++){
                        for(Color color: Color.values()){
                                if(color != Color.NEGRO) {
                                        for(Valor valor: Valor.values()){
                                                if(valor != Valor.MAS_CUATRO && valor != Valor.CAMBIO_COLOR){
                                                        cartas.add(new Carta(color,valor));
                                                }
                                        }
                                }
                        }
                }
                //Creo las cartas comodines
                for(int i = 0; i<4; i++){
                        cartas.add(new Carta(Color.NEGRO,Valor.MAS_CUATRO));
                        cartas.add(new Carta(Color.NEGRO,Valor.CAMBIO_COLOR));
                }
                Collections.shuffle(cartas);
        }
        public boolean barajaVacia(){
                return cartas.isEmpty();
        }
        public synchronized Carta robarCarta(){
                if(cartas.isEmpty()){
                        return null;
                }else{
                        return cartas.remove(0);
                }
        }
}