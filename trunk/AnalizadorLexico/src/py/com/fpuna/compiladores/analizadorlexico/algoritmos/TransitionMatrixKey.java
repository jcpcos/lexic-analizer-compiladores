package py.com.fpuna.compiladores.analizadorlexico.algoritmos;

import py.com.fpuna.compiladores.analizadorlexico.Token;
import py.com.fpuna.compiladores.analizadorlexico.automata.ListaEstados;


public class TransitionMatrixKey {
    // fila, indicadas por una lista de estados
    private ListaEstados indiceEstados;
    // columna, indicada por un token del lenguaje
    private Token indiceToken;

    public TransitionMatrixKey(ListaEstados list, Token tok) {
        this.indiceEstados = list;
        this.indiceToken = tok;
    }
    
    public ListaEstados getIndiceEstados() {
        return this.indiceEstados;
    }
    public void setIndiceEstados(ListaEstados indiceEstados) {
        this.indiceEstados = indiceEstados;
    }
    public Token getIndiceToken() {
        return this.indiceToken;
    }
    public void setIndiceToken(Token indiceToken) {
        this.indiceToken = indiceToken;
    }
    public int compareTo(Object otro){
        TransitionMatrixKey o = (TransitionMatrixKey) otro;
        if(indiceToken.getValor().compareTo(o.getIndiceToken().getValor()) == 0) {
            if(indiceEstados.compareTo(o.getIndiceEstados()) == 0){
                return 0;
            }else{
                return -1;    
            }
        }else{
            return -1;
        }
    }
    
}
