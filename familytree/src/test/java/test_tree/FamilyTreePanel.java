package test_tree;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class FamilyTreePanel extends JPanel {
    static class Person {
        private String name;
        private Person spouse;
        private List<Person> children;

        public Person(String name) {
            this.name = name;
            this.children = new ArrayList<>();
        }

        public String getName() {
            return name;
        }

        public Person getSpouse() {
            return spouse;
        }

        public void setSpouse(Person spouse) {
            this.spouse = spouse;
        }

        public List<Person> getChildren() {
            return children;
        }

        public void addChild(Person child) {
            children.add(child);
        }
    }

    private static final int PERSON_HEIGHT = 30;
    private static final int PERSON_WIDTH = 100;
    private static final int SPACING_X = 150;
    private static final int SPACING_Y = 100;

    private List<int[]> lines = new ArrayList<>();

    public FamilyTreePanel(Person rootPerson) {
        setLayout(null);
        displayTree(rootPerson, 100, 20);
    }

    private void displayTree(Person person, int x, int y) {
        JLabel nameLabel = new JLabel(person.getName());
        nameLabel.setBounds(x, y, PERSON_WIDTH, PERSON_HEIGHT);
        add(nameLabel);

        if (person.getSpouse() != null) {
            int spouseX = x + PERSON_WIDTH + SPACING_X;
            int spouseY = y;
            JLabel spouseLabel = new JLabel(person.getSpouse().getName());
            spouseLabel.setBounds(spouseX, spouseY, PERSON_WIDTH, PERSON_HEIGHT);
            add(spouseLabel);
            // Collect line coordinates
            lines.add(new int[]{x + PERSON_WIDTH, y + PERSON_HEIGHT / 2, spouseX, spouseY + PERSON_HEIGHT / 2});
        }

        int childrenX = x;
        int childrenY = y + PERSON_HEIGHT + SPACING_Y;
        for (Person child : person.getChildren()) {
            displayTree(child, childrenX, childrenY);
            // Collect line coordinates
            lines.add(new int[]{x + PERSON_WIDTH / 2, y + PERSON_HEIGHT, childrenX + PERSON_WIDTH / 2, childrenY});
            childrenX += PERSON_WIDTH + SPACING_X;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw lines
        for (int[] line : lines) {
            g.drawLine(line[0], line[1], line[2], line[3]);
        }
    }

    public static void main(String[] args) {
        Person john = new Person("John");
        Person mary = new Person("Mary");
        Person adam = new Person("Adam");
        Person lisa = new Person("Lisa");
        Person sarah = new Person("Sarah");

        john.addChild(adam);
        mary.addChild(adam);
        adam.addChild(lisa);
        adam.addChild(sarah);

        john.setSpouse(mary);
        mary.setSpouse(john);

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Family Tree");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(500, 500);
            frame.setLocationRelativeTo(null);

            JPanel mainPanel = new FamilyTreePanel(john);
            JScrollPane scrollPane = new JScrollPane(mainPanel);
            frame.add(scrollPane);

            frame.setVisible(true);
        });
    }
}
