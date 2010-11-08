/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package py.com.fpuna.compiladores.analizadorlexico;

import java.util.ArrayList;
import java.util.Iterator;
import py.com.fpuna.compiladores.analizadorlexico.automata.Enlace;
import py.com.fpuna.compiladores.analizadorlexico.automata.Estado;
import py.com.fpuna.compiladores.analizadorlexico.automata.ListaEnlaces;
import py.com.fpuna.compiladores.analizadorlexico.automata.ListaEstados;

/**
 *
 * @author markos
 */
public class Automata {
private enum TipoAutomata{};
/*------------------------ ATRIBUTOS ------------------------*/

    /**
     * Lista de Estados que componen el automata
     */
    protected ListaEstados estados;

    /**
     * Apuntador al Estado inicial del mismo
     */
    protected  Estado inicial;

    /**
     * Lista de apuntadores a estados finales
     */
    protected ListaEstados finales;

    /**
     * Identificador del tipo de Automata representado. Puede ser: <br>
     * <ul>
     *  <li>AFN (Automata Finito No-Determinístico)        </li>
     *  <li>AFD (Automata Finito Determinístico)           </li>
     *  <li>AFDMin (Automata Finito Determinístico Mínimo) </li>
     * </ul>
     */
    private TipoAutomata tipo;

    // Los siguientes atributos podríán ser eliminados

    /**
     * Expresion regular representada
     */
    private String regex;

    /**
     * Alfabeto sobre el cual se define la expresión regular
     */
    private ArrayList<String> alpha;

    public String empty = "";


    // VARIABLES AUXILIARES

    private int level = 0;

/*------------------------------ CONSTRUCTORES ------------------------------*/

    /**
     * Constructor Vacío
     */
    public Automata() {
        this.estados = new ListaEstados();
        this.finales = new ListaEstados();
    }

    /**
     * Constructor de un automata simple. Compuesto por dos estados y un solo
     * enlace a través del simbolo especificado.
     * @param simbolo Expresion regular simple (de un solo caracter)
     */
    public Automata(String simbolo) {
        this.estados = new ListaEstados();

        Estado e1 = new Estado(0, true, false, false);
        Estado e2 = new Estado(1, false, true, false);
        Enlace enlace = new Enlace(e1, e2, simbolo);
        e1.addEnlace(enlace);

        this.estados.insertar(e1);
        this.estados.insertar(e2);

        // Actualización de apuntadores auxiliares
        this.inicial = e1;
        this.finales = new ListaEstados();
        this.finales.add(e2);
    }

    /**
     * Constructor auxiliar para automatas simples con especificación del tipo
     * de automata a construir.
     * @param simbolo Expresion regular simple (de un solo caracter)
     * @param tipo Especificación del tipo de automata en construcción
     */
    public Automata(String simbolo, TipoAutomata tipo) {
        this(simbolo);
        this.tipo = tipo;
    }

    /* --------------- GETTERS Y SETTERS -------------------- */



    /**
     * Obtener el estado referenciado por el índice correspondiente
     *
     * @param index indice en el listado donde se encuentra el estado.
     * @return Estado guardado en index
     */
    public Estado getEstado(int index){
        return this.estados.getEstado(index);
    }

    public ListaEstados getEstados() {
        return this.estados;
    }

    public Estado getEstadoById(int id) {
        return this.estados.getEstadoById(id);
    }

    /**
     * Obtener la lista de estados finales.
     *
     * En el AFN, siempre hay un solo estado final, cuya referencia se guarda en
     * la primera posición de este listado.
     * @return ListaEstados Lista de Estados finales del Automata
     */
    public ListaEstados getFinales() {
        return finales;
    }

    /**
     * Obtiene la lista de estados no finales.
     **/
    public ListaEstados getNoFinales(){
        ListaEstados lista = new ListaEstados();
        for(Estado x : estados){
            if(!x.isEstadofinal()){
                lista.insertar(x);
            }
        }
        return lista;
    }


    /**
     * Obtener el estado inicial del automata.
     *
     * @return Estado inicial del automata
     */
    public Estado getInicial() {
        return inicial;
    }

    public void setInicial(Estado ini) {
        this.inicial = ini;
    }

    public ArrayList<String> getAlpha() {
        return this.alpha;
    }

    public String getRegex() {
        return this.regex;
    }


    public void setAlpha(ArrayList<String> alpha) {
        this.alpha = alpha;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }


    /**
     * Renumera los identificadores del Automata incrementando su valor según un
     * incremento dado.
     *
     * @param incremento para renumerar los estados del automata.
     */
    public void renumerar(int incremento){

        //Renumerar Estados
        Iterator it = this.estados.getIterator();
        while (it.hasNext()){
            Estado e = (Estado) it.next();
            e.setId(e.getId()+incremento);
        }

    }

/* TEST */

    public String imprimir(){

        String result ="";

        Iterator it = this.estados.getIterator();
        while (it.hasNext()){
            Estado e = (Estado) it.next();
            result += "\nE."+ e.getId();

            if (e.isEstadoinicial()) {
                result += "(ini)";
            }

            if (e.isEstadofinal()) {
                result += "(fin)";
            }


            result+="\n";

            Iterator itenlaces = e.getEnlaces().getIterator();
            while(itenlaces.hasNext()){
                Enlace enlace = (Enlace) itenlaces.next();
                result +="\t"+
                        enlace.getOrigen().getId() + " ---"+enlace.getEtiqueta()+"---> " + enlace.getDestino().getId()+"\n";
            }
        }
        return result;
    }


    private void eliminarEstado(Estado e){

        for(Estado est: this.estados){
            for(Enlace enlace: est.getEnlaces()){
                if( e.getId() != est.getId() && enlace.getDestino().getId() == e.getId()){
                        est.eliminarEnlace(enlace);
                }
            }
        }
    }


    /***
     * Método que elimina de este Automata los estados muertos, es decir, los
     * estados en el que todos sus enlaces van a si mismo y no es estado final.
     */
    public void eliminar_estados_muertos(){

       for(Estado e : this.getEstados()){
           if(e.esEstadoMuerto()){
               eliminarEstado(e);
           }
       }
   }


    public ListaEnlaces getEnlaces(){
        ListaEnlaces ret = new ListaEnlaces();

        for(Estado est: getEstados()){
            for(Enlace enlace: est.getEnlaces()){
                ret.add(enlace);
            }
        }

        return ret;
    }

    /**
     * Genera un String que puede ser utilizado para graficar con el GraphViz<br><br>
     *
     * Ejemplo: <br><br>
     * <code>
     * digraph test123 {
     *         a -> b -> c;
     *         a -> {x y};
     *         b [shape=box];
     *         c [label="hello\nworld",color=blue,fontsize=24,
     *              fontname="Palatino-Italic",fontcolor=red,style=filled];
     *         a -> z [label="hi", weight=100];
     *         x -> z [label="multi-line\nlabel"];
     *         edge [style=dashed,color=red];
     *         b -> x;
     *         {rank=same; b x}
     * }
     * </code>
     *
     * @return String del grafo formateado para dot (GraphViz)
     */
    public String imprimirGraphViz(){

        String result_header = "Digraph AFN {\n" +
                "\trankdir=LR;\n\toverlap=scale;\n";

        String result_nodes = "";
        String result_edges = "";

        Iterator it = this.estados.getIterator();
        while (it.hasNext()){
            Estado e = (Estado) it.next();
            String shape = "circle";

            if (e.isEstadofinal()) {
                shape = "doublecircle";
            }

            result_nodes+=e.getId() + " [shape="+shape+"];\n";

            shape="circle";

            Iterator itenlaces = e.getEnlaces().getIterator();
            while(itenlaces.hasNext()){

                Enlace enlace = (Enlace) itenlaces.next();

                Estado orig = enlace.getOrigen();
                Estado dest = enlace.getDestino();
                String label = enlace.getEtiqueta();

                result_edges += orig.getId() + " -> " + dest.getId() +
                                " [label = \""+label+"\" ];\n";

            }
        }
        String result = result_header + result_nodes + result_edges + "}";
        return result;
    }


    /**
     * Genera un automata sencillo de prueba.
     * @return
     */
    public static Automata dameAutomata(){
        Automata A1 = new Automata();
        A1.estados.insertar(new Estado(0,true,false, false));
        A1.estados.insertar(new Estado(1,true,false, false));
        A1.estados.insertar(new Estado(2,true,false, false));
        A1.estados.insertar(new Estado(3,true,false, false));
        A1.estados.insertar(new Estado(4,true,false, false));
        A1.estados.insertar(new Estado(5,true,false, false));

        //Estado 0
        A1.estados.getEstadoById(0).addEnlace( new Enlace(A1.estados.getEstadoById(0),
                                           A1.estados.getEstadoById(1), "a"));

        A1.estados.getEstadoById(0).addEnlace( new Enlace(A1.estados.getEstadoById(0),
                                           A1.estados.getEstadoById(2), "b"));


        //Estado 1 y 2
        A1.estados.getEstadoById(1).addEnlace( new Enlace(A1.estados.getEstadoById(1),
                                           A1.estados.getEstadoById(3), "a"));

        A1.estados.getEstadoById(2).addEnlace( new Enlace(A1.estados.getEstadoById(2),
                                           A1.estados.getEstadoById(4), "a"));


        //Estado 3 y 4
        A1.estados.getEstadoById(3).addEnlace( new Enlace(A1.estados.getEstadoById(3),
                                           A1.estados.getEstadoById(5), "b"));

        A1.estados.getEstadoById(4).addEnlace( new Enlace(A1.estados.getEstadoById(4),
                                           A1.estados.getEstadoById(5), "a"));
        return A1;
    }

    public TipoAutomata getTipo() {
        return tipo;
    }

    public void setTipo(TipoAutomata tipo) {
        this.tipo = tipo;
    }

    public void addEstado(Estado e){
        this.estados.insertar(e);
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
