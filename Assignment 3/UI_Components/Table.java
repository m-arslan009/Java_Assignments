package UI_Components;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

public class Table extends JScrollPane {
    JTable table;
    DefaultTableModel model;
    int selectedRow = -1;
    Button deleteButton = new Button("Delete");
    int totalColumns;
    private Consumer<Integer> onEditCallback;
    private Consumer<Integer> onDeleteCallback;

    public Table(String[] ColumnArr, boolean editable) {
        super();
        this.totalColumns = ColumnArr.length;
        model = new DefaultTableModel(ColumnArr, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return editable;
            }
        };
        table = new JTable(model);
        setViewportView(table);
        table.setVisible(true);

        table.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                int row = table.rowAtPoint(e.getPoint());
                int column = table.columnAtPoint(e.getPoint());

                // Edit column (second to last)
                if(column == totalColumns - 2) {
                    if(onEditCallback != null) {
                        onEditCallback.accept(row);
                    } else {
                        JOptionPane.showMessageDialog(null, "Edit functionality not set up yet.");
                    }
                }

                // Delete column (last column)
                else if(column == totalColumns - 1) {
                    int val = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this task?",
                            "Confirm Delete", JOptionPane.YES_NO_OPTION);
                    if(val == JOptionPane.YES_OPTION){
                        if(onDeleteCallback != null) {
                            onDeleteCallback.accept(row);
                        } else {
                            model.removeRow(row);
                        }
                    }
                }

                table.repaint();
            }
        });
    }

    public void setOnEditCallback(Consumer<Integer> callback) {
        this.onEditCallback = callback;
    }

    public void setOnDeleteCallback(Consumer<Integer> callback) {
        this.onDeleteCallback = callback;
    }

    public void insertRow(String[] row) {
        model.addRow(row);
        int lastRow = table.getRowCount() - 1;
        model.setValueAt("X", lastRow, totalColumns - 1);
    }

    public void clearTable() {
        model.setRowCount(0);
        table.repaint();
    }

    public void selectCell() {
        int row = table.getSelectedRow();
    }

    public void updateCell(String val, int row, int col) {
        model.setValueAt(val, row, col);
    }

    public JTable getTable() {
        return table;
    }

    public Object getValueAt(int row, int col) {
        return model.getValueAt(row, col);
    }

    public void removeRow(int row) {
        model.removeRow(row);
    }
}