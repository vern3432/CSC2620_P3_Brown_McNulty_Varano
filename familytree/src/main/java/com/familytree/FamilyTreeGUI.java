package com.familytree;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.IntervalCategoryDataset;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import java.awt.event.*;
import java.io.File;
import java.util.List;
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

        JMenuItem importText = new JMenuItem("Import Text");
        importText.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Select Text File");
                int result = fileChooser.showOpenDialog(null);

                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    List<String[]> parsedData = TextFileParser.parseTextFile(selectedFile);
                    // FamilyDatabase.addParsedDataToDatabase(parsedData);
                    System.out.println("Data imported successfully.");
                }
            }
        });

        importText.setAccelerator(
           KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));
            fileMenu.add(importText);



        
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

    @Override
    public void paint(Graphics arg0) {
        // TODO Auto-generated method stub
        super.paint(arg0);
    }
    private JPanel createGanttChartPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // Retrieve all deceased family members
        ArrayList<FamilyMember> deceasedMembers = db.getAllDeceasedFamilyMembers();

        // Create a task series for the Gantt chart
        TaskSeries taskSeries = new TaskSeries("Deceased Family Members");
        for (FamilyMember member : deceasedMembers) {
            // Create a task for each deceased family member
            Task task = new Task(member.getName(), member.getDeathDate(), member.getDeathDate());
            taskSeries.add(task);
        }

        // Create a dataset for the Gantt chart
        TaskSeriesCollection dataset = new TaskSeriesCollection();
        dataset.add(taskSeries);

        // Create the Gantt chart
        JFreeChart chart = ChartFactory.createGanttChart(
                "Deceased Family Members Gantt Chart", // Chart title
                "Members", // X-axis label
                "Time", // Y-axis label
                dataset, // Dataset
                false, // Include legend
                true, // Include tooltips
                false // Include URLs
        );

        // Customize chart appearance 
        // Create a chart panel to display the chart
        ChartPanel chartPanel = new ChartPanel(chart);
        panel.add(chartPanel, BorderLayout.CENTER);

        return panel;
    }

    

    public JPanel createFamilyMemberListPanel() {
        JPanel panel = new JPanel(new BorderLayout());
   // Initialize your database object here
        FamilyMemberListPanel familyMemberListPanel = new FamilyMemberListPanel(this.db);
        panel.add(familyMemberListPanel, BorderLayout.CENTER);
        return panel;
    }

    public JPanel createEventManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        // Initialize your database object here
        FamilyDatabase database = new FamilyDatabase(); // Example initialization
        EventManagementPanel eventManagementPanel = new EventManagementPanel(database);
        panel.add(eventManagementPanel, BorderLayout.CENTER);
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FamilyTreeGUI());
    }
}
