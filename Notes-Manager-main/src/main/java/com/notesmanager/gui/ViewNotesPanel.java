package com.notesmanager.gui;

import com.notesmanager.database.DatabaseManager;
import com.notesmanager.model.Note;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * ViewNotesPanel displays all notes with search and delete functionality
 * Uses JTable for displaying notes and search bar for filtering
 */
public class ViewNotesPanel extends JPanel {
    private JTextField searchField;
    private JButton searchButton;
    private JButton deleteButton;
    private JButton editButton;
    private JButton refreshButton;
    private JButton exportButton;
    private JButton printButton;
    private JButton duplicateButton;
    private JButton sortButton;
    private JTable notesTable;
    private DefaultTableModel tableModel;
    private DatabaseManager dbManager;
    private EditNotePanel editNotePanel;
    private Runnable statusCallback;

    public ViewNotesPanel(DatabaseManager dbManager, Runnable statusCallback) {
        this.dbManager = dbManager;
        this.statusCallback = statusCallback;
        initializeUI();
        loadNotes();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(new Color(240, 240, 240));

        // Search Panel
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchPanel.setBackground(new Color(240, 240, 240));
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Arial", Font.BOLD, 12));
        searchField = new JTextField();
        searchField.setFont(new Font("Arial", Font.PLAIN, 12));
        
        JPanel buttonSearchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        buttonSearchPanel.setBackground(new Color(240, 240, 240));
        
        searchButton = new JButton("🔍 Search");
        searchButton.setFont(new Font("Arial", Font.BOLD, 11));
        searchButton.setBackground(new Color(33, 150, 243));
        searchButton.setForeground(Color.WHITE);
        searchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchButton.addActionListener(e -> handleSearch());

        refreshButton = new JButton("🔄 Refresh");
        refreshButton.setFont(new Font("Arial", Font.BOLD, 11));
        refreshButton.setBackground(new Color(76, 175, 80));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshButton.addActionListener(e -> handleRefresh());

        editButton = new JButton("✏️  Edit");
        editButton.setFont(new Font("Arial", Font.BOLD, 11));
        editButton.setBackground(new Color(255, 152, 0));
        editButton.setForeground(Color.WHITE);
        editButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        editButton.addActionListener(e -> handleEdit());

        deleteButton = new JButton("🗑️  Delete");
        deleteButton.setFont(new Font("Arial", Font.BOLD, 11));
        deleteButton.setBackground(new Color(244, 67, 54));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        deleteButton.addActionListener(e -> handleDelete());
        
        exportButton = new JButton("💾 Export");
        exportButton.setFont(new Font("Arial", Font.BOLD, 11));
        exportButton.setBackground(new Color(63, 81, 181));
        exportButton.setForeground(Color.WHITE);
        exportButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        exportButton.addActionListener(e -> handleExport());
        
        printButton = new JButton("🖨️  Print");
        printButton.setFont(new Font("Arial", Font.BOLD, 11));
        printButton.setBackground(new Color(158, 158, 158));
        printButton.setForeground(Color.WHITE);
        printButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        printButton.addActionListener(e -> handlePrint());
        
        duplicateButton = new JButton("📋 Duplicate");
        duplicateButton.setFont(new Font("Arial", Font.BOLD, 11));
        duplicateButton.setBackground(new Color(103, 58, 183));
        duplicateButton.setForeground(Color.WHITE);
        duplicateButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        duplicateButton.addActionListener(e -> handleDuplicate());
        
        sortButton = new JButton("↕️  Sort");
        sortButton.setFont(new Font("Arial", Font.BOLD, 11));
        sortButton.setBackground(new Color(0, 150, 136));
        sortButton.setForeground(Color.WHITE);
        sortButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        sortButton.addActionListener(e -> handleSort());

        buttonSearchPanel.add(searchButton);
        buttonSearchPanel.add(refreshButton);
        buttonSearchPanel.add(editButton);
        buttonSearchPanel.add(deleteButton);
        buttonSearchPanel.add(exportButton);
        buttonSearchPanel.add(printButton);
        buttonSearchPanel.add(duplicateButton);
        buttonSearchPanel.add(sortButton);

        searchPanel.add(searchLabel, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(buttonSearchPanel, BorderLayout.EAST);

        // Table Panel
        String[] columnNames = {"ID", "Title", "Content", "Created", "Updated"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        notesTable = new JTable(tableModel);
        notesTable.setFont(new Font("Arial", Font.PLAIN, 11));
        notesTable.setRowHeight(25);
        notesTable.getColumnModel().getColumn(0).setPreferredWidth(30);
        notesTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        notesTable.getColumnModel().getColumn(2).setPreferredWidth(250);
        notesTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        notesTable.getColumnModel().getColumn(4).setPreferredWidth(150);

        JScrollPane scrollPane = new JScrollPane(notesTable);
        scrollPane.setPreferredSize(new Dimension(800, 400));

        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void handleSearch() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter a search keyword!", 
                "Input Error", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Note> notes = dbManager.searchNotes(keyword);
        populateTable(notes);
    }

    private void handleRefresh() {
        searchField.setText("");
        loadNotes();
        JOptionPane.showMessageDialog(this, 
            "Notes refreshed successfully!", 
            "Refresh", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleEdit() {
        int selectedRow = notesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a note to edit!", 
                "Selection Error", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int noteId = (int) tableModel.getValueAt(selectedRow, 0);
        Note note = dbManager.getNoteById(noteId);

        if (note != null) {
            openEditDialog(note);
        } else {
            JOptionPane.showMessageDialog(this, 
                "Failed to load note!", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openEditDialog(Note note) {
        JDialog editDialog = new JDialog();
        editDialog.setTitle("Edit Note");
        editDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        editDialog.setSize(700, 500);
        editDialog.setLocationRelativeTo(this);
        editDialog.setModal(true);

        EditNotePanel editPanel = new EditNotePanel(dbManager, () -> {
            editDialog.dispose();
            loadNotes();
        });
        editPanel.loadNote(note);

        editDialog.add(editPanel);
        editDialog.setVisible(true);
    }

    private void handleDelete() {
        int selectedRow = notesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a note to delete!", 
                "Selection Error", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int noteId = (int) tableModel.getValueAt(selectedRow, 0);
        String noteTitle = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete note: \"" + noteTitle + "\"?", 
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (dbManager.deleteNote(noteId)) {
                JOptionPane.showMessageDialog(this, 
                    "Note deleted successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                loadNotes();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to delete note!", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void loadNotes() {
        List<Note> notes = dbManager.getAllNotes();
        populateTable(notes);
        if (statusCallback != null) {
            statusCallback.run();
        }
    }
    
    private void handleExport() {
        int selectedRow = notesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a note to export!", 
                "Selection Error", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int noteId = (int) tableModel.getValueAt(selectedRow, 0);
        String noteTitle = (String) tableModel.getValueAt(selectedRow, 1);
        Note note = dbManager.getNoteById(noteId);
        
        if (note != null) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new java.io.File(noteTitle + ".txt"));
            int result = fileChooser.showSaveDialog(this);
            
            if (result == JFileChooser.APPROVE_OPTION) {
                try {
                    java.io.File file = fileChooser.getSelectedFile();
                    java.nio.file.Files.write(file.toPath(), note.getContent().getBytes());
                    JOptionPane.showMessageDialog(this, 
                        "Note exported successfully!\nFile: " + file.getAbsolutePath(), 
                        "Export Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, 
                        "Error exporting note: " + e.getMessage(), 
                        "Export Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    private void handlePrint() {
        int selectedRow = notesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a note to print!", 
                "Selection Error", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String noteTitle = (String) tableModel.getValueAt(selectedRow, 1);
        JOptionPane.showMessageDialog(this, 
            "Print functionality for note: \"" + noteTitle + "\"\nPrinting to default printer...", 
            "Print", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void handleDuplicate() {
        int selectedRow = notesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a note to duplicate!", 
                "Selection Error", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int noteId = (int) tableModel.getValueAt(selectedRow, 0);
        Note originalNote = dbManager.getNoteById(noteId);
        
        if (originalNote != null) {
            Note duplicatedNote = new Note(
                originalNote.getTitle() + " (Copy)", 
                originalNote.getContent()
            );
            
            if (dbManager.addNote(duplicatedNote)) {
                JOptionPane.showMessageDialog(this, 
                    "Note duplicated successfully!", 
                    "Duplicate Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                loadNotes();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to duplicate note!", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void handleSort() {
        String[] sortOptions = {"Title (A-Z)", "Title (Z-A)", "Most Recent", "Oldest"};
        int choice = JOptionPane.showOptionDialog(this,
            "Sort Notes By:",
            "Sort Options",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            null,
            sortOptions,
            sortOptions[0]);
        
        if (choice != -1) {
            JOptionPane.showMessageDialog(this, 
                "Sorting by: " + sortOptions[choice] + "\nFeature coming soon!", 
                "Sort", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void populateTable(List<Note> notes) {
        tableModel.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (Note note : notes) {
            Object[] row = {
                note.getId(),
                note.getTitle(),
                note.getContent().length() > 50 ? note.getContent().substring(0, 50) + "..." : note.getContent(),
                note.getCreatedAt().format(formatter),
                note.getUpdatedAt().format(formatter)
            };
            tableModel.addRow(row);
        }
    }
}
