/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package uno;

/**
 *
 * @author asier
 */
public class Carta {

        public enum Color {
                AMARILLO, AZUL, ROJO, VERDE, NEGRO
        }

        public enum Valor {
                CERO, UNO, DOS, TRES, CUATRO, CINCO, SEIS, SIETE, OCHO, NUEVE, MAS_DOS, CAMBIO_SENTIDO, SALTO, CAMBIO_COLOR, MAS_CUATRO
        }

        private final Color color;
        private final Valor valor;

        public Carta(Color color, Valor valor) {
                this.color =color;
                this.valor = valor;
                }
        

        public Color getColor() {
                return color;
        }

        public Valor getValor() {
                return valor;
        }

        @Override
        public String toString() {
                return color + " " + valor;
        }
        
        public boolean cartaValida(Carta c){
                // falta cuando negra y cambia de color 
                if(c.color.equals(color.NEGRO)){
                        return true;
                }else return color.equals(c.color) || valor.equals(c.valor);
        }
}
