package com.familytree;

import com.familytree.views.FamilyMemberPopupMenu;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;

public class FamilyMemberButton extends JButton {
    private int memberId;
    private Connection connection;

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
