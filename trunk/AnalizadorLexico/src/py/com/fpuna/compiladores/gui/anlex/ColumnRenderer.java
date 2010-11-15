/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package py.com.fpuna.compiladores.gui.anlex;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class ColumnRenderer extends DefaultTableCellRenderer {

    private int columna;
    private Color background;
    private Color foreground;

    public ColumnRenderer() {
        this.columna = 0;
        this.background = Color.white;
        this.foreground = Color.black;

    }

    public ColumnRenderer(int columna, Color b, Color f) {
        this.columna = columna;
        this.background = b;
        this.foreground = f;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focused, int row, int column) {
        setEnabled(table == null || table.isEnabled()); // see question above

        if ((column == this.columna)) {
            setBackground(this.background);
            setForeground(this.foreground);
            setFont(new Font("Verdana", Font.BOLD, 12));
        } else {
            setBackground(Color.white);
            setForeground(Color.black);
        }
        

        setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
        super.getTableCellRendererComponent(table, value, selected, focused, row, column);

        return this;
    }
}
