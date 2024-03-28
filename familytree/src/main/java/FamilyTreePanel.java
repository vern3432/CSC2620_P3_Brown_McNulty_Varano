import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class FamilyTreePanel extends JPanel {
    private ArrayList<ArrayList<JButton>> nodes;

    public FamilyTreePanel() {
        nodes = new ArrayList<>();

        // Example: Creating some sample nodes
        int numLayers = 10;
        int numNodesPerLayer = 5;

        for (int i = 0; i < numLayers; i++) {
            ArrayList<JButton> level = new ArrayList<>();
            for (int j = 0; j < numNodesPerLayer; j++) {
                level.add(createNode("Node " + (i + 1) + "-" + (j + 1)));
            }
            nodes.add(level);
        }

        // Set JPanel properties
        setLayout(null); // Use absolute positioning

        // Add nodes to JPanel and calculate positions
        int y = 50;
        int maxWidth = 0;
        for (int i = 0; i < nodes.size(); i++) {
            ArrayList<JButton> level = nodes.get(i);
            int x = 50;
            for (int j = 0; j < level.size(); j++) {
                JButton node = level.get(j);
                node.setBounds(x, y, 100, 30);
                x += 150;
                add(node);
            }
            y += 100;
            int width = x - 100;
            if (width > maxWidth) {
                maxWidth = width;
            }
        }

        // Set preferred size to accommodate the entire graph
        setPreferredSize(new Dimension(maxWidth + 100, y + 50));
    }

    private JButton createNode(String nodeName) {
        JButton node = new JButton(nodeName);
        node.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Show node-specific popup here
                JOptionPane.showMessageDialog(FamilyTreePanel.this, "Popup for " + nodeName);
            }
        });
        return node;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw lines connecting nodes within the same layer
        for (int i = 0; i < nodes.size() - 1; i++) {
            ArrayList<JButton> currentLevel = nodes.get(i);
            ArrayList<JButton> nextLevel = nodes.get(i + 1);
            for (JButton currentNode : currentLevel) {
                int x1 = currentNode.getX() + currentNode.getWidth() / 2;
                int y1 = currentNode.getY() + currentNode.getHeight();
                for (JButton nextNode : nextLevel) {
                    int x2 = nextNode.getX() + nextNode.getWidth() / 2;
                    int y2 = nextNode.getY();
                    g.drawLine(x1, y1, x2, y2);
                }
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Family Tree");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create scroll pane
        JScrollPane scrollPane = new JScrollPane(new FamilyTreePanel());
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        frame.getContentPane().add(scrollPane);
        frame.pack();
        frame.setLocationRelativeTo(null); // Center the JFrame on the screen
        frame.setVisible(true);
    }
}
