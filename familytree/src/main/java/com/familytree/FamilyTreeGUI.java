package com.familytree;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;

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
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class FamilyTreeGUI extends JFrame {
    FamilyDatabase db = new FamilyDatabase();
    FamilyTreeContainer TreeContainer;

    public FamilyTreeGUI() {

        TreeContainer = new FamilyTreeContainer(db.getAllFamilyMembers(), db.getRelationships());
        setTitle("Family Tree Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);

        JTabbedPane tabbedPane = new JTabbedPane();

        JScrollPane familyTreePanel = createFamilyTreePanel(this.db.getConnection());
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

    public ArrayList<ArrayList<FamilyMember>> generateFamilyTreeRows(HashMap<Integer, FamilyMember> members) {
        ArrayList<ArrayList<FamilyMember>> familyTreeRows = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();

        for (FamilyMember member : members.values()) {
            if (!visited.contains(member.getId())) {
                ArrayList<FamilyMember> row = new ArrayList<>();
                traverseFamilyTree(member, members, visited, row);
                familyTreeRows.add(row);
            }
        }

        return familyTreeRows;
    }

    private void traverseFamilyTree(FamilyMember member, HashMap<Integer, FamilyMember> members,
            Set<Integer> visited, ArrayList<FamilyMember> row) {
        if (member == null || visited.contains(member.getId())) {
            return;
        }

        row.add(member);
        visited.add(member.getId());

        for (Integer childId : member.getChildren()) {
            FamilyMember child = members.get(childId);
            traverseFamilyTree(child, members, visited, row);
        }
    }

    // Helper method to check if a parent is in the row
    private static boolean isParentInRow(FamilyMember parent, ArrayList<FamilyMember> row) {
        for (FamilyMember member : row) {
            if (parent.getParents().contains(member.getId())) {
                return true;
            }
        }
        return false;
    }

    public static void printFamilyTree(ArrayList<ArrayList<FamilyMember>> familyTreeRows) {
        for (ArrayList<FamilyMember> row : familyTreeRows) {
            for (FamilyMember member : row) {
                System.out.print(member.getName() + " ");
            }
            System.out.println(); // Move to the next row
        }
    }

    private static ArrayList<ArrayList<FamilyMember>> adjustSpouse(ArrayList<ArrayList<FamilyMember>> familyTreeRows,
            HashMap<Integer, FamilyMember> members) {
        for (int i = familyTreeRows.size() - 1; i >= 0; i--) {
            ArrayList<FamilyMember> row = familyTreeRows.get(i);
            for (int j = 0; j < row.size(); j++) {
                FamilyMember member = row.get(j);
                int spouseId = member.getSpouse();
                if (spouseId != -1) {
                    for (int k = i - 1; k >= 0; k--) {
                        ArrayList<FamilyMember> prevRow = familyTreeRows.get(k);
                        boolean foundSpouse = false;
                        for (FamilyMember prevMember : prevRow) {
                            if (prevMember.getId() == spouseId) {
                                // Move the member to the previous row
                                prevRow.add(member);
                                row.remove(j);
                                foundSpouse = true;
                                break;
                            }
                        }
                        if (foundSpouse) {
                            break; // Stop traversing up if spouse is found
                        }
                    }
                }
            }
        }
        return familyTreeRows;
    }

    private static ArrayList<ArrayList<FamilyMember>> removeEmptyArrays(
            ArrayList<ArrayList<FamilyMember>> familyTreeRows) {
        ArrayList<ArrayList<FamilyMember>> nonEmptyRows = new ArrayList<>();
        for (ArrayList<FamilyMember> row : familyTreeRows) {
            if (!row.isEmpty()) {
                nonEmptyRows.add(row);
            }
        }
        return nonEmptyRows;
    }

    public static ArrayList<ArrayList<FamilyMember>> moveFamilyMember(ArrayList<ArrayList<FamilyMember>> familyGrid,
            int x, int y) {
        // Check if coordinates are valid
        if (x >= 0 && x <= familyGrid.size() && y >= 0 && y <= familyGrid.get(x - 1).size()) {

            System.out.println("x:" + x + "y" + y);
            FamilyMember memberToMove = familyGrid.get(x - 1).get(y - 1);

            familyGrid.get(x - 1).set(y - 1, null);

            // Move to the end of the previous ArrayList if not in the first row
            if (x > 0) {
                System.out.println("memberToMove:" + memberToMove.getName());
                familyGrid.get(x - 2).add(memberToMove);
            } else {
                System.out.println("Cannot move to the previous row, already at the first row.");
            }
        } else {
            System.out.println("Invalid coordinates.");
            System.out.println("size" + familyGrid.size());
            System.out.println("lenght" + familyGrid.get(x - 1).size());

        }
        return familyGrid;
    }

    private static ArrayList<ArrayList<FamilyMember>> adjustParents(ArrayList<ArrayList<FamilyMember>> familyTreeRows,
            HashMap<Integer, FamilyMember> members) {
        for (int x = 1; x < familyTreeRows.size(); x++) {
            for (int y = 0; y < familyTreeRows.get(x).size(); y++) {
                Boolean found = false;
                // familyTreeRows =removeNullValues(familyTreeRows);
                // familyTreeRows = removeEmptyArrays(familyTreeRows);
                // familyTreeRows = adjustSpouse(familyTreeRows, members);

                if (familyTreeRows.get(x).get(y).getParents().contains(-5000)) {
                    // System.out.println("No parents."+familyTreeRows.get(x).get(y).getName());
                } else {
                    for (int q = 0; q < familyTreeRows.get(x - 1).size(); q++) {
                        familyTreeRows = removeNullValues(familyTreeRows);
                        familyTreeRows = removeEmptyArrays(familyTreeRows);
                        familyTreeRows = adjustSpouse(familyTreeRows, members);

                        // System.out.println("getting checking during:" +
                        // familyTreeRows.get(x).get(y).getName() + q);
                        int checking = familyTreeRows.get(x - 1).get(q).getId();
                        // System.out.println("got checking");

                        if (familyTreeRows.get(x).get(y).getParents().contains(checking)) {
                            found = true;
                            System.out.println("found");
                        }
                    }
                    if (found == false) {
                        moveFamilyMember(familyTreeRows, x + 1, y + 1);
                        System.out.println(" moved.to:");
                        y = -1;
                        x = 1;

                    }

                }

            }

        }

        return familyTreeRows;
    }

    private static ArrayList<ArrayList<FamilyMember>> removeNullValues(
            ArrayList<ArrayList<FamilyMember>> familyTreeRows) {
        for (ArrayList<FamilyMember> row : familyTreeRows) {
            row.removeAll(Collections.singleton(null));
        }
        return familyTreeRows;

    }

    public List<List<Node>> createNodeshelf(ArrayList<ArrayList<FamilyMember>> familyTreeRows,
            HashMap<Integer, FamilyMember> members, Connection connection) {
        List<List<Node>> myList = new ArrayList<>();
        ArrayList<ArrayList<Node>> nodeList = new ArrayList<>();

        HashMap<Integer, Node> nodesMap = new HashMap<>();

        // Loop through the members HashMap and generate Node objects
        for (Integer memberId : members.keySet()) {
            FamilyMember familyMember = members.get(memberId);
            if (familyMember.getChildren().contains(-5000) || familyMember.getParents().contains(-5000)) {
                familyMember.processRelationships(connection);

            }

            Node node = new Node(familyMember.getName());
            // Assuming you have methods getX(), getY(), isCouple(), etc. in FamilyMember
            // Assuming you have a setter method setMember() in Node
            node.setMember(familyMember);
            // Add the Node to the nodesMap
            nodesMap.put(memberId, node);
        }
        for (Integer nodeId : nodesMap.keySet()) {
            ArrayList children = nodesMap.get(nodeId).getMember().getChildren();
            for (int x = 0; x < children.size(); x++) {
                nodesMap.get(nodeId).addConnection(nodesMap.get(x));

            }
            ArrayList parents = nodesMap.get(nodeId).getMember().getChildren();
            for (int x = 0; x < parents.size(); x++) {
                nodesMap.get(nodeId).addConnection(nodesMap.get(x));

            }
        }
        for (ArrayList<FamilyMember> row : familyTreeRows) {
            ArrayList<Node> nodeRow = new ArrayList<>();
            for (FamilyMember member : row) {
                int memberId = member.getId();
                if (nodesMap.containsKey(memberId)) {
                    Node node = nodesMap.get(memberId);
                    nodeRow.add(node);
                }
            }
            nodeList.add(nodeRow);
        }
        myList = new ArrayList<>(nodeList);

        return myList;
    }

    public static int findRowAbove(ArrayList<ArrayList<FamilyMember>> familyTreeRows, int rowIndex,
            final int memberId) {
        // Iterate through rows from the specified row index - 1 (going upwards)
        for (int i = rowIndex - 1; i >= 0; i--) {
            // Get the current row
            ArrayList<FamilyMember> row = familyTreeRows.get(i);
            // Iterate through family members in the current row
            for (FamilyMember member : row) {
                // Check if the member's IDs match the specified member's ID
                if (member.getParents().contains(memberId)) {
                    // Return the index of the row where the matching member was found
                    System.out.println(
                            "Returning:" + i + " from " + member.getName() + " with array: " + member.getParents());
                    return i;
                }
            }
        }
        // If no match is found, return -1
        return -1;
    }

    // moveFamilyMember(ArrayList<ArrayList<FamilyMember>> familyGrid,int x, int y)

    public ArrayList<ArrayList<FamilyMember>> generateFamilyTree(HashMap<Integer, FamilyMember> familyMap) {
        ArrayList<ArrayList<FamilyMember>> familyTreeLayers = new ArrayList<>();

        // First, identify individuals without parents
        ArrayList<FamilyMember> topLevelMembers = new ArrayList<>();
        for (FamilyMember member : familyMap.values()) {
            if (member.getParents().isEmpty()) {
                topLevelMembers.add(member);
            }
        }
        familyTreeLayers.add(topLevelMembers);

        // Iterate through each layer of the family tree
        int layerIndex = 0;
        while (layerIndex < familyTreeLayers.size()) {
            ArrayList<FamilyMember> currentLayer = familyTreeLayers.get(layerIndex);
            ArrayList<FamilyMember> nextLayer = new ArrayList<>();
            // Iterate through each member in the current layer
            for (FamilyMember member : currentLayer) {
                // Add children of the current member to the next layer
                for (int childId : member.getChildren()) {
                    FamilyMember childMember = familyMap.get(childId);
                    boolean alreadyAdded = false;
                    // Check if the child has been added to a previous layer
                    for (int i = 0; i < familyTreeLayers.size(); i++) {
                        ArrayList<FamilyMember> layer = familyTreeLayers.get(i);
                        for (FamilyMember existingMember : layer) {
                            if (existingMember.getId() == childMember.getId()) {
                                alreadyAdded = true;
                                break;
                            }
                        }
                        if (alreadyAdded) {
                            break;
                        }
                    }
                    // If the child hasn't been added before, add it to the next layer
                    if (!alreadyAdded) {
                        nextLayer.add(childMember);
                    }
                }
            }
            // Add the next layer if it's not empty
            if (!nextLayer.isEmpty()) {
                familyTreeLayers.add(nextLayer);
            }
            layerIndex++;
        }

        return familyTreeLayers;
    }

    public ArrayList<ArrayList<FamilyMember>> organizeFamilyTreeRows(
            ArrayList<ArrayList<FamilyMember>> familyTreeRows) {
        // Sort each row based on generation
        for (ArrayList<FamilyMember> row : familyTreeRows) {
            Collections.sort(row, Comparator.comparingInt(FamilyMember::getStackLayer));
        }

        // Iterate through each row and organize spouses
        for (int i = 0; i < familyTreeRows.size(); i++) {
            ArrayList<FamilyMember> row = familyTreeRows.get(i);
            HashMap<Integer, FamilyMember> spouseMap = new HashMap<>();

            // Group spouses by ID
            for (FamilyMember member : row) {
                if (member.getSpouse() != -1) {
                    spouseMap.put(member.getSpouse(), member);
                }
            }

            // Find spouses with higher placement
            for (Map.Entry<Integer, FamilyMember> entry : spouseMap.entrySet()) {
                FamilyMember spouse = entry.getValue();
                int spouseIndex = row.indexOf(spouse);
                if (spouseIndex != -1) {
                    ArrayList<FamilyMember> higherGenerationRow = findHigherGenerationRow(familyTreeRows, spouseIndex,
                            i);
                    if (higherGenerationRow != null) {
                        // Move spouse to the higher generation row
                        row.remove(spouse);
                        higherGenerationRow.add(spouse);
                    }
                }
            }
        }

        return familyTreeRows;
    }

    private ArrayList<FamilyMember> findHigherGenerationRow(ArrayList<ArrayList<FamilyMember>> familyTreeRows,
            int spouseIndex, int currentRowIndex) {
        // Start from the row above the current row
        for (int i = currentRowIndex - 1; i >= 0; i--) {
            ArrayList<FamilyMember> higherGenerationRow = familyTreeRows.get(i);
            if (!higherGenerationRow.isEmpty()) {
                return higherGenerationRow;
            }
        }
        return null;
    }

    private static ArrayList<ArrayList<FamilyMember>> buildFamilyTree(HashMap<Integer, FamilyMember> members) {
        ArrayList<ArrayList<FamilyMember>> familyTree = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();

        // Identify individuals without children and place them in the bottom row
        ArrayList<FamilyMember> bottomRow = new ArrayList<>();
        for (FamilyMember member : members.values()) {
            if (member.getChildren().contains(-5000)) {
                bottomRow.add(member);
                visited.add(member.getId());
            }
        }
        familyTree.add(bottomRow);

        // Build the rest of the family tree
        while (true) {
            ArrayList<FamilyMember> currentRow = new ArrayList<>();

            // Place parents in the row above their children
            for (FamilyMember parent : familyTree.get(familyTree.size() - 1)) {
                for (int parentId : parent.getParents()) {
                    if (!visited.contains(parentId)) {
                        FamilyMember parentMember = members.get(parentId);
                        if (parentMember != null) {
                            currentRow.add(parentMember);
                            visited.add(parentId);
                        }
                    }
                }
            }

            // If there are no more individuals to add, break out of the loop
            if (currentRow.isEmpty()) {
                break;
            }

            // Add the current row to the family tree
            familyTree.add(currentRow);
        }

        // Reverse the family tree so that the top row is at index 0
        Collections.reverse(familyTree);

        return familyTree;
    }

    public ArrayList<ArrayList<FamilyMember>> recurse(ArrayList<ArrayList<FamilyMember>> familyTreeRows){





        return familyTreeRows;



    }

    public static ArrayList<ArrayList<FamilyMember>> moveFamilyMembers(ArrayList<ArrayList<FamilyMember>> familyGrid, int x, int y) {
        if (x < 1 || y < 1 || x > familyGrid.size() || y > familyGrid.get(x - 1).size()) {
            System.out.println("Invalid coordinates.");
            return familyGrid;
        }

        FamilyMember currentMember = familyGrid.get(x - 1).get(y - 1);
        moveFamilyMemberRecursive(familyGrid, x, y, currentMember);
        return familyGrid;
    }

    private static void moveFamilyMemberRecursive(ArrayList<ArrayList<FamilyMember>> familyGrid, int x, int y, FamilyMember currentMember) {
        boolean moved = false; // Flag to track if any member is moved in this iteration
        do {
            moved = false; // Reset moved flag for each iteration
            if (x <= 1)
                return; // Base case: reached the first row, stop recursion

            ArrayList<FamilyMember> previousRow = familyGrid.get(x - 2);
            for (int i = 0; i < previousRow.size(); i++) {
                FamilyMember prevMember = previousRow.get(i);
                if (prevMember != null && prevMember.getParents().contains(currentMember.getId())) {
                    previousRow.set(i, null);
                    familyGrid.get(x - 2).add(prevMember);
                    moved = true; // Set moved flag to true since a member is moved
                    moveFamilyMemberRecursive(familyGrid, x - 1, i + 1, prevMember);
                    break; // Move only one member up in each iteration
                }
            }
        } while (moved); // Continue iterating until no more movements are made
    }

    @SuppressWarnings("unused")
    public JScrollPane createFamilyTreePanel(Connection db) {
        System.out.println(this.TreeContainer.getMembers().toString());
        HashMap<Integer, FamilyMember> members = this.TreeContainer.getMembers();

        ArrayList<ArrayList<FamilyMember>> familyTreeRows = buildFamilyTree(members);

        familyTreeRows = moveFamilyMembers(familyTreeRows, 3, 1);
        familyTreeRows = removeNullValues(familyTreeRows);
        familyTreeRows = removeEmptyArrays(familyTreeRows);
        familyTreeRows = adjustSpouse(familyTreeRows, members);

        if (true == false) {
            familyTreeRows = moveFamilyMember(familyTreeRows, 3, 2);
            familyTreeRows = removeNullValues(familyTreeRows);
            familyTreeRows = removeEmptyArrays(familyTreeRows);
            familyTreeRows = adjustSpouse(familyTreeRows, members);
            familyTreeRows = moveFamilyMember(familyTreeRows, 3, 2);
            familyTreeRows = removeNullValues(familyTreeRows);
            familyTreeRows = removeEmptyArrays(familyTreeRows);
            familyTreeRows = adjustSpouse(familyTreeRows, members);
            familyTreeRows = moveFamilyMember(familyTreeRows, 3, 2);
            familyTreeRows = removeNullValues(familyTreeRows);
            familyTreeRows = removeEmptyArrays(familyTreeRows);
            familyTreeRows = adjustSpouse(familyTreeRows, members);
        }

        // familyTreeRows=moveFamilyMembers(familyTreeRows);
        // familyTreeRows = adjustRows(familyTreeRows, members);
        // familyTreeRows = adjustSpouse(familyTreeRows, members);
        // // familyTreeRows = removeEmptyArrays(familyTreeRows);
        // familyTreeRows = adjustParents(familyTreeRows, members);

        // familyTreeRows = removeNullValues(familyTreeRows);
        // familyTreeRows = removeEmptyArrays(familyTreeRows);
        // int foundRowIndex = findRowAbove(familyTreeRows, 1, 4);
        // familyTreeRows = adjustChildren(familyTreeRows);
        // System.out.println("rw above:"+foundRowIndex);
        familyTreeRows = removeNullValues(familyTreeRows);
        familyTreeRows = removeEmptyArrays(familyTreeRows);
        // familyTreeRows = adjustSpouse(familyTreeRows, members);

        System.out.println(familyTreeRows.size());
        printFamilyTree(familyTreeRows);
        List<List<Node>> nodeStructure = createNodeshelf(familyTreeRows, members, db);
        CustomFamilyTreePanel panelTree = new CustomFamilyTreePanel(nodeStructure,this.db.getConnection());

        // Create a JScrollPane and add the custom tree panel to it
        JScrollPane scrollPane = new JScrollPane(panelTree);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // You may need to adjust the preferred size of the scroll pane if necessary
        // scrollPane.setPreferredSize(new Dimension(width, height));

        return scrollPane;
    }

    private JPanel createGanttChartPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // Retrieve all deceased family members
        ArrayList<FamilyMember> deceasedMembers = FamilyDatabase.getAllDeceasedFamilyMembers();

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
