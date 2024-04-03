package com.familytree;

import com.familytree.data.entities.Client;
import com.familytree.data.entities.FamilyMember;
import com.familytree.listeners.ClearAllTablesListener;
import com.familytree.listeners.ImportFileListener;
import com.familytree.views.LoggedInPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.*;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;

public class FamilyTreeGUI extends JFrame {

    private final Connection connection;
    private final Client client;
    FamilyTreeContainer TreeContainer;
    FamilyDatabase db;

    public void Update() {
        Dimension previousSize = getSize();
        int selectedTabIndex = ((JTabbedPane) getContentPane().getComponent(1)).getSelectedIndex();

        Connection connection=this.connection;
        Client client=this.client;
        // Dispose of the current window
        dispose();
        // Create a new instance of FamilyTreeGUI
        FamilyTreeGUI New1=new FamilyTreeGUI(connection, client);
        New1.setSize(previousSize);

        JTabbedPane tabbedPane = (JTabbedPane) New1.getContentPane().getComponent(1);
        tabbedPane.setSelectedIndex(selectedTabIndex);

    }
    


    public FamilyTreeGUI(Connection connection, Client client) {
        this.client = client;
        this.connection = connection;
        FamilyDatabase.setConnection(connection);
        FamilyDatabase db=new FamilyDatabase();
        TreeContainer = new FamilyTreeContainer(db.getAllFamilyMembers(), db.getRelationships());
        setTitle("Family Tree Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 1000);
        add(new LoggedInPanel(client, this, connection), BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();

        JScrollPane familyTreePanel = createFamilyTreePanel(this.connection);
        JPanel ganttChartPanel = createGanttChartPanel(connection);
        JPanel familyMemberListPanel = createFamilyMemberListPanel(this.db);
        JPanel eventManagementPanel = createEventManagementPanel();

        tabbedPane.addTab("Family Tree", familyTreePanel);
        tabbedPane.addTab("Gantt Chart", ganttChartPanel);
        tabbedPane.addTab("Family Member List", familyMemberListPanel);
        tabbedPane.addTab("Event Management", eventManagementPanel);

        add(tabbedPane, BorderLayout.CENTER);

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");

        JMenuItem importText = new JMenuItem("Import Text");
        importText.addActionListener(new ImportFileListener(client, connection));

        importText.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));
        fileMenu.add(importText);

        JMenu optionMenu = new JMenu("Options");

        JMenuItem Refresh = new JMenuItem("Refresh");
        Refresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Call your function here
                Update();
            }
        });

        Refresh.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK));
        // Add file and option menu items


        optionMenu.add(Refresh);
        optionMenu.add(addClearAllOption());
        menuBar.add(fileMenu);
        menuBar.add(optionMenu);

        setJMenuBar(menuBar);

        setVisible(true);

    }

    private JMenuItem addClearAllOption() {
        var result = new JMenuItem("Clear All");
        result.addActionListener(new ClearAllTablesListener(client, connection));
        return result;
    }


    /** 
     * @param HashMap<Integer
     * @param members
     * @return ArrayList<ArrayList<FamilyMember>>
     */
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
        CustomFamilyTreePanel panelTree = new CustomFamilyTreePanel(nodeStructure,this.connection);

        // Create a JScrollPane and add the custom tree panel to it
        JScrollPane scrollPane = new JScrollPane(panelTree);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // You may need to adjust the preferred size of the scroll pane if necessary
        // scrollPane.setPreferredSize(new Dimension(width, height));

        return scrollPane;
    }

    private JPanel createGanttChartPanel(Connection connection) {
        return new GanttChartPanel(connection);
    }

    public JPanel createFamilyMemberListPanel(FamilyDatabase db) {
        JPanel panel = new JPanel(new BorderLayout());
        // Initialize your database object here
        FamilyMemberListPanel familyMemberListPanel = new FamilyMemberListPanel(db);
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
}
