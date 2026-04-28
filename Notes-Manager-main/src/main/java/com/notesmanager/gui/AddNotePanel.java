package com.notesmanager.gui;

import com.notesmanager.database.DatabaseManager;
import com.notesmanager.model.Note;

import javax.swing.*;
import java.awt.*;

/**
 * AddNotePanel provides the GUI form for adding new notes
 * Uses Java Swing components
 */
public class AddNotePanel extends JPanel {
    private JTextField titleField;
    private JTextArea contentArea;
    private JComboBox<String> categoryCombo;
    private JComboBox<String> priorityCombo;
    private JButton addButton;
    private JButton clearButton;
    private JButton saveAsTemplateButton;
    private DatabaseManager dbManager;
    private Runnable onNoteAdded;

    public AddNotePanel(DatabaseManager dbManager, Runnable onNoteAdded) {
        this.dbManager = dbManager;
        this.onNoteAdded = onNoteAdded;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder("Add New Note"));
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
        
        // Category and Priority Panel
        JPanel metaPanel = new JPanel(new GridLayout(1, 4, 10, 5));
        metaPanel.setBackground(new Color(240, 240, 240));
        
        JLabel categoryLabel = new JLabel("Category:");
        categoryLabel.setFont(new Font("Arial", Font.BOLD, 11));
        String[] categories = {"General", "Work", "Personal", "Ideas", "Todo", "Important"};
        categoryCombo = new JComboBox<>(categories);
        categoryCombo.setFont(new Font("Arial", Font.PLAIN, 11));
        
        JLabel priorityLabel = new JLabel("Priority:");
        priorityLabel.setFont(new Font("Arial", Font.BOLD, 11));
        String[] priorities = {"Normal", "High", "Low", "Urgent"};
        priorityCombo = new JComboBox<>(priorities);
        priorityCombo.setFont(new Font("Arial", Font.PLAIN, 11));
        
        metaPanel.add(categoryLabel);
        metaPanel.add(categoryCombo);
        metaPanel.add(priorityLabel);
        metaPanel.add(priorityCombo);

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
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 10));
        buttonPanel.setBackground(new Color(240, 240, 240));
        
        addButton = new JButton("✓ Add Note");
        addButton.setFont(new Font("Arial", Font.BOLD, 12));
        addButton.setBackground(new Color(76, 175, 80));
        addButton.setForeground(Color.WHITE);
        addButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addButton.addActionListener(e -> handleAddNote());

        clearButton = new JButton("🔄 Clear");
        clearButton.setFont(new Font("Arial", Font.BOLD, 12));
        clearButton.setBackground(new Color(158, 158, 158));
        clearButton.setForeground(Color.WHITE);
        clearButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearButton.addActionListener(e -> handleClear());
        
        saveAsTemplateButton = new JButton("💾 Save as Template");
        saveAsTemplateButton.setFont(new Font("Arial", Font.BOLD, 12));
        saveAsTemplateButton.setBackground(new Color(63, 81, 181));
        saveAsTemplateButton.setForeground(Color.WHITE);
        saveAsTemplateButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveAsTemplateButton.addActionListener(e -> handleSaveAsTemplate());

        buttonPanel.add(addButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(saveAsTemplateButton);

        // Layout assembly
        add(titlePanel, BorderLayout.NORTH);
        add(metaPanel, BorderLayout.NORTH);
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(titlePanel, BorderLayout.NORTH);
        topPanel.add(metaPanel, BorderLayout.SOUTH);
        topPanel.setBackground(new Color(240, 240, 240));
        
        add(topPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void handleAddNote() {
        String title = titleField.getText().trim();
        String content = contentArea.getText().trim();
        String category = (String) categoryCombo.getSelectedItem();
        String priority = (String) priorityCombo.getSelectedItem();

        if (title.isEmpty() || content.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter both title and content!", 
                "Input Error", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        String enrichedContent = "[Category: " + category + "] [Priority: " + priority + "]\n\n" + content;
        Note note = new Note(title, enrichedContent);
        if (dbManager.addNote(note)) {
            JOptionPane.showMessageDialog(this, 
                "Note added successfully!\nCategory: " + category + "\nPriority: " + priority, 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            clearFields();
            if (onNoteAdded != null) {
                onNoteAdded.run();
            }
        } else {
            JOptionPane.showMessageDialog(this, 
                "Failed to add note!", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleClear() {
        titleField.setText("");
        contentArea.setText("");
        categoryCombo.setSelectedIndex(0);
        priorityCombo.setSelectedIndex(0);
        titleField.requestFocus();
    }

    private void clearFields() {
        titleField.setText("");
        contentArea.setText("");
        categoryCombo.setSelectedIndex(0);
        priorityCombo.setSelectedIndex(0);
        titleField.requestFocus();
    }
    
    private void handleSaveAsTemplate() {
        String title = titleField.getText().trim();
        String content = contentArea.getText().trim();
        
        if (title.isEmpty() && content.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter some content to save as template!", 
                "Empty Template", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JOptionPane.showMessageDialog(this, 
            "Template saved!\nTitle: " + (title.isEmpty() ? "[No Title]" : title) + "\nThis feature will persist templates in future versions.", 
            "Template Saved", 
            JOptionPane.INFORMATION_MESSAGE);
    }
}
