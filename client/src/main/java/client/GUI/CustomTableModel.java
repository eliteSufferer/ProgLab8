package client.GUI;

import common.data.Worker;

import javax.swing.table.DefaultTableModel;
import java.util.HashMap;
import java.util.Map;

public class CustomTableModel extends DefaultTableModel {
    private final Map<Integer, Worker> rowToObjectMap = new HashMap<>();
    public CustomTableModel(Object[] columnNames, int rowCount){
        super(columnNames, rowCount);
    }
    @Override
    public void setValueAt(Object value, int row, int column) {
        Worker worker = (Worker) value;
        super.setValueAt(value, row, column);
        rowToObjectMap.put(row, worker);
    }

    @Override
    public Object getValueAt(int row, int column) {
        return rowToObjectMap.get(row);
    }
    public void clearData() {
        // Удаление всех строк
        setRowCount(0);

        // Очистка ассоциативного массива
        rowToObjectMap.clear();
    }
    public void addWorkerRow(Object[] rowData, Worker worker) {
        super.addRow(rowData);
//        rowToObjectMap.put(row)
    }

}