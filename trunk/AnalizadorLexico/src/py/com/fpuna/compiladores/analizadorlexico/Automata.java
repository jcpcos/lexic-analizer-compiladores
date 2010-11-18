package py.com.fpuna.compiladores.analizadorlexico;

import java.util.ArrayList;
import java.util.Iterator;
import py.com.fpuna.compiladores.analizadorlexico.automata.Arco;
import py.com.fpuna.compiladores.analizadorlexico.automata.Estado;
import py.com.fpuna.compiladores.analizadorlexico.automata.ListaArcos;
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
    public static final String EMPTY = " [ empty ] ";
    public int tipoAutomata;
    private ListaEstados listaEstados;
    private ListaEstados listaEstadosFinales;
    private Estado estadoInicial;    
    private String regex;
    private ArrayList<String> alphabet;

    public Automata() {
        listaEstados = new ListaEstados();
        listaEstadosFinales = new ListaEstados();
    }

    public Automata(String simbolo) {
        listaEstados = new ListaEstados();

        Estado origen = new Estado(0, true, false, false);
        Estado destino = new Estado(1, false, true, false);
        Arco enlace = new Arco(origen, destino, simbolo);
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
                Arco enlace = (Arco) itenlaces.next();
                result += "\t"
                        + enlace.getOrigen().getId() + " ---" + enlace.getEtiqueta() + "---> " + enlace.getDestino().getId() + "\n";
            }
        }
        return result;
    }

    public ListaArcos getEnlaces() {
        ListaArcos ret = new ListaArcos();

        for (Estado est : getEstados()) {
            for (Arco enlace : est.getEnlaces()) {
                ret.add(enlace);
            }
        }

        return ret;
    }

    public void addEstado(Estado e) {
        this.listaEstados.insertar(e);
    }

    private void eliminarEstado(Estado e) {
        for (Estado est : this.listaEstados) {
            for (Arco enlace : est.getEnlaces()) {
                if (e.getId() != est.getId() && enlace.getDestino().getId()
                        == e.getId()) {
                    est.eliminarEnlace(enlace);
                }
            }
        }
    }


}
