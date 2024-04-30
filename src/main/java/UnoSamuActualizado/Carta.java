/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package UnoSamuActualizado;

import java.io.Serializable;

/**
 *
 * @author alumno
 */


public class Carta implements Serializable {

    private final Color color;
    private final Valor valor;

    public Carta(Color color, Valor valor) {
        this.color = color;
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

    public boolean cartaValida(Carta c) {
        // falta cuando negra y cambia de color 
        if (c.color.equals(color.NEGRO)) {
            return true;
        } else {
            return color.equals(c.color) || valor.equals(c.valor);
        }
    }
}
