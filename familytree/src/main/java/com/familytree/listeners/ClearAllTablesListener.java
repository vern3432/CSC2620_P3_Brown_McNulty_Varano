package com.familytree.listeners;

import com.familytree.data.entities.Client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class ClearAllTablesListener implements ActionListener {

    private final Connection connection;
    private final Client client;

    public ClearAllTablesListener(Client client, Connection connection) {
        this.connection = connection;
        this.client = client;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final String SQL =
                String.format(
                        "DELETE FROM Addresses AS a WHERE a.member_id IN (SELECT b.member_id FROM FamilyMembers "
                                + "AS b WHERE b.client_id = %d);DELETE FROM Relationships WHERE client_id = %d;"
                                + "DELETE FROM FamilyMembers WHERE client_id = %d",
                        client.getClientId(), client.getClientId(), client.getClientId()
                );

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(SQL);
            JOptionPane.showMessageDialog(null, "All data has been successfully deleted.");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to clear all data, Please try again later.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
