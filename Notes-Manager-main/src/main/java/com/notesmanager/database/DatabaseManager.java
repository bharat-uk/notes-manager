package com.notesmanager.database;

import com.notesmanager.model.Note;

import java.io.File;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DatabaseManager handles all JDBC database operations
 * Uses SQLite database with JDBC driver
 */
public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:db/notesdb.db";
    private static final String DRIVER = "org.sqlite.JDBC";
    
    private Connection connection;

    /**
     * Establish database connection and initialize database
     */
    public void connect() {
        try {
            Class.forName(DRIVER);
            File dbDirectory = new File("db");
            if (!dbDirectory.exists()) {
                dbDirectory.mkdirs();
            }
            connection = DriverManager.getConnection(DB_URL);
            System.out.println("Connected to database: " + DB_URL);
            initializeDatabase();
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC Driver not found!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Error connecting to database!");
            e.printStackTrace();
        }
    }

    /**
     * Initialize database schema if not exists
     */
    private void initializeDatabase() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS notes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title TEXT NOT NULL," +
                "content TEXT NOT NULL," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ");";

        try (Statement statement = connection.createStatement()) {
            statement.execute(createTableSQL);
            System.out.println("Database initialized successfully");
        } catch (SQLException e) {
            System.err.println("Error initializing database!");
            e.printStackTrace();
        }
    }

    /**
     * Add a new note to database
     */
    public boolean addNote(Note note) {
        String insertSQL = "INSERT INTO notes (title, content, created_at, updated_at) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
            pstmt.setString(1, note.getTitle());
            pstmt.setString(2, note.getContent());
            pstmt.setString(3, note.getCreatedAt().toString());
            pstmt.setString(4, note.getUpdatedAt().toString());
            
            int rowsInserted = pstmt.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            System.err.println("Error adding note!");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Update existing note
     */
    public boolean updateNote(Note note) {
        String updateSQL = "UPDATE notes SET title = ?, content = ?, updated_at = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(updateSQL)) {
            pstmt.setString(1, note.getTitle());
            pstmt.setString(2, note.getContent());
            pstmt.setString(3, LocalDateTime.now().toString());
            pstmt.setInt(4, note.getId());
            
            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.println("Error updating note!");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Delete note by ID
     */
    public boolean deleteNote(int id) {
        String deleteSQL = "DELETE FROM notes WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(deleteSQL)) {
            pstmt.setInt(1, id);
            int rowsDeleted = pstmt.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting note!");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get all notes from database
     */
    public List<Note> getAllNotes() {
        List<Note> notes = new ArrayList<>();
        String selectSQL = "SELECT * FROM notes ORDER BY updated_at DESC";
        
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(selectSQL)) {
            
            while (resultSet.next()) {
                Note note = createNoteFromResultSet(resultSet);
                notes.add(note);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving all notes!");
            e.printStackTrace();
        }
        
        return notes;
    }

    /**
     * Search notes by title or content
     */
    public List<Note> searchNotes(String keyword) {
        List<Note> notes = new ArrayList<>();
        String searchSQL = "SELECT * FROM notes WHERE title LIKE ? OR content LIKE ? ORDER BY updated_at DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(searchSQL)) {
            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            
            try (ResultSet resultSet = pstmt.executeQuery()) {
                while (resultSet.next()) {
                    Note note = createNoteFromResultSet(resultSet);
                    notes.add(note);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching notes!");
            e.printStackTrace();
        }
        
        return notes;
    }

    /**
     * Get note by ID
     */
    public Note getNoteById(int id) {
        String selectSQL = "SELECT * FROM notes WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(selectSQL)) {
            pstmt.setInt(1, id);
            
            try (ResultSet resultSet = pstmt.executeQuery()) {
                if (resultSet.next()) {
                    return createNoteFromResultSet(resultSet);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving note by ID!");
            e.printStackTrace();
        }
        
        return null;
    }

    /**
     * Helper method to create Note object from ResultSet
     */
    private Note createNoteFromResultSet(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String title = resultSet.getString("title");
        String content = resultSet.getString("content");
        LocalDateTime createdAt = LocalDateTime.parse(resultSet.getString("created_at"));
        LocalDateTime updatedAt = LocalDateTime.parse(resultSet.getString("updated_at"));
        
        return new Note(id, title, content, createdAt, updatedAt);
    }

    /**
     * Close database connection
     */
    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Disconnected from database");
            }
        } catch (SQLException e) {
            System.err.println("Error disconnecting from database!");
            e.printStackTrace();
        }
    }
}
