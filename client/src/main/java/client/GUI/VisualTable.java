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

    private ArrayList<Worker> workersCollection;
    private HashMap<Integer, String> owners;
    private HashMap<AnimatedCircle, Integer> circles;
    private ResourceBundle messages = ResourceBundle.getBundle("client.GUI.Messages", UserSettings.getInstance().getSelectedLocale());

    public VisualTable(Client client, CommunicationControl communicationControl) {
        circles = new HashMap<>();
        owners = new HashMap<>();
        workersCollection = new ArrayList<>();
        ArrayList<ArrayList<Worker>> workers = new ArrayList<>();


        DefaultTableModel tableModel = new DefaultTableModel(new Object[][]{}, new String[]{messages.getString("ownerID"), messages.getString("ownerName")}) {
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        };

        // Создаем таблицу и устанавливаем модель
        JTable table = new JTable(tableModel);

        // Создаем панель прокрутки и добавляем таблицу
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(200, 10));

        // Размещаем панель прокрутки в правой части окна
        getContentPane().add(scrollPane, BorderLayout.WEST);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 1200);
        CirclesPanel circlesPanel = new CirclesPanel(circles, workers, client, communicationControl);
        getContentPane().add(circlesPanel);

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

        Timer timer = new Timer(2000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    // Здесь происходит обновление данных в таблице
                    client.sendRequest(new Request("sendNewList", "", client.getCurrentUser()));
                    Response response = client.receiveResponse();

                    int count = (Integer) response.getResponseObject();
                    for (int i = 0; i < count; i++) {
                        Response tempResponse = client.receiveResponse();
                        workers.add((ArrayList<Worker>) tempResponse.getResponseObject());
                    }
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }


                int countOwners = 0;
                for (ArrayList<Worker> workersCollection : workers) {
                    for (Worker worker : workersCollection) {
                        if (!owners.containsValue(worker.getOwner().getUsername())) {
                            owners.put(countOwners, worker.getOwner().getUsername());
                            countOwners++;
                        }
                    }
                }

                circles.clear();


                int i = 1;
                for (ArrayList<Worker> workersCollection : workers) {
                    for (Worker worker : workersCollection) {
                        int originalX = worker.getCoordinates().getX();
                        int originalY = worker.getCoordinates().getY();

                        int xOffset = 3 * i;
                        int yOffset = 3 * i;

                        int x = (int) ((originalX * 0.3 - 200 + xOffset) + 500 * Math.random());
                        int y = (int) (originalY * 0.4 - 50 + yOffset);
                        int radius = 50;
                        String username = worker.getOwner().getUsername();
                        int colorIndex = getKeyByUsername(owners, username);
                        circles.put(new AnimatedCircle(x, y, radius, colorIndex, worker.getName()), worker.getId());
                        i += 20;
                    }
                }


                tableModel.setRowCount(0);
                for (HashMap.Entry<Integer, String> entry : owners.entrySet()) {
                    Integer id = entry.getKey();
                    String owner = entry.getValue();
                    tableModel.addRow(new Object[]{id, owner});
                }

                circlesPanel.repaint();


            }
        });

        timer.start();
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