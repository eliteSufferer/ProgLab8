package client.GUI.sort;


import client.GUI.UserSettings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;
import java.util.ResourceBundle;

public class WorkerSortingPanel extends JPanel {
    private JComboBox<String> sortByComboBox;
    private JComboBox<String> sortOrderComboBox;
    private JComboBox<String> filterByComboBox;
    private JComboBox<String> filterOperationComboBox;
    private JTextField filterValueTextField;
    private JButton sortButton;
    private JButton filterButton;
    private JButton resetButton;
    private JTable workerTable;
    private SortingAndFilteringParameters sortingAndFilteringParameters;

    private ResourceBundle messages = ResourceBundle.getBundle("client.GUI.Messages", UserSettings.getInstance().getSelectedLocale());

    public WorkerSortingPanel() {
        sortingAndFilteringParameters = new SortingAndFilteringParameters();
        setLayout(new FlowLayout(FlowLayout.CENTER));

        // Создаем и добавляем элементы управления
        initControls();



        // Здесь будет код для обновления таблицы каждые 2 секунды
    }

    private void initControls() {
        JPanel sortPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        String[] columnNames = {"", "id", "name", "coordX", "coordY",
                "creationDate", "salary", "position",
                "status", "birthday", "height",
                "passport", "locX", "locY",
                "locZ", "loc_name"};
        sortByComboBox = new JComboBox<>(columnNames);
        sortOrderComboBox = new JComboBox<>(new String[] {"", messages.getString("ascending"), messages.getString("descending")});
        sortButton = new JButton("OK");

        filterByComboBox = new JComboBox<>(columnNames);
        filterOperationComboBox = new JComboBox<>(new String[] {"", ">", "<", "="});
        filterValueTextField = new JTextField(10);
        filterButton = new JButton("OK");

        resetButton = new JButton(messages.getString("reset"));

        sortPanel.add(new JLabel(messages.getString("sortBy")), BorderLayout.NORTH);
        sortPanel.add(sortByComboBox, BorderLayout.WEST);
        sortPanel.add(sortOrderComboBox, BorderLayout.CENTER);
        sortPanel.add(sortButton, BorderLayout.EAST);

        filterPanel.add(new JLabel(messages.getString("filterBy")), BorderLayout.NORTH);
        filterPanel.add(filterByComboBox, BorderLayout.WEST);
        filterPanel.add(filterOperationComboBox, BorderLayout.CENTER);
        filterPanel.add(filterValueTextField, BorderLayout.EAST);
        filterPanel.add(filterButton, BorderLayout.SOUTH);

        add(resetButton, BorderLayout.SOUTH);

        add(sortPanel, BorderLayout.WEST);
        add(filterPanel, BorderLayout.EAST);

        // Add action listeners for the buttons
        sortButton.addActionListener(new SortButtonListener());
        filterButton.addActionListener(new FilterButtonListener());
        resetButton.addActionListener(new ResetButtonListener());
    }

    private class SortButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Получить выбранные значения для сортировки
            boolean ascending;
            String selectedColumn = (String) sortByComboBox.getSelectedItem();
            String chooseAscending = (String) sortOrderComboBox.getSelectedItem();
            if (Objects.equals(chooseAscending, messages.getString("ascending"))){
                ascending = true;
            } else {
                ascending = false;
            }

            // Обновить параметры сортировки
            sortingAndFilteringParameters.setSortingColumn(selectedColumn);
            sortingAndFilteringParameters.setAscending(ascending);

        }
    }

    private class FilterButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Получить выбранные значения для фильтрации
            String selectedColumn = (String) filterByComboBox.getSelectedItem();
            String selectedOperation = (String) filterOperationComboBox.getSelectedItem();
            String filteringValue = filterValueTextField.getText();

            // Обновить параметры фильтрации
            sortingAndFilteringParameters.setFilteringColumn(selectedColumn);
            sortingAndFilteringParameters.setFilteringOperation(selectedOperation);
            sortingAndFilteringParameters.setFilteringValue(filteringValue);
        }
    }

    private class ResetButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Сбросить параметры сортировки и фильтрации
            sortingAndFilteringParameters.reset();

            // Сбросить значения виджетов
            sortByComboBox.setSelectedIndex(0);
            sortOrderComboBox.setSelectedIndex(0);
            filterByComboBox.setSelectedIndex(0);
            filterOperationComboBox.setSelectedIndex(0);
            filterValueTextField.setText("");
        }
    }
    public SortingAndFilteringParameters getParamaters(){
        return sortingAndFilteringParameters;
    }
}

