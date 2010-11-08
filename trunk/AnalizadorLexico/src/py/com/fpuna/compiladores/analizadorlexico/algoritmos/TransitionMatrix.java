package py.com.fpuna.compiladores.analizadorlexico.algoritmos;

import java.util.HashMap;
import py.com.fpuna.compiladores.analizadorlexico.Automata;
import py.com.fpuna.compiladores.analizadorlexico.Token;
import py.com.fpuna.compiladores.analizadorlexico.automata.Enlace;
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
    public ListaEstados obtenerValor(TransitionMatrixKey clave){
        return obtenerValor(clave.getIndiceEstados(), clave.getIndiceToken());
    }
    
    public ListaEstados obtenerValor(ListaEstados lista, Token token){
        TransitionMatrixKey comparar = new TransitionMatrixKey(lista, token);
        Iterable<TransitionMatrixKey> s = dtrans.keySet();
        TransitionMatrixKey clave;
        while(s.iterator().hasNext()){
            clave = (TransitionMatrixKey)s.iterator().next();
            if(clave.compareTo(comparar) == 0){
                return (ListaEstados) dtrans.get(clave);
            }
        }
        return null;
    }
    
    
    public void setValor(TransitionMatrixKey clave, ListaEstados valor){
        dtrans.put(clave, valor);
    }
    
    
    /**
     *  Método que convierte el Dtrans en un "Automata", ya que las listas 
     * de estados A,B,C, etc son los estados del nuevo Automata creado.
     * 
     * @return Automata convertido.
     */
    public Automata convertAutomata(){
        Automata a = new Automata(); 
        
        Iterable<TransitionMatrixKey> s = dtrans.keySet();
        while(s.iterator().hasNext()){
            TransitionMatrixKey clave = (TransitionMatrixKey) s.iterator().next();
            ListaEstados valor = obtenerValor(clave);
            
            int id_new_origen = clave.getIndiceEstados().getId();
            int id_new_dest = valor.getId();
            Estado st_new_origen, st_new_dest;
            
            try{
                 st_new_origen = a.getEstadoById(id_new_origen);
            }catch(Exception ex){
                //No existe el estado entonces creamos 
                st_new_origen = new Estado(id_new_origen, 
                                            clave.getIndiceEstados().contieneInicial(), 
                                            clave.getIndiceEstados().contieneFinal(), 
                                            false);
                a.addEstado(st_new_origen);
                if(clave.getIndiceEstados().contieneInicial()){
                    a.setInicial(st_new_origen);
                }
                if(clave.getIndiceEstados().contieneFinal()){
                    a.getFinales().insertar(st_new_origen);
                }
                
            }
            
            
            try{
                 st_new_dest = a.getEstadoById(id_new_dest);
            }catch(Exception ex){
                //No existe el estado entonces creamos 
                st_new_dest = new Estado(id_new_dest, 
                                        valor.contieneInicial(), 
                                        valor.contieneFinal(), 
                                        false);
                a.addEstado(st_new_dest);
                if(valor.contieneInicial()){
                    a.setInicial(st_new_dest);
                }
                if(valor.contieneFinal()){
                    a.getFinales().insertar(st_new_dest);
                }
            }

            //Agregamos los enlaces.
            Enlace enlace_new = new Enlace( st_new_origen, st_new_dest, 
                                            clave.getIndiceToken().getValor());
            
            st_new_origen.addEnlace(enlace_new);
        }
               
        return a;
    }
    
    
    public String imprimir(){
        String print = "";
        Iterable<TransitionMatrixKey> s = dtrans.keySet();
        while(s.iterator().hasNext()){
            TransitionMatrixKey clave = (TransitionMatrixKey) s.iterator().next();
            ListaEstados lista = obtenerValor(clave);
            
            print += "\n" + clave.getIndiceEstados().imprimir() +
                    " -#- " + clave.getIndiceToken().getValor() +
                    " = " + lista.imprimir();
            
        }
        return print;
    }

}

