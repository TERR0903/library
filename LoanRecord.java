import java.time.LocalDate;
import java.util.UUID;

public class LoanRecord implements Identifiable {
    private UUID id;
    private final Book book;
    private final User user; 
    private final LocalDate loanDate;
    private LocalDate returnDate;

    public LoanRecord(Book book, User user) {
        this.id = UUID.randomUUID();
        this.book = book;
        this.user = user;
        this.loanDate = LocalDate.now();
        this.returnDate = null; 
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public void setId(UUID id) {
        this.id = id;
    }

    public Book getBook() {
        return book;
    }

    public User getUser() {
        return user;
    }

    public LocalDate getLoanDate() {
        return loanDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void markReturned(LocalDate returnDate) {
        if (this.returnDate == null) {
            this.returnDate = returnDate;
        } else {
            throw new IllegalStateException("この貸出記録は既に返却済みです。");
        }
    }

    public boolean isActive() {
        return returnDate == null;
    }

    @Override
    public String toString() {
        return "LoanRecord{" +
                "id=" + getId() +
                ", book=" + book.getTitle() +
                ", user=" + user.getName() +
                ", loanDate=" + loanDate +
                ", returnDate=" + returnDate +
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
