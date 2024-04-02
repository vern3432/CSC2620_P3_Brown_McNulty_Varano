package com.familytree.views;

import com.familytree.FamilyTreeGUI;
import com.familytree.data.access.ClientDataAccess;
import com.familytree.data.entities.Client;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;

public class LoginDialog extends JFrame implements ActionListener {

    private Connection connection;
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginDialog(Connection connection) {
        this.connection = connection;
        setTitle("Login");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        getContentPane().add(panel, BorderLayout.CENTER);

        JLabel usernameLabel = new JLabel("Username:");
        panel.add(usernameLabel);

        usernameField = new JTextField();
        panel.add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        panel.add(passwordLabel);

        passwordField = new JPasswordField();
        panel.add(passwordField);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(this);
        panel.add(loginButton);

        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(this);
        panel.add(registerButton);

        // Align buttons to the right
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Login")) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            try {
                var client = authenticateUser(username, password);
                if (client != null) {
                    JOptionPane.showMessageDialog(this, "Login successful!");
                    dispose();
                    var familyTreeGUI = new FamilyTreeGUI(connection, client);
                    familyTreeGUI.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid username or password. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Failed to authenticate user. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getActionCommand().equals("Register")) {
            RegisterDialog registerDialog = new RegisterDialog(this, connection);
            registerDialog.setVisible(true);
        }
    }

    private Client authenticateUser(String username, String password) throws SQLException {
        return ClientDataAccess.getByUserNameAndPassword(username, password, connection);
    }
}
