package com.familytree;
import com.familytree.data.access.FamilyDataAccess;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.renderer.category.GanttRenderer;
import org.jfree.data.category.IntervalCategoryDataset;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class GanttChartPanel extends JPanel {

    private static final String ERROR_FAILED_TO_RETRIEVE = "Failed to retrieve all family members, please try again later" ;
    private final FamilyDatabase database;
    private final FamilyDataAccess familyDataAccess;

    public GanttChartPanel(FamilyDatabase database) {
        this.database = database;
        this.familyDataAccess = new FamilyDataAccess(FamilyDatabase.getConnection());
        try {
            initialize();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, ERROR_FAILED_TO_RETRIEVE, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initialize() throws SQLException {
        setLayout(new BorderLayout());

        // Retrieve all family members sorted by birthday
        var members = familyDataAccess.getAllSortedByBirthday();

        // Create a task series for the Gantt chart
        TaskSeriesCollection dataset = getTaskSeriesCollection(members);

        // Create the Gantt chart
        JFreeChart chart = ChartFactory.createGanttChart(
                "Deceased Family Members Gantt Chart", // Chart title
                "Members", // X-axis label
                "Time", // Y-axis label
                dataset, // Dataset
                true, // Include legend
                true, // Include tooltips
                false // Include URLs
        );


        // Create a chart panel to display the chart
        ChartPanel chartPanel = new ChartPanel(chart);
        add(chartPanel, BorderLayout.CENTER);
    }

    private static TaskSeriesCollection getTaskSeriesCollection(List<FamilyMember> members) {
        TaskSeries deceaseSeries = new TaskSeries("Deceased Family Members");
        TaskSeries aliveSeries = new TaskSeries("Alive Family Members");
        for (FamilyMember member : members) {
            // Create a task for each deceased family member
            var endDate = member.getDeathDate() == null ? new Date() : member.getDeathDate();
            Task task = new Task(member.getName(), member.getBirthDate(), endDate);
            if (member.getDeathDate() == null) {
                aliveSeries.add(task);
            } else {
                deceaseSeries.add(task);
            }
        }

        // Create a dataset for the Gantt chart
        TaskSeriesCollection dataset = new TaskSeriesCollection();
        dataset.add(aliveSeries);
        dataset.add(deceaseSeries);
        return dataset;
    }

}
