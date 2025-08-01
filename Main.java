import java.util.List;


public class Main {
    public static void main(String[] args) {
        // 図書館システムのインスタンスを作成
        Library library = new Library();
        System.out.println("--- 図書館システム運用開始 ---");

        // 1. 管理者と利用者の登録
        System.out.println("\n--- 1. 管理者と利用者の登録 ---");
        Admin admin1 = new Admin("乙津昂光海", "otsuakiomi", "adminpass");
        User user1 = new User("岩井優弥", "iwaiyuya", "userpass1");
        User user2 = new User("松村拓", "matsumuratakumi", "userpass2");

        try {
            library.createUser(admin1);
            System.out.println("管理者 " + admin1.getName() + " を登録しました。");
            library.createUser(user1);
            System.out.println("利用者 " + user1.getName() + " を登録しました。");
            library.createUser(user2);
            System.out.println("利用者 " + user2.getName() + " を登録しました。");
        } catch (IllegalArgumentException e) {
            System.err.println("利用者登録エラー: " + e.getMessage());
        }

        // 2. 蔵書の登録
        System.out.println("\n--- 2. 蔵書の登録 ---");
        Book book1 = new Book("空想科学読本", "柳田理科雄", 3, true);
        Book book2 = new Book("国宝", "吉田修一", 2, true);
        Book book3 = new Book("君の膵臓をたべたい", "住野よる", 1, true);
        Book book4 = new Book("広辞苑", "岩波新書", 1, false);

        try {
            library.createBook(book1);
            System.out.println("蔵書: " + book1.getTitle() + " を登録しました。 (在庫: " + book1.getAvailableCopies() + ", 持ち出し可: " + book1.isBorrowable() + ")");
            library.createBook(book2);
            System.out.println("蔵書: " + book2.getTitle() + " を登録しました。 (在庫: " + book2.getAvailableCopies() + ", 持ち出し可: " + book2.isBorrowable() + ")");
            library.createBook(book3);
            System.out.println("蔵書: " + book3.getTitle() + " を登録しました。 (在庫: " + book3.getAvailableCopies() + ", 持ち出し可: " + book3.isBorrowable() + ")");
            library.createBook(book4);
            System.out.println("蔵書: " + book4.getTitle() + " を登録しました。 (在庫: " + book4.getAvailableCopies() + ", 持ち出し可: " + book4.isBorrowable() + ")");
        } catch (IllegalArgumentException e) {
            System.err.println("蔵書登録エラー: " + e.getMessage());
        }

        // 全蔵書を表示
        System.out.println("\n--- 現在の蔵書一覧 (持ち出し可能な本のみ) ---");
        library.findBooks("").forEach(book ->
                System.out.println("  - " + book.getTitle() + " by " + book.getAuthor() + " (利用可能: " + book.getAvailableCopies() + "/" + book.getTotalCopies() + ", 持ち出し可: " + book.isBorrowable() + ")")
        );

        // 3. 蔵書の検索
        System.out.println("\n--- 3. 蔵書の検索 (タイトル: Refactoring - 持ち出し禁止) ---");
        List<Book> foundBooksRefactoring = library.findBooks("Refactoring");
        if (!foundBooksRefactoring.isEmpty()) {
            foundBooksRefactoring.forEach(book -> System.out.println("  - 見つかった本: " + book.getTitle() + " (" + book.getAuthor() + ")"));
        } else {
            System.out.println("指定されたタイトルを含む持ち出し可能な本は見つかりませんでした。");
        }


        // 4. 蔵書の貸出
        System.out.println("\n--- 4. 蔵書の貸出 ---");
        LoanRecord loan1 = null;
        LoanRecord loan2 = null;
        LoanRecord loan3 = null;
        LoanRecord loan4 = null;

        try {
            System.out.println(user1.getName() + "が " + book1.getTitle() + " を借りようとしています。");
            loan1 = library.loanBook(user1.getId(), book1.getId());
            System.out.println("貸出成功: " + loan1);
            System.out.println(book1.getTitle() + "の現在の在庫: " + book1.getAvailableCopies());
            System.out.println(user1.getName() + "の貸出中の本数: " + user1.getLoanedCopies());

            System.out.println(user1.getName() + "が " + book2.getTitle() + " を借りようとしています。");
            loan2 = library.loanBook(user1.getId(), book2.getId());
            System.out.println("貸出成功: " + loan2);
            System.out.println(book2.getTitle() + "の現在の在庫: " + book2.getAvailableCopies());
            System.out.println(user1.getName() + "の貸出中の本数: " + user1.getLoanedCopies());

            System.out.println(user2.getName() + "が " + book3.getTitle() + " を借りようとしています。");
            loan3 = library.loanBook(user2.getId(), book3.getId());
            System.out.println("貸出成功: " + loan3);
            System.out.println(book3.getTitle() + "の現在の在庫: " + book3.getAvailableCopies());
            System.out.println(user2.getName() + "の貸出中の本数: " + user2.getLoanedCopies());

            // 持ち出し禁止の本を借りる試行
            System.out.println(user1.getName() + "が持ち出し禁止の " + book4.getTitle() + " を借りようとしています。");
            loan4 = library.loanBook(user1.getId(), book4.getId());
            System.out.println("貸出成功: " + loan4);
        } catch (IllegalStateException | IllegalArgumentException e) {
            System.err.println("貸出エラー: " + e.getMessage());
        }

        // 5. 貸出履歴の表示
        System.out.println("\n--- 5. 貸出履歴の表示 ---");
        System.out.println(user1.getName() + "の貸出履歴:");
        user1.getLoanHistory().forEach(record -> System.out.println("  - " + record));

        System.out.println(user2.getName() + "の貸出履歴:");
        user2.getLoanHistory().forEach(record -> System.out.println("  - " + record));

        System.out.println("全利用者の貸出履歴:");
        library.getAllLoanRecords().forEach(record -> System.out.println("  - " + record));


        // 6. 蔵書の返却
        System.out.println("\n--- 6. 蔵書の返却 ---");
        try {
            if (loan1 != null) {
                System.out.println(user1.getName() + "が " + loan1.getBook().getTitle() + " を返却しようとしています。");
                library.returnBook(loan1.getId());
                System.out.println("返却成功: " + loan1);
                System.out.println(loan1.getBook().getTitle() + "の現在の在庫: " + loan1.getBook().getAvailableCopies());
                System.out.println(user1.getName() + "の貸出中の本数: " + user1.getLoanedCopies());
            }

            if (loan3 != null) {
                System.out.println(user2.getName() + "が " + loan3.getBook().getTitle() + " を返却しようとしています。");
                library.returnBook(loan3.getId());
                System.out.println("返却成功: " + loan3);
                System.out.println(loan3.getBook().getTitle() + "の現在の在庫: " + loan3.getBook().getAvailableCopies());
                System.out.println(user2.getName() + "の貸出中の本数: " + user2.getLoanedCopies());
            }

            // 既に返却済みの本を再度返却する試行
            if (loan1 != null) {
                System.out.println(user1.getName() + "が " + loan1.getBook().getTitle() + " を再度返却しようとしています。");
                library.returnBook(loan1.getId());
            }
        } catch (IllegalStateException | IllegalArgumentException e) {
            System.err.println("返却エラー: " + e.getMessage());
        }

        // 7. 利用者情報の編集・削除 (管理者権限)
        System.out.println("\n--- 7. 利用者情報の編集・削除 (管理者権限) ---");
        try {
            System.out.println("管理者(" + admin1.getName() + ")が利用者 " + user1.getName() + " の名前を編集します。");
            user1.setName("岩井優弥 (編集済)");
            library.updateUser(user1);
            System.out.println("利用者情報更新成功: " + library.findUserById(user1.getId()));

            System.out.println("管理者(" + admin1.getName() + ")が利用者 " + user2.getName() + " を削除しようとしています。");
            // user2は現在本を借りていないため削除可能
            library.deleteUser(user2);
            System.out.println("利用者 " + user2.getName() + " を削除しました。");

            System.out.println("現在の利用者一覧:");
            library.findUsers("").forEach(user -> System.out.println("  - " + user));

            System.out.println("管理者(" + admin1.getName() + ")が本 " + book4.getTitle() + " の貸出可否を編集します。");
            book4.setBorrowable(true); // 持ち出し可能にする
            library.updateBook(book4);
            System.out.println("蔵書情報更新成功: " + library.findBookById(book4.getId()));

            System.out.println("管理者(" + admin1.getName() + ")が本 " + book4.getTitle() + " を削除しようとしています。");
            library.deleteBook(book4);
            System.out.println("蔵書 " + book4.getTitle() + " を削除しました。");

            System.out.println("現在の蔵書一覧:");
            library.findBooks("").forEach(book ->
                    System.out.println("  - " + book.getTitle() + " by " + book.getAuthor() + " (利用可能: " + book.getAvailableCopies() + "/" + book.getTotalCopies() + ", 持ち出し可: " + book.isBorrowable() + ")")
            );

        } catch (IllegalStateException | IllegalArgumentException e) {
            System.err.println("利用者/蔵書管理エラー: " + e.getMessage());
        }

        System.out.println("\n--- 図書館システム運用終了 ---");
    }
}
