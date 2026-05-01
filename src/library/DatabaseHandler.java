package library;

import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class DatabaseHandler {
    private static final String URL = "jdbc:sqlite:st_marys_library.db";

    public Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL);
        } catch (SQLException e) {
            System.err.println("❌ Critical Database Error: " + e.getMessage());
        }
        return conn;
    }

    public void optimizeDatabase() {
        String[] indexQueries = {
            "CREATE INDEX IF NOT EXISTS idx_book_title ON books(title)",
            "CREATE INDEX IF NOT EXISTS idx_member_name ON members(member_name)",
            "CREATE INDEX IF NOT EXISTS idx_borrow_date ON borrow_records(borrow_date)"
        };
        try (Connection conn = this.connect(); Statement stmt = conn.createStatement()) {
            for (String q : indexQueries) stmt.execute(q);
        } catch (SQLException e) { System.out.println(e.getMessage()); }
    }

    public DefaultTableModel getBooksTableModel() {
        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Title", "Author", "Category", "Status"}, 0);
        String sql = "SELECT book_id, title, author, category, availability_status FROM books";
        try (Connection conn = this.connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                model.addRow(new Object[]{rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5)});
            }
        } catch (SQLException e) { System.out.println(e.getMessage()); }
        return model;
    }

    public DefaultTableModel getMembersTableModel() {
        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Name", "Email", "Type"}, 0);
        String sql = "SELECT member_id, member_name, email, membership_type FROM members";
        try (Connection conn = this.connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                model.addRow(new Object[]{rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4)});
            }
        } catch (SQLException e) { System.out.println(e.getMessage()); }
        return model;
    }

    public DefaultTableModel getBorrowingTableModel() {
        DefaultTableModel model = new DefaultTableModel(new String[]{"Record ID", "Book Title", "Member Name", "Borrow Date", "Due Date", "Status"}, 0);
        String sql = "SELECT r.record_id, b.title, m.member_name, r.borrow_date, r.due_date, r.return_status " +
                     "FROM borrow_records r JOIN books b ON r.book_id = b.book_id JOIN members m ON r.member_id = m.member_id";
        try (Connection conn = this.connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                model.addRow(new Object[]{rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6)});
            }
        } catch (SQLException e) { System.out.println(e.getMessage()); }
        return model;
    }

    public DefaultTableModel searchBooksMulti(String title, String author, String category) {
        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Title", "Author", "Category", "Status"}, 0);
        String sql = "SELECT book_id, title, author, category, availability_status FROM books WHERE title LIKE ? AND author LIKE ? AND category LIKE ?";
        try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + title + "%");
            pstmt.setString(2, "%" + author + "%");
            pstmt.setString(3, "%" + category + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5)});
            }
        } catch (SQLException e) { System.out.println(e.getMessage()); }
        return model;
    }

    public DefaultTableModel getBorrowingByDateRange(String startDate, String endDate) {
        DefaultTableModel model = new DefaultTableModel(new String[]{"Record ID", "Book Title", "Member Name", "Borrow Date", "Due Date", "Status"}, 0);
        String sql = "SELECT r.record_id, b.title, m.member_name, r.borrow_date, r.due_date, r.return_status " +
                     "FROM borrow_records r JOIN books b ON r.book_id = b.book_id " +
                     "JOIN members m ON r.member_id = m.member_id " +
                     "WHERE r.borrow_date BETWEEN ? AND ?";
        try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, startDate); pstmt.setString(2, endDate);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6)});
            }
        } catch (SQLException e) { System.out.println(e.getMessage()); }
        return model;
    }

    public DefaultTableModel getOverdueLoans() {
        DefaultTableModel model = new DefaultTableModel(new String[]{"Record ID", "Book Title", "Member Name", "Due Date", "Status"}, 0);
        String sql = "SELECT r.record_id, b.title, m.member_name, r.due_date, r.return_status " +
                     "FROM borrow_records r JOIN books b ON r.book_id = b.book_id " +
                     "JOIN members m ON r.member_id = m.member_id " +
                     "WHERE r.due_date < date('now') AND r.return_status = 'Borrowed'";
        try (Connection conn = this.connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                model.addRow(new Object[]{rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5)});
            }
        } catch (SQLException e) { System.out.println(e.getMessage()); }
        return model;
    }

    public DefaultTableModel filterBooksGUI(String term, String column) {
        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Title", "Author", "Category", "Status"}, 0);
        String sql = "SELECT book_id, title, author, category, availability_status FROM books WHERE " + column + " LIKE ?";
        try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + term + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5)});
            }
        } catch (SQLException e) { System.out.println(e.getMessage()); }
        return model;
    }

    public DefaultTableModel searchBooksGUI(String term) {
        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Title", "Author", "Category", "Status"}, 0);
        String sql = "SELECT book_id, title, author, category, availability_status FROM books WHERE book_id = ? OR title LIKE ? OR author LIKE ? OR category LIKE ?";
        try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            int searchId = -1; try { searchId = Integer.parseInt(term); } catch (NumberFormatException e) {}
            pstmt.setInt(1, searchId); pstmt.setString(2, "%" + term + "%"); pstmt.setString(3, "%" + term + "%"); pstmt.setString(4, "%" + term + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5)});
            }
        } catch (SQLException e) { System.out.println(e.getMessage()); }
        return model;
    }

    public DefaultTableModel filterMembersGUI(String term, String column) {
        DefaultTableModel model = new DefaultTableModel(new String[] { "ID", "Name", "Email", "Type" }, 0);
        String sql = "SELECT member_id, member_name, email, membership_type FROM members WHERE " + column + " LIKE ?";
        try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + term + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[] { rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4) });
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return model;
    }
    
    public DefaultTableModel searchBorrowingRecords(String term, String startDate, String endDate) {
    DefaultTableModel model = new DefaultTableModel(new String[]{"Record ID", "Book Title", "Member Name", "Borrow Date", "Due Date", "Status"}, 0);
    
    String sql = "SELECT r.record_id, b.title, m.member_name, r.borrow_date, r.due_date, r.return_status " +
                 "FROM borrow_records r JOIN books b ON r.book_id = b.book_id " +
                 "JOIN members m ON r.member_id = m.member_id " +
                 "WHERE (m.member_name LIKE ? OR b.title LIKE ?) " +
                 "AND r.borrow_date BETWEEN ? AND ?";

    try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, "%" + term + "%");
        pstmt.setString(2, "%" + term + "%");
        pstmt.setString(3, startDate);
        pstmt.setString(4, endDate);
        
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            model.addRow(new Object[]{rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6)});
        }
    } catch (SQLException e) { 
        System.out.println("Search Error: " + e.getMessage()); 
    }
    return model;
}

    public DefaultTableModel searchMembersGUI(String term) {
        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Name", "Email", "Type"}, 0);
        String sql = "SELECT member_id, member_name, email, membership_type FROM members WHERE member_id = ? OR member_name LIKE ? OR email LIKE ?";
        try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            int searchId = -1; try { searchId = Integer.parseInt(term); } catch (NumberFormatException e) {}
            pstmt.setInt(1, searchId); pstmt.setString(2, "%" + term + "%"); pstmt.setString(3, "%" + term + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4)});
            }
        } catch (SQLException e) { System.out.println(e.getMessage()); }
        return model;
    }

public synchronized String addBook(String title, String author, String category, String status) {
    String checkSql = "SELECT COUNT(*) FROM books WHERE LOWER(title) = LOWER(?) AND LOWER(author) = LOWER(?)";
    String insertSql = "INSERT INTO books(title, author, category, availability_status) VALUES(?,?,?,?)";
    
    try (Connection conn = this.connect()) {
        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setString(1, title);
            checkStmt.setString(2, author);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return "Duplicate Error: This book title by this author already exists.";
            }
        }

        try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
            insertStmt.setString(1, title);
            insertStmt.setString(2, author);
            insertStmt.setString(3, category);
            insertStmt.setString(4, status);
            insertStmt.executeUpdate();
            return "Success";
        }
    } catch (SQLException e) {
        return "Database Error: " + e.getMessage();
    }
}

public synchronized boolean updateBookDetails(int id, String title, String category, String status) {
    String sql = "UPDATE books SET title = ?, category = ?, availability_status = ? WHERE book_id = ?";
    
    try (Connection conn = this.connect(); 
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setString(1, title);
        pstmt.setString(2, category);
        pstmt.setString(3, status); // This matches 'availability_status'
        pstmt.setInt(4, id);
        
        int rowsAffected = pstmt.executeUpdate();
        return rowsAffected > 0; // Crucial: This returns true so the GUI knows to refresh
        
    } catch (SQLException e) {
        System.err.println("SQL Update Error: " + e.getMessage());
        return false;
    }
}

    public synchronized void deleteBook(int id) {
        String sql = "DELETE FROM books WHERE book_id = ?";
        try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id); pstmt.executeUpdate();
        } catch (SQLException e) { System.out.println(e.getMessage()); }
    }

    public synchronized void addMember(String name, String email, String type) {
        String sql = "INSERT INTO members(member_name, email, membership_type) VALUES(?,?,?)";
        try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name); pstmt.setString(2, email); pstmt.setString(3, type);
            pstmt.executeUpdate();
        } catch (SQLException e) { System.out.println(e.getMessage()); }
    }

public synchronized boolean updateMember(int id, String name, String email, String type) {
    String sql = "UPDATE members SET member_name = ?, email = ?, membership_type = ? WHERE member_id = ?";
    try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, name);
        pstmt.setString(2, email);
        pstmt.setString(3, type);
        pstmt.setInt(4, id);
        return pstmt.executeUpdate() > 0;
    } catch (SQLException e) { 
        System.err.println("Member Update Error: " + e.getMessage());
        return false; 
    }
}

public synchronized boolean deleteMember(int id) {
    String sql = "DELETE FROM members WHERE member_id = ?";
    try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setInt(1, id);
        return pstmt.executeUpdate() > 0;
    } catch (SQLException e) { 
        System.err.println("Member Delete Error: " + e.getMessage());
        return false; 
    }
}

    public void recordLoan(int bookId, int memId, String bDate, String dDate) throws LibraryException {
        String checkBookSql = "SELECT availability_status FROM books WHERE book_id = ?";
        String checkMemberSql = "SELECT member_id FROM members WHERE member_id = ?";
        String insertLoanSql = "INSERT INTO borrow_records(book_id, member_id, borrow_date, due_date, return_status) VALUES(?,?,?,?,'Borrowed')";
        String updateBookSql = "UPDATE books SET availability_status = 'Borrowed' WHERE book_id = ?";
        try (Connection conn = this.connect()) {
            if (conn == null) throw new LibraryException("Database connection failed.");
            try (PreparedStatement pstmt = conn.prepareStatement(checkBookSql)) {
                pstmt.setInt(1, bookId); ResultSet rs = pstmt.executeQuery();
                if (!rs.next()) throw new LibraryException("❌ Error: Book ID " + bookId + " does not exist.");
                if (rs.getString(1).equalsIgnoreCase("Borrowed")) throw new LibraryException("❌ Error: Book already taken.");
            }
            try (PreparedStatement pstmt = conn.prepareStatement(checkMemberSql)) {
                pstmt.setInt(1, memId); ResultSet rs = pstmt.executeQuery();
                if (!rs.next()) throw new LibraryException("❌ Error: Member ID " + memId + " not found.");
            }
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(insertLoanSql)) {
                pstmt.setInt(1, bookId); pstmt.setInt(2, memId); pstmt.setString(3, bDate); pstmt.setString(4, dDate); pstmt.executeUpdate();
            }
            try (PreparedStatement pstmt = conn.prepareStatement(updateBookSql)) {
                pstmt.setInt(1, bookId); pstmt.executeUpdate();
            }
            conn.commit();
        } catch (SQLException e) { throw new LibraryException("DB Transaction failed."); }
    }

    public synchronized void updateReturnStatus(int recordId, String status) {
        String findBookSql = "SELECT book_id FROM borrow_records WHERE record_id = ?";
        String updateRecordSql = "UPDATE borrow_records SET return_status = ? WHERE record_id = ?";
        String updateBookSql = "UPDATE books SET availability_status = 'Available' WHERE book_id = ?";
        try (Connection conn = this.connect()) {
            PreparedStatement updateStmt = conn.prepareStatement(updateRecordSql);
            updateStmt.setString(1, status); updateStmt.setInt(2, recordId); updateStmt.executeUpdate();
            if (status.equalsIgnoreCase("Returned")) {
                PreparedStatement findStmt = conn.prepareStatement(findBookSql);
                findStmt.setInt(1, recordId); ResultSet rs = findStmt.executeQuery();
                if (rs.next()) {
                    PreparedStatement bookStmt = conn.prepareStatement(updateBookSql);
                    bookStmt.setInt(1, rs.getInt(1)); bookStmt.executeUpdate();
                }
            }
        } catch (SQLException e) { System.out.println(e.getMessage()); }
    }

public synchronized boolean deleteBorrowRecord(int recordId) {
    String sql = "DELETE FROM borrow_records WHERE record_id = ?";
    
    try (Connection conn = this.connect(); 
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setInt(1, recordId);
        int affectedRows = pstmt.executeUpdate();
        
        return affectedRows > 0; 
        
    } catch (SQLException e) {
        System.err.println("❌ DELETE ERROR: " + e.getMessage());
        return false;
    }
}
}