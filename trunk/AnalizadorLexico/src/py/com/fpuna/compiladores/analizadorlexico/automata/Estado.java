package py.com.fpuna.compiladores.analizadorlexico.automata;

import java.util.ArrayList;
import py.com.fpuna.compiladores.analizadorlexico.Token;

/**
 *
 * @author markos
 */
public class Estado implements Comparable<Estado> {

    private int id;
    private ListaArcos enlaces;
    private boolean visitado, inicial, fin;

    public Estado(int id, boolean esInicial, boolean esFinal, boolean visitado) {
        this.id = id;
        this.visitado = visitado;
        this.fin = esFinal;
        this.inicial = esInicial;
        this.enlaces = new ListaArcos();
    }


    public int compareTo(Estado e) {
        if (this.getId() == e.getId()) {
            return 0;
        } else if (this.getId() > e.getId()) {
            return 1;
        } else {
            return -1;
        }
    }

    @Override
    public String toString() {
        String result = "" + id;
        if (this.isEstadofinal()) {
            result = result + "(fin)";
        }

        if (this.isEstadoinicial()) {
            result = result + "(ini)";
        }
        return result;
    }

    /**
     * Obtener Id del Estado
     * @return Id del estado
     */
    public int getId() {
        return id;
    }

    public ListaArcos getEnlaces() {
        return enlaces;
    }

    public boolean isEstadofinal() {
        return this.fin;
    }

    public boolean isEstadoinicial() {
        return this.inicial;
    }

    public boolean isVisitado() {
        return visitado;
    }

    // ------------------------------ SETTERS ------------------------------ //
    /**
     * Establece un valor para el identificador del estado
     * @param id Identificador del Estado
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Establece si el estado es Final
     * @param estadofinal Boolean que establece si el estado es o no Final
     */
    public void setEstadofinal(boolean estadofinal) {
        this.fin = estadofinal;
    }

    /**
     * Establece si el estado es inicial
     * @param estadoinicial Boolean que establece si el estado es o no Inicial
     */
    public void setEstadoinicial(boolean estadoinicial) {
        this.inicial = estadoinicial;
    }

    /**
     * Establece si el estado fue o no visitado en un recorrido
     * @param visitado Boolean que establece si el estado fue o no visitado en un recorrido
     */
    public void setVisitado(boolean visitado) {
        this.visitado = visitado;
    }

    // --------------------------- OTROS MÉTODOS --------------------------- //
    /**
     * Agrega un nuevo enlace que sale de este estado
     * @param e Arco a agregar
     */
    public void addEnlace(Arco e) {
        // Insertar en la lista de enlaces para tener un método eficiente de
        // recorrido en el futuro
        enlaces.insertar(e);
    }

    /**
     * Retorna el estado destino buscando entre todos los enlaces de este estado.
     * @param a Token de la transicion.
     * @return El estado destino al que va desde este estado por el token a
     */
    public Estado estadoDestino(Token a) {
        return estadoDestinoString(a.getValor());
    }

    /**
     * Retorna el estado destino buscando entre todos los enlaces de este estado.
     * @param a String que es la etiqueta de la transicion.
     * @return El estado destino al que va desde este estado por el token a
     */
    public Estado estadoDestinoString(String a) {
        for (Arco x : enlaces) {
            if (x.getEtiqueta().compareTo(a) == 0) {
                return x.getDestino();
            }
        }
        return null;
    }

    /**
     * Obtiene el primer enlace asociado al simbolo especificado que está
     * cargado en el Hash de enlaces
     * @param simbolo
     * @return
     */
    public Estado getDestinoFromHash(String simbolo) {
        Arco link = this.getEnlaceSimboloFromHash(simbolo);
        Estado result = null;

        if (link != null) {
            result = link.getDestino();
        }
        return result;
    }

    /**
     * Devuelve el enlace relacionado con el símbolo
     * @param simbolo
     * @return
     */
    public Arco getEnlaceSimboloFromHash(String simbolo) {
        return this.enlaces.getEnlaceSimbolo(simbolo);
    }

    /**
     * Si el automata es un AFN, devuelve los enlaces vacios asociado a este
     * estado.
     * @return
     */
    public ArrayList<Arco> getEnlacesVacios() {
        return this.enlaces.getVacios();
    }

    public void eliminarEnlace(Arco e) {
        this.enlaces.borrar(e);
    }

    public boolean esIsla() {
        if (isEstadofinal()) {
            return false;
        }

        boolean esMuerto = true;
        for (Arco e : this.enlaces) {
            if (e.getDestino().getId() != this.getId()) {
                esMuerto = false;
            }
        }
        return esMuerto;
    }
}
