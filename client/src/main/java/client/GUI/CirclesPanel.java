package client.GUI;

import client.Client;
import client.utils.CommunicationControl;
import common.data.Worker;
import common.functional.Request;
import common.functional.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class CirclesPanel extends JPanel {
    private HashMap<AnimatedCircle, Integer> circles;
    private ArrayList<ArrayList<Worker>> workersCollection;
    private final Color[] colors = {Color.RED, Color.LIGHT_GRAY, Color.GREEN, Color.YELLOW};

    public CirclesPanel(HashMap<AnimatedCircle, Integer> circles, ArrayList<ArrayList<Worker>> workersCollection, Client client, CommunicationControl communicationControl) {
        this.circles = circles;
        this.workersCollection = workersCollection;

        Timer timer = new Timer(70, e -> {
            for (AnimatedCircle circle : circles.keySet()) {
                if (circle.growing) {
                    circle.radius += 2;
                    if (circle.radius >= 50) {
                        circle.growing = false;
                    }
                } else {
                    circle.radius -= 2;
                    if (circle.radius <= 30) {
                        circle.growing = true;
                    }
                }
            }
            repaint();
        });
        timer.start();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                for (AnimatedCircle circle : circles.keySet()){
                    int x = e.getX();
                int y = e.getY();
                int distance = (int) Math.sqrt(Math.pow(x - circle.x, 2) + Math.pow(y - circle.y, 2));

                if (distance <= circle.radius) {
                    // Действие при клике на круг
                    Worker worker = findWorkerByID(circles.get(circle));
                    if (worker != null) {
                        EditWorker editWorker = new EditWorker(communicationControl);
                        editWorker.setInfo(worker);
                        JButton saveButton;
                        saveButton = editWorker.getSaveButton();
                        saveButton.addActionListener(e1 -> {
                            try {
                                client.sendRequest(new Request("update_by_id", String.valueOf(worker.getId()), editWorker.update(), client.getCurrentUser()));
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                        });
                    }
                    break;
                }
            }
        }
    });
}

    private Worker findWorkerByID(int id) {
        for (ArrayList<Worker> workers : workersCollection){
            for (Worker worker : workers) {
                if (worker.getId() == id) {
                    return worker;
                }
            }
        }
        return null;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (AnimatedCircle circle : circles.keySet()) {
            g2d.setColor(colors[circle.colorIndex]);
            g2d.fillOval(circle.x - circle.radius, circle.y - circle.radius, 2 * circle.radius, 2 * circle.radius);
            g2d.setColor(Color.BLACK);
            TextInCircle.drawCenteredString(g2d, circles.get(circle) + " " + circle.worker + " " + circle.colorIndex, circle.x, circle.y, g.getFont());
        }
    }
}