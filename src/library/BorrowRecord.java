package library;


public class BorrowRecord {
    private int recordId;
    private int bookId;
    private int memberId;
    private String borrowDate;
    private String dueDate;
    private String status;

    public BorrowRecord(int rId, int bId, int mId, String bDate, String dDate, String status) {
        this.recordId = rId;
        this.bookId = bId;
        this.memberId = mId;
        this.borrowDate = bDate;
        this.dueDate = dDate;
        this.status = status;
    }
}
