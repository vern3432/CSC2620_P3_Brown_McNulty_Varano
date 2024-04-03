package com.familytree;

import javax.swing.*;

import java.awt.CardLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FamilyMemberPopupMenu extends JPopupMenu {
    private int memberId;
    private Connection connection;
    private JLabel nameLabel;
    private JLabel addressLabel;
    private JLabel State;
    public int address_id;

    private JTextArea relationshipsArea;
    private JButton editButton;
    private JButton submitButton;

    public FamilyMemberPopupMenu(int memberId, Connection connection) {
        this.memberId = memberId;
        this.connection = connection;

        nameLabel = new JLabel();
        addressLabel = new JLabel();
        State = new JLabel();

        relationshipsArea = new JTextArea();
        editButton = new JButton("Edit");
        submitButton = new JButton("Submit");

        // Populate data
        loadData();

        // Add components to the popup menu
        add(nameLabel);
        add(addressLabel);
        add(State);

        add(new JScrollPane(relationshipsArea));
        add(editButton);
        add(submitButton);

        // Set action listeners
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enableEditMode(address_id);
            }
        });

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitChanges(address_id);
            }
        });
    }

    private void loadData() {
        try {
            // Fetch data from the database based on the memberId
            String query = "SELECT Addresses.member_id, Addresses.city, Addresses.state, FamilyMembers.name " +
            "FROM Addresses " +
            "JOIN FamilyMembers ON Addresses.member_id = FamilyMembers.member_id " +
            "WHERE FamilyMembers.member_id = ?";
            String query_dead = "SELECT name FROM FamilyMembers WHERE FamilyMembers.member_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, memberId);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    String name = resultSet.getString("name");
                    String residence = resultSet.getString("city");
                    String StateString = resultSet.getString("state");
                    // this.address_id=resultSet.getInt("address_id");
                    System.out.println(name);
                    System.out.println(residence);
                    nameLabel.setText("Name: " + name);
                    addressLabel.setText("Address: " + residence);
                    State.setText("State: " + StateString);

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
            StringBuilder relationships=new StringBuilder();;
            // Fetch relationships
            boolean found_spouse=false;
            query = "SELECT r.relation_type, fm.name FROM Relationships r JOIN FamilyMembers fm ON r.related_member_id = fm.member_id WHERE r.member_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, memberId);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    String relationType = resultSet.getString("relation_type");
                    if (relationType.contains("marriedto")) {
                        found_spouse=true;
                    }
                    String relatedName = resultSet.getString("name");
                    relationships.append(relationType).append(": ").append(relatedName).append("\n");
            
                    
                }

            }
            if(found_spouse==false){
                System.out.println("First found not found");
                query = "SELECT r.relation_type, fm.name FROM Relationships r JOIN FamilyMembers fm ON fm.member_id = r.member_id WHERE r.related_member_id = ?";
                try (PreparedStatement statement2 = connection.prepareStatement(query)) {
                    statement2.setInt(1, memberId);
                    ResultSet resultSet = statement2.executeQuery();
                    while (resultSet.next()) {
                        String relationType = resultSet.getString("relation_type");
                        if (relationType.contains("marriedto")) {
                            String relatedName = resultSet.getString("name");
                            relationships.append(relationType).append(": ").append(relatedName).append("\n"); 
                            
                        }

                    }


                    
                }
                
            }
            relationshipsArea.setText(relationships.toString());


            found_spouse=false;




            // Disable edit and submit buttons initially
            editButton.setEnabled(true);
            submitButton.setEnabled(false);
        } catch (SQLException e) {
            System.out.println("caugt");
            e.printStackTrace();
        }
    }

    
    /** 
     * @param address_id
     */
    private void enableEditMode(int address_id) {
        // Create a dialog box with buttons for options
        String[] options = {"Name", "Address","State"};
        int optionSelected = JOptionPane.showOptionDialog(null, "Choose an option to edit:", "Edit Information",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
    
        if (optionSelected == 0) { // Edit Name
            String currentName = nameLabel.getText().substring(6).trim(); // Extract current name
            String newName = JOptionPane.showInputDialog(null, "Enter new name:", "Edit Name", JOptionPane.PLAIN_MESSAGE, null, null, currentName).toString();
            if (newName != null && !newName.isEmpty()) {
                nameLabel.setText("Name: " + newName);
                submitChanges(address_id); // Automatically submit changes for simplicity
            }
        } else if (optionSelected == 1) { // Edit Address
            String currentAddress = addressLabel.getText().substring(9).trim(); // Extract current address
            String newAddress = JOptionPane.showInputDialog(null, "Enter new address:", "Edit Address", JOptionPane.PLAIN_MESSAGE, null, null, currentAddress).toString();
            if (newAddress != null && !newAddress.isEmpty()) {
                addressLabel.setText("Address: " + newAddress);
                submitChanges(address_id); // Automatically submit changes for simplicity
            }
        }else if (optionSelected == 2) { // Edit Address
            String currentState = State.getText().replace("State:", ""); // Extract current address
            String newAddress = JOptionPane.showInputDialog(null, "Enter new State:", "Edit Address", JOptionPane.PLAIN_MESSAGE, null, null, currentState).toString();
            if (newAddress != null && !newAddress.isEmpty()) {
                State.setText("State: " + newAddress);
                submitChanges(address_id); // Automatically submit changes for simplicity
            }
        }



    }
    

    // public void updateNameAddressState(int memberId, String newName, String newCity, String newState) {
    //     String query = "UPDATE FamilyMembers " +
    //                    "JOIN Addresses ON FamilyMembers.member_id = Addresses.member_id " +
    //                    "SET FamilyMembers.name = ?, Addresses.city = ?, Addresses.state = ? " +
    //                    "WHERE FamilyMembers.member_id = ?";
        
    //     try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    //          PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            
    //         preparedStatement.setString(1, newName);
    //         preparedStatement.setString(2, newCity);
    //         preparedStatement.setString(3, newState);
    //         preparedStatement.setInt(4, memberId);
            
    //         preparedStatement.executeUpdate();
            
    //         System.out.println("Update successful.");
            
    //     } catch (SQLException e) {
    //         e.printStackTrace();
    //     }
    // }

    private void submitChanges(int address_id) {
        try {
            // Retrieve edited data
            String editedName = nameLabel.getText().replace("Name: ", ""); // Remove "Name: " prefix
            String editedAddress = addressLabel.getText().replace("Address: ", ""); // Remove "Address: " prefix
            String editedState = State.getText().replace("State: ", ""); // Remove "State: " prefix
    
            // Update database with edited data
            String updateQuery = "UPDATE Addresses " +
                                 "SET state = ? " +
                                 "WHERE member_id = ?";
    
            try (PreparedStatement statement = connection.prepareStatement(updateQuery)) {
                statement.setString(1, editedState);
                statement.setInt(2, address_id);
                statement.executeUpdate();
                System.out.println("Update successful for state: " + editedState + ", member ID: " + address_id);
            }
    
            updateQuery = "UPDATE Addresses " +
                          "SET city = ? " +
                          "WHERE member_id = ?";
    
            try (PreparedStatement statement = connection.prepareStatement(updateQuery)) {
                statement.setString(1, editedAddress);
                statement.setInt(2, address_id);
                statement.executeUpdate();
                System.out.println("Update successful for city: " + editedAddress + ", member ID: " + address_id);
            }
    
            String updateNameQuery = "UPDATE FamilyMembers " +
                                     "SET name = ? " +
                                     "WHERE member_id = ?";
    
            try (PreparedStatement statement = connection.prepareStatement(updateNameQuery)) {
                statement.setString(1, editedName);
                statement.setInt(2, memberId);
                statement.executeUpdate();
                System.out.println("Update successful for name: " + editedName + ", member ID: " + memberId);
            }
    
            // Inform the user about successful submission
            JOptionPane.showMessageDialog(null, "Changes submitted successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
}
