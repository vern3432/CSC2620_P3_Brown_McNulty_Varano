package com.familytree.views;

import com.familytree.FamilyTreeGUI;
import com.familytree.data.entities.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;

public class LoggedInPanel extends JPanel {

    private JLabel userLabel;
    private JButton logoutButton;

    public LoggedInPanel(Client client, FamilyTreeGUI main, Connection conn) {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 240));

        // Create user label
        userLabel = new JLabel("Logged in as: " + client.getName());
        userLabel.setFont(new Font("Arial", Font.BOLD, 16));
        userLabel.setHorizontalAlignment(SwingConstants.LEFT);
        add(userLabel, BorderLayout.WEST);

        // Create logout button
        logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Arial", Font.PLAIN, 14));
        logoutButton.setFocusPainted(false);
        logoutButton.addActionListener(e -> {
            // Perform logout action here
            JOptionPane.showMessageDialog(LoggedInPanel.this, "Logged out successfully!");
            main.dispose();
            new LoginDialog(conn);
        });

        // Add logout button to the bottom
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(logoutButton);
        add(buttonPanel, BorderLayout.EAST);
        setVisible(true);
    }
}
