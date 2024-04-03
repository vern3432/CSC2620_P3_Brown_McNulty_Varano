package com.familytree.views;

import org.jdatepicker.impl.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class NewFamilyMemberForm extends JFrame {
    private JTextField nameField;
    private JDatePickerImpl birthDatePicker;
    private JDatePickerImpl deathDatePicker;
    private JTextField currentResidenceField;
    private JButton addSpouseButton;
    private JButton addChildButton;
    private JButton submitButton;
    private JLabel relationshipLabel;

    private Connection connection;
    private List<String> relationships;

    public NewFamilyMemberForm(Connection connection) {
        this.connection = connection;

        setTitle("New Family Member Form");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window

        relationships = new ArrayList<>();

        // Initialize components
        nameField = new JTextField();
        birthDatePicker = createDatePicker();
        deathDatePicker = createDatePicker();
        currentResidenceField = new JTextField();
        addSpouseButton = new JButton("Add Spouse");
        addChildButton = new JButton("Add Child");
        submitButton = new JButton("Submit");
        submitButton.setEnabled(false); // Initially disabled
        relationshipLabel = new JLabel();

        // Layout setup
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(5, 5, 5, 5);

        panel.add(new JLabel("Name:"), gbc);
        gbc.gridy++;
        panel.add(nameField, gbc);
        gbc.gridy++;
        panel.add(new JLabel("Birth Date:"), gbc);
        gbc.gridy++;
        panel.add(birthDatePicker, gbc);
        gbc.gridy++;
        panel.add(new JLabel("Death Date:"), gbc);
        gbc.gridy++;
        panel.add(deathDatePicker, gbc);
        gbc.gridy++;
        panel.add(new JLabel("Current Residence:"), gbc);
        gbc.gridy++;
        panel.add(currentResidenceField, gbc);
        gbc.gridy++;
        panel.add(addSpouseButton, gbc);
        gbc.gridy++;
        panel.add(addChildButton, gbc);
        gbc.gridy++;
        panel.add(submitButton, gbc);
        gbc.gridy++;
        panel.add(relationshipLabel, gbc);

        // Add action listeners
        addSpouseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addSpouse();
            }
        });

        addChildButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addChild();
            }
        });

        // Add action listener for the submit button
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitForm();
            }
        });

        // Add listeners to enable/disable submit button
        nameField.getDocument().addDocumentListener(new FormDocumentListener());
        birthDatePicker.addActionListener(new FormActionListener());

        // Create scroll pane and add panel to it
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // Add scroll pane to frame
        add(scrollPane);
    }

    
    /** 
     * @return JDatePickerImpl
     */
    private JDatePickerImpl createDatePicker() {
        UtilDateModel model = new UtilDateModel();
        Properties p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");
        JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
        return new JDatePickerImpl(datePanel, new DateLabelFormatter());
    }

    private void addSpouse() {
        // Implement adding spouse functionality here
        relationships.add("X married to Y"); // Example text
        updateRelationshipLabel();
    }

    private void addChild() {
        // Implement adding child functionality here
        relationships.add("X parent of Y"); // Example text
        updateRelationshipLabel();
    }

    private void submitForm() {
        // Implement form submission here
    }

    private void updateRelationshipLabel() {
        StringBuilder sb = new StringBuilder("<html>");
        for (String relationship : relationships) {
            sb.append(relationship).append("<br>");
        }
        sb.append("</html>");
        relationshipLabel.setText(sb.toString());
    }

    private class DateLabelFormatter extends JFormattedTextField.AbstractFormatter {
        private final String DATE_PATTERN = "yyyy-MM-dd";

        @Override
        public Object stringToValue(String text) throws ParseException {
            return null; // Not used
        }

        @Override
        public String valueToString(Object value) throws ParseException {
            if (value != null) {
                return new java.text.SimpleDateFormat(DATE_PATTERN).format((Date) value);
            }
            return ""; // Return empty string if value is null
        }
    }

    private class FormDocumentListener implements javax.swing.event.DocumentListener {
        @Override
        public void insertUpdate(javax.swing.event.DocumentEvent e) {
            updateSubmitButtonState();
        }

        @Override
        public void removeUpdate(javax.swing.event.DocumentEvent e) {
            updateSubmitButtonState();
        }

        @Override
        public void changedUpdate(javax.swing.event.DocumentEvent e) {
            updateSubmitButtonState();
        }
    }

    private class FormActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            updateSubmitButtonState();
        }
    }

    private void updateSubmitButtonState() {
        String name = nameField.getText();
        Date birthDate = (Date) birthDatePicker.getModel().getValue();
        if (name.trim().isEmpty() || birthDate == null) {
            submitButton.setEnabled(false);
        } else {
            submitButton.setEnabled(true);
        }
    }

    public static void main(String[] args) {
        // You can test the form independently if needed
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Connection connection = null; // Initialize your database connection
                NewFamilyMemberForm form = new NewFamilyMemberForm(connection);
                form.setVisible(true);
            }
        });
    }
}
