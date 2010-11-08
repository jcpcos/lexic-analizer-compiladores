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
    public enum TipoAutomata {

        AFN,
        AFD,
        AFDMin
    }
    private ListaEstados listaEstados;
    private ListaEstados listaEstadosFinales;
    private Estado estadoInicial;
    public int tipoAutomata;
    private String regex;
    private ArrayList<String> alphabet;
    public static final String EMPTY = "";

    public Automata() {
        listaEstados = new ListaEstados();
        listaEstadosFinales = new ListaEstados();
    }

    public Automata(String simbolo) {
        listaEstados = new ListaEstados();

        Estado origen = new Estado(0, true, false, false);
        Estado destino = new Estado(1, false, true, false);
        Enlace enlace = new Enlace(origen, destino, simbolo);
        origen.addEnlace(enlace);
        estadoInicial = origen;

        listaEstados.insertar(origen);
        listaEstados.insertar(destino);

        listaEstadosFinales = new ListaEstados();
        listaEstadosFinales.add(destino);
    }

    public Automata(String simbolo, int tipo) {
        this(simbolo);
        this.tipoAutomata = tipo;
    }

    private void eliminarEstado(Estado e) {
        for (Estado est : this.listaEstados) {
            for (Enlace enlace : est.getEnlaces()) {
                if (e.getId() != est.getId() && enlace.getDestino().getId()
                        == e.getId()) {
                    est.eliminarEnlace(enlace);
                }
            }
        }
    }

    public void eliminarIslas() {
        for (Estado e : this.getEstados()) {
            if (e.esIsla()) {
                eliminarEstado(e);
            }
        }
    }

    public Estado getEstadoInicial() {
        return estadoInicial;
    }

    public void setEstadoInicial(Estado estadoInicial) {
        this.estadoInicial = estadoInicial;
    }

    public Automata(ListaEstados listaEstados, ListaEstados listaEstadosFinales, Estado estadoInicial) {
        this.listaEstados = listaEstados;
        this.listaEstadosFinales = listaEstadosFinales;
        this.estadoInicial = estadoInicial;
    }

    public ListaEstados getListaEstados() {
        return listaEstados;
    }

    public void setListaEstados(ListaEstados listaEstados) {
        this.listaEstados = listaEstados;
    }

    public ListaEstados getListaEstadosFinales() {
        return listaEstadosFinales;
    }

    public void setListaEstadosFinales(ListaEstados listaEstadosFinales) {
        this.listaEstadosFinales = listaEstadosFinales;
    }
    
    public Estado getEstado(int index) {
        return this.listaEstados.getEstado(index);
    }

    public ListaEstados getEstados() {
        return this.listaEstados;
    }

    public Estado getEstadoById(int id) {
        return this.listaEstados.getEstadoById(id);
    }

    public ListaEstados getFinales() {
        return listaEstadosFinales;
    }

    public ListaEstados getNoFinales() {
        ListaEstados lista = new ListaEstados();
        for (Estado x : listaEstados) {
            if (!x.isEstadofinal()) {
                lista.insertar(x);
            }
        }
        return lista;
    }

    public Estado getInicial() {
        return estadoInicial;
    }

    public void setInicial(Estado ini) {
        this.estadoInicial = ini;
    }

    public ArrayList<String> getAlpha() {
        return this.alphabet;
    }

    public String getRegex() {
        return this.regex;
    }

    public void setAlpha(ArrayList<String> alpha) {
        this.alphabet = alpha;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public void renumerar(int incremento) {
       //Renumerar Estados
        Iterator it = this.listaEstados.getIterator();
        while (it.hasNext()) {
            Estado e = (Estado) it.next();
            e.setId(e.getId() + incremento);
        }

    }

    /* TEST */
    public String imprimir() {

        String result = "";

        Iterator it = this.listaEstados.getIterator();
        while (it.hasNext()) {
            Estado e = (Estado) it.next();
            result += "\nE." + e.getId();

            if (e.isEstadoinicial()) {
                result += "(ini)";
            }

            if (e.isEstadofinal()) {
                result += "(fin)";
            }


            result += "\n";

            Iterator itenlaces = e.getEnlaces().getIterator();
            while (itenlaces.hasNext()) {
                Enlace enlace = (Enlace) itenlaces.next();
                result += "\t"
                        + enlace.getOrigen().getId() + " ---" + enlace.getEtiqueta() + "---> " + enlace.getDestino().getId() + "\n";
            }
        }
        return result;
    }

    public ListaEnlaces getEnlaces() {
        ListaEnlaces ret = new ListaEnlaces();

        for (Estado est : getEstados()) {
            for (Enlace enlace : est.getEnlaces()) {
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
    public String imprimirGraphViz() {

        String result_header = "Digraph AFN {\n"
                + "\trankdir=LR;\n\toverlap=scale;\n";

        String result_nodes = "";
        String result_edges = "";

        Iterator it = this.listaEstados.getIterator();
        while (it.hasNext()) {
            Estado e = (Estado) it.next();
            String shape = "circle";

            if (e.isEstadofinal()) {
                shape = "doublecircle";
            }

            result_nodes += e.getId() + " [shape=" + shape + "];\n";

            shape = "circle";

            Iterator itenlaces = e.getEnlaces().getIterator();
            while (itenlaces.hasNext()) {

                Enlace enlace = (Enlace) itenlaces.next();

                Estado orig = enlace.getOrigen();
                Estado dest = enlace.getDestino();
                String label = enlace.getEtiqueta();

                result_edges += orig.getId() + " -> " + dest.getId()
                        + " [label = \"" + label + "\" ];\n";

            }
        }
        String result = result_header + result_nodes + result_edges + "}";
        return result;
    }

    /**
     * Genera un automata sencillo de prueba.
     * @return
     */
    public static Automata dameAutomata() {
        Automata A1 = new Automata();
        A1.listaEstados.insertar(new Estado(0, true, false, false));
        A1.listaEstados.insertar(new Estado(1, true, false, false));
        A1.listaEstados.insertar(new Estado(2, true, false, false));
        A1.listaEstados.insertar(new Estado(3, true, false, false));
        A1.listaEstados.insertar(new Estado(4, true, false, false));
        A1.listaEstados.insertar(new Estado(5, true, false, false));

        //Estado 0
        A1.listaEstados.getEstadoById(0).addEnlace(new Enlace(A1.listaEstados.getEstadoById(0),
                A1.listaEstados.getEstadoById(1), "a"));

        A1.listaEstados.getEstadoById(0).addEnlace(new Enlace(A1.listaEstados.getEstadoById(0),
                A1.listaEstados.getEstadoById(2), "b"));


        //Estado 1 y 2
        A1.listaEstados.getEstadoById(1).addEnlace(new Enlace(A1.listaEstados.getEstadoById(1),
                A1.listaEstados.getEstadoById(3), "a"));

        A1.listaEstados.getEstadoById(2).addEnlace(new Enlace(A1.listaEstados.getEstadoById(2),
                A1.listaEstados.getEstadoById(4), "a"));


        //Estado 3 y 4
        A1.listaEstados.getEstadoById(3).addEnlace(new Enlace(A1.listaEstados.getEstadoById(3),
                A1.listaEstados.getEstadoById(5), "b"));

        A1.listaEstados.getEstadoById(4).addEnlace(new Enlace(A1.listaEstados.getEstadoById(4),
                A1.listaEstados.getEstadoById(5), "a"));
        return A1;
    }

    public void addEstado(Estado e) {
        this.listaEstados.insertar(e);
    }
}
