package com.familytree;
import com.familytree.data.entities.FamilyMember;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;


public class CustomFamilyTreePanel extends JPanel {
    private final ArrayList<ArrayList<Node>> nodes;
    private static final int HORIZONTAL_GAP = 150;
    private static final int VERTICAL_GAP = 100;
    private static final int PADDING_LEFT = 1000;
    private static final int PADDING_TOP = 50;
    Connection connection;

    
    /** 
     * @return Connection
     */
    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public CustomFamilyTreePanel(List<List<Node>> initialNodes,Connection connection) {
        this.setConnection(connection);
        nodes = new ArrayList<>();

        // Load nodes into their respective layers
        for (List<Node> layer : initialNodes) {
            ArrayList<Node> copiedLayer = new ArrayList<>();
            for (Node node : layer) {
                Node copiedNode = new Node(node.getName());
                copiedNode.setMember(node.getMember());
                copiedLayer.add(copiedNode);
            }
            nodes.add(copiedLayer);
        }

        // Establish connections between nodes based on family relationships
        establishConnections();

        // Set JPanel properties, calculate positions, adjust panel size, and add nodes
        // to JPanel
        setLayout(null);
        calculateNodePositions();
        adjustPanelSize();
        addNodesToPanel();
    }

    // Helper method to find a node by its ID
    private Node findNodeById(int id) {
        for (ArrayList<Node> layer : nodes) {
            for (Node node : layer) {
                if (node.getMember().getId() == id) {
                    return node;
                }
            }
        }
        System.out.println("Node with ID " + id + " not found.");
        return null; // Return null if node with the given ID is not found
    }

    // Establish connections between nodes based on family relationships
   // Establish connections between nodes based on family relationships
private void establishConnections() {
    for (ArrayList<Node> layer : nodes) {
        for (Node node : layer) {
            FamilyMember member = node.getMember();
            for (int childId : member.getChildren()) {
                Node childNode = findNodeById(childId); // Find the corresponding child Node
                if (childNode != null) {
                    node.addConnection(childNode); // Connect parent Node to child Node
                }
            }
            for (int parentId : member.getParents()) {
                Node parentNode = findNodeById(parentId); // Find the corresponding parent Node
                if (parentNode != null && !parentNode.getConnections().contains(node)) {
                    parentNode.addConnection(node); // Connect child Node to parent Node
                }
            }
            int spouseId = member.getSpouse();
            if (spouseId != -1) {
                Node spouseNode = findNodeById(spouseId); // Find the corresponding spouse Node
                if (spouseNode != null) {
                    node.addConnection(spouseNode); // Connect current Node to spouse Node
                }
            }
        }
    }
}

    private void calculateNodePositions() {
        int xStart = PADDING_LEFT;
        int yStart = PADDING_TOP;
        for (ArrayList<Node> level : nodes) {
            int maxNodes = level.size();
            int totalWidth = maxNodes * HORIZONTAL_GAP;
            int x = xStart + (getWidth() - totalWidth) / 2;
            int y = yStart;
            for (Node node : level) {
                node.setX(x);
                node.setY(y);
                x += HORIZONTAL_GAP;
            }
            yStart += VERTICAL_GAP;
        }
    }

    private void adjustPanelSize() {
        int maxWidth = 0;
        int totalHeight = (nodes.size() - 1) * VERTICAL_GAP + PADDING_TOP;
        for (ArrayList<Node> level : nodes) {
            int levelWidth = level.size() * HORIZONTAL_GAP;
            if (levelWidth > maxWidth) {
                maxWidth = levelWidth;
            }
        }
        setPreferredSize(new Dimension(maxWidth + 2 * PADDING_LEFT, totalHeight));
    }

    private void addNodesToPanel() {
        for (ArrayList<Node> level : nodes) {
            for (Node node : level) {
                JButton button = createButton(node.getName(), node);
                button.setBounds(node.getX(), node.getY(), 100, 30);
                add(button);
            }
        }
    }

    private JButton createButton(String nodeName,Node node) {
        JButton button = new FamilyMemberButton( node,this.getConnection());


        // button.addActionListener(e -> {
        //     // Show node-specific popup here
        //     JOptionPane.showMessageDialog(CustomFamilyTreePanel.this, "Popup for " + nodeName);
        // });
        return button;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw lines connecting nodes within the same layer
        for (ArrayList<Node> layer : nodes) {
            for (Node node : layer) {
                int x1 = node.getX() + 50;
                int y1 = node.getY() + 15;
                for (Node connectedNode : node.getConnections()) {
                    int x2 = connectedNode.getX() + 50;
                    int y2 = connectedNode.getY();
                    g.drawLine(x1, y1, x2, y2);
                }
            }
        }
    }

    public static void main(String[] args) {
        // Example usage:
        List<List<Node>> initialNodes = new ArrayList<>();
        // Populate initialNodes with your data here...

        JFrame frame = new JFrame("Custom Family Tree");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create scroll pane
        // JScrollPane scrollPane = new JScrollPane(new CustomFamilyTreePanel(initialNodes));
        // scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        // scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // frame.getContentPane().add(scrollPane);
        // frame.pack();
        // frame.setLocationRelativeTo(null); // Center the JFrame on the screen
        // frame.setVisible(true);
    }
}
