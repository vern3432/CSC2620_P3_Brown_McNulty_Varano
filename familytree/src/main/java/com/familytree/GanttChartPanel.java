package com.familytree;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.IntervalCategoryDataset;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GanttChartPanel extends JPanel {

    private final FamilyDatabase database;

    public GanttChartPanel(FamilyDatabase database) {
        this.database = database;
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());

        // Retrieve all deceased family members from the database
        List<FamilyMember> deceasedMembers = database.getAllDeceasedFamilyMembers();

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

        // Customize chart appearance if needed

        // Create a chart panel to display the chart
        ChartPanel chartPanel = new ChartPanel(chart);
        add(chartPanel, BorderLayout.CENTER);
    }

    // Method to create and return GanttChartPanel based on input database
    public static GanttChartPanel createGanttChartPanel(FamilyDatabase database) {
        return new GanttChartPanel(database);
    }
}
