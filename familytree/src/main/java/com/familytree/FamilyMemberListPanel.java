package com.familytree;
import com.familytree.data.entities.FamilyMember;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.util.List;

/**
* Represents panel for displaying list of family members and providing user interaction
*/
public class FamilyMemberListPanel extends JPanel {

    private final DefaultListModel<FamilyMember> listModel;
    private final JList<FamilyMember> memberList;
    private final JTextField searchField; 
    Connection connection;

    
    /** 
     * @return connection
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * sets connection in regards to family member list
     * 
     * @param connection
     */
    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    /**
     * Constructor for family member list panel
     * 
     * @param database
     */
    public FamilyMemberListPanel(FamilyDatabase database) {
        this.setConnection(database.getConnection());
        this.listModel = new DefaultListModel<>();
        this.memberList = new JList<>(listModel);
        this.searchField = new JTextField(20);
        initialize(this.connection);
    }

    /**
     * displays updated family member form which includes new changes
     */
    public static void displayNewFamilyMemberForm(Connection connection) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    NewFamilyMemberForm frame = new NewFamilyMemberForm(connection);
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void initialize(Connection connection) {
        setLayout(new BorderLayout());

        // Panel for search bar
        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        add(searchPanel, BorderLayout.NORTH);

        // Panel for displaying family member list
        JScrollPane scrollPane = new JScrollPane(memberList);

        
        // Add button panel for adding new event
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("Add Family Member");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayNewFamilyMemberForm(connection);
                // Implement logic for adding new event
            }


        });
        buttonPanel.add(addButton);
        add(buttonPanel, BorderLayout.SOUTH);

        add(scrollPane, BorderLayout.CENTER);

        // Fetch and display all family members initially
        updateFamilyMemberList();

        // Add ActionListener to search field for dynamically updating the list
        searchField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateFamilyMemberList();
            }
        });
        
        // Add MouseListener to the list for handling clicks on family members
        memberList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                JList<FamilyMember> list = (JList<FamilyMember>) evt.getSource();
                if (evt.getClickCount() == 2) {
                    // Double-click detected
                    int x = evt.getX(); // Get x position
                    int y = evt.getY();
                    int index = list.locationToIndex(evt.getPoint());
                    FamilyMember selectedMember = listModel.getElementAt(index);
                    // Show popup for editing the selected family member
                    System.out.println("Edit:"+selectedMember.getName());
                    showPopupMenu(selectedMember,x,y);
                }
            }
        });




    }

    // Method to update the family member list based on the search field
    private void updateFamilyMemberList() {
        String searchTerm = searchField.getText().trim();
        List<FamilyMember> familyMembers = FamilyDatabase.searchFamilyMembers(searchTerm);
        listModel.clear();
        for (FamilyMember member : familyMembers) {
            listModel.addElement(member);
        }
    }
        // Method to show a popup for editing the selected family member

    private void showPopupMenu(FamilyMember memberId,int x, int y) {
        FamilyMemberPopupMenu popupMenu = new FamilyMemberPopupMenu( memberId.getId(), connection);
        popupMenu.show(FamilyMemberListPanel.this, x, y);
    }

}
