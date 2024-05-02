/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package uno;

/**
 *
 * @author asier
 */
public class Uno {

        public static void main(String[] args) {

                int turno = 0;
                int sentido = -1;
                turno = (turno + sentido) % 4;
                if (turno == -1) {
                        turno = 3;
                        System.out.println(turno);
                
        }}}
