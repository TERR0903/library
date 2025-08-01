import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.UUID;
import java.util.Vector;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class Main extends JFrame {

    private final Library library;
    private JTextArea logTextArea;
    private JTable bookTable;
    private DefaultTableModel bookTableModel;

    private User currentUser;
    private Book sampleBook1, sampleBook2;

    public Main() {
        super("図書館管理システム");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        library = new Library();
        initializeData();

        initComponents();
        updateBookTable();

        setVisible(true);
    }

    private void initializeData() {
        System.out.println("--- 図書館システム運用開始 ---");

        Admin admin1 = new Admin("乙津昂光海", "otsuakiomi", "adminpass");
        User user1 = new User("岩井優弥", "iwaiyuya", "userpass1");
        User user2 = new User("松村拓", "matsumurahiraku", "userpass2");

        try {
            library.createUser(admin1);
            log("管理者 " + admin1.getName() + " を登録しました。");
            library.createUser(user1);
            log("利用者 " + user1.getName() + " を登録しました。");
            library.createUser(user2);
            log("利用者 " + user2.getName() + " を登録しました。");
            this.currentUser = user1;
        } catch (IllegalArgumentException e) {
            logError("利用者登録エラー: " + e.getMessage());
        }

        sampleBook1 = new Book("君の膵臓をたべたい", "住野よる", 3, true);
        sampleBook2 = new Book("空想科学読本", "柳田理科雄", 2, true);
        Book book3 = new Book("国宝", "吉田修一", 1, true);
        Book book4 = new Book("広辞苑", "岩波書店", 1, false);

        try {
            library.createBook(sampleBook1);
            log("蔵書: " + sampleBook1.getTitle() + " を登録しました。 (在庫: " + sampleBook1.getAvailableCopies() + ")");
            library.createBook(sampleBook2);
            log("蔵書: " + sampleBook2.getTitle() + " を登録しました。 (在庫: " + sampleBook2.getAvailableCopies() + ")");
            library.createBook(book3);
            log("蔵書: " + book3.getTitle() + " を登録しました。 (在庫: " + book3.getAvailableCopies() + ")");
            library.createBook(book4);
            log("蔵書: " + book4.getTitle() + " を登録しました。 (在庫: " + book4.getAvailableCopies() + ")");
        } catch (IllegalArgumentException e) {
            logError("蔵書登録エラー: " + e.getMessage());
        }
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refreshButton = new JButton("蔵書一覧を更新");
        JButton loanButton = new JButton("選択した本を貸出す");
        JButton returnButton = new JButton("選択した本を返却");

        topPanel.add(refreshButton);
        topPanel.add(loanButton);
        topPanel.add(returnButton);
        add(topPanel, BorderLayout.NORTH);

        String[] columnNames = {"ID", "タイトル", "著者", "総冊数", "利用可能数", "持ち出し可否"};
        bookTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        bookTable = new JTable(bookTableModel);
        JScrollPane tableScrollPane = new JScrollPane(bookTable);
        add(tableScrollPane, BorderLayout.CENTER);

        logTextArea = new JTextArea(10, 50);
        logTextArea.setEditable(false);
        JScrollPane logScrollPane = new JScrollPane(logTextArea);
        log("ログ出力エリア:\n");
        add(logScrollPane, BorderLayout.SOUTH);

        refreshButton.addActionListener(e -> updateBookTable());

        loanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = bookTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(Main.this, "貸出する本を選択してください。", "エラー", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                UUID bookId = (UUID) bookTableModel.getValueAt(selectedRow, 0);
                try {
                    if (currentUser == null) {
                         logError("テストユーザーが設定されていません。");
                         return;
                    }
                    LoanRecord loanRecord = library.loanBook(currentUser.getId(), bookId);
                    log("貸出成功: " + loanRecord.getBook().getTitle() + " を " + currentUser.getName() + " が借りました。");
                    updateBookTable();
                } catch (IllegalStateException | IllegalArgumentException ex) {
                    logError("貸出エラー: " + ex.getMessage());
                    JOptionPane.showMessageDialog(Main.this, ex.getMessage(), "貸出エラー", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        returnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
                if (currentUser == null || currentUser.getLoanHistory().isEmpty()) {
                    JOptionPane.showMessageDialog(Main.this, "返却可能な本がありません。", "情報", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                List<LoanRecord> activeLoans = currentUser.getLoanHistory().stream()
                        .filter(LoanRecord::isActive)
                        .collect(Collectors.toList());

                if (activeLoans.isEmpty()) {
                     JOptionPane.showMessageDialog(Main.this, "現在、貸出中の本はありません。", "情報", JOptionPane.INFORMATION_MESSAGE);
                     return;
                }

                LoanRecord selectedLoan = (LoanRecord) JOptionPane.showInputDialog(
                    Main.this,
                    "返却する貸出記録を選択してください:",
                    "返却",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    activeLoans.toArray(),
                    activeLoans.get(0));

                if (selectedLoan != null) {
                    try {
                        library.returnBook(selectedLoan.getId());
                        log("返却成功: " + selectedLoan.getBook().getTitle() + " が " + selectedLoan.getUser().getName() + " に返却されました。");
                        updateBookTable();
                    } catch (IllegalStateException | IllegalArgumentException ex) {
                        logError("返却エラー: " + ex.getMessage());
                        JOptionPane.showMessageDialog(Main.this, ex.getMessage(), "返却エラー", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
    }

    private void updateBookTable() {
        bookTableModel.setRowCount(0);
        List<Book> books = library.findBooks(null);
        for (Book book : books) {
            Vector<Object> row = new Vector<>();
            row.add(book.getId());
            row.add(book.getTitle());
            row.add(book.getAuthor());
            row.add(book.getTotalCopies());
            row.add(book.getAvailableCopies());
            row.add(book.isBorrowable() ? "はい" : "いいえ");
            bookTableModel.addRow(row);
        }
        log("蔵書一覧を更新しました。表示数: " + books.size());
    }

    private void log(String message) {
        SwingUtilities.invokeLater(() -> {
            logTextArea.append(message + "\n");
            logTextArea.setCaretPosition(logTextArea.getDocument().getLength());
        });
    }

    private void logError(String message) {
        log("エラー: " + message);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main());
    }
}
