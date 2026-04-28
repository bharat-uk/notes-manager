NOTES MANAGER - ARCHITECTURE DOCUMENTATION
===========================================

PROJECT REQUIREMENTS MET:
=========================

✅ 1. JAVA (COMPULSORY)
   ├─ Logic Implementation
   │  └─ JDBC connection management
   │  └─ SQL query construction
   │  └─ Business logic for CRUD operations
   │
   ├─ Classes (OOP)
   │  ├─ Note.java - Data model with encapsulation
   │  ├─ DatabaseManager.java - Database operations
   │  ├─ MainFrame.java - Main window
   │  ├─ AddNotePanel.java - Form panel
   │  └─ ViewNotesPanel.java - Display panel
   │
   └─ File/Database Handling
      └─ JDBC with PreparedStatements
      └─ Connection pooling ready
      └─ Transaction management

✅ 2. GUI (JAVA SWING)
   ├─ Add Note Form
   │  ├─ JTextField for title
   │  ├─ JTextArea for content (with scroll)
   │  ├─ Add Note button
   │  └─ Clear button
   │
   ├─ View Notes Screen
   │  ├─ JTable for displaying notes
   │  ├─ Columns: ID, Title, Content, Created, Updated
   │  ├─ JScrollPane for scrolling
   │  └─ Delete functionality
   │
   └─ Search Bar
      ├─ JTextField for keyword
      ├─ Search button
      ├─ Refresh button
      └─ Real-time filtering

✅ 3. DATABASE (IMPORTANT - CORRECT IMPLEMENTATION)
   ├─ Technology: SQLite with JDBC
   ├─ Driver: org.xerial.sqlite-jdbc
   ├─ Location: db/notesdb.db
   ├─ Schema:
   │  └─ CREATE TABLE notes (
   │     id INTEGER PRIMARY KEY AUTOINCREMENT,
   │     title TEXT NOT NULL,
   │     content TEXT NOT NULL,
   │     created_at TIMESTAMP,
   │     updated_at TIMESTAMP
   │  )
   │
   ├─ Operations via JDBC:
   │  ├─ INSERT - addNote()
   │  ├─ UPDATE - updateNote()
   │  ├─ DELETE - deleteNote()
   │  ├─ SELECT - getAllNotes(), searchNotes(), getNoteById()
   │  └─ PreparedStatements for SQL injection prevention
   │
   └─ Persistence: Permanent storage, database survives application restart

═══════════════════════════════════════════════════════════════

ARCHITECTURE DIAGRAM:
═══════════════════════════════════════════════════════════════

                    USER INTERFACE LAYER
                    (Java Swing - GUI)
                            ↓
          ┌─────────────────────────────────────┐
          │        MainFrame (JFrame)            │
          │  ┌────────────────────────────────┐  │
          │  │  JTabbedPane                   │  │
          │  │ ┌──────────┬──────────────────┤  │
          │  │ │  Add Tab │  View Tab        │  │
          │  │ └──────────┴──────────────────┤  │
          │  │        Content Panels          │  │
          │  └────────────────────────────────┘  │
          └──────────────┬──────────────────────┘
                         │
                         ↓
                  BUSINESS LOGIC LAYER
                  (Java Classes - OOP)
                         ↓
          ┌─────────────────────────────────────┐
          │    DatabaseManager (Singleton)      │
          │  - JDBC Connection management       │
          │  - PreparedStatement creation       │
          │  - ResultSet mapping                │
          │  - Exception handling               │
          └──────────────┬──────────────────────┘
                         │
                         ↓
                   DATA ACCESS LAYER
                   (JDBC + SQL)
                         ↓
          ┌─────────────────────────────────────┐
          │    SQLite Database (db/notesdb.db)  │
          │    ┌─────────────────────────────┐  │
          │    │  notes                      │  │
          │    │  ├─ id (PK)                 │  │
          │    │  ├─ title                   │  │
          │    │  ├─ content                 │  │
          │    │  ├─ created_at              │  │
          │    │  └─ updated_at              │  │
          │    └─────────────────────────────┘  │
          └─────────────────────────────────────┘

═══════════════════════════════════════════════════════════════

CLASS HIERARCHY:
═══════════════════════════════════════════════════════════════

com.notesmanager
├── NotesApp (main entry point)
├── model/
│   └── Note (data model)
│       ├── id: int
│       ├── title: String
│       ├── content: String
│       ├── createdAt: LocalDateTime
│       └── updatedAt: LocalDateTime
├── database/
│   └── DatabaseManager (JDBC operations)
│       ├── connect(): void
│       ├── addNote(Note): boolean
│       ├── updateNote(Note): boolean
│       ├── deleteNote(int): boolean
│       ├── getAllNotes(): List<Note>
│       ├── searchNotes(String): List<Note>
│       ├── getNoteById(int): Note
│       └── disconnect(): void
└── gui/
    ├── MainFrame (extends JFrame)
    │   ├── dbManager: DatabaseManager
    │   ├── tabbedPane: JTabbedPane
    │   └── initializeUI(): void
    ├── AddNotePanel (extends JPanel)
    │   ├── titleField: JTextField
    │   ├── contentArea: JTextArea
    │   ├── handleAddNote(): void
    │   └── handleClear(): void
    └── ViewNotesPanel (extends JPanel)
        ├── notesTable: JTable
        ├── searchField: JTextField
        ├── handleSearch(): void
        ├── handleDelete(): void
        └── loadNotes(): void

═══════════════════════════════════════════════════════════════

DATA FLOW (Add Note Example):
═══════════════════════════════════════════════════════════════

User Input (GUI)
    ↓
AddNotePanel.handleAddNote()
    ├─ Get title from JTextField
    ├─ Get content from JTextArea
    └─ Validate input
         ↓
Create Note Object
    └─ new Note(title, content)
         ↓
DatabaseManager.addNote(Note)
    ├─ Prepare SQL: INSERT INTO notes (title, content, ...)
    ├─ Bind parameters (prevent SQL injection)
    ├─ Execute update
    └─ Return success/failure
         ↓
Update GUI
    ├─ Show success message
    ├─ Clear input fields
    └─ Refresh ViewNotesPanel
         ↓
Persist to Database
    └─ Note stored in SQLite (db/notesdb.db)

═══════════════════════════════════════════════════════════════

DATA FLOW (Search Notes Example):
═══════════════════════════════════════════════════════════════

User Input (Search Bar)
    ↓
ViewNotesPanel.handleSearch()
    └─ Get keyword from JTextField
         ↓
DatabaseManager.searchNotes(keyword)
    ├─ Prepare SQL: SELECT * FROM notes WHERE title LIKE ? OR content LIKE ?
    ├─ Bind search pattern: "%keyword%"
    ├─ Execute query
    └─ Map ResultSet to List<Note>
         ↓
Populate JTable
    ├─ Clear existing rows
    └─ Add new rows from results
         ↓
Display to User
    └─ JTable shows filtered notes

═══════════════════════════════════════════════════════════════

KEY DESIGN PATTERNS:
═══════════════════════════════════════════════════════════════

1. MVC Pattern (Model-View-Controller)
   ├─ Model: Note.java
   ├─ View: MainFrame, AddNotePanel, ViewNotesPanel
   └─ Controller: DatabaseManager

2. DAO Pattern (Data Access Object)
   └─ DatabaseManager acts as DAO for Note objects

3. Singleton Pattern (Database Connection)
   └─ Single DatabaseManager instance manages all DB ops

4. Observer Pattern (Callbacks)
   └─ onNoteAdded callback in AddNotePanel

5. PreparedStatement Pattern (Security)
   └─ Prevents SQL injection attacks

═══════════════════════════════════════════════════════════════

OOP PRINCIPLES APPLIED:
═══════════════════════════════════════════════════════════════

✅ Encapsulation
   ├─ Note.java: Private fields with public getters/setters
   ├─ DatabaseManager: Private connection, public interface
   └─ GUI Classes: Organized private components

✅ Abstraction
   ├─ JDBC abstraction hides SQL complexity
   ├─ GUI components hide Swing details
   └─ DatabaseManager provides simple interface

✅ Inheritance
   ├─ JFrame, JPanel inheritance for GUI
   └─ Swing component hierarchy

✅ Polymorphism
   ├─ Interface implementations (Action listeners)
   └─ Method overriding (TableModel, etc.)

═══════════════════════════════════════════════════════════════

SECURITY CONSIDERATIONS:
═══════════════════════════════════════════════════════════════

✅ SQL Injection Prevention
   └─ Uses PreparedStatement with parameter binding

✅ Input Validation
   └─ GUI validates title and content before DB insertion

✅ Exception Handling
   ├─ Try-catch blocks for all JDBC operations
   └─ User-friendly error messages

✅ Resource Management
   ├─ Proper connection cleanup
   └─ PreparedStatement closing

═══════════════════════════════════════════════════════════════

FUTURE ENHANCEMENTS READY:
═══════════════════════════════════════════════════════════════

✅ Database Level:
   ├─ Add note categories/tags
   ├─ Add user authentication
   └─ Add data encryption

✅ Application Level:
   ├─ Export to PDF/Word
   ├─ Note sharing
   ├─ Cloud backup
   └─ Full-text search

✅ GUI Level:
   ├─ Rich text editor
   ├─ Color-coded notes
   ├─ Note templates
   └─ Dark theme option

═══════════════════════════════════════════════════════════════

END OF DOCUMENTATION
