package com.familytree;

import com.familytree.data.entities.FamilyMember;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class FamilyTreePanel extends JPanel {
    private FamilyMember rootMember;

    /**
     * Constructor for family tree panel, uses specified member as root
     * 
     * @param rootMember member of family to be designated as root node of tree
     */
    public FamilyTreePanel(FamilyMember rootMember) {
        this.rootMember = rootMember;
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());
        
        // Add your custom drawing logic here to display the family tree graphically
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                // Handle mouse clicks for interacting with family members
                // For example, show information about the clicked family member
                // You can also implement adding/editing family members on click
            }
        });
    }

    
    /** 
     * paints specific component onto family tree GUI
     * 
     * @param g
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawFamilyTree(g, rootMember, getWidth() / 2, 50, true);
    }

    private void drawFamilyTree(Graphics g, FamilyMember member, int x, int y, boolean isRoot) {
        // Implement drawing logic for displaying family members on the panel
        // Consider drawing lines to represent connections between family members
        // Adjust x and y coordinates for positioning family members on the panel
        // Recursively draw children and other family members if applicable
    }
}
