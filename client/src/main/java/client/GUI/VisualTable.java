package client.GUI;

import client.Client;
import client.utils.CommunicationControl;
import common.data.Worker;
import common.functional.Request;
import common.functional.Response;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class VisualTable extends JFrame{

    private ArrayList<Worker> workersCollection;
    private HashMap<Integer, String> owners;
    private HashMap<AnimatedCircle, String> circles;
    private ResourceBundle messages = ResourceBundle.getBundle("client.GUI.Messages", UserSettings.getInstance().getSelectedLocale());

    public VisualTable(Client client, CommunicationControl communicationControl){

        circles = new HashMap<>();
        try {
            client.sendRequest(new Request("sendNewList", "", client.getCurrentUser()));
            Response response = client.receiveResponse();
            workersCollection = (ArrayList<Worker>) response.getResponseObject();
        }catch (Exception e){
            throw new RuntimeException(e);
        }

        owners = new HashMap<>();
        int countOwners = 0;
        for (Worker worker : workersCollection) {
            if (!owners.containsValue(worker.getOwner().getUsername())){
                owners.put(countOwners, worker.getOwner().getUsername());
                countOwners ++;
            }
        }

        JPanel labelsPanel = new JPanel();
        labelsPanel.setLayout(new BoxLayout(labelsPanel, BoxLayout.Y_AXIS));

        for (HashMap.Entry<Integer, String> entry : owners.entrySet()) {
            Integer id = entry.getKey();
            String owner = entry.getValue();
            JLabel ownersLabel = new JLabel(id + " " + owner);
            labelsPanel.add(ownersLabel);
        }

        int i = 1;
        for (Worker worker : workersCollection) {
            int originalX = worker.getCoordinates().getX();
            int originalY = worker.getCoordinates().getY();

            int xOffset = 3 * i; // Небольшое смещение по оси X
            int yOffset = 3 * i; // Небольшое смещение по оси Y

            int x = (int) ((((double) (originalX - 1) / (Integer.MAX_VALUE - 1)) * 500 + 50 + xOffset) + (Math.random()*100));
            int y = (int) ((((double) (originalY - 1) / (Integer.MAX_VALUE - 1)) * 500 + 50 + yOffset) + (Math.random()*100));
            int radius = 50;
            String username = worker.getOwner().getUsername();
            int colorIndex = getKeyByUsername(owners, username);
            circles.put(new AnimatedCircle(x, y, radius, colorIndex), worker.getName());
            i += 20;
        }

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 1200);
        CirclesPanel circlesPanel = new CirclesPanel(circles, workersCollection, client, communicationControl);
        getContentPane().add(circlesPanel);

        JButton openMainFrameButton = new JButton(messages.getString("openDataTable"));

        // Добавьте обработчик событий, который открывает окно MainFrame при нажатии на кнопку
        openMainFrameButton.addActionListener(e -> {
            MainWindow mainFrame = new MainWindow(client, communicationControl); // Замените MainFrame на ваш класс окна
            mainFrame.setVisible(true);
            dispose(); // Закройте текущее окно VisualTable
        });

        // Создайте панель с FlowLayout для размещения кнопки в верхнем правом углу
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
        buttonPanel.add(openMainFrameButton);

        // Создайте панель для размещения кнопки и кругов
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(circlesPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.NORTH);
        mainPanel.add(labelsPanel, BorderLayout.EAST);

        // Добавьте панель на основной контейнер
        getContentPane().add(mainPanel);



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