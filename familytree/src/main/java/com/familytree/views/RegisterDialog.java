package com.familytree.views;

import com.familytree.data.access.ClientDataAccess;
import com.familytree.data.entities.Client;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;

public class RegisterDialog extends JDialog implements ActionListener {

    private Connection connection;
    private JTextField nameField;
    private JTextField usernameField;
    private JPasswordField passwordField;

    public RegisterDialog(JFrame parent, Connection connection) {
        super(parent, "Register", true);
        this.connection = connection;
        setSize(300, 200);
        setLocationRelativeTo(parent);

        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        getContentPane().add(panel, BorderLayout.CENTER);

        JLabel nameLabel = new JLabel("Name:");
        panel.add(nameLabel);

        nameField = new JTextField();
        panel.add(nameField);

        JLabel usernameLabel = new JLabel("Username:");
        panel.add(usernameLabel);

        usernameField = new JTextField();
        panel.add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        panel.add(passwordLabel);

        passwordField = new JPasswordField();
        panel.add(passwordField);

        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(this);
        panel.add(registerButton);

        // Align buttons to the right
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(registerButton);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Register")) {
            String name = nameField.getText();
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (!isValidEntries(name, username, password)) {
                JOptionPane.showMessageDialog(this, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                if (ClientDataAccess.getByUserName(name, connection) != null) {
                    JOptionPane.showMessageDialog(this, "User name already taken, Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Here you can implement your registration logic
                if (registerUser(name, username, password)) {
                    JOptionPane.showMessageDialog(this, "Registration successful!");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Registration failed. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Unexpected error was caught, Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean registerUser(String name, String username, String password) throws SQLException {
        ClientDataAccess.create(new Client(0, name, username), password, connection);
        return true;
    }

    private boolean isValidEntries(String name, String username, String password) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        if (password == null || password.trim().isEmpty()) {
            return false;
        }
        return true;
    }
}
