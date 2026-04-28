# Notes Manager - Document Management System

A professional Notes/Document Manager application built with Java, featuring:
- **Java 8+** for core logic and OOP classes
- **Java Swing** for desktop GUI
- **SQLite Database** with JDBC for data persistence

## Project Architecture

```
GUI (Java Swing)
    ↓
Java Classes (OOP)
    ↓
JDBC ↔ SQLite Database
```

## Project Structure

```
pp/
├── src/main/java/com/notesmanager/
│   ├── NotesApp.java                 # Main entry point
│   ├── model/
│   │   └── Note.java                 # Note data model
│   ├── database/
│   │   └── DatabaseManager.java      # JDBC database operations
│   └── gui/
│       ├── MainFrame.java            # Main application window
│       ├── AddNotePanel.java         # Add note form
│       └── ViewNotesPanel.java       # View and search notes
├── db/                               # Database directory (created at runtime)
├── lib/                              # Required libraries
└── README.md
```

## Requirements

- **Java 8 or higher**
- **SQLite JDBC Driver** (sqlite-jdbc-3.x.x.jar)

## Installation & Setup

### 1. Download SQLite JDBC Driver

Download the JDBC driver from: [https://github.com/xerial/sqlite-jdbc/releases](https://github.com/xerial/sqlite-jdbc/releases)

Or download using command line:
```bash
# Windows (PowerShell)
wget https://github.com/xerial/sqlite-jdbc/releases/download/3.44.0.0/sqlite-jdbc-3.44.0.0.jar -O lib/sqlite-jdbc-3.44.0.0.jar

# Linux/Mac
curl -L https://github.com/xerial/sqlite-jdbc/releases/download/3.44.0.0/sqlite-jdbc-3.44.0.0.jar -o lib/sqlite-jdbc-3.44.0.0.jar
```

### 2. Create lib directory
```bash
mkdir lib
```

### 3. Compile the Project

**Windows (Command Prompt/PowerShell):**
```bash
# Create compilation directory
mkdir build

# Compile all Java files
javac -cp "lib/sqlite-jdbc-3.44.0.0.jar" -d build src/main/java/com/notesmanager/*.java src/main/java/com/notesmanager/model/*.java src/main/java/com/notesmanager/database/*.java src/main/java/com/notesmanager/gui/*.java
```

**Linux/Mac:**
```bash
mkdir build
javac -cp "lib/sqlite-jdbc-3.44.0.0.jar" -d build src/main/java/com/notesmanager/*.java src/main/java/com/notesmanager/model/*.java src/main/java/com/notesmanager/database/*.java src/main/java/com/notesmanager/gui/*.java
```

### 4. Run the Application

**Windows (Command Prompt/PowerShell):**
```bash
java -cp "build;lib/sqlite-jdbc-3.44.0.0.jar" com.notesmanager.NotesApp
```

**Linux/Mac:**
```bash
java -cp "build:lib/sqlite-jdbc-3.44.0.0.jar" com.notesmanager.NotesApp
```

## Features

### ✅ Add Note Form
- Title input field
- Content text area with word wrap
- Add and Clear buttons
- Real-time validation
- Success/error notifications

### ✅ View Notes Screen
- Table display of all notes
- Shows ID, Title, Content preview, Created date, Updated date
- Sortable by update time (newest first)
- Delete selected notes with confirmation

### ✅ Search Bar
- Search by title or content
- Real-time filtering
- Refresh to show all notes
- Case-insensitive search

### ✅ Database (JDBC + SQLite)
- Persistent storage using SQLite
- Proper schema with auto-increment IDs
- Timestamps for created and updated dates
- Full ACID compliance
- Connection pooling ready

## Database Schema

```sql
CREATE TABLE notes (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## Class Details

### Model Layer (OOP)
- **Note.java** - Encapsulates note data with getters/setters

### Database Layer (JDBC)
- **DatabaseManager.java** - Handles all SQL operations
  - `connect()` - Establish connection
  - `addNote()` - Insert new note
  - `updateNote()` - Modify existing note
  - `deleteNote()` - Remove note
  - `getAllNotes()` - Retrieve all notes
  - `searchNotes()` - Filter by keyword
  - `getNoteById()` - Get specific note

### GUI Layer (Swing)
- **MainFrame.java** - Main application window with tabs
- **AddNotePanel.java** - Form for adding notes
- **ViewNotesPanel.java** - Table for viewing/managing notes

## Usage

1. **Adding a Note:**
   - Click the "Add Note" tab
   - Enter title and content
   - Click "Add Note" button
   - Confirmation message appears

2. **Viewing Notes:**
   - Click "View Notes" tab
   - All notes displayed in table
   - Click any row to select
   - Click "Delete Selected" to remove

3. **Searching Notes:**
   - Enter keyword in search field
   - Click "Search" button
   - Results filtered in table
   - Click "Refresh" to show all again

## Technical Highlights

✅ **Pure Java** - No external frameworks, just JDBC and Swing
✅ **OOP Principles** - Encapsulation, abstraction, proper class design
✅ **JDBC Direct** - Raw SQL with PreparedStatements for security
✅ **Professional GUI** - Responsive Swing application
✅ **Real Database** - SQLite via JDBC (not ArrayList/file storage)
✅ **Error Handling** - Comprehensive exception handling
✅ **Data Persistence** - All changes saved to database

## Troubleshooting

**"SQLite JDBC Driver not found"**
- Ensure sqlite-jdbc-3.44.0.0.jar is in the lib/ directory
- Check classpath includes the JAR file

**"Database file not found"**
- The db/ directory will be created automatically
- Ensure write permissions in the project directory

**Port already in use (if using network features)**
- This application doesn't use ports, runs locally

## Future Enhancements

- Export notes to PDF/Word
- Note categories/tags
- Color-coded notes
- Rich text editor
- Backup/restore functionality
- Note sharing
- Cloud sync

## License

Educational Project - Free to use for learning purposes

## Author

Created as a demonstration of Java Swing + JDBC + SQLite integration
