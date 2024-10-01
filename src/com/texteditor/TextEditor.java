package com.texteditor;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.PrinterException;
import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.undo.UndoManager;

public class TextEditor extends JFrame implements ActionListener {
    private JMenuBar menuBar;
    private JMenu fileMenu, editMenu, saveAndSubmitMenu;
    private JMenuItem newFileItem, openFileItem, saveFileItem, printFileItem, exitItem;
    private JMenuItem cutItem, copyItem, pasteItem, selectAllItem, undoItem, redoItem, findItem;
    private JMenuItem saveAndSubmitItem;
    private JTextArea textArea;
    private UndoManager undoManager;
    private JLabel statusBar;
    private JFileChooser fileChooser;

    public TextEditor() {
        setTitle("Enhanced Text Editor");
        setBounds(100, 100, 800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        menuBar = new JMenuBar();
        fileMenu = new JMenu("File");
        editMenu = new JMenu("Edit");
        saveAndSubmitMenu = new JMenu("Save and Submit");

        newFileItem = createMenuItem("New", fileMenu, KeyEvent.VK_N, KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK));
        openFileItem = createMenuItem("Open", fileMenu, KeyEvent.VK_O, KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));
        saveFileItem = createMenuItem("Save", fileMenu, KeyEvent.VK_S, KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
        printFileItem = createMenuItem("Print", fileMenu, KeyEvent.VK_P, KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.CTRL_DOWN_MASK));
        exitItem = createMenuItem("Exit", fileMenu, KeyEvent.VK_E, KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.CTRL_DOWN_MASK));

        cutItem = createMenuItem("Cut", editMenu, KeyEvent.VK_X, KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK));
        copyItem = createMenuItem("Copy", editMenu, KeyEvent.VK_C, KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK));
        pasteItem = createMenuItem("Paste", editMenu, KeyEvent.VK_V, KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK));
        selectAllItem = createMenuItem("Select All", editMenu, KeyEvent.VK_A, KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_DOWN_MASK));
        undoItem = createMenuItem("Undo", editMenu, KeyEvent.VK_Z, KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK));
        redoItem = createMenuItem("Redo", editMenu, KeyEvent.VK_Y, KeyStroke.getKeyStroke(KeyEvent.VK_Y, KeyEvent.CTRL_DOWN_MASK));
        findItem = createMenuItem("Find", editMenu, KeyEvent.VK_F, KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK));

        saveAndSubmitItem = createMenuItem("Save and Exit", saveAndSubmitMenu, KeyEvent.VK_Q, KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_DOWN_MASK));

        setJMenuBar(menuBar);
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(saveAndSubmitMenu);

        textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);

        textArea.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        undoManager = new UndoManager();
        textArea.getDocument().addUndoableEditListener(e -> undoManager.addEdit(e.getEdit()));

        statusBar = new JLabel("Status Bar");
        add(statusBar, BorderLayout.SOUTH);

        fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files (.txt)", "txt");
        fileChooser.setFileFilter(filter);
    }

    private JMenuItem createMenuItem(String name, JMenu menu, int mnemonic, KeyStroke accelerator) {
        JMenuItem menuItem = new JMenuItem(name);
        menuItem.setMnemonic(mnemonic);
        menuItem.setAccelerator(accelerator);
        menuItem.addActionListener(this);
        menu.add(menuItem);
        return menuItem;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        switch (command) {
            case "New":
                textArea.setText(null);
                statusBar.setText("New File");
                break;
            case "Open":
                openFile();
                break;
            case "Save":
            case "Save and Exit":
                saveFile();
                if (command.equals("Save and Exit")) {
                    System.exit(0);
                }
                break;
            case "Print":
                printFile();
                break;
            case "Exit":
                System.exit(0);
                break;
            case "Cut":
                textArea.cut();
                break;
            case "Copy":
                textArea.copy();
                break;
            case "Paste":
                textArea.paste();
                break;
            case "Select All":
                textArea.selectAll();
                break;
            case "Undo":
                if (undoManager.canUndo()) {
                    undoManager.undo();
                }
                break;
            case "Redo":
                if (undoManager.canRedo()) {
                    undoManager.redo();
                }
                break;
            case "Find":
                findText();
                break;
        }
    }

    private void openFile() {
        int action = fileChooser.showOpenDialog(null);

        if (action == JFileChooser.APPROVE_OPTION) {
            try (BufferedReader reader = new BufferedReader(new FileReader(fileChooser.getSelectedFile()))) {
                textArea.read(reader, null);
                statusBar.setText("Opened: " + fileChooser.getSelectedFile().getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveFile() {
        int action = fileChooser.showSaveDialog(null);

        if (action == JFileChooser.APPROVE_OPTION) {
            String fileName = fileChooser.getSelectedFile().getAbsolutePath();
            if (!fileName.endsWith(".txt")) {
                fileName += ".txt";
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
                textArea.write(writer);
                statusBar.setText("Saved: " + fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void printFile() {
        try {
            textArea.print();
            statusBar.setText("Printed");
        } catch (PrinterException e) {
            e.printStackTrace();
        }
    }

    private void findText() {
        String searchText = JOptionPane.showInputDialog(this, "Find:");
        if (searchText != null) {
            String text = textArea.getText();
            int index = text.indexOf(searchText);
            if (index >= 0) {
                textArea.setCaretPosition(index);
                textArea.select(index, index + searchText.length());
                textArea.grabFocus();
                statusBar.setText("Found: " + searchText);
            } else {
                statusBar.setText("Not Found: " + searchText);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            new TextEditor().setVisible(true);
        });
    }
}

