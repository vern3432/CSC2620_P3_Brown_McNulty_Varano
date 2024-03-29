package com.familytree;
import javax.swing.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
public class CustomFamilyTreePanel extends JPanel {
    private ArrayList<ArrayList<Node>> nodes;
    private static final int HORIZONTAL_GAP = 150;
    private static final int VERTICAL_GAP = 100;
    private static final int PADDING_LEFT = 500;  
    private static final int PADDING_TOP = 50;
    FamilyMember member;

    public void setMember(FamilyMember member) {
        this.member = member;
    }

    public CustomFamilyTreePanel(List<List<Node>> initialNodes) {
        nodes = new ArrayList<>();

        // Set initial nodes
        for (List<Node> layer : initialNodes) {
            ArrayList<Node> level = new ArrayList<>();
            for (Node node : layer) {
                level.add(node);
            }
            nodes.add(level);
        }

        // Set JPanel properties
        setLayout(null); // Use absolute positioning

        // Calculate positions of nodes
        calculateNodePositions();

        // Adjust panel size
        adjustPanelSize();

        // Add nodes to JPanel
        addNodesToPanel();
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
                JButton button = createButton(node.getName());
                button.setBounds(node.getX(), node.getY(), 100, 30);
                add(button);
            }
        }
    }

    private JButton createButton(String nodeName) {
        JButton button = new JButton(nodeName);
        button.addActionListener(e -> {
            // Show node-specific popup here
            JOptionPane.showMessageDialog(CustomFamilyTreePanel.this, "Popup for " + nodeName);
        });
        return button;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw lines connecting nodes within the same layer
        for (int i = 0; i < nodes.size() - 1; i++) {
            ArrayList<Node> currentLevel = nodes.get(i);
            ArrayList<Node> nextLevel = nodes.get(i + 1);
            for (Node currentNode : currentLevel) {
                int x1 = currentNode.getConnections().isEmpty() ? currentNode.getX() + 50 : currentNode.getX() + 100;
                int y1 = currentNode.getY() + 15;
                for (Node nextNode : nextLevel) {
                    if(currentNode.getConnections().contains(nextNode)){
                        int x2 = nextNode.getX() + 50;
                        int y2 = nextNode.getY();
                        g.drawLine(x1, y1, x2, y2);

                    }

                }
            }
        }
    }

    public static void main(String[] args) {
        // Example usage:
        List<List<Node>> initialNodes = new ArrayList<>();
        List<Node> layer1 = new ArrayList<>();
        layer1.add(new Node("Node 1-1"));
        layer1.add(new Node("Node 1-2"));

        List<Node> layer2 = new ArrayList<>();
        Node node21 = new Node("Node 2-1");
        Node node22 = new Node("Node 2-2");
        Node node23 = new Node("Node 2-3");
        node21.addConnection(node22);
        layer2.add(node21);
        layer2.add(node22);
        layer2.add(node23);
        
        Node node31=new Node("Node 3-1");
        Node node32=new Node("Node 3-2");

        List<Node> layer3 = new ArrayList<>();
        layer3.add(node31);
        layer3.add(node32);


        node21.addConnection(node31);
        node21.addConnection(node32);

        initialNodes.add(layer1);

        initialNodes.add(layer2);

        initialNodes.add(layer3);

        JFrame frame = new JFrame("Custom Family Tree");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create scroll pane
        JScrollPane scrollPane = new JScrollPane(new CustomFamilyTreePanel(initialNodes));
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        frame.getContentPane().add(scrollPane);
        frame.pack();
        frame.setLocationRelativeTo(null); // Center the JFrame on the screen
        frame.setVisible(true);
    }
}