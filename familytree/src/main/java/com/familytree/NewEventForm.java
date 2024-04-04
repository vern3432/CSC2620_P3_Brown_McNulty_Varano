package com.familytree;

import org.jdatepicker.impl.*;
import org.jdatepicker.util.*;

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

public class NewEventForm extends JFrame {
    private JLabel dateLabel;
    private JLabel typeLabel;
    private JButton submitButton;
    private JDatePickerImpl datePicker;
    private JTextField typeField;

    private Connection connection;

    /**
    * Represents a form for creating a new event. This form allows users
    * to enter the date and type of the event and submit it to the database
    * @param connection
    */
    public NewEventForm(Connection connection) {
        this.connection = connection;

        setTitle("New Event Form");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window

        dateLabel = new JLabel("Date:");
        typeLabel = new JLabel("Type:");
        submitButton = new JButton("Submit");

        // UtilDateModel for date picker
        UtilDateModel model = new UtilDateModel();
        Properties p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");
        JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
        datePicker = new JDatePickerImpl(datePanel, new JFormattedTextField.AbstractFormatter() {
            @Override
            public Object stringToValue(String text) throws ParseException {
                return null; // Not used
            }

            @Override
            public String valueToString(Object value) throws ParseException {
                if (value != null) {
                    return value.toString();
                }
                return ""; // Return empty string if value is null
            }
        });

        typeField = new JTextField(10);

        // Layout setup
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));
        panel.add(dateLabel);
        panel.add(datePicker);
        panel.add(typeLabel);
        panel.add(typeField);
        panel.add(submitButton);

        add(panel);

        // Set action listener for the submit button
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitEvent();
            }
        });
    }

    private void submitEvent() {
        try {
            // Get selected date from the date picker
            Date eventDate = (Date) datePicker.getModel().getValue();
            String eventDateLocalString=eventDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().toString();
            
            // Get event type from the type field
            String eventType = typeField.getText();

            // Insert the new event into the database
            String query = "INSERT INTO Event (event_id,event_date, event_type) VALUES (?,?, ?)";
            String query2 = "SELECT MAX(event_id) AS max_id FROM Event";
            int nextMemberId = 1; // Start from ID 1

            try (PreparedStatement statement2 = connection.prepareStatement(query2)) {
                ResultSet resultSet2 = statement2.executeQuery();
                nextMemberId = resultSet2.getInt("max_id") + 1;
            }




            System.out.println(nextMemberId);
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, nextMemberId);
                preparedStatement.setString(2, eventDateLocalString);
                preparedStatement.setString(3, eventType);
                int rowsAffected = preparedStatement.executeUpdate();
                            // Checking if the query was successful
            if (rowsAffected > 0) {
                System.out.println("Event inserted successfully.");
            } else {
                System.out.println("Failed to insert event.");
            }

            }


            // Inform the user about successful submission
            JOptionPane.showMessageDialog(this, "Event submitted successfully.");

            // Close the form
            dispose();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error submitting event: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    
    /** 
     * @param args
     */
    public static void main(String[] args) {
        // You can test the form independently if needed
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Connection connection = null; // initialize your database connection
                NewEventForm form = new NewEventForm(connection);
                form.setVisible(true);
            }
        });
    }
}
