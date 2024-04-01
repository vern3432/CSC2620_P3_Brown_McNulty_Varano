package com.familytree;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PopupMenuExample {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Popup Menu Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        JButton button = new JButton("Show Popup Menu");
        panel.add(button);

        frame.add(panel);
        frame.pack();
        frame.setVisible(true);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPopupMenu popupMenu = new JPopupMenu();
                String[] items = {"Item 1", "Item 2", "Item 3", "Item 4"};
                for (String item : items) {
                    JMenuItem menuItem = new JMenuItem(item);
                    menuItem.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            String selectedValue = ((JMenuItem) e.getSource()).getText();
                            System.out.println("Selected value: " + selectedValue);
                        }
                    });
                    popupMenu.add(menuItem);
                }

                JButton submitButton = new JButton("Submit");
                submitButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // Code to handle submit button action
                        // For demonstration purposes, let's close the popup menu
                        popupMenu.setVisible(false);
                    }
                });
                popupMenu.add(submitButton);

                // Display the popup menu at the location of the button
                popupMenu.show(button, 0, button.getHeight());
            }
        });
    }
}
