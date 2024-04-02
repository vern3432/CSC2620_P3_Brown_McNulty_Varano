package com.familytree;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class EventManagementPanel extends JPanel {

    private final FamilyDatabase database;
    private final List<Event> events;
    private final DefaultListModel<Event> listModel;
    private final JList<Event> eventList;
    private Connection connection;

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public EventManagementPanel(FamilyDatabase database) {
        this.connection=FamilyDatabase.getConnection();
        this.database = database;
        this.events = new ArrayList<>();
        this.listModel = new DefaultListModel<>();
        this.eventList = new JList<>(listModel);

        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());

        // Panel for displaying event list
        JScrollPane scrollPane = new JScrollPane(eventList);
        add(scrollPane, BorderLayout.CENTER);

        // Add ActionListener to event list for handling clicks on events
        eventList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                JList<Event> list = (JList<Event>) evt.getSource();
                if (evt.getClickCount() == 2) {
                    int x = evt.getX(); // Get x position
                    int y = evt.getY();
                    // Double-click detected
                    int index = list.locationToIndex(evt.getPoint());
                    Event selectedEvent = listModel.getElementAt(index);
                    // Show popup for editing the selected event
                    showEditPopup(selectedEvent,x,y);
                }
            }
        });

        // Add button panel for adding new event
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("Add Event");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                 
                // Implement logic for adding new event
                addNewEvent(connection);
            }


        });
        buttonPanel.add(addButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load events from the database initially
        loadEventsFromDatabase();
    }

    // Method to load events from the database and populate the list
    private void loadEventsFromDatabase() {
        events.clear();
        listModel.clear();

        // Fetch events from the database
        List<Event> dbEvents = database.getAllEvents();
        System.out.println(dbEvents);
        // Add fetched events to the list
        for (Event event : dbEvents) {
            events.add(event);
            listModel.addElement(event);
        }
    }

    // Method to add a new event to the list and database
    private void addNewEvent(Connection connection) {
        // Implement logic for adding a new event
        // For example, show a dialog to input event details
        // JOptionPane.showMessageDialog(this, "Adding a new event...");
        NewEventForm form = new NewEventForm(connection);
        form.setVisible(true);    }

    // Method to show a popup for editing the selected event
    private void showEditPopup(Event event,int x, int y) {
        int event_id=event.getEventId();
        // Implement logic for editing the event in a popup window
        EventPopupMenu popupMenu=new EventPopupMenu(event_id,this.getConnection());
        popupMenu.show(EventManagementPanel.this, x, y);
        // JOptionPane.showMessageDialog(this, "Editing event: " + event.getEventType());
    }
}
