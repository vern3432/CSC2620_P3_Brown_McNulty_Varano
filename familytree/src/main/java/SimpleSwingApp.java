import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SimpleSwingApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI() {
        // Create and set up the window
        JFrame frame = new JFrame("Simple Swing App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 150);

        // Create and set up the panel
        JPanel panel = new JPanel();
        frame.add(panel);
        placeComponents(panel);

        // Display the window
        frame.setVisible(true);
    }

    private static void placeComponents(JPanel panel) {
        panel.setLayout(null);

        // Create a text field
        JTextField textField = new JTextField("Enter text here");
        textField.setBounds(10, 10, 200, 25);
        panel.add(textField);

        // Create a submit button
        JButton submitButton = new JButton("Submit");
        submitButton.setBounds(10, 40, 80, 25);
        panel.add(submitButton);

        // Add action listener to the button
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Get text from the text field and trim leading and trailing whitespace
                String text = textField.getText().trim();
                // Print the trimmed text
                System.out.println("Text submitted: " + text);
                // Close the window
                Window window = SwingUtilities.windowForComponent((Component) e.getSource());
                window.dispose();
            }
        });
    }
}
