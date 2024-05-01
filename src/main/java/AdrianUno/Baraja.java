/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package AdrianUno;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author alumno
 */
public class Baraja implements Serializable {
        private final List <Carta> cartas;
        private final List<Carta> cartasTiradas;
        
        public Baraja(){
                cartas = new ArrayList<>();
                cartasTiradas = new ArrayList<Carta>();
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
        public synchronized void tirarCarta(Carta carta){
                cartasTiradas.add(carta);
        }
        public synchronized Carta mostrarCartaArriba(){
                return cartasTiradas.get(-1);
        }
        
        
}
