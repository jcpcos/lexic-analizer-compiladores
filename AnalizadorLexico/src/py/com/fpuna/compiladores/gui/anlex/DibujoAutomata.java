/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DibujoAutomata.java
 *
 * Created on 12/11/2010, 12:32:58 PM
 */
package py.com.fpuna.compiladores.gui.anlex;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultGraphModel;
import py.com.fpuna.compiladores.analizadorlexico.algoritmos.Thompson;
import py.com.fpuna.compiladores.analizadorlexico.automata.Enlace;
import py.com.fpuna.compiladores.analizadorlexico.automata.Estado;
import py.com.fpuna.compiladores.analizadorlexico.automata.ListaEstados;

/**
 *
 * @author lizj
 */
public class DibujoAutomata extends javax.swing.JFrame {

    /** Creates new form Dibujo */
    public DibujoAutomata() {
        initComponents();
    }
    private Thompson automata;
    private AutomataGraph jgraph;
    DefaultGraphCell[] cells;
    private JLabel imageLabel;
    private Simulacion simulacion;
    private boolean simulacionResult;
    private String imgDir = "/tmp";
    private String imgUrl;
    private boolean enSimulacion = false;
    private Estado EstadoActual;
    private int IndexActual;
    private String CaracterActual = "";
    private Estado EstadoSiguiente;
    private ArrayList<File> fileList;
    private File imagenOriginal;
    private boolean CargarOriginal = true;
    private boolean HayArchivos = false;
    private boolean primeraVez = true;

    public DibujoAutomata(Thompson a) {

        this.automata = a;
        this.imageLabel = new javax.swing.JLabel();
        this.imageLabel.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        this.imageLabel.setForeground(new java.awt.Color(100, 100, 255));
        this.imageLabel.setText("<Aqui va la Imagen>");

        DefaultGraphModel model = new DefaultGraphModel();
        this.jgraph = new AutomataGraph(a, model);

        initComponents();
        this.cargarAutomataGraphViz();
    }

    void setSimulacionResult(boolean simResult) {
        this.simulacionResult = simResult;
    }

    private String generateImageUrl() {
        Random r = new Random();
        r.nextInt(100000);
        String rand = "" + r.nextInt(100000);

        String dir = this.imgDir;

        File fileDir = new File(dir);
        String dibujo = "<NO>";

        System.out.println("ImageDir: " + fileDir);
        if (fileDir.isDirectory()) {
            dibujo = imgDir + File.separator + "automata_" + rand + ".gif";
        }

        return dibujo;
    }

    private void cargarAutomataGraphViz() {
        this.imgUrl = this.generateImageUrl();
        GraphViz gv = new GraphViz();

        if (gv.testGraphViz()) {
            if (this.imgUrl.compareTo("<NO>") == 0) {
                this.imageLabel.setText("<El Path al Directorio de imagen es incorrecto>");
                this.imageLabel.setFont(new Font("Verdana", Font.BOLD, 14));
                this.imageLabel.setForeground(Color.orange);
            } else {

                boolean dibujar = this.primeraVez;     // variable que indica que debemos generar el dibujo

                ImageIcon i = null;

                if (dibujar) { // Usar las imágenes guardadas
                    i = this.dibujarNuevo();
                    this.imagenOriginal = new File(this.imgUrl);
                    this.primeraVez = false;
                } else {
                    if (this.fileList != null && ((this.fileList.size() - 1) < this.IndexActual)) {
                        dibujar = true;
                        i = this.dibujarNuevo();
                        File f = new File(this.imgUrl);
                        this.fileList.add(f);
                        this.HayArchivos = true;
                    } else if (this.fileList != null) {
                        dibujar = false; // no generar, utilizar uno ya creado.
                        File f = this.fileList.get(this.IndexActual);
                        i = new ImageIcon(f.getAbsolutePath());
                    }
                }

                if (i != null) {
                    i.setDescription("Automata Generado");
                    this.imageLabel.setIcon(i);
                }
            }
        } else {
            this.imageLabel.setText("<GraphViz no esta instalado>");
            this.imageLabel.setFont(new Font("Verdana", Font.BOLD, 14));
            this.imageLabel.setForeground(Color.red);
        }
        this.imageLabel.setHorizontalAlignment(JLabel.CENTER);
        this.imageLabel.setVisible(true);
    }

    private ImageIcon dibujarNuevo() {
        GraphViz gv = new GraphViz();
        gv.dibujar(this.getDotSyntax(), this.imgUrl);
        this.imageLabel.setText("");
        return (new ImageIcon(this.imgUrl));
    }

    /**
     * Construye la cadena de atributos visuales para un estado en particular
     * @param e El estado que vamos a dibujar
     * @param marcado Si es true, significa que está marcado y tiene un color especial
     * @return
     */
    private String getColorEstado(Estado e, boolean marcado) {

        String style = "[";
        // Características gráficas de cada estado
        String shape = e.isEstadofinal() ? "shape=doublecircle" : "shape=circle";

        style += shape;

        /**
         * Estilos Especiales.
         *
         * Definen los estilos para estados finales e iniciales. Si el nodo está
         * marcado, define los atributos de un nodo marcado.
         */
        String coloresp = marcado ? "color=green4" : "color=blue4";
        String fillcolor = marcado ? "style=filled,fillcolor=green" : "style=filled,fillcolor=blue";
        String fontcolor = marcado ? "fontcolor=white" : "fontcolor=white";
        String label = e.isEstadoinicial() ? ",label=inicio" : "";

        if (e.isEstadofinal() || e.isEstadoinicial() || marcado) {
            style += "," + fillcolor + "," + coloresp + "," + fontcolor + label;
        }

        return style + "];";
    }

    /**
     * Construye la cadena de atributos visuales para un enlace en particular
     * @param enalce El enlace que vamos a dibujar
     * @param lbl Label del enlace
     * @param marcado Si es true, significa que está marcado y tiene un color especial
     * @return
     */
    private String getEnlaceStyle(Enlace enlace, String lbl, boolean marcado) {

        String style = "[";
        // Características gráficas de cada estado
        String label = "label=\"" + lbl + "\"";

        style += label;

        /**
         * Estilos Especiales.
         *
         * Definen los estilos para enlaces marcados en una simulacion
         */
        String coloresp = marcado ? ",color=green4" : "";

        style += coloresp + "];";

        return style;
    }

    /**
     * Construye la sintaxis adecuada para generar el gráfico por medio de la
     * aplicación "dot" del toolkit de GraphViz. <br> <br>
     *
     * De acuerdo a ciertos criterios del entorno de simulación, establece los
     * colores y otras características del grafo. <br><br>
     *
     * El estado inicial y los finales también tienen un formato especial <br><br>
     *
     * La sintaxis de GraphViz (El lenguaje DOT) se define aquí
     * <href="http://www.graphviz.org/doc/info/lang.html">DOT Language</href>
     *
     * @return String Cadena completa formateada del automata en versión grapviz
     */
    public String getDotSyntax() {

        String result_header =
                "Digraph AFN {\n"
                + "\trankdir=LR;\n\toverlap=scale;\n";

        String result_nodes = "\tnode [shape = circle];\n";
        String result_edges = "";


        ListaEstados estados = this.automata.getEstados();

        for (Estado e : estados) {
            boolean mark = false;

            if (this.enSimulacion) {
                mark = (e.getId() == this.EstadoActual.getId());

                if (!mark && this.EstadoSiguiente != null) {
                    mark = (e.getId() == this.EstadoSiguiente.getId());
                }
            }

            String EstadoStyle = this.getColorEstado(e, mark);

            result_nodes += "\t" + e.getId() + " " + EstadoStyle + "\n";

            for (Enlace enlace : e.getEnlaces()) {

                Estado orig = enlace.getOrigen();
                Estado dest = enlace.getDestino();
                String label = enlace.getEtiqueta();

                mark = ((label.compareTo(this.CaracterActual) == 0) && (orig.getId() == this.EstadoActual.getId()));

                String EnlaceStyle = this.getEnlaceStyle(enlace, label, mark);

                result_edges += "\t" + orig.getId() + " -> " + dest.getId()
                        + " " + EnlaceStyle + "\n";

            }
        }
        String result = result_header + result_nodes + result_edges + "}";
        return result;
    }

    /**
     * GETTERS Y SETTERS DE ATRIBUTOS DE LA CLASE
     */
    public void setAutomata(Thompson automata) {
        this.automata = automata;
    }

    public void setJLabelTituloText(String label) {
        this.jLabelTitulo.setText(label);
    }

    public void setJTextReGexString(String jTextReGex) {
        this.jTextReGex.setText(jTextReGex);
    }

    public void setJTextAlphaString(String jTextAlpha) {
        this.jTextAlpha.setText(jTextAlpha);
    }
    private javax.swing.JLabel jLabelTitulo;

    private Component ScrollPaneConstructor() {
        return this.imageLabel;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTextAlpha = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jTextReGex = new javax.swing.JTextField();
        jScrollPane1 = new JScrollPane(this.ScrollPaneConstructor());
        ;
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTextAlpha.setEditable(false);
        jTextAlpha.setName("jTextAlpha"); // NOI18N

        jLabel2.setText("Expresi�n Regular:");
        jLabel2.setName("jLabel2"); // NOI18N

        jLabel3.setText("Alfabeto:");
        jLabel3.setName("jLabel3"); // NOI18N

        jTextReGex.setEditable(false);
        jTextReGex.setName("jTextReGex"); // NOI18N

        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder("Graph"));
        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jButton1.setText("Salir");
        jButton1.setName("jButton1"); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(16, 16, 16)
                        .addComponent(jTextAlpha, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextReGex, javax.swing.GroupLayout.PREFERRED_SIZE, 314, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 710, Short.MAX_VALUE)
                    .addComponent(jButton1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextAlpha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jTextReGex, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 351, Short.MAX_VALUE)
                .addGap(28, 28, 28))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void borrarArchivos(boolean incluirOri) {
        if (this.fileList != null) {
            for (File f : this.fileList) {
                if (f.exists()) {
                    f.delete();
                }
            }
        }

        this.fileList = null;

        if (incluirOri) {
            if (this.imagenOriginal != null && this.imagenOriginal.exists()) {
                this.imagenOriginal.delete();
            }
        }

        this.HayArchivos = false;
    }
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        this.setVisible(false);

        /* File imageIcon = new File(this.imgUrl);
        if (this.library.compareTo("graphviz")==0 && imageIcon.exists()) {
        if (imageIcon.exists()) {
        imageIcon.delete();
        }
        }    */

        this.borrarArchivos(true);
        this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextAlpha;
    private javax.swing.JTextField jTextReGex;
    // End of variables declaration//GEN-END:variables
}
