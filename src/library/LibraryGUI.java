package library;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

public class LibraryGUI extends JFrame {
    private DatabaseHandler db = new DatabaseHandler();
    private JTable bookTable, memberTable, borrowTable;
    private JProgressBar progressBar; 

    public LibraryGUI() {
        setTitle("St Mary's Digital Library Dashboard");
        setSize(1300, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(45, 52, 54));
        header.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel lblTitle = new JLabel("ST MARY'S LIBRARY DASHBOARD");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 18));

        progressBar = new JProgressBar();
        progressBar.setIndeterminate(false);
        progressBar.setStringPainted(true);
        progressBar.setVisible(false);

        JButton btnSync = new JButton("🔄 Async Sync");
        btnSync.addActionListener(e -> refreshTablesAsync());

        header.add(lblTitle, BorderLayout.WEST);
        header.add(progressBar, BorderLayout.CENTER);
        header.add(btnSync, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Books", createBookPanel());
        tabs.addTab("Members", createMemberPanel());
        tabs.addTab("Borrowing", createBorrowingPanel());
        add(tabs, BorderLayout.CENTER);

        db.optimizeDatabase(); 
        styleTables();
        refreshTablesAsync(); 
        setVisible(true);
    }

    private void refreshTablesAsync() {
        progressBar.setVisible(true);
        progressBar.setIndeterminate(true);
        progressBar.setString("Processing Database...");

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            DefaultTableModel bookM, memberM, borrowM;

            @Override
            protected Void doInBackground() {
                bookM = db.getBooksTableModel();
                memberM = db.getMembersTableModel();
                borrowM = db.getBorrowingTableModel();
                return null;
            }

            @Override
            protected void done() {
                bookTable.setModel(bookM);
                memberTable.setModel(memberM);
                borrowTable.setModel(borrowM);
                
                bookTable.setAutoCreateRowSorter(true);
                memberTable.setAutoCreateRowSorter(true);
                borrowTable.setAutoCreateRowSorter(true);

                progressBar.setIndeterminate(false);
                progressBar.setVisible(false);
            }
        };
        worker.execute();
    }

    private JPanel createBookPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField txtSearch = new JTextField(12);
        JComboBox<String> comboFilter = new JComboBox<>(new String[]{"All", "Title", "Author", "Category"});
        JButton btnGo = new JButton("🔍 Search");
        JButton btnAdvanced = new JButton("⚙️ Multi-Search"); 
        JButton btnReset = new JButton("↺ Reset");
        
        searchBar.add(new JLabel("Find: ")); searchBar.add(txtSearch);
        searchBar.add(new JLabel(" By: ")); searchBar.add(comboFilter);
        searchBar.add(btnGo); searchBar.add(btnAdvanced); searchBar.add(btnReset);
        panel.add(searchBar, BorderLayout.NORTH);

        bookTable = new JTable(); 
        panel.add(new JScrollPane(bookTable), BorderLayout.CENTER);

        btnGo.addActionListener(e -> {
            String term = txtSearch.getText().trim();
            String criteria = (String) comboFilter.getSelectedItem();
            if (criteria.equals("All")) bookTable.setModel(db.searchBooksGUI(term));
            else bookTable.setModel(db.filterBooksGUI(term, criteria.toLowerCase()));
        });
        btnAdvanced.addActionListener(e -> handleAdvancedSearch());
        btnReset.addActionListener(e -> { txtSearch.setText(""); comboFilter.setSelectedIndex(0); refreshTablesAsync(); });

        JPanel controls = new JPanel();
        JButton btnAdd = new JButton("Add Book"); JButton btnUpdate = new JButton("Update"); JButton btnDelete = new JButton("Delete");
        btnAdd.addActionListener(e -> handleAddBook()); btnUpdate.addActionListener(e -> handleUpdateBook()); btnDelete.addActionListener(e -> handleDeleteBook());
        controls.add(btnAdd); controls.add(btnUpdate); controls.add(btnDelete);
        panel.add(controls, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createMemberPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField txtSearch = new JTextField(15);
        JComboBox<String> comboFilter = new JComboBox<>(new String[]{"All", "Name", "Email", "Type"});
        JButton btnGo = new JButton("🔍 Search");
        JButton btnReset = new JButton("↺ Reset");
        searchBar.add(new JLabel("Find: ")); searchBar.add(txtSearch);
        searchBar.add(new JLabel(" By: ")); searchBar.add(comboFilter);
        searchBar.add(btnGo); searchBar.add(btnReset);
        panel.add(searchBar, BorderLayout.NORTH);

        memberTable = new JTable(); 
        panel.add(new JScrollPane(memberTable), BorderLayout.CENTER);

        btnGo.addActionListener(e -> {
            String term = txtSearch.getText().trim();
            String criteria = ((String) comboFilter.getSelectedItem()).toLowerCase();
            if (criteria.equals("all")) memberTable.setModel(db.searchMembersGUI(term));
            else {
                if (criteria.equals("name")) criteria = "member_name";
                if (criteria.equals("type")) criteria = "membership_type";
                memberTable.setModel(db.filterMembersGUI(term, criteria));
            }
        });
        btnReset.addActionListener(e -> { txtSearch.setText(""); comboFilter.setSelectedIndex(0); refreshTablesAsync(); });

        JPanel controls = new JPanel();
        JButton btnAdd = new JButton("Register Member"); JButton btnUpdate = new JButton("Update"); JButton btnDelete = new JButton("Delete");
        btnAdd.addActionListener(e -> handleAddMember()); btnUpdate.addActionListener(e -> handleUpdateMember()); btnDelete.addActionListener(e -> handleDeleteMember());
        controls.add(btnAdd); controls.add(btnUpdate); controls.add(btnDelete);
        panel.add(controls, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createBorrowingPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField txtStart = new JTextField("2026-05-01", 8);
        JTextField txtEnd = new JTextField("2026-05-31", 8);
        JButton btnFilter = new JButton("📅 Range Filter"); 
        JButton btnOverdue = new JButton("⚠️ Overdue Only"); 
        JButton btnReset = new JButton("↺ Reset");
        filterBar.add(new JLabel("From:")); filterBar.add(txtStart);
        filterBar.add(new JLabel(" To:")); filterBar.add(txtEnd);
        filterBar.add(btnFilter); filterBar.add(btnOverdue); filterBar.add(btnReset);
        panel.add(filterBar, BorderLayout.NORTH);

        borrowTable = new JTable();
        panel.add(new JScrollPane(borrowTable), BorderLayout.CENTER);

        btnFilter.addActionListener(e -> {
            String start = txtStart.getText().trim(); String end = txtEnd.getText().trim();
            if (isValidDate(start) && isValidDate(end)) borrowTable.setModel(db.getBorrowingByDateRange(start, end));
            else showError("Use YYYY-MM-DD format.");
        });
        btnOverdue.addActionListener(e -> {
            borrowTable.setModel(db.getOverdueLoans());
            JOptionPane.showMessageDialog(this, "🚨 Displaying all active overdue records.", "Overdue Identification", JOptionPane.WARNING_MESSAGE);
        });
        btnReset.addActionListener(e -> refreshTablesAsync());

        JPanel controls = new JPanel();
        JButton btnIssue = new JButton("Issue Loan"); JButton btnReturn = new JButton("Mark Returned"); JButton btnDelete = new JButton("Delete Record");
        btnIssue.addActionListener(e -> handleIssueLoan());
        btnReturn.addActionListener(e -> {
    int row = borrowTable.getSelectedRow();
    
    if (row == -1) {
        JOptionPane.showMessageDialog(this, "Please select a borrowing record from the table first.", "Selection Error", JOptionPane.WARNING_MESSAGE);
        return; 
    }
    
    int recordId = (int) borrowTable.getValueAt(row, 0);
    db.updateReturnStatus(recordId, "Returned");
    refreshTablesAsync();
    
    JOptionPane.showMessageDialog(this, "Book successfully marked as returned!", "Success", JOptionPane.INFORMATION_MESSAGE);
});
        btnDelete.addActionListener(e -> handleDeleteBorrowRecord());

        controls.add(btnIssue); controls.add(btnReturn); controls.add(btnDelete);
        panel.add(controls, BorderLayout.SOUTH);
        return panel;
    }

    private void styleTables() {
        Color selectColor = new Color(173, 216, 230);
        JTable[] tables = {bookTable, memberTable, borrowTable};
        for (JTable t : tables) {
            t.setRowHeight(25); t.setGridColor(Color.LIGHT_GRAY);
            t.setSelectionBackground(selectColor); t.setSelectionForeground(Color.BLACK);
        }
    }

    private void handleAdvancedSearch() {
        JPanel f = new JPanel(new GridLayout(3, 2, 5, 5)); JTextField t = new JTextField(), a = new JTextField(), c = new JTextField();
        f.add(new JLabel("Title contains:")); f.add(t);
        f.add(new JLabel("Author contains:")); f.add(a);
        f.add(new JLabel("Category contains:")); f.add(c);
        if (JOptionPane.showConfirmDialog(this, f, "Multi-Attribute Search", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            bookTable.setModel(db.searchBooksMulti(t.getText().trim(), a.getText().trim(), c.getText().trim()));
        }
    }

    private void showError(String msg) { JOptionPane.showMessageDialog(this, msg, "System Error", JOptionPane.ERROR_MESSAGE); }
    private boolean isValidEmail(String email) { return email.matches("^[A-Za-z0-9+_.-]+@(.+)$"); }
    private boolean isValidDate(String date) { try { java.time.LocalDate.parse(date); return true; } catch (Exception e) { return false; } }

private void handleAddBook() {
    JPanel f = new JPanel(new GridLayout(4, 2, 5, 5)); 
    JTextField t = new JTextField(), a = new JTextField(), c = new JTextField();
    JComboBox<String> s = new JComboBox<>(new String[]{"Available", "Borrowed"});
    
    f.add(new JLabel("Title:")); f.add(t); 
    f.add(new JLabel("Author:")); f.add(a); 
    f.add(new JLabel("Category:")); f.add(c); 
    f.add(new JLabel("Status:")); f.add(s);
    
    if (JOptionPane.showConfirmDialog(this, f, "New Book Entry", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
        String title = t.getText().trim();
        String author = a.getText().trim();
        String category = c.getText().trim();
        String status = (String) s.getSelectedItem();
        
        if (title.isEmpty() || author.isEmpty() || category.isEmpty() || status == null) {
            JOptionPane.showMessageDialog(this, 
                "Submission Denied: All fields (Title, Author, Category, and Status) are required.", 
                "Validation Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        String result = db.addBook(title, author, category, status);
        
        if (result.equals("Success")) {
            refreshTablesAsync();
            JOptionPane.showMessageDialog(this, "Book added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, result, "Entry Denied", JOptionPane.WARNING_MESSAGE);
        }
    }
}

private void handleUpdateBook() {
    int r = bookTable.getSelectedRow(); 
    if (r == -1) {
        JOptionPane.showMessageDialog(this, "Please select a book from the table first.", "Selection Error", JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    try {
        int id = Integer.parseInt(bookTable.getValueAt(r, 0).toString());
        String currentTitle = bookTable.getValueAt(r, 1).toString();
        String currentCategory = bookTable.getValueAt(r, 3).toString();
        String currentStatus = bookTable.getValueAt(r, 4).toString();

        JPanel f = new JPanel(new GridLayout(4, 2)); 
        JTextField idF = new JTextField(String.valueOf(id)); 
        idF.setEditable(false); idF.setBackground(Color.LIGHT_GRAY);
        
        JTextField t = new JTextField(currentTitle);
        JTextField c = new JTextField(currentCategory);
        JComboBox<String> s = new JComboBox<>(new String[]{"Available", "Borrowed"});
        s.setSelectedItem(currentStatus);

        f.add(new JLabel("Book ID:")); f.add(idF); 
        f.add(new JLabel("New Title:")); f.add(t); 
        f.add(new JLabel("New Category:")); f.add(c); 
        f.add(new JLabel("New Status:")); f.add(s);

        if (JOptionPane.showConfirmDialog(this, f, "Modify Book", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            boolean success = db.updateBookDetails(id, t.getText().trim(), c.getText().trim(), (String) s.getSelectedItem());
            
            if (success) {
                refreshTablesAsync(); // Reload the table if successful
            } else {
                showError("Database update failed. Check console for details.");
            }
        }
    } catch (Exception ex) {
        showError("Error reading table data: " + ex.getMessage());
    }
}

    private void handleDeleteBook() {
        int r = bookTable.getSelectedRow(); 
    if (r == -1) {
        JOptionPane.showMessageDialog(this, "Please select a book from the table first.", "Selection Error", JOptionPane.WARNING_MESSAGE);
        return;
    }
        int id = (int) bookTable.getValueAt(r, 0);
        if (JOptionPane.showConfirmDialog(this, "Permanently delete ID " + id + "?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            db.deleteBook(id);
            refreshTablesAsync();
            JOptionPane.showMessageDialog(this, "Book deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

private void handleAddMember() {
    JPanel f = new JPanel(new GridLayout(3, 2, 5, 5)); 
    JTextField n = new JTextField(), e = new JTextField(); 
    JComboBox<String> t = new JComboBox<>(new String[]{"Student", "Staff"});
    
    f.add(new JLabel("Full Name:")); f.add(n); 
    f.add(new JLabel("Email Address:")); f.add(e); 
    f.add(new JLabel("Membership:")); f.add(t);
    
    if (JOptionPane.showConfirmDialog(this, f, "Register Member", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
        String name = n.getText().trim();
        String email = e.getText().trim();
        String type = (String) t.getSelectedItem();

        if (name.isEmpty() || email.isEmpty() || type == null) {
            JOptionPane.showMessageDialog(this, 
                "Registration Failed: All fields are required.", 
                "Validation Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            JOptionPane.showMessageDialog(this, 
                "Invalid Email: Please enter a valid email address (e.g., name@stmarys.ac.uk).", 
                "Format Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        db.addMember(name, email, type);
        refreshTablesAsync();
        JOptionPane.showMessageDialog(this, "Member registered successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}

private void handleUpdateMember() {
    int r = memberTable.getSelectedRow(); 
    if (r == -1) {
        JOptionPane.showMessageDialog(this, "Please select a member from the table first.", "Selection Error", JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    try {
        // Match indices to your table: 0=ID, 1=Name, 2=Email, 3=Type
        int id = Integer.parseInt(memberTable.getValueAt(r, 0).toString());
        String currentName = memberTable.getValueAt(r, 1).toString();
        String currentEmail = memberTable.getValueAt(r, 2).toString();
        String currentType = memberTable.getValueAt(r, 3).toString();

        JPanel f = new JPanel(new GridLayout(4, 2, 5, 5)); 
        JTextField nameF = new JTextField(currentName);
        JTextField emailF = new JTextField(currentEmail);
        JComboBox<String> typeC = new JComboBox<>(new String[]{"Student", "Staff"});
        typeC.setSelectedItem(currentType);

        f.add(new JLabel("Member ID:")); f.add(new JLabel(String.valueOf(id))); 
        f.add(new JLabel("Name:")); f.add(nameF); 
        f.add(new JLabel("Email:")); f.add(emailF); 
        f.add(new JLabel("Type:")); f.add(typeC);

        if (JOptionPane.showConfirmDialog(this, f, "Update Member Details", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            String newName = nameF.getText().trim();
            String newEmail = emailF.getText().trim();
            String newType = (String) typeC.getSelectedItem();

            // Validation: Fields cannot be empty [cite: 254]
            if (newName.isEmpty() || newEmail.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name and Email are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validation: Email format [cite: 91, 257]
            if (!newEmail.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                JOptionPane.showMessageDialog(this, "Please enter a valid email format.", "Format Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean success = db.updateMember(id, newName, newEmail, newType);
            if (success) {
                refreshTablesAsync(); 
                JOptionPane.showMessageDialog(this, "Member updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                showError("Update failed. Check if the database is locked.");
            }
        }
    } catch (Exception ex) {
        showError("Error: " + ex.getMessage());
    }
}

private void handleDeleteMember() {
    int r = memberTable.getSelectedRow();
    if (r == -1) {
        JOptionPane.showMessageDialog(this, "Please select a member to delete.", "Selection Error", JOptionPane.WARNING_MESSAGE);
        return;
    }

    int id = (int) memberTable.getValueAt(r, 0);
    int confirm = JOptionPane.showConfirmDialog(this, "Permanently delete Member ID: " + id + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
    
    if (confirm == JOptionPane.YES_OPTION) {
        boolean success = db.deleteMember(id);
        if (success) {
            refreshTablesAsync();
            JOptionPane.showMessageDialog(this, "Member removed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            showError("Could not delete member. They may have active borrowing records.");
        }
    }
}

private void handleIssueLoan() {
    // Set up Date Spinners for a professional UI[cite: 3, 9]
    SpinnerDateModel borrowModel = new SpinnerDateModel();
    JSpinner borrowSpinner = new JSpinner(borrowModel);
    JSpinner.DateEditor borrowEditor = new JSpinner.DateEditor(borrowSpinner, "yyyy-MM-dd");
    borrowSpinner.setEditor(borrowEditor);

    SpinnerDateModel dueModel = new SpinnerDateModel();
    JSpinner dueSpinner = new JSpinner(dueModel);
    JSpinner.DateEditor dueEditor = new JSpinner.DateEditor(dueSpinner, "yyyy-MM-dd");
    dueSpinner.setEditor(dueEditor);

    JPanel f = new JPanel(new GridLayout(4, 2, 5, 5)); 
    JTextField b = new JTextField(), m = new JTextField();
    
    f.add(new JLabel("Book ID:")); f.add(b); 
    f.add(new JLabel("Member ID:")); f.add(m); 
    f.add(new JLabel("Issue Date:")); f.add(borrowSpinner); 
    f.add(new JLabel("Return By:")); f.add(dueSpinner);
    
    if (JOptionPane.showConfirmDialog(this, f, "Issue Book Loan", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
        String bookIdStr = b.getText().trim();
        String memIdStr = m.getText().trim();
        
        // 1. Check for empty fields
        if (bookIdStr.isEmpty() || memIdStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int bookId = Integer.parseInt(bookIdStr);
            int memId = Integer.parseInt(memIdStr);
            
            // 2. Format dates from spinners[cite: 4, 9]
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
            String bDate = sdf.format(borrowSpinner.getValue());
            String dDate = sdf.format(dueSpinner.getValue());

            // 3. Date Logic Validation: Due date must be after borrow date
            if (dueModel.getDate().before(borrowModel.getDate())) {
                JOptionPane.showMessageDialog(this, "Due date cannot be earlier than the issue date.", "Logic Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            db.recordLoan(bookId, memId, bDate, dDate);
            refreshTablesAsync();
            JOptionPane.showMessageDialog(this, "Loan issued successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Book and Member IDs must be numeric.", "Format Error", JOptionPane.ERROR_MESSAGE);
        } catch (LibraryException ex) {
            showError(ex.getMessage());
        }
    }
}

private void handleDeleteBorrowRecord() {
    // 1. Selection Check
    int row = borrowTable.getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(this, 
            "Please select a borrowing record from the table first.", 
            "Selection Error", 
            JOptionPane.WARNING_MESSAGE);
        return;
    }

    // 2. Extract Record ID from Column 0
    int recordId = (int) borrowTable.getValueAt(row, 0);

    // 3. Confirmation Dialogue (Assessment Requirement)
    int confirm = JOptionPane.showConfirmDialog(this, 
        "Are you sure you want to delete borrowing record #" + recordId + "?", 
        "Confirm Deletion", 
        JOptionPane.YES_NO_OPTION);
    
    if (confirm == JOptionPane.YES_OPTION) {
        try {
            boolean success = db.deleteBorrowRecord(recordId);
            
            if (success) {
                refreshTablesAsync(); 
                JOptionPane.showMessageDialog(this, 
                    "Borrowing record deleted successfully.", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                showError("Database failed to delete the record.");
            }
        } catch (Exception ex) {
            showError("System Error: " + ex.getMessage());
        }
    }
}

    public static void main(String[] args) { new LibraryGUI(); }
}