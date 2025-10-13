package UI_Components;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class Table extends JScrollPane {
    JTable table;
    DefaultTableModel model;

    public Table(String[] ColumnArr, boolean editable) {
        super();
        model = new DefaultTableModel(ColumnArr, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return editable;
            }
        };
        table = new JTable(model);
        setViewportView(table);
    }

    public void insertRow(String[] row) {
        model.addRow(row);
    }

    public void clearTable() {
        model.setRowCount(0);
        table.repaint();
    }

    public void updateCell(String val, int row, int col) {
        model.setValueAt(val, row, col);
    }
}