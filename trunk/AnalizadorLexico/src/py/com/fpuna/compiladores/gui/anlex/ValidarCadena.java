/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package py.com.fpuna.compiladores.gui.anlex;

import java.util.ArrayList;
import java.util.Stack;
import py.com.fpuna.compiladores.analizadorlexico.Automata.TipoAutomata;
import py.com.fpuna.compiladores.analizadorlexico.Token;
import py.com.fpuna.compiladores.analizadorlexico.algoritmos.Subconjunto;
import py.com.fpuna.compiladores.analizadorlexico.algoritmos.Thompson;
import py.com.fpuna.compiladores.analizadorlexico.automata.Arco;
import py.com.fpuna.compiladores.analizadorlexico.automata.Estado;
import py.com.fpuna.compiladores.analizadorlexico.automata.ListaEstados;

/**
 *
 * @author lizj
 */
public class ValidarCadena {

    private String validationString;
    private ArrayList<ListaEstados> estadosAFN;
    private ListaEstados estadosPath;
    private int currentIndex = -1;
    private Thompson automata;
    Stack<Estado> estadosAnt;           // conjunto actual de estados
    Stack<Estado> estadosNew;           // conjunto siguiente de estados
    boolean[] yaEstaEn;                 // cuales estados ya están en EstadosNew
    ArrayList<Arco> enlacesVacios;         // lista utilizada para almacenar mover[s,vacio]
    String currentCar;

    public ValidarCadena() {
    }

    public ValidarCadena(String validationString, Thompson automata) {
        this.validationString = validationString;
        this.automata = automata;
        this.estadosAFN = new ArrayList<ListaEstados>();
        this.estadosPath = new ListaEstados();
        this.yaEstaEn    = new boolean[automata.getEstados().size()];
        this.estadosAnt  = new Stack<Estado>();
        this.estadosNew  = new Stack<Estado>();

    }


    /**
     * Proceso que recorre el automata para verificar si la cadena de prueba
     * perteneces al lenguaje descrito por la expresión regular.
     */
    public boolean validar() {
        boolean pertenece = true;

        if (this.automata.tipoAutomata == TipoAutomata.AFN.ordinal()) {
            pertenece = this.validaAFN();
        } else {
            pertenece = this.validaAFD();
        }

        return pertenece;
    }

    private boolean contieneFinal(ListaEstados S) {
        boolean pertenece = false;
        for (Estado e : S ) {
            pertenece = e.isEstadofinal();
            if (pertenece) {
                break;
            }
        }
        return pertenece;
    }

    private boolean validaAFN() {
        boolean pertenece = true;

        Subconjunto subconjunto = new Subconjunto(this.automata);
        ListaEstados listState = new ListaEstados();
        listState = subconjunto.cerradura_empty(this.automata.getInicial(), listState);
        String symbol = this.nextSymbol(); //obtiene el sgte simbolo
        this.estadosAFN.add(listState);

        while (symbol.compareToIgnoreCase("")!=0) {
            listState = subconjunto.mueve(listState, new Token(symbol));
            listState = subconjunto.cerradura_empty(listState);

            if (listState == null || listState.size() == 0) {
                pertenece = false;
                break;
            }

            this.estadosAFN.add(listState);
            symbol = this.nextSymbol();
        }

        if (pertenece) {
            pertenece = this.contieneFinal(listState);
        }

        return pertenece;
    }

    private boolean validaAFD() {
        Estado state = this.automata.getInicial();
        String symbol = this.nextSymbol();
        boolean pertenece = true;

        this.estadosPath.insertar(state);
        while (symbol.compareToIgnoreCase("")!=0) {
            state = this.mover(state, symbol);
            if (state == null) {
                pertenece = false;
                break;
            }

            this.estadosPath.insertar(state);
            symbol = this.nextSymbol();
        }

        if (state != null && !state.isEstadofinal()) {
           pertenece = false;
        }

        return pertenece;
    }

    private Estado mover(Estado s, String c) {
        Estado next = s.getDestinoFromHash(c);
        return next;
    }

    private String nextSymbol() {
        String siguiente = "";
        this.currentIndex++;
        if (this.currentIndex < this.validationString.length()) {
            siguiente = this.validationString.charAt(this.currentIndex)+"";
        } else {
            this.currentIndex = 0;
        }
        return siguiente;
    }

    public String getValidationString() {
        return validationString;
    }

    public void setValidationString(String validationString) {
        this.validationString = validationString;
        this.currentIndex = 0;
        this.estadosPath = new ListaEstados();
    }

}
