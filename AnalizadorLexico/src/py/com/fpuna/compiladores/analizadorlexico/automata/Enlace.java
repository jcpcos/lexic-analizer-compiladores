package py.com.fpuna.compiladores.analizadorlexico.automata;

public class Enlace implements Comparable<Enlace> {    
    private Estado origen;
    private Estado destino;
    private String etiqueta;
    private boolean vacio;
          
     /**
      * 
      * @param origen  Estado de origen del enlace.
      * @param destino Estado de destino del enlace.
      * @param label   Etiqueta del Enlace
      */
    public Enlace(Estado origen, Estado destino, String label) {
        this.origen = origen;
        this.destino = destino;
        this.etiqueta = label;
        
        if (label.compareTo("") == 0) {
            this.vacio = true;
        } else {
            this.vacio = false;
        }
    }

    public Estado getOrigen() {
        return origen;
    }
    public void setOrigen(Estado origen) {
        this.origen = origen;
    }
    public Estado getDestino() {
        return destino;
    }
    public void setDestino(Estado destino) {
        this.destino = destino;
    }
    public String getEtiqueta() {
        return this.etiqueta;
    }
    public void setEtiqueta(String label) {
        this.etiqueta = label;
    }
    public void setVacio(boolean vacio) {
        this.vacio = vacio;
    }
    public boolean isVacio() {
        return vacio;
    }

    /**
     * @param e Estado al cual queremos comparar el actual
     * @return <ul> <li><b>0 (Cero)</b> si son  iguales</li>
     *              <li><b>-1 (Menos Uno)</b> si son <b>distintos</b></li>
     *         </ul>
     */
    public int compareTo(Enlace e) {
        if (e.getOrigen() == this.getOrigen()
                && e.getDestino() == this.getDestino()
                && e.getEtiqueta().equals(this.getEtiqueta())
                ) {
            return 0;
        } else {
            return -1;
        }
    }
    
    @Override
    public String toString(){
        return getEtiqueta();
    }
}
