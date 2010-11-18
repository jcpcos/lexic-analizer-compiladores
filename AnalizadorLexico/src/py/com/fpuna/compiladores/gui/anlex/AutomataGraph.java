/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package py.com.fpuna.compiladores.gui.anlex;

import com.jgraph.layout.JGraphFacade;
import com.jgraph.layout.organic.JGraphOrganicLayout;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import javax.swing.BorderFactory;
import java.util.ArrayList;

import java.util.Map;
import org.jgraph.JGraph;
import org.jgraph.graph.*;
import py.com.fpuna.compiladores.analizadorlexico.Automata;
import py.com.fpuna.compiladores.analizadorlexico.automata.Arco;
import py.com.fpuna.compiladores.analizadorlexico.automata.Estado;

/**
 * Representacion del Grafo usando la definicion jgraph.
 */
public class AutomataGraph extends JGraph{

    // Lista de cells que se agregan al jgraph
    private ArrayList<DefaultGraphCell> cells;    
    int width = 40, height = 40;
    private Automata automata;

    public AutomataGraph(Automata a, DefaultGraphModel model){
        super(model);
        this.automata = a;
        
        //Imprimir en consola.
        System.out.println("El automata que se dibujara es\n_____________________\n");
        System.out.println(a.imprimir());       
        convertirJgraph(); //Convertimos y dibujamos con jgraph.
        aplicar_layout_organico();
    }
    
    
    
/**
 * METODO PRINCIPAL PARA CONVERTIR EL AUTOMATA(nodos, arcos) A LOS ELEMENTOS DE UN JGRAPH
 */
    private void convertirJgraph(){

        //Borramos todo lo que habia en cells y creamos uno nuevo 
        cells = new ArrayList<DefaultGraphCell>();
        
        for(Estado elEstado : automata.getEstados()){
            incluirEnlacesEstado(elEstado);
        }
        
        Object[] elementosObj = cells.toArray();
        
        // Control-drag should clone selection
        setCloneable(true);

        // Enable edit without final RETURN keystroke
        setInvokesStopCellEditing(true);

        // When over a cell, jump to its default port (we only have one, anyway)
        setJumpToDefaultPort(true);

        // Insert the cells via the cache, so they get selected
        getGraphLayoutCache().insert(elementosObj);
        
    }
  
    private void aplicar_layout_organico(){
        JGraphFacade facade = new JGraphFacade(this);
        facade.setDirected(true);
        
        JGraphOrganicLayout layout = new JGraphOrganicLayout();
        layout.setOptimizeEdgeDistance(true);
        layout.setEdgeCrossingCostFactor(500000);
        layout.setOptimizeEdgeDistance(true);
        layout.setEdgeDistanceCostFactor(5000);
       
        layout.run(facade);
        Map nested = facade.createNestedMap(true, true);
        getGraphLayoutCache().edit(nested); 
        
    }   

    private void incluirEnlacesEstado(Estado estado){
        
        // Crear un "cell" para el Estado
        DefaultGraphCell origen = createCell(estado, width * automata.getEstados().cantidad()/2, 250);
        double x = 0;
        double y;
        for (Arco link : estado.getEnlaces()) {
                    if (estado.getEnlaces().indexOf(link)  % 2 == 0) {
                        y = 50;
                    } else {
                        y = 450;
                    }
                    
                    DefaultGraphCell destino = createCell( link.getDestino(), x, y);
                    DefaultGraphCell currentLink = createEdge(link, origen, destino);
                    x = x + width;
                }
    }

    private DefaultGraphCell createCell(Estado estado, double x, double y) {
        DefaultGraphCell cell  = obtenerEstado(estado);
        if(cell == null){
            cell = new DefaultGraphCell(estado);
            GraphConstants.setBounds(cell.getAttributes(), new Rectangle2D.Double(x, y, width, height));
            GraphConstants.setBorder(cell.getAttributes(), BorderFactory.createRaisedBevelBorder());
            GraphConstants.setOpaque(cell.getAttributes(), true);
            GraphConstants.setGradientColor(cell.getAttributes(), Color.LIGHT_GRAY);
            cell.addPort(new Point2D.Double(0, 0));
            
            //Agregamos al la lista
            cells.add(cell);
        }
        return cell;
    }

    private DefaultGraphCell createEdge(Arco enlace, DefaultGraphCell source, DefaultGraphCell target) {
        DefaultEdge edge = new DefaultEdge(enlace);
        source.addPort();
        edge.setSource(source.getChildAt(source.getChildCount() -1));
        target.addPort();
        edge.setTarget(target.getChildAt(target.getChildCount() -1));
        GraphConstants.setLabelAlongEdge(edge.getAttributes(), true);
        GraphConstants.setLineEnd(edge.getAttributes(), GraphConstants.ARROW_CLASSIC);
        cells.add(edge);
        return edge;
    }
 

    private DefaultGraphCell obtenerEstado(Estado estado){
        
        for(DefaultGraphCell oneCell: cells){
              if(oneCell.getUserObject() instanceof Estado && oneCell !=null){
                    if(((Estado)oneCell.getUserObject()).getId() == estado.getId()){
                        return oneCell;
                    }
              }
        }
        return null;
    } 
 
}
