import java.util.UUID;

public class Book implements Identifiable {
    private UUID id;
    private final String title;
    private final String author;
    private final int totalCopies;
    private int availableCopies;
    private boolean isBorrowable;

    public Book(String title, String author, int totalCopies, boolean isBorrowable) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.author = author;
        this.totalCopies = totalCopies;
        this.availableCopies = totalCopies;
        this.isBorrowable = isBorrowable;
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getTotalCopies() {
        return totalCopies;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public boolean isAvailable() {
        return availableCopies > 0 && isBorrowable;
    }

    public boolean isBorrowable() {
        return isBorrowable;
    }

    public void setBorrowable(boolean borrowable) { 
        isBorrowable = borrowable;
    }

    public void loanBook() {
        if (isAvailable()) {
            availableCopies--;
        } else if (!isBorrowable) {
            throw new IllegalStateException(title + "は持ち出し禁止です。"); 
        } else {
            throw new IllegalStateException(title + "の在庫がありません。");
        }
    }

    public void returnBook() {
        if (availableCopies < totalCopies) {
            availableCopies++;
        } else {
            throw new IllegalStateException(title + "のすべての本はすでに返却済みです。");
        }
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + getId() +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", totalCopies=" + totalCopies +
                ", availableCopies=" + availableCopies +
                ", isBorrowable=" + isBorrowable +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        return equalsById(o);
    }

    @Override
    public int hashCode() {
        return hashCodeById();
    }
}
