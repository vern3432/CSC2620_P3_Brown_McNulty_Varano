package com.familytree;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Properties;
import java.time.ZoneId;
import java.util.Date;
import java.time.LocalDate;

/** represents popup menu for managing events */
public class EventPopupMenu extends JPopupMenu {
    private int eventId;
    private Connection connection;
    
    /** 
     * @return Connection
     */
    public Connection getConnection() {
        return connection;
    }

    /** 
     * set connection
     */
    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    private JLabel dateLabel;
    private JLabel typeLabel;
    private JTextArea attendeesArea;
    private JButton addAttendeeButton;

    /**
     * Constructs new event popup menu with the specified event ID and connection
     * 
     * @param eventId    ID of the event
     * @param connection database connection
     */
    public EventPopupMenu(int eventId, Connection connection) {
        this.eventId = eventId;
        this.connection = connection;

        dateLabel = new JLabel();
        typeLabel = new JLabel();
        attendeesArea = new JTextArea();
        addAttendeeButton = new JButton("Add Attendee");

        // pupulates the data
        loadData();

        // adds components components to the popup menu
        add(dateLabel);
        add(typeLabel);
        add(new JScrollPane(attendeesArea));
        add(addAttendeeButton);

        // sets action listener for add attendee button
        addAttendeeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addAttendee();
            }
        });
    }

    private void loadData() {
        try {
            // Fetch data from the database based on the event id
            String query = "SELECT event_date, event_type FROM Event WHERE event_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, eventId);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    String date = resultSet.getString("event_date");
                    String type = resultSet.getString("event_type");
                    dateLabel.setText("Date: " + date);
                    typeLabel.setText("Type: " + type);
                }
            }

            // Fetch attendees
            query = "SELECT fm.name, fm.birth_date FROM EventAttendee ea JOIN FamilyMembers fm ON ea.member_id = fm.member_id WHERE ea.event_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, eventId);
                ResultSet resultSet = statement.executeQuery();
                StringBuilder attendees = new StringBuilder();
                while (resultSet.next()) {
                    String name = resultSet.getString("name");
                    String birthDate = resultSet.getString("birth_date");
                    attendees.append(name).append(" (").append(birthDate).append(")\n");
                }
                attendeesArea.setText(attendees.toString());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    
    public void addAttendee2() {
        try {
            // Fetch all family members to populate the dropdown menu
            String query = "SELECT member_id, name, birth_date FROM FamilyMembers";
            System.out.println("query");
            try (PreparedStatement statement = this.getConnection().prepareStatement(query)) {
                System.out.println("1st statement made");

                ResultSet resultSet = statement.executeQuery();
                // Create a dropdown menu with family member names and birthdates
                JPopupMenu menu = new JPopupMenu();
                while (resultSet.next()) {
                    int memberId = resultSet.getInt("member_id");
                    String name = resultSet.getString("name");
                    String birthDate = resultSet.getString("birth_date");
                    JMenuItem menuItem = new JMenuItem(name + " (" + birthDate + ")");

                    menuItem.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            System.out.println("Actually cought");
                            // Add the selected family member as an attendee to the event
                            try {
                                System.out.println("Insert suceed for new attendee");

                                String insertQuery = "INSERT INTO EventAttendee (event_id, member_id) VALUES (?, ?)";
                                PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
                                    insertStatement.setInt(1, eventId);
                                    insertStatement.setInt(2, memberId);
                                    insertStatement.executeUpdate();
                                
                                // Reload data after adding attendee
                                loadData();
                            } catch (SQLException ex) {
                                System.out.println("Insert failed for new attendee");
                                ex.printStackTrace();
                            }
                        }
                    });

                    // Assuming this is where you add the JMenuItem to a JMenu or JPopupMenu
                    // For demonstration purposes, let's assume menu is your JMenu or JPopupMenu
                    // object
                    menu.add(menuItem);

                    menu.add(menuItem);
                }
                // Display the dropdown menu
                menu.show(addAttendeeButton, 0, addAttendeeButton.getHeight());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addAttendee() {
        try {
            // Fetch all family members to populate the dropdown menu
            String query = "SELECT member_id, name, birth_date FROM FamilyMembers";
            try (PreparedStatement statement = this.getConnection().prepareStatement(query)) {
                ResultSet resultSet = statement.executeQuery();
                
                // Create a new JFrame for displaying options
                JFrame frame = new JFrame("Select Family Member");
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                JPanel panel = new JPanel();
                panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    
                // Populate the panel with options
                while (resultSet.next()) {
                    int memberId = resultSet.getInt("member_id");
                    String name = resultSet.getString("name");
                    String birthDate = resultSet.getString("birth_date");
                    JMenuItem menuItem = new JMenuItem(name + " (" + birthDate + ")");
                    menuItem.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            // Print the selected family member
                            System.out.println("Selected: " + name + " (" + birthDate + ")");
                            
                            // Add the selected family member as an attendee to the event
                            try {
                                String insertQuery = "INSERT INTO EventAttendee (event_id, member_id) VALUES (?, ?)";
                                PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
                                insertStatement.setInt(1, eventId);
                                insertStatement.setInt(2, memberId);
                                insertStatement.executeUpdate();
                                
                                // Reload data after adding attendee
                                loadData();
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                            }
                            frame.dispose(); // Close the window after selection
                        }
                    });
                    panel.add(menuItem);
                }
    
                // Add panel to frame and display
                frame.getContentPane().add(panel);
                frame.pack();
                frame.setLocationRelativeTo(null); // Center the frame on the screen
                frame.setVisible(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    
}
