package com.familytree;
import org.jdatepicker.impl.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Date;

public class NewFamilyMemberForm extends JFrame {
    private JTextField nameField;
    private JDatePickerImpl birthDatePicker;
    private JDatePickerImpl deathDatePicker;
    private JTextField currentResidenceField;
    private JButton addSpouseButton;
    private JButton addChildButton;
    private JButton submitButton;
    private JLabel relationshipLabel;
    private JDatePickerImpl datePicker;
    Date deathDate;
    Date birthDate;

    private Connection connection;

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

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
                addAttendee();
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

    private JDatePickerImpl createDatePicker() {
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

        return datePicker;




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
        try {
            insertNewFamilyMember(nameField.getText());
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

    private void addAttendee() {
        try {
            // Fetch all family members to populate the dropdown menu
            String query = "SELECT member_id, name, birth_date FROM FamilyMembers";
            try (PreparedStatement statement = this.getConnection().prepareStatement(query)) {
                ResultSet resultSet = statement.executeQuery();

                // Create a new JFrame for displaying options
                JFrame frame = new JFrame("Select Family Member");
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

                // Create a panel with BoxLayout
                JPanel panel = new JPanel();
                panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

                // Create a scroll pane and add the panel to it
                JScrollPane scrollPane = new JScrollPane(panel);
                scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

                // Populate the panel with options
                while (resultSet.next()) {
                    int memberId = resultSet.getInt("member_id");
                    String name = resultSet.getString("name");
                    String birthDate = resultSet.getString("birth_date");
                    JMenuItem menuItem = new JMenuItem(name + " (" + birthDate + ")");

                    // Add ActionListener to each menu item
                    menuItem.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            // Print the selected family member
                            System.out.println("Selected: " + name + " (" + birthDate + ")");

                            // Add the selected family member as an attendee to the event
                            try {
                                // Add your code here to handle adding the selected family member to an event
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            frame.dispose(); // Close the window after selection
                        }
                    });
                    panel.add(menuItem);
                }

                // Add scroll pane to the frame and display
                frame.getContentPane().add(scrollPane);
                frame.pack();
                frame.setLocationRelativeTo(null); // Center the frame on the screen
                frame.setVisible(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
         birthDate = (Date) birthDatePicker.getModel().getValue();
        deathDate = (Date) deathDatePicker.getModel().getValue();

        if (name.trim().isEmpty() || birthDate == null) {
            submitButton.setEnabled(false);
        } else {
            submitButton.setEnabled(true);
        }
    }

    private void insertNewFamilyMember(String name) throws SQLException {
        // Retrieve the next available member_id
        int nextMemberId = getNextMemberId();
        birthDate = (Date) birthDatePicker.getModel().getValue();
        deathDate = (Date) deathDatePicker.getModel().getValue();
        Boolean isDead=false;
        if(isDead==null){
            isDead=true;
            
        }
        // Prepare and execute the SQL statement to insert the new family member
        String sql = "INSERT INTO FamilyMembers (member_id, name, birth_date, death_date, is_deceased, client_id) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, nextMemberId); // member_id
        preparedStatement.setString(2, name); // name
        // new java.sql.Date(this.birthDate.getTime());
        preparedStatement.setDate(3,  new java.sql.Date(this.birthDate.getTime())); // birth_date (example date)
        preparedStatement.setDate(4, new java.sql.Date(this.deathDate.getTime())); // death_date (null for now)
        preparedStatement.setBoolean(5, isDead); // is_deceased (false for now)
        // preparedStatement.setInt(6, 1); // client_id (assuming 1 for now)

        // Execute the update
        int rowsAffected = preparedStatement.executeUpdate();
        if (rowsAffected > 0) {
            System.out.println("New family member inserted successfully.");
        } else {
            System.out.println("Failed to insert new family member.");
        }
    }

    private int getNextMemberId() throws SQLException {
        int nextMemberId = 1; // Assuming starting from 1
        String sql = "SELECT MAX(member_id) FROM FamilyMembers";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            if (resultSet.next()) {
                nextMemberId = resultSet.getInt(1) + 1;
            }
        }
        return nextMemberId;
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
