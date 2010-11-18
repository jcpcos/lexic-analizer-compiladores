package py.com.fpuna.compiladores.analizadorlexico.algoritmos;

import java.util.ArrayList;
import java.util.Iterator;
import py.com.fpuna.compiladores.analizadorlexico.Automata;
import py.com.fpuna.compiladores.analizadorlexico.Token;
import py.com.fpuna.compiladores.analizadorlexico.automata.Arco;
import py.com.fpuna.compiladores.analizadorlexico.automata.Estado;
import py.com.fpuna.compiladores.analizadorlexico.automata.ListaEstados;
import py.com.fpuna.compiladores.exceptions.AutomataException;

/**
 *
 * @author markos
 */
public class Subconjunto {

    private Thompson automataSubconj;
    private TransitionMatrix afdMatrix;
    private ArrayList<ListaEstados> listaEstados;

    public Subconjunto(Thompson a) {
        automataSubconj = a; //donde a sería el AFN
        afdMatrix = new TransitionMatrix();
        listaEstados = new ArrayList();  //la lista de estadosD (estados en el AFD)
    }

    //construcción de subconjuntos (Fig 3.25)
    public TransitionMatrix ejecutar() throws AutomataException {
        Iterator it;
        Token simbolo;
        ListaEstados lista_1; 
        int i = 0; //para el id de la lista listaEstados

        Estado est_inicial = automataSubconj.getEstados().getEstadoInicial(); //obtiene el estado inicial de la entrada que fue el AFN
        ListaEstados lista_2 = cerradura_empty(est_inicial, new ListaEstados());
        lista_2.setId(0);
        listaEstados.add(lista_2); //al inicio, lo q está en lista_2 es el único estado dentro de listaEstados y no está marcado
        ListaEstados lista_3 = estadoSinMarcar();

        while (lista_3!=null) { //mientras haya un estado no marcado lista_3 en listaEstados
            TransitionMatrixKey clave;
            System.out.println(lista_3);
            lista_3.setMarcado(true); //marcar T

            it = automataSubconj.getAlpha().iterator();
            while (it.hasNext()) { //itera sobre el alfabeto de símbolos de entrada
                simbolo = new Token((String) it.next());
                lista_1 = cerradura_empty(mueve(lista_3, simbolo));
                if (lista_1 == null) {
                    continue;
                }
                int id_U = estaEnEstadosD(lista_1);
                if (id_U == -1) { //si lista_1 no está en listaEstados (estadosD en el pseudocódigo)
                    lista_1.setMarcado(false); //añadir lista_1 como estado no-marcado a listaEstados
                    lista_1.setId(listaEstados.size());
                    listaEstados.add(lista_1);
                } else {
                    lista_1.setId(id_U); 
                }
                clave = new TransitionMatrixKey(lista_3, simbolo);
                afdMatrix.setValor(clave, lista_1);
            }
            lista_3 = estadoSinMarcar();
            //System.out.println("iteracion " + i++);
        }
        //System.out.println("aca no llega");
        return this.afdMatrix;
    }

    /**
     * Cálculo de cerradura-e (Fig 2.6)
     * @param s Estado que se agrega y recorre por sus vacios.
     * @param listaCurr lista de estados donde se van agregando. Al inicio está vacia
     * @return La lista de estados por los que se recorre mediante vacio desde el estado "s"
     */
    public ListaEstados cerradura_empty(Estado s, ListaEstados listaCurr) {
        Iterator it = s.getEnlaces().getIterator(); //todos los enlaces del estado s
        ListaEstados listaNew = null;
        while (it.hasNext()) { //mientras haya arco
            Arco e = (Arco) it.next(); //sería lo correspondiente a sacar un elemento de la pila
            if (e.getEtiqueta().compareTo(Automata.EMPTY) == 0) { //si la etiqueta del enlace es EMPTY
                listaNew = cerradura_empty(e.getDestino(), listaCurr);
                listaCurr = unirListas(listaCurr, listaNew);

            }
        }
        listaCurr.insertar(s); //lo correspondiente a meter el estado u en la pila
        return listaCurr;
    }

    /***
     * Por cada estado de la lista recibida se recorre recursivamente por los
     * enlaces "vacio" y se genera una nueva lista.
     * @param T
     * @return
     */
    public ListaEstados cerradura_empty(ListaEstados T) {
        if (T == null) {
            return null;
        }

        ListaEstados lista_ret = new ListaEstados();
        Iterator it = T.getIterator();
        Estado act;

        while (it.hasNext()) {
            act = (Estado) it.next();
            lista_ret = unirListas(lista_ret, cerradura_empty(act, new ListaEstados()));
        }

        return lista_ret;
    }

    /***
     * Realiza el algoritmo mover que se propone en el capítulo 3.
     * Dado una lista de estados "T" y un símbolo "a" del alfabeto, mover
     * retorna una lista con los estados en donde existe una transición por "a"
     * desde alguno de los estados que hay en "T".
     *
     * @param T Lista de Estados.
     * @param a Símbolo del alfabeto.
     * @return Lista de Estados alos que se puede ir por a desde c/ estado en T
     */
    public ListaEstados mueve(ListaEstados T, Token a) {
        Iterator itEstados = null;
        Iterator itEnlaces = null;
        Estado estado = null;
        Arco enlace = null;
        ListaEstados lista = new ListaEstados();

        itEstados = T.getIterator();
        while (itEstados.hasNext()) {
            estado = (Estado) itEstados.next();
            itEnlaces = estado.getEnlaces().getIterator();

            while (itEnlaces.hasNext()) {
                enlace = (Arco) itEnlaces.next();
                if (enlace.getEtiqueta().compareTo(a.getValor()) == 0) {
                    lista.insertar(enlace.getDestino());
                }
            }
        }
        if (lista.size() == 0) {
            return null;
        } else {
            return lista;
        }
    }

    public static ListaEstados unirListas(ListaEstados A, ListaEstados B) {
        ListaEstados new_list = new ListaEstados();
        Iterator it;
        Estado est_tmp;

        if (A != null) {
            it = A.getIterator();
            while (it.hasNext()) {
                est_tmp = (Estado) it.next();
                try {
                    new_list.getEstadoById(est_tmp.getId());
                } catch (Exception ex) {
                    new_list.insertar(est_tmp);
                }
            }
        }

        if (B != null) {
            it = B.getIterator();
            while (it.hasNext()) {
                est_tmp = (Estado) it.next();
                try {
                    new_list.getEstadoById(est_tmp.getId());
                } catch (Exception ex) {
                    new_list.insertar(est_tmp);
                }
            }
        }

        return new_list;
    }

    /**
     * Eliminación de los estados inalcanzables.
     * Método estatico que recibe un AFD y retorna un nuevo AFD sin los estados
     * inalcanzables. Necesita del metodo estatico "recorrer"
     *
     * @param AFD
     * @return AFD sin estados inalcanzables
     */
    public static Thompson eliminar_estados_inalcanzables(Thompson AFD) {
        Estado inicial = AFD.getInicial();
        AFD.getEstados().resetVisitas();
        visitarRecursivo(inicial);

        Thompson AFDNEW = new Thompson();
        AFDNEW.setAlpha(AFD.getAlpha());
        AFDNEW.setRegex(AFD.getRegex());

        Iterator it = AFD.getEstados().getIterator();
        while (it.hasNext()) {
            Estado e = (Estado) it.next();
            if (e.isVisitado()) {

                if (e.isEstadoinicial()) {
                    AFDNEW.setInicial(e);
                }
                if (e.isEstadofinal()) {
                    AFDNEW.getFinales().insertar(e);
                }
                AFDNEW.addEstado(e);
            }

        }

        return AFDNEW;
    }

    /**
     * Método que marca como visitado un nodo con sus respectivos
     * hijos, lo hace recursivamente.
     *
     * @param Estado actual a marcar como visitado
     */
    public static void visitarRecursivo(Estado actual) {
        if (!actual.isVisitado()) {
            actual.setVisitado(true);
            Iterator it = actual.getEnlaces().iterator();
            while (it.hasNext()) {
                Arco enlace = (Arco) it.next();
                visitarRecursivo(enlace.getDestino());
            }
        }
    }
    
    /**
     * Retorna el primer estado sin marcar que encuentra en listaEstados.
     * Si no existe ninguno sin marcar, lanza una excepción.
     *
     * @return
     * @throws exceptions.AutomataException
     */
    private ListaEstados estadoSinMarcar() throws AutomataException {
        Iterator it = listaEstados.iterator();
        ListaEstados list_est;
        while (it.hasNext()) {
            list_est = (ListaEstados) it.next();
            if (!list_est.isMarcado()) {
                return list_est;
            }
        }
       // throw new AutomataException("No hay lista de estados sin marcar en listaEstados.");
        return null;
    }

    /***
     * Metodo que retorna el id de la lista de estados U dentro de
     * listaEstados, si es que U no esta en la lista de estados retorna -1.
     *
     * @param U Lista de estados
     * @return El id de la lista U dentro de Destados
     */
    private int estaEnEstadosD(ListaEstados U) {
        Iterator it = listaEstados.iterator();
        ListaEstados tmp;
        while (it.hasNext()) {
            tmp = (ListaEstados) it.next();
            if (tmp.compareTo(U) == 0) {
                return tmp.getId();
            }
        }
        return -1;
    }
}
