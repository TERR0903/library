public class Admin extends User { 

    public Admin(String name, String userId, String passwordHash) {
        super(name, userId, passwordHash);
    }

    public void createUser(User u) {

        System.out.println("Admin " + getName() + " is attempting to create user: " + u.getName());
    }

    public void deleteUser(User u) {
        System.out.println("Admin " + getName() + " is attempting to delete user: " + u.getName());
    }

    public void createBook(Book b) {
        System.out.println("Admin " + getName() + " is attempting to create book: " + b.getTitle());
    }

    public void deleteBook(Book b) { // 仕様書にはないが追加
        System.out.println("Admin " + getName() + " is attempting to delete book: " + b.getTitle());
    }

    public void updateBook(Book b) { // 仕様書にはないが追加
        System.out.println("Admin " + getName() + " is attempting to update book: " + b.getTitle());
    }

    public void updateUser(User u) { // 仕様書にはないが追加
        System.out.println("Admin " + getName() + " is attempting to update user: " + u.getName());
    }

    @Override 
    public String toString() {
        return "Admin{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", userId='" + getUserId() + '\'' +
                '}';
    }
}
