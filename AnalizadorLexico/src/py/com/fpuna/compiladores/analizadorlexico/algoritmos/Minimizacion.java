package py.com.fpuna.compiladores.analizadorlexico.algoritmos;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import py.com.fpuna.compiladores.analizadorlexico.Automata;
import py.com.fpuna.compiladores.analizadorlexico.automata.Enlace;
import py.com.fpuna.compiladores.analizadorlexico.automata.Estado;
import py.com.fpuna.compiladores.analizadorlexico.automata.ListaEstados;
import py.com.fpuna.compiladores.exceptions.AutomataException;

/**
 * Implementación del algoritmo de minimización de un AFD definido en la sección 3.9.6 del libro.
 */
public class Minimizacion {

    Automata AFD;

    public Minimizacion(Automata a) {
        this.AFD = a;
    }

    /**
     * @return AFDmin Un nuevo automata con el menor nro de estados posibles
     * @throws exceptions.AutomataException
     */
    public Automata afdmin() throws AutomataException {

        ArrayList<ListaEstados> P = new ArrayList<ListaEstados>();
        ArrayList<ListaEstados> Pnew = new ArrayList<ListaEstados>();

        //Contruir una particion inicial P = {No_Finales, Finales}

        int nro_estado = 0;
        ListaEstados nofinales = AFD.getNoFinales();
        ListaEstados finales = AFD.getFinales();

        if (nofinales != null && nofinales.cantidad() > 0) {
            nofinales.setId(nro_estado++);
            P.add(nofinales);
        }

        if (finales != null && finales.cantidad() > 0) {
            finales.setId(nro_estado++);
            P.add(finales);
        }

        /**
         * Construir Pnew aplicando para cada grupo G en P lo siguiente:
            1. Partir G en subgrupos de forma que r y s (siendo que r, al igual que s, pertenece a G)
              se encuentren en el mismo subgrupo, si y solo si para todo el alfabeto r y s tienen
              transiciones hacia los mismos grupos de P.
            2. Agregar en Pnew cada subgrupo y eliminar G.
            3. Si P = Pnew, entonces Pfinal = Pnew, e ir al paso 4. Sino, ir al paso 2
            4. Elegir un estado de c/ grupo como representante y actualizar los enlaces
         */
     
        do{
            int cant = 0;
            for (ListaEstados laLista : P) {
                Iterator it = partirGrupos(P, laLista); //va obteniendo los grupos que están en la partición P

                while (it != null && it.hasNext()) {
                    ListaEstados list_states = (ListaEstados) it.next();
                    list_states.setId(cant++);
                    Pnew.add(list_states);
                }
            }

            if (P.size() != Pnew.size()) {
                P = Pnew;
                Pnew = new ArrayList<ListaEstados>();
            }

        }while ( P.size() == Pnew.size() );

        //Fin del Algoritmo de Minimizacion.


        /** Para convertir "Pnew" en "Automata":
            - Crear los estados
            - Crear los enlaces
         */

        Automata AFDmin = new Automata();
        Iterator it = Pnew.iterator();
        while (it.hasNext()) {
            ListaEstados states = (ListaEstados) it.next();
            Estado nuevo = new Estado(states.getId(), false, false, false);

            try {
                states.getEstadoInicial();
                nuevo.setEstadoinicial(true);
                AFDmin.setInicial(nuevo);
            } catch (Exception ex) {
                nuevo.setEstadoinicial(false);
            }

            if (states.getEstadosFinales().cantidad() > 0) {
                nuevo.setEstadofinal(true);
                AFDmin.getFinales().insertar(nuevo);
            } else {
                nuevo.setEstadofinal(false);
            }
            AFDmin.addEstado(nuevo);
        }

        it = Pnew.iterator();
        while (it.hasNext()) {
            ListaEstados states = (ListaEstados) it.next();
            Estado estado_afdm = AFDmin.getEstadoById(states.getId());
            Estado representante = states.get(0);

            //crear enlaces
            Iterator itenlaces = representante.getEnlaces().getIterator();
            while (itenlaces.hasNext()) {

                Enlace e = (Enlace) itenlaces.next();
                ListaEstados list_dest = enqueLista(Pnew, e.getDestino());
                Estado est_destino = AFDmin.getEstadoById(list_dest.getId());
                Enlace nuevo_enlace = new Enlace(estado_afdm, est_destino, e.getEtiqueta());
                estado_afdm.addEnlace(nuevo_enlace);
            }
        }

        return AFDmin;
    }

    /**
     * Método para separar una lista en varios grupos. Para cada estado de la lista se itera sobre
     * todos sus enlaces para determinar si va a crearse un nuevo subgrupo o agrandar uno existente.
     *
     * @param P_ant (Todas las listas actuales)
     * @param laLista (la lista que será separa en grupos)
     *
     * @return Iterador de las sublistas en que se dividió laLista
     */
    public Iterator partirGrupos(ArrayList<ListaEstados> P_ant, ListaEstados laLista) {
        Hashtable listasNuevas = new Hashtable();

        for (Estado estado : laLista) {
            String claveSimbolos = "";
            String claveEstados = "";

            System.out.println("---------Estado: "+estado);
            System.out.println("Enlaces: ");
            
            for (Enlace enlace : estado.getEnlaces()) {

                Estado dest = enlace.getDestino();

                System.out.print(dest + " ");

                ListaEstados tmp = enqueLista(P_ant, dest);
                claveSimbolos += enlace.getEtiqueta().trim();
                claveEstados += tmp.getId();

            }

            String clave = generarClaveHash(claveSimbolos, claveEstados);
            if (listasNuevas.containsKey(clave)) {
                ((ListaEstados) listasNuevas.get(clave)).insertar(estado);
            } else {
                ListaEstados nueva = new ListaEstados();
                nueva.insertar(estado);
                listasNuevas.put(clave, nueva);
            }
        }
        return listasNuevas.values().iterator();
    }

    /**
     * Método que genera una clave hash que tendrá las sublistas que pertenecen a un mismo grupo.
     * Todas las listas que  generen el mismo hash tendrán la misma clave y por tanto estarán en
     * la misma lista dentro del hash.
     *
     * @param simbolos
     * @param estados
     * @return cadenaFinal
     */
    public String generarClaveHash(String simbolos, String estados) {
        String cadenaFinal = "";

        char est[] = estados.toCharArray();
        char c[] = simbolos.toCharArray();

        boolean hayCambios = true;
        while (hayCambios) {
            hayCambios = false;
            for (int j = 0; j < c.length - 1; j++) {
                if (c[j] > c[j + 1]) {

                    //intercambiar
                    char tmp = c[j + 1];
                    c[j + 1] = c[j];
                    c[j] = tmp;

                    char tmpEst = est[j + 1];
                    est[j + 1] = est[j];
                    est[j] = tmpEst;
                    
                    hayCambios = true;
                }
            }
        }
        cadenaFinal = String.copyValueOf(c) + String.copyValueOf(est);
        return cadenaFinal;
    }

    /***
     * Método que retorna una lista de estado de entre las "listas", en la que
     * se encuentra un "estado" en particular.
     *
     * @param listas
     * @param estado
     * @return null
     */
    public ListaEstados enqueLista(ArrayList<ListaEstados> listas, Estado estado) {
        for (ListaEstados lista : listas) {
            try {
                lista.getEstadoById(estado.getId());
                return lista;
            } catch (Exception ex) {
            }
        }
        return null;
    }
}
