package py.com.fpuna.compiladores.analizadorlexico.algoritmos;

import java.util.HashMap;
import py.com.fpuna.compiladores.analizadorlexico.Token;
import py.com.fpuna.compiladores.analizadorlexico.automata.Arco;
import py.com.fpuna.compiladores.analizadorlexico.automata.Estado;
import py.com.fpuna.compiladores.analizadorlexico.automata.ListaEstados;

public class TransitionMatrix {

    private HashMap dtrans;

    /** Creates a new instance of Dtrans */
    public TransitionMatrix() {
        dtrans = new HashMap();
    }

    /**
     * Retorna el valor(ListaEstados) apartir de la clave (ListaEstados, Token)
     * 
     * @param clave
     * @return
     */
    public ListaEstados obtenerValor(TransitionMatrixKey clave) {
        return obtenerValor(clave.getIndiceEstados(), clave.getIndiceToken());
    }

    public ListaEstados obtenerValor(ListaEstados lista, Token token) {
        TransitionMatrixKey comparar = new TransitionMatrixKey(lista, token);
        Iterable<TransitionMatrixKey> s = dtrans.keySet();
        for (TransitionMatrixKey clave : s) {
            if (clave.compareTo(comparar) == 0) {
                return (ListaEstados) dtrans.get(clave);
            }
        }

        return null;
    }

    public void setValor(TransitionMatrixKey clave, ListaEstados valor) {
        dtrans.put(clave, valor);
    }

    /**
     *  Método que convierte el Dtrans en un "Automata", ya que las listas 
     * de estados A,B,C, etc son los estados del nuevo Automata creado.
     * 
     * @return Automata convertido.
     */
    public Thompson convertAutomata() {
        Thompson th = new Thompson();

        Iterable<TransitionMatrixKey> s = dtrans.keySet();
        for (TransitionMatrixKey clave : s) {
            ListaEstados valor = obtenerValor(clave);

            int id_new_origen = clave.getIndiceEstados().getId();
            int id_new_dest = valor.getId();
            Estado st_new_origen, st_new_dest;

            try {
                st_new_origen = th.getEstadoById(id_new_origen);
            } catch (Exception ex) {
                //No existe el estado entonces creamos
                st_new_origen = new Estado(id_new_origen,
                        clave.getIndiceEstados().contieneInicial(),
                        clave.getIndiceEstados().contieneFinal(),
                        false);
                th.addEstado(st_new_origen);
                if (clave.getIndiceEstados().contieneInicial()) {
                    th.setInicial(st_new_origen);
                }
                if (clave.getIndiceEstados().contieneFinal()) {
                    th.getFinales().insertar(st_new_origen);
                }

            }


            try {
                st_new_dest = th.getEstadoById(id_new_dest);
            } catch (Exception ex) {
                //No existe el estado entonces creamos
                st_new_dest = new Estado(id_new_dest,
                        valor.contieneInicial(),
                        valor.contieneFinal(),
                        false);
                th.addEstado(st_new_dest);
                if (valor.contieneInicial()) {
                    th.setInicial(st_new_dest);
                }
                if (valor.contieneFinal()) {
                    th.getFinales().insertar(st_new_dest);
                }
            }

            //Agregamos los enlaces.
            Arco enlace_new = new Arco(st_new_origen, st_new_dest,
                    clave.getIndiceToken().getValor());

            st_new_origen.addEnlace(enlace_new);
        }

        return th;
    }

    public String imprimir() {
        String print = "";
        Iterable<TransitionMatrixKey> s = dtrans.keySet();
        for (TransitionMatrixKey clave : s) {
            ListaEstados lista = obtenerValor(clave);

            print += "\n" + clave.getIndiceEstados().imprimir()
                    + " -#- " + clave.getIndiceToken().getValor()
                    + " = " + lista.imprimir();
        }
        return print;
    }
}
