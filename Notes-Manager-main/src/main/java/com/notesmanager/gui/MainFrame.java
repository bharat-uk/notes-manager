package com.notesmanager.gui;

import com.notesmanager.database.DatabaseManager;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * MainFrame is the main application window
 * Uses JTabbedPane to manage multiple panels
 */
public class MainFrame extends JFrame {
    private DatabaseManager dbManager;
    private AddNotePanel addNotePanel;
    private ViewNotesPanel viewNotesPanel;
    private EditNotePanel editNotePanel;
    private JTabbedPane tabbedPane;
    private JLabel statusLabel;

    public MainFrame() {
        setTitle("Notes Manager - Document Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 650);
        setLocationRelativeTo(null);
        setResizable(true);
        setIconImage(createAppIcon());

        // Initialize database
        dbManager = new DatabaseManager();
        dbManager.connect();

        // Initialize UI
        initializeUI();

        // Handle window close
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                handleExit();
            }
        });
    }

    private void initializeUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 13));

        // Add Note Panel
        addNotePanel = new AddNotePanel(dbManager, () -> {
            // Callback when note is added - refresh view notes
            if (viewNotesPanel != null) {
                viewNotesPanel.loadNotes();
            }
            updateStatusBar();
        });

        // Edit Note Panel
        editNotePanel = new EditNotePanel(dbManager, () -> {
            // Callback when note is updated - refresh view notes
            if (viewNotesPanel != null) {
                viewNotesPanel.loadNotes();
            }
            updateStatusBar();
        });

        // View Notes Panel
        viewNotesPanel = new ViewNotesPanel(dbManager, this::updateStatusBar);

        // Add tabs
        tabbedPane.addTab("➕ Add Note", new ImageIcon(), addNotePanel, "Add a new note");
        tabbedPane.addTab("✏️  Edit Note", new ImageIcon(), editNotePanel, "Edit existing note");
        tabbedPane.addTab("📋 View Notes", new ImageIcon(), viewNotesPanel, "View and manage notes");

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        // Header panel with app title and toolbar
        JPanel headerPanel = new JPanel(new BorderLayout(5, 5));
        headerPanel.setBackground(new Color(33, 150, 243));
        headerPanel.setPreferredSize(new Dimension(0, 60));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        JLabel titleLabel = new JLabel("📝 Notes Manager");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        
        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        toolbarPanel.setBackground(new Color(33, 150, 243));
        
        JButton helpButton = new JButton("❓ Help");
        helpButton.setFont(new Font("Arial", Font.BOLD, 11));
        helpButton.setBackground(new Color(255, 193, 7));
        helpButton.setForeground(Color.BLACK);
        helpButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        helpButton.addActionListener(e -> showHelp());
        
        JButton settingsButton = new JButton("⚙️  Settings");
        settingsButton.setFont(new Font("Arial", Font.BOLD, 11));
        settingsButton.setBackground(new Color(156, 39, 176));
        settingsButton.setForeground(Color.WHITE);
        settingsButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        settingsButton.addActionListener(e -> showSettings());
        
        JButton aboutButton = new JButton("ℹ️  About");
        aboutButton.setFont(new Font("Arial", Font.BOLD, 11));
        aboutButton.setBackground(new Color(0, 150, 136));
        aboutButton.setForeground(Color.WHITE);
        aboutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        aboutButton.addActionListener(e -> showAbout());
        
        toolbarPanel.add(helpButton);
        toolbarPanel.add(settingsButton);
        toolbarPanel.add(aboutButton);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(toolbarPanel, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Footer panel with status
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(new Color(240, 240, 240));
        footerPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        footerPanel.add(statusLabel, BorderLayout.WEST);
        
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private Image createAppIcon() {
        // Create a simple icon (can be enhanced with actual icon file)
        return new ImageIcon().getImage();
    }

    private void handleExit() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to exit?", 
            "Exit Confirmation", 
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            dbManager.disconnect();
            System.exit(0);
        }
    }
    
    private void showHelp() {
        String helpText = "NOTES MANAGER - HELP\n\nFeatures:\n\n" +
            "1. ADD NOTE - Create new notes with title and content\n" +
            "2. EDIT NOTE - Modify existing notes\n" +
            "3. VIEW NOTES - Search, sort, and manage all notes\n" +
            "4. EXPORT - Save notes to files\n" +
            "5. SEARCH - Find notes by keywords\n\n" +
            "Tips:\n" +
            "• Use Search to quickly find notes\n" +
            "• Sort notes by title or date\n" +
            "• Export important notes for backup\n" +
            "• Use categories to organize notes\n";
        
        JTextArea textArea = new JTextArea(helpText);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("Arial", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        
        JOptionPane.showMessageDialog(this, scrollPane, "Help", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showSettings() {
        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));
        settingsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JCheckBox autoSaveCheckBox = new JCheckBox("Enable Auto-Save", true);
        JCheckBox darkModeCheckBox = new JCheckBox("Dark Mode (Future)", false);
        JCheckBox notificationsCheckBox = new JCheckBox("Enable Notifications", true);
        
        autoSaveCheckBox.setFont(new Font("Arial", Font.PLAIN, 12));
        darkModeCheckBox.setFont(new Font("Arial", Font.PLAIN, 12));
        notificationsCheckBox.setFont(new Font("Arial", Font.PLAIN, 12));
        
        settingsPanel.add(autoSaveCheckBox);
        settingsPanel.add(darkModeCheckBox);
        settingsPanel.add(notificationsCheckBox);
        
        JOptionPane.showMessageDialog(this, settingsPanel, "Settings", JOptionPane.PLAIN_MESSAGE);
    }
    
    private void showAbout() {
        String aboutText = "Notes Manager v1.0\n\n" +
            "A simple and elegant document management system\n" +
            "for organizing your notes and ideas.\n\n" +
            "© 2024 Notes Manager\n" +
            "All rights reserved.\n\n" +
            "Built with Java Swing";
        
        JOptionPane.showMessageDialog(this, aboutText, "About Notes Manager", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void updateStatusBar() {
        if (statusLabel != null) {
            int totalNotes = dbManager.getAllNotes().size();
            statusLabel.setText("Total Notes: " + totalNotes + " | Status: Ready");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
