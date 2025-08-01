import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class Library {
    private final Map<UUID, Book> books;
    private final Map<UUID, User> users;
    private final List<LoanRecord> records;

    public Library() {
        this.books = new HashMap<>();
        this.users = new HashMap<>();
        this.records = new ArrayList<>();
    }

    public void createBook(Book book) {
        if (books.containsKey(book.getId())) {
            throw new IllegalArgumentException("同じIDの本が既に存在します。");
        }
        books.put(book.getId(), book);
    }

    public Book findBookById(UUID bookId) {
        return books.get(bookId);
    }

    public List<Book> getAllBooks() {
        return new ArrayList<>(books.values());
    }

    public List<Book> findBooks(String query) {
        if (query == null || query.trim().isEmpty()) {
            return books.values().stream()
                        .filter(Book::isBorrowable) 
                        .collect(Collectors.toList());
        }
        String lowerCaseQuery = query.toLowerCase();
        return books.values().stream()
                .filter(book -> book.isBorrowable() && 
                        (book.getTitle().toLowerCase().contains(lowerCaseQuery) ||
                        book.getAuthor().toLowerCase().contains(lowerCaseQuery)))
                .collect(Collectors.toList());
    }

    public void updateBook(Book book) {
        if (!books.containsKey(book.getId())) {
            throw new IllegalArgumentException("更新対象の本が見つかりません。");
        }
        books.put(book.getId(), book);
    }

    public void deleteBook(Book book) {
        if (!books.containsKey(book.getId())) {
            throw new IllegalArgumentException("削除対象の本が見つかりません。");
        }
        boolean hasActiveLoans = records.stream()
                .filter(LoanRecord::isActive)
                .anyMatch(record -> record.getBook().equals(book));
        if (hasActiveLoans) {
            throw new IllegalStateException("この本は貸出中のため削除できません。");
        }
        books.remove(book.getId());
    }

    public void createUser(User user) {
        if (users.containsKey(user.getId()) || users.values().stream().anyMatch(u -> u.getUserId().equals(user.getUserId()))) {
            throw new IllegalArgumentException("同じIDまたはユーザーIDの利用者が既に存在します。");
        }
        users.put(user.getId(), user);
    }

    public User findUserById(UUID userId) {
        return users.get(userId);
    }

    public User findUserByUserId(String userId) {
        return users.values().stream()
                .filter(u -> u.getUserId().equals(userId))
                .findFirst()
                .orElse(null);
    }

    public List<User> findUsers(String query) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>(users.values());
        }
        String lowerCaseQuery = query.toLowerCase();
        return users.values().stream()
                .filter(user -> user.getName().toLowerCase().contains(lowerCaseQuery) ||
                        user.getUserId().toLowerCase().contains(lowerCaseQuery))
                .collect(Collectors.toList());
    }

    public void updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            throw new IllegalArgumentException("更新対象の利用者が見つかりません。");
        }
        users.put(user.getId(), user);
    }

    public void deleteUser(User user) {
        if (!users.containsKey(user.getId())) {
            throw new IllegalArgumentException("削除対象の利用者が見つかりません。");
        }
        boolean hasActiveLoans = records.stream()
                .filter(LoanRecord::isActive)
                .anyMatch(record -> record.getUser().equals(user));
        if (hasActiveLoans) {
            throw new IllegalStateException("この利用者は貸出中の本があるため削除できません。");
        }
        users.remove(user.getId());
    }

    public LoanRecord loanBook(UUID userId, UUID bookId) {
        User user = users.get(userId);
        if (user == null) {
            throw new IllegalArgumentException("指定された利用者は存在しません。");
        }
        Book book = books.get(bookId);
        if (book == null) {
            throw new IllegalArgumentException("指定された蔵書は存在しません。");
        }

        if (!book.isBorrowable()) { 
            throw new IllegalStateException(book.getTitle() + "は持ち出し禁止のため貸出できません。");
        }
        if (!user.canLoan()) {
            throw new IllegalStateException(user.getName() + "はこれ以上本を借りられません。");
        }
        if (!book.isAvailable()) { 
            throw new IllegalStateException(book.getTitle() + "は現在貸出できません。");
        }

        LoanRecord loanRecord = new LoanRecord(book, user);
        book.loanBook();
        user.addLoanRecord(loanRecord);
        records.add(loanRecord);
        return loanRecord;
    }

    public void returnBook(UUID loanRecordId) {
        LoanRecord recordToReturn = records.stream()
                .filter(record -> record.getId().equals(loanRecordId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("指定された貸出記録が見つかりません。"));

        if (!recordToReturn.isActive()) {
            throw new IllegalStateException("この貸出記録は既に返却済みです。");
        }

        recordToReturn.markReturned(LocalDate.now());
        recordToReturn.getBook().returnBook();
        recordToReturn.getUser().removeLoanRecord(recordToReturn);
    }

    public List<LoanRecord> getAllLoanRecords() {
        return new ArrayList<>(records);
    }
}
