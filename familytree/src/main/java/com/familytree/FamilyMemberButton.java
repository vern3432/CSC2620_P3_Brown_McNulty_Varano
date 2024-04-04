package com.familytree;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;


/**
 * Represents button associated with a member in the family tree
 */
public class FamilyMemberButton extends JButton {
    private int memberId;
    private Connection connection;

    /**
     * Constructs a new FamilyMemberButton with the specified node and database connection
     * 
     * @param node representing family member
     * @param connection database connection object
     */
    public FamilyMemberButton(Node node,Connection connection) {
        this.connection = connection;
        this.memberId=node.getMember().getId();

        setText(node.getName());

        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showPopupMenu();
            }
        });
    }

    private void showPopupMenu() {
        FamilyMemberPopupMenu popupMenu = new FamilyMemberPopupMenu(memberId, connection);
        popupMenu.show(FamilyMemberButton.this, 0, getHeight());
    }
}
