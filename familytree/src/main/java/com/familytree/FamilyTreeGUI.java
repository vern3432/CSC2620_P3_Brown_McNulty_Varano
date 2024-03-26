package com.familytree;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class FamilyTreeGUI extends JFrame {
    FamilyDatabase db=new FamilyDatabase();


    public FamilyTreeGUI() {
        setTitle("Family Tree Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);

        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel familyTreePanel = createFamilyTreePanel();
        JPanel ganttChartPanel = createGanttChartPanel();
        JPanel familyMemberListPanel = createFamilyMemberListPanel();
        JPanel eventManagementPanel = createEventManagementPanel();

        tabbedPane.addTab("Family Tree", familyTreePanel);
        tabbedPane.addTab("Gantt Chart", ganttChartPanel);
        tabbedPane.addTab("Family Member List", familyMemberListPanel);
        tabbedPane.addTab("Event Management", eventManagementPanel);

        add(tabbedPane, BorderLayout.CENTER);

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenu optionMenu = new JMenu("Options");

        // Add file and option menu items

        menuBar.add(fileMenu);
        menuBar.add(optionMenu);

        setJMenuBar(menuBar);

        setVisible(true);
    }

    private JPanel createFamilyTreePanel() {
        JPanel panel = new JPanel();
        // Add components for displaying the family tree
        return panel;
    }

    private JPanel createGanttChartPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        ArrayList<FamilyMember> deceasedMembers = db.getAllDeceasedFamilyMembers();
        JList<FamilyMember> ganttChartList = new JList<>(deceasedMembers.toArray(new FamilyMember[0]));
        JScrollPane scrollPane = new JScrollPane(ganttChartList);
        panel.add(scrollPane, BorderLayout.CENTER);
    
        return panel;
    }
    

    private JPanel createFamilyMemberListPanel() {
        JPanel panel = new JPanel();
        // Add components for displaying and interacting with the family member list
        return panel;
    }

    private JPanel createEventManagementPanel() {
        JPanel panel = new JPanel();
        // Add components for managing events
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FamilyTreeGUI());
    }
}
