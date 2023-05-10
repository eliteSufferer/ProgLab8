package client.GUI;

import client.Client;
import client.utils.CommunicationControl;
import common.data.Worker;
import common.functional.Request;
import common.functional.Response;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class VisualTable extends JFrame{

    private HashMap<Integer, String> owners;
    private HashMap<AnimatedCircle, Integer> circles;
    private ResourceBundle messages = ResourceBundle.getBundle("client.GUI.Messages", UserSettings.getInstance().getSelectedLocale());
    private Client client;
    private CommunicationControl communicationControl;
    private DefaultTableModel tableModel;
    private JTable table;

    public VisualTable(Client client, CommunicationControl communicationControl, ArrayList<ArrayList<Worker>> workersCollection){
        this.client = client;
        this.communicationControl = communicationControl;
        circles = new HashMap<>();

        owners = new HashMap<>();
        tableModel = new DefaultTableModel(new Object[][]{}, new String[]{messages.getString("ownerID"), messages.getString("ownerName")}){
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        };

        // Создаем таблицу и устанавливаем модель
        table = new JTable(tableModel);

        // Создаем панель прокрутки и добавляем таблицу
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(200, 10));

        CirclesPanel circlesPanel = new CirclesPanel(circles, workersCollection, client, communicationControl);
        getContentPane().add(circlesPanel);

        // Размещаем панель прокрутки в правой части окна
        getContentPane().add(scrollPane, BorderLayout.WEST);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 1200);


        JButton openMainFrameButton = new JButton(messages.getString("openDataTable"));

        // Добавьте обработчик событий, который открывает окно MainFrame при нажатии на кнопку
        openMainFrameButton.addActionListener(e -> {
            MainWindow mainFrame = new MainWindow(client, communicationControl);
            mainFrame.setVisible(true);
            dispose(); // Закройте текущее окно VisualTable
        });

        // Создайте панель с FlowLayout для размещения кнопки в верхнем правом углу
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
        buttonPanel.add(openMainFrameButton);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(circlesPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.NORTH);

        // Добавьте панель на основной контейнер
        getContentPane().add(mainPanel);

        // Создаем таймер для обновления данных каждые 2 секунды


        Timer timer = new Timer(2000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateData(MainWindow.getWorkersss());
            }
        });
        timer.start();
    }

    public void updateData(ArrayList<Worker> workersCollection) {
        // Очищаем коллекции
        owners.clear();
        circles.clear();

        int countOwners = 0;
            for (Worker worker : workersCollection) {
                if (!owners.containsValue(worker.getOwner().getUsername())){
                    owners.put(countOwners, worker.getOwner().getUsername());
                    countOwners ++;
                }
            }

        // Очищаем модель таблицы и заполняем новыми данными
        tableModel.setRowCount(0);
        for (HashMap.Entry<Integer, String> entry : owners.entrySet()) {
            Integer id = entry.getKey();
            String owner = entry.getValue();
            tableModel.addRow(new Object[]{id, owner});
        }

            for (Worker worker : workersCollection) {
                int originalX = worker.getCoordinates().getX();
                int originalY = worker.getCoordinates().getY();

                int x = originalX + 400;
                int y = originalY + 400;
                int radius = 50;
                String username = worker.getOwner().getUsername();
                int colorIndex = getKeyByUsername(owners, username);
                circles.put(new AnimatedCircle(x, y, radius, colorIndex, worker.getName()), worker.getId());
            }

        // После обновления данных в таблице и списка кругов, нужно вызвать repaint для обновления GUI
        repaint();


    }



    public static Integer getKeyByUsername(HashMap<Integer, String> map, String username) {
        for (HashMap.Entry<Integer, String> entry : map.entrySet()) {
            if (entry.getValue().equals(username)) {
                return entry.getKey();
            }
        }
        return null;
    }
}