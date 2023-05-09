package client.GUI;


import client.Client;
import client.RunClient;
import client.utils.CommunicationControl;
import client.utils.PasswordHasher;
import common.exceptions.UserAlreadyExists;
import common.exceptions.UserIsNotFoundException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Locale;
import java.util.ResourceBundle;

public class LoginForm extends JFrame {

    Client client;
    RunClient runClient;

    ResourceBundle messages;

    JLabel titleLabel;
    JLabel usernameLabel;
    JLabel passwordLabel;
    JCheckBox accountExistsCheckBox;
    JButton loginButton;

    public LoginForm(CommunicationControl communicationControl) {
        // Задаем язык по умолчанию (английский)
        setLanguage(new Locale("en", "IE"));

        setTitle(messages.getString("authorization"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 300);
        setLayout(new BorderLayout());

        JPanel languagePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        ButtonGroup languageGroup = new ButtonGroup();

        JRadioButton englishButton = new JRadioButton("English");
        JRadioButton russianButton = new JRadioButton("Русский");
        JRadioButton portugueseButton = new JRadioButton("Português");
        JRadioButton swedishButton = new JRadioButton("Svenska");

        languageGroup.add(englishButton);
        languageGroup.add(russianButton);
        languageGroup.add(portugueseButton);
        languageGroup.add(swedishButton);

        englishButton.setSelected(true);

        englishButton.addActionListener(e -> setLanguage(new Locale("en", "IE")));
        russianButton.addActionListener(e -> setLanguage(new Locale("ru", "RU")));
        portugueseButton.addActionListener(e -> setLanguage(new Locale("pt", "PT")));
        swedishButton.addActionListener(e -> setLanguage(new Locale("sv", "SE")));

        languagePanel.add(englishButton);
        languagePanel.add(russianButton);
        languagePanel.add(portugueseButton);
        languagePanel.add(swedishButton);

        getContentPane().add(languagePanel, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        usernameLabel = new JLabel(messages.getString("username"));
        inputPanel.add(usernameLabel, gridBagConstraints);

        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        JTextField usernameField = new JTextField();
        usernameField.setPreferredSize(new Dimension(150, 30));
        inputPanel.add(usernameField, gridBagConstraints);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        passwordLabel = new JLabel(messages.getString("password"));
        inputPanel.add(passwordLabel, gridBagConstraints);

        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        JPasswordField passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(150, 30));
        inputPanel.add(passwordField, gridBagConstraints);

        getContentPane().add(inputPanel, BorderLayout.CENTER);

        accountExistsCheckBox = new JCheckBox(messages.getString("accountExists"));
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        inputPanel.add(accountExistsCheckBox, gridBagConstraints);
        gridBagConstraints.gridwidth = 1;

        JPanel buttonPanel = new JPanel(new FlowLayout());

        loginButton = new JButton(messages.getString("login"));
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                    String password = new String(passwordField.getPassword());

                    if (!usernameLabel.getText().trim().isEmpty() && !password.isEmpty() && client.processAuthentication(accountExistsCheckBox.isSelected(), usernameField.getText(), PasswordHasher.hashPassword(String.valueOf(passwordField.getPassword())))) {
                        MainWindow mainFrame = new MainWindow(client, communicationControl);
                        mainFrame.setVisible(true);
                        dispose();
                    }
                    else{
                        JOptionPane.showMessageDialog(null, "Ошибка при входе, проверьте правильность данных и повторите попытку!");
                    }

            }
        });
        buttonPanel.add(loginButton);

        // Добавление панели кнопок на основную форму
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    }

    public void setRunClient(RunClient app) {
        this.runClient = app;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    private void setLanguage(Locale locale) {
        UserSettings.getInstance().setSelectedLocale(locale);
        messages = ResourceBundle.getBundle("client.GUI.Messages", locale);
        if (titleLabel != null) {
            titleLabel.setText(messages.getString("authorization"));
        }
        if (usernameLabel != null) {
            usernameLabel.setText(messages.getString("username"));
        }
        if (passwordLabel != null) {
            passwordLabel.setText(messages.getString("password"));
        }
        if (accountExistsCheckBox != null) {
            accountExistsCheckBox.setText(messages.getString("accountExists"));
        }
        if (loginButton != null) {
            loginButton.setText(messages.getString("login"));
        }
    }
}