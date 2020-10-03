package lenguajesyautomatas;

import java.awt.*;
import java.util.*;

import javax.swing.*;
import javax.swing.table.*;


public class ColorCeldas extends DefaultTableCellRenderer {
private int columna ;
ArrayList<Integer> erroresSintacticos = new ArrayList<Integer>();

public ColorCeldas(int Colpatron)
{
    this.columna = Colpatron;
}

@Override
public Component getTableCellRendererComponent (JTable table, Object value, boolean selected, boolean focused, int row, int column)
{        
    setBackground(Color.white);
    table.setForeground(Color.black);
    super.getTableCellRendererComponent(table, value, selected, focused, row, column);
    if(erroresSintacticos.contains(table.getValueAt(row, columna)))
    {
        this.setBackground(Color.RED);
        return this;
    }
    else 
        table.setForeground(Color.black);

    return this;
  }
  }