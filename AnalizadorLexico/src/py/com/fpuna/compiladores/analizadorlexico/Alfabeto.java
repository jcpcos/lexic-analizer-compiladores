package py.com.fpuna.compiladores.analizadorlexico;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/**
 *
 * @author markos
 */
public class Alfabeto extends ArrayList<String>  {

    public Alfabeto(String simbolos) {
        String tmp;
        for (int i = 0; i < simbolos.length(); i++) {
            tmp = "" + simbolos.charAt(i);

            if (!this.contains(tmp) && tmp.length() > 0) {
                this.add(tmp);
            }
        }
        this.ordenar();
    }

    public Iterator getIterator() {
        return this.iterator();
    }

    public int getTamanho() {
        return this.size();
    }

    public boolean contiene(String simbolo) {
        return this.contains(simbolo);
    }

    public String imprimir() {

        String result = "{ ";
        for (int i = 0; i < this.size(); i++) {

            result += this.get(i);

            if (!(i == (this.size() - 1))) {
                result += ", ";
            }
        }

        return result + " } ";

    }

    private void ordenar() {
        String a[] = new String[1];
        a = this.toArray(a);
        java.util.Arrays.sort(a);

        this.removeAll(this);
        this.addAll(Arrays.asList(a));
    }
}
