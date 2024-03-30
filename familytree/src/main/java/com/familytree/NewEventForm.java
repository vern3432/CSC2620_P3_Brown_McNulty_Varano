package com.familytree;

import org.jdatepicker.impl.*;
import org.jdatepicker.util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Properties;

public class NewEventForm extends JFrame {
    private JLabel dateLabel;
    private JLabel typeLabel;
    private JButton submitButton;
    private JDatePickerImpl datePicker;
    private JTextField typeField;

    private Connection connection;

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
            LocalDate eventDate = (LocalDate) datePicker.getModel().getValue();

            // Get event type from the type field
            String eventType = typeField.getText();

            // Insert the new event into the database
            String query = "INSERT INTO Event (event_date, event_type) VALUES (?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setDate(1, java.sql.Date.valueOf(eventDate));
                statement.setString(2, eventType);
                statement.executeUpdate();
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

    public static void main(String[] args) {
        // You can test the form independently if needed
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Connection connection = null; // Initialize your database connection
                NewEventForm form = new NewEventForm(connection);
                form.setVisible(true);
            }
        });
    }
}
