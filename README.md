# Family Tree Application

This application allows users to manage and visualize family tree data through a graphical user interface (GUI). It includes features such as importing family tree data from text files, organizing family members into a tree structure, managing events associated with family members, and displaying Gantt charts.

## Installation

1. Ensure you have Java Development Kit (JDK) installed on your system.
2. Download or clone the repository to your local machine.
3. Open the project in your preferred Integrated Development Environment (IDE).
4. Build and run the project.

## Usage

### Main Class (Main.java)

The `Main` class serves as the entry point for the application. It initializes the SQLite database, loads initial data from a text file, and launches the login dialog.

#### Execution Steps:

1. Establishes a connection to the SQLite database.
2. Creates the necessary schema in the database.
3. Loads initial family tree data from a text file.
4. Initializes the login dialog for user authentication.

### GUI Class (FamilyTreeGUI.java)

The `FamilyTreeGUI` class represents the main graphical user interface of the application. It provides functionalities to display the family tree, Gantt chart, family member list, and event management panel.

#### Features:

- Display Family Tree: Visualizes the family tree hierarchy using a custom tree panel.
- Gantt Chart: Provides a graphical representation of events associated with family members.
- Family Member List: Lists all family members with details and options for management.
- Event Management: Allows users to manage events associated with family members.

## Contributing

Contributions to this project are welcome. If you find any issues or have suggestions for improvements, please feel free to open an issue or submit a pull request.

## License

This project is licensed under the [MIT License](LICENSE).
