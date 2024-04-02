package com.familytree.listeners;

import com.familytree.parsing.TextFileParser;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

public class ImportFileListener implements ActionListener {

    private final Connection connection;

    public ImportFileListener(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Text File");
        int result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            try {
                var importResult =  TextFileParser.parse(new FileInputStream(selectedFile), connection);
                String message = "Import Result:\n" +
                        "Created: " + importResult.getCreated() + "\n" +
                        "Duplicated: " + importResult.getDuplicated() + "\n" +
                        "Relationships Created: " + importResult.getRelationshipsCreated() + "\n" +
                        "Relationships Duplicated: " + importResult.getRelationshipsDuplicated();

                JOptionPane.showMessageDialog(null, message, "Import Result", JOptionPane.INFORMATION_MESSAGE);

            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "selected file was not found", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Failed to upload completely the file, please try again", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "An unexpected was caught, please try again", "Error", JOptionPane.ERROR_MESSAGE);
            }

            System.out.println("Data imported successfully.");
        }

    }
}
