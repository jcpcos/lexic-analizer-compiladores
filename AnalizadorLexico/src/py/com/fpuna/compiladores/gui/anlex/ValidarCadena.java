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
import py.com.fpuna.compiladores.analizadorlexico.automata.Enlace;
import py.com.fpuna.compiladores.analizadorlexico.automata.Estado;
import py.com.fpuna.compiladores.analizadorlexico.automata.ListaEstados;

/**
 *
 * @author lizj
 */
public class ValidarCadena {

    private String validationString;
    private ArrayList<ListaEstados> estadosPathAFN;
    private ListaEstados estadosPath;
    private int currentIndex = -1;
    private Thompson automata;
    Stack<Estado> estadosAnt;           // conjunto actual de estados
    Stack<Estado> estadosNew;           // conjunto siguiente de estados
    boolean[] yaEstaEn;                 // cuales estados ya están en EstadosNew
    ArrayList<Enlace> enlacesVacios;         // lista utilizada para almacenar mover[s,vacio]
    String currentCar;

    public ValidarCadena() {
    }

    public ValidarCadena(String validationString, Thompson automata) {
        this.validationString = validationString;
        this.automata = automata;

        this.estadosPathAFN = new ArrayList<ListaEstados>();
        this.estadosPath = new ListaEstados();
        this.yaEstaEn    = new boolean[automata.getEstados().size()];
        this.estadosAnt  = new Stack<Estado>();
        this.estadosNew  = new Stack<Estado>();

    }


    /**
     * Proceso que recorre el automata para verificar si la cadena de prueba
     * perteneces al lenguaje descrito por la expresión regular.
     * @param test cadena de prueba cuya pertenencia queremos verificar
     * @return boolean True si la cadena pertenece, false en caso contrario.
     */
    public boolean validar() {
        boolean exito = true;

        if (this.automata.tipoAutomata == TipoAutomata.AFN.ordinal()) {
            exito = this.validar_AFN();
        } else {
            exito = this.validar_AFD();
        }

        return exito;
    }

    private boolean contieneFinal(ListaEstados S) {
        boolean exito = false;
        for (Estado e : S ) {
            exito = e.isEstadofinal();
            if (exito) {
                break;
            }
        }
        return exito;
    }

    private boolean validar_AFN() {
        boolean exito = true;

        Subconjunto subc = new Subconjunto(this.automata);
        ListaEstados S = new ListaEstados();
        S = subc.e_cerradura(this.automata.getInicial(), S);
        String c = this.sigCar();

        this.estadosPathAFN.add(S);

        while (c.compareToIgnoreCase("")!=0) {
            S = subc.mover(S, new Token(c));
            S = subc.e_cerradura(S);

            if (S == null || S.size() == 0) {
                exito = false;
                break;
            }

            this.estadosPathAFN.add(S);

            c = this.sigCar();
        }

        if (exito) {
            exito = this.contieneFinal(S);
        }

        return exito;
    }

    private boolean validar_AFD() {
        Estado s = this.automata.getInicial();
        String c = this.sigCar();
        boolean exito = true;

        // empezamos a cargar el camino de la simulación
        this.estadosPath.insertar(s);

        while (c.compareToIgnoreCase("")!=0) {
            s = this.mover(s, c);
            if (s == null) {
                exito = false;
                break;
            }

            this.estadosPath.insertar(s);
            c = this.sigCar();
        }

        if (s != null && !s.isEstadofinal()) {
           exito = false;
        }

        return exito;
    }

    private Estado mover(Estado s, String c) {
        Estado next = s.getDestinoFromHash(c);
        return next;
    }

    private String sigCar() {
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
