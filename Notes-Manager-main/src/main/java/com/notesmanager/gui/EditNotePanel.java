package com.notesmanager.gui;

import com.notesmanager.database.DatabaseManager;
import com.notesmanager.model.Note;

import javax.swing.*;
import java.awt.*;

/**
 * EditNotePanel provides the GUI form for editing existing notes
 * Uses Java Swing components
 */
public class EditNotePanel extends JPanel {
    private JTextField titleField;
    private JTextArea contentArea;
    private JButton updateButton;
    private JButton cancelButton;
    private JButton loadButton;
    private JButton revertButton;
    private DatabaseManager dbManager;
    private Runnable onNoteUpdated;
    private Note currentNote;
    private Note originalNote;

    public EditNotePanel(DatabaseManager dbManager, Runnable onNoteUpdated) {
        this.dbManager = dbManager;
        this.onNoteUpdated = onNoteUpdated;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder("Edit Note"));
        setBackground(new Color(240, 240, 240));

        // Title Panel
        JPanel titlePanel = new JPanel(new BorderLayout(5, 5));
        titlePanel.setBackground(new Color(240, 240, 240));
        JLabel titleLabel = new JLabel("Title:");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        titleField = new JTextField();
        titleField.setFont(new Font("Arial", Font.PLAIN, 12));
        titlePanel.add(titleLabel, BorderLayout.WEST);
        titlePanel.add(titleField, BorderLayout.CENTER);

        // Content Panel
        JPanel contentPanel = new JPanel(new BorderLayout(5, 5));
        contentPanel.setBackground(new Color(240, 240, 240));
        JLabel contentLabel = new JLabel("Content:");
        contentLabel.setFont(new Font("Arial", Font.BOLD, 12));
        contentArea = new JTextArea(8, 40);
        contentArea.setFont(new Font("Arial", Font.PLAIN, 12));
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(contentArea);
        contentPanel.add(contentLabel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(new Color(240, 240, 240));
        
        updateButton = new JButton("✓ Update Note");
        updateButton.setFont(new Font("Arial", Font.BOLD, 12));
        updateButton.setBackground(new Color(33, 150, 243));
        updateButton.setForeground(Color.WHITE);
        updateButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        updateButton.addActionListener(e -> handleUpdateNote());

        cancelButton = new JButton("✕ Cancel");
        cancelButton.setFont(new Font("Arial", Font.BOLD, 12));
        cancelButton.setBackground(new Color(158, 158, 158));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelButton.addActionListener(e -> handleCancel());
        
        loadButton = new JButton("📂 Load Note");
        loadButton.setFont(new Font("Arial", Font.BOLD, 12));
        loadButton.setBackground(new Color(76, 175, 80));
        loadButton.setForeground(Color.WHITE);
        loadButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loadButton.addActionListener(e -> handleLoadNote());
        
        revertButton = new JButton("↶ Revert");
        revertButton.setFont(new Font("Arial", Font.BOLD, 12));
        revertButton.setBackground(new Color(244, 67, 54));
        revertButton.setForeground(Color.WHITE);
        revertButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        revertButton.addActionListener(e -> handleRevert());

        buttonPanel.add(loadButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(revertButton);
        buttonPanel.add(cancelButton);

        // Layout assembly
        add(titlePanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void loadNote(Note note) {
        this.currentNote = note;
        this.originalNote = new Note(note.getTitle(), note.getContent());
        this.originalNote.setId(note.getId());
        if (note != null) {
            titleField.setText(note.getTitle());
            contentArea.setText(note.getContent());
        }
    }
    
    private void handleLoadNote() {
        if (currentNote == null) {
            JOptionPane.showMessageDialog(this, 
                "No note is currently loaded.\nGo to View Notes tab to select a note for editing.", 
                "No Note Loaded", 
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, 
                "Current Note: \"" + currentNote.getTitle() + "\"\nLoaded from database.", 
                "Note Loaded", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void handleRevert() {
        if (originalNote == null) {
            JOptionPane.showMessageDialog(this, 
                "No original version to revert to!", 
                "Revert Error", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        titleField.setText(originalNote.getTitle());
        contentArea.setText(originalNote.getContent());
        JOptionPane.showMessageDialog(this, 
            "Note reverted to original version!", 
            "Revert Success", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleUpdateNote() {
        String title = titleField.getText().trim();
        String content = contentArea.getText().trim();

        if (title.isEmpty() || content.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter both title and content!", 
                "Input Error", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (currentNote == null) {
            JOptionPane.showMessageDialog(this, 
                "No note selected for editing!", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        currentNote.setTitle(title);
        currentNote.setContent(content);

        if (dbManager.updateNote(currentNote)) {
            JOptionPane.showMessageDialog(this, 
                "Note updated successfully!", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            clearFields();
            currentNote = null;
            if (onNoteUpdated != null) {
                onNoteUpdated.run();
            }
        } else {
            JOptionPane.showMessageDialog(this, 
                "Failed to update note!", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleCancel() {
        clearFields();
        currentNote = null;
    }

    private void clearFields() {
        titleField.setText("");
        contentArea.setText("");
    }
}
