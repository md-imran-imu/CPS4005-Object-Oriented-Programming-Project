package library;

public class Book {
    private int bookId;
    private String title;
    private String author;
    private String category;
    private String availabilityStatus; 

    public Book(int id, String title, String author, String category, String status) {
        this.bookId = id;
        this.title = title;
        this.author = author;
        this.category = category;
        this.availabilityStatus = status;
    }

    public int getBookId() { return bookId; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getCategory() { return category; }
    
    public String getAvailabilityStatus() { return availabilityStatus; }

    public void setTitle(String title) { this.title = title; }
    public void setCategory(String category) { this.category = category; }
    public void setAvailabilityStatus(String status) { this.availabilityStatus = status; }
}