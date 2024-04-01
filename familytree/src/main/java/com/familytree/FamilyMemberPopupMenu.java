package com.familytree;

import javax.swing.*;

import java.awt.CardLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FamilyMemberPopupMenu extends JPopupMenu {
    private int memberId;
    private Connection connection;
    private JLabel nameLabel;
    private JLabel addressLabel;
    private JTextArea relationshipsArea;
    private JButton editButton;
    private JButton submitButton;

    public FamilyMemberPopupMenu(int memberId, Connection connection) {
        this.memberId = memberId;
        this.connection = connection;

        nameLabel = new JLabel();
        addressLabel = new JLabel();
        relationshipsArea = new JTextArea();
        editButton = new JButton("Edit");
        submitButton = new JButton("Submit");

        // Populate data
        loadData();

        // Add components to the popup menu
        add(nameLabel);
        add(addressLabel);
        add(new JScrollPane(relationshipsArea));
        add(editButton);
        add(submitButton);

        // Set action listeners
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enableEditMode();
            }
        });

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitChanges();
            }
        });
    }

    private void loadData() {
        try {
            // Fetch data from the database based on the memberId
            String query = "SELECT Addresses.city,FamilyMembers.name FROM Addresses JOIN FamilyMembers ON FamilyMembers.current_residence=Addresses.address_id WHERE FamilyMembers.member_id = ?";
            String query_dead = "SELECT name FROM FamilyMembers WHERE FamilyMembers.member_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, memberId);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    String name = resultSet.getString("name");
                    String residence = resultSet.getString("city");
                    System.out.println(name);
                    System.out.println(residence);
                    nameLabel.setText("Name: " + name);
                    addressLabel.setText("Address: " + residence);
                } else {
                    try (PreparedStatement statement2 = connection.prepareStatement(query_dead)) {
                        System.out.println("Empty" + memberId);
                        statement2.setInt(1, memberId);
                        ResultSet resultSet2 = statement2.executeQuery();
                        if (resultSet2.next()) {
                            String name2 = resultSet2.getString("name");
                            System.out.println(name2);
                            nameLabel.setText("Name: " + name2);
                            addressLabel.setText("Address: " + "N/A");

                        } else {

                            System.out.println("Empty_Second" + memberId);

                        }
                    }
                }
            } catch (SQLException e) {
                System.out.println("caugt");
                e.printStackTrace();
            }

            // Fetch relationships
            query = "SELECT r.relation_type, fm.name FROM Relationships r JOIN FamilyMembers fm ON r.related_member_id = fm.member_id WHERE r.member_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, memberId);
                ResultSet resultSet = statement.executeQuery();
                StringBuilder relationships = new StringBuilder();
                while (resultSet.next()) {
                    String relationType = resultSet.getString("relation_type");
                    String relatedName = resultSet.getString("name");
                    relationships.append(relationType).append(": ").append(relatedName).append("\n");
                }
                relationshipsArea.setText(relationships.toString());
            }

            // Disable edit and submit buttons initially
            editButton.setEnabled(true);
            submitButton.setEnabled(false);
        } catch (SQLException e) {
            System.out.println("caugt");
            e.printStackTrace();
        }
    }

    private void enableEditMode() {
        // Create a dialog box with buttons for options
        String[] options = {"Name", "Address"};
        int optionSelected = JOptionPane.showOptionDialog(null, "Choose an option to edit:", "Edit Information",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
    
        if (optionSelected == 0) { // Edit Name
            String currentName = nameLabel.getText().substring(6).trim(); // Extract current name
            String newName = JOptionPane.showInputDialog(null, "Enter new name:", "Edit Name", JOptionPane.PLAIN_MESSAGE, null, null, currentName).toString();
            if (newName != null && !newName.isEmpty()) {
                nameLabel.setText("Name: " + newName);
                submitChanges(); // Automatically submit changes for simplicity
            }
        } else if (optionSelected == 1) { // Edit Address
            String currentAddress = addressLabel.getText().substring(9).trim(); // Extract current address
            String newAddress = JOptionPane.showInputDialog(null, "Enter new address:", "Edit Address", JOptionPane.PLAIN_MESSAGE, null, null, currentAddress).toString();
            if (newAddress != null && !newAddress.isEmpty()) {
                addressLabel.setText("Address: " + newAddress);
                submitChanges(); // Automatically submit changes for simplicity
            }
        }
    }
    

    

    private void submitChanges() {
        try {
            // Retrieve edited data
            String editedName = nameLabel.getText().substring(6); // Remove "Name: " prefix
            String editedAddress = addressLabel.getText().substring(9); // Remove "Address: " prefix
            String editedRelationships = relationshipsArea.getText();

            // Update database with edited data
            String query = "UPDATE FamilyMembers SET name = ?, current_residence = ? WHERE member_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, editedName);
                statement.setString(2, editedAddress);
                statement.setInt(3, memberId);
                statement.executeUpdate();
            }

            // Inform the user about successful submission
            JOptionPane.showMessageDialog(null, "Changes submitted successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
