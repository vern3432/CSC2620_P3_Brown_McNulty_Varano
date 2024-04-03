# Family Tree Application

This application allows users to manage and visualize family tree data through a graphical user interface (GUI). It includes features such as importing family tree data from text files, organizing family members into a tree structure, managing events associated with family members, and displaying Gantt charts.

## Installation

1. Ensure you have Java Development Kit (JDK) installed on your system.
2. Download or clone the repository to your local machine.
3. Open the project in your preferred Integrated Development Environment (IDE).
4. Build and run the project.

## Usage
### Login Process:

1. Launch the application.
2. The login dialog will appear.
3. Enter your credentials (username and password).
4. Click the "Login" button.
5. If the credentials are valid, the main GUI window will open.
6. If the credentials are invalid, an error message will be displayed.

### Family Tree Tab:

- **Display Family Tree:**
  - Visualize the family tree hierarchy using a custom tree panel.
  - Implement zoom in/out functionality for better visualization.
  - Provide options to expand/collapse nodes for better navigation.

### Gantt Chart Tab:

- **Display Gantt Chart:**
  - Show a graphical representation of events associated with family members.
  - Implement filtering options to view specific types of events.
  - Allow users to add, edit, or delete events directly from the chart.

### Family Member List Tab:

- **List Family Members:**
  - Display a list of all family members with relevant details.
  - Implement sorting and filtering options for easy navigation.
  - Allow users to add, edit, or delete family members.

### Event Management Tab:

- **Manage Events:**
  - Provide a user-friendly interface to manage events associated with family members.
  - Allow users to add, edit, or delete events.
  - Implement reminders or notifications for upcoming events.

### Menu Bar Functions:

- **File Menu:**
  - **Import Text:** Allow users to import family tree data from text files.
  - **Export Data:** Provide an option to export family tree data to text files.
  - **Exit:** Close the application.

- **Options Menu:**
  - **Refresh:** Implement a refresh function to update the displayed data.
  - **Settings:** Allow users to customize application settings, such as theme or font size.



### Main Class (Main.java)

The `Main` class serves as the entry point for the application. It initializes the SQLite database, loads initial data from a text file, and launches the login dialog.





####  Steps:

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


## License

