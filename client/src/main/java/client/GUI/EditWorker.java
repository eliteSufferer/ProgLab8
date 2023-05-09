package client.GUI;

import client.Client;
import client.utils.CommunicationControl;
import common.data.*;
import common.functional.Request;
import common.functional.Response;
import common.functional.ServerResponseCode;
import common.functional.WorkerPacket;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.lang.ref.Cleaner;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class EditWorker extends JFrame {
    private ArrayList<JTextField> fields = new ArrayList<>();
    private ArrayList<JLabel> labels = new ArrayList<>();
    private JButton saveButton;
    private CommunicationControl communicationControl;
    private ResourceBundle messages = ResourceBundle.getBundle("client.GUI.Messages", UserSettings.getInstance().getSelectedLocale());


    public EditWorker(CommunicationControl communicationControl) {
        this.communicationControl = communicationControl;

        String[] fieldNames = {"ID", "Name", "x", "y", "Salary", "Position", "Status",
                "Birthday", "Height", "Passport ID", "Location X", "Location Y", "Location Z", "Location Name"};

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(15, 2));

        for (int i = 0; i < fieldNames.length; i++) {
            JLabel label = new JLabel(fieldNames[i] + ":");
            JTextField field = new JTextField(10);
            if (i == 0) {
                field.setEditable(false);
            }
            fields.add(i, field);
            labels.add(i, label);
            addLabelAndField(panel, label, field);
        }

        saveButton = new JButton(messages.getString("save"));

        panel.add(saveButton);
        add(panel);

        // Other JFrame setup code
        setTitle("Worker");
        setVisible(true);
        setSize(1000, 600);
        pack();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Центрируем окно


    }

    private void addLabelAndField(JPanel panel, JLabel label, JTextField field) {
        panel.add(label);
        panel.add(field);
    }
    public void setInfo(Worker worker){
        fields.get(0).setText(String.valueOf(worker.getId()));
        fields.get(1).setText(worker.getName());
        fields.get(2).setText(String.valueOf(worker.getCoordinates().getX()));

        fields.get(3).setText(String.valueOf(worker.getCoordinates().getY()));

        fields.get(4).setText(String.valueOf(worker.getSalary()));

        fields.get(5).setText(String.valueOf(worker.getPosition()));

        fields.get(6).setText(String.valueOf(worker.getStatus()));

        fields.get(7).setText(String.valueOf(worker.getPerson().getBirthday()).substring(0, 10));

        fields.get(8).setText(String.valueOf(worker.getPerson().getHeight()));

        fields.get(9).setText(String.valueOf(worker.getPerson().getPassportID()));

        fields.get(10).setText(String.valueOf(worker.getPerson().getLocation().getX()));

        fields.get(11).setText(String.valueOf(worker.getPerson().getLocation().getY()));

        fields.get(12).setText(String.valueOf(worker.getPerson().getLocation().getZ()));

        fields.get(13).setText(String.valueOf(worker.getPerson().getLocation().getName()));
    }
    public void setNonEditable(){
        fields.get(1).setEditable(false);
        fields.get(2).setEditable(false);
        fields.get(3).setEditable(false);
        fields.get(4).setEditable(false);
        fields.get(5).setEditable(false);
        fields.get(6).setEditable(false);
        fields.get(7).setEditable(false);
        fields.get(8).setEditable(false);
        fields.get(9).setEditable(false);
        fields.get(10).setEditable(false);
        fields.get(11).setEditable(false);
        fields.get(12).setEditable(false);
        fields.get(13).setEditable(false);


    }
    public void setEditable(){
        fields.get(1).setEditable(true);
        fields.get(2).setEditable(true);
        fields.get(3).setEditable(true);
        fields.get(4).setEditable(true);
        fields.get(5).setEditable(true);
        fields.get(6).setEditable(true);
        fields.get(7).setEditable(true);
        fields.get(8).setEditable(true);
        fields.get(9).setEditable(true);
        fields.get(10).setEditable(true);
        fields.get(11).setEditable(true);
        fields.get(12).setEditable(true);
        fields.get(13).setEditable(true);


    }

    public WorkerPacket update(){
        String newName = communicationControl.setName(fields.get(1).getText());

        int x = communicationControl.setCoodrinateX(fields.get(2).getText());

        int y = communicationControl.setCoodrinateY(fields.get(3).getText());


        double newSalary = communicationControl.setSalary(fields.get(4).getText());


        Position position = communicationControl.choosePosition(fields.get(5).getText());


        Status status = communicationControl.chooseStatus(fields.get(6).getText());

        Location location = communicationControl.setLocation(fields.get(10).getText(), fields.get(11).getText(), fields.get(12).getText(), fields.get(13).getText());

        Person person = communicationControl.setPerson(fields.get(7).getText(),
                fields.get(8).getText(), fields.get(9).getText(), location);

        WorkerPacket packet = new WorkerPacket(newName, new Coordinates(x, y),
                newSalary, position, status, person);

        return packet;
        // Пересоздаем объект Worker с новыми значениями


    }
    public JButton getSaveButton() {
        return saveButton;
    }


}
