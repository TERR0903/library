import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class User implements Identifiable {
    private UUID id;
    private String name;
    private final String userId;
    private final String passwordHash;
    private final List<LoanRecord> loanHistory;
    private int loanedCopies; 

    public User(String name, String userId, String passwordHash) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.userId = userId;
        this.passwordHash = passwordHash;
        this.loanHistory = new ArrayList<>();
        this.loanedCopies = 0;
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public boolean authenticate(String pw) {
        return this.passwordHash.equals(pw);
    }

    public List<LoanRecord> getLoanHistory() {
        return Collections.unmodifiableList(loanHistory);
    }

    public void addLoanRecord(LoanRecord record) {
        loanHistory.add(record);
        loanedCopies++;
    }

    public void removeLoanRecord(LoanRecord record) {
        if (loanHistory.remove(record)) {
            loanedCopies--;
        }
    }

    public int getLoanedCopies() {
        return loanedCopies;
    }

    public boolean canLoan() {
        return loanedCopies < 5;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", userId='" + userId + '\'' +
                ", loanedCopies=" + loanedCopies +
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
