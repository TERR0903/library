import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;


public class AddBookGUI extends JFrame {
    private final Library library; 

  
    private final JTextField titleField;
    private final JTextField authorField;
    private final JTextField totalCopiesField;
    private final JCheckBox isBorrowableCheckBox;
    private final JButton addButton;
    private final JTextArea resultArea;

    private final BookPreviewComponent previewComponent;

    public AddBookGUI() {
        super("〇〇図書館　貸し出し図書管理ページ"); 
        library = new Library();

        library.createBook(new Book("広辞苑", "岩波書店", 5, false));
        library.createBook(new Book("君の膵臓をたべたい", "住野よる", 3, true));

       
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        setSize(700, 500); 
        setLocationRelativeTo(null); 
        setLayout(new BorderLayout(10, 10)); 

       
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("新しい本の情報"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); 
        gbc.fill = GridBagConstraints.HORIZONTAL; 

        
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        inputPanel.add(new JLabel("タイトル:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        titleField = new JTextField(20);
        inputPanel.add(titleField, gbc);

        
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        inputPanel.add(new JLabel("著者:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        authorField = new JTextField(20);
        inputPanel.add(authorField, gbc);

     
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        inputPanel.add(new JLabel("総冊数:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        totalCopiesField = new JTextField(5);
        inputPanel.add(totalCopiesField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        isBorrowableCheckBox = new JCheckBox("持ち出し可能");
        inputPanel.add(isBorrowableCheckBox, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.EAST;
        addButton = new JButton("本を追加");
        inputPanel.add(addButton, gbc);

        previewComponent = new BookPreviewComponent();
        previewComponent.setPreferredSize(new Dimension(200, 150)); // サイズ指定
        previewComponent.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        previewComponent.setBackground(new Color(245, 245, 245));

        titleField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { updatePreview(); }
            @Override public void removeUpdate(DocumentEvent e) { updatePreview(); }
            @Override public void changedUpdate(DocumentEvent e) { updatePreview(); }
        });
        authorField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { updatePreview(); }
            @Override public void removeUpdate(DocumentEvent e) { updatePreview(); }
            @Override public void changedUpdate(DocumentEvent e) { updatePreview(); }
        });


        resultArea = new JTextArea(10, 30);
        resultArea.setEditable(false);
        resultArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("追加された本の情報"));


        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.add(inputPanel, BorderLayout.NORTH);
        leftPanel.add(previewComponent, BorderLayout.CENTER); 

        add(leftPanel, BorderLayout.WEST);
        add(scrollPane, BorderLayout.CENTER); 

        addButton.addActionListener(e -> addBook());

        refreshBookList();
    }

    private void updatePreview() {
        previewComponent.setBookTitle(titleField.getText());
        previewComponent.setAuthorName(authorField.getText());
    }

    private void addBook() {
        try {
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();
            int totalCopies = Integer.parseInt(totalCopiesField.getText().trim());
            boolean isBorrowable = isBorrowableCheckBox.isSelected();

            if (title.isEmpty() || author.isEmpty() || totalCopies <= 0) {
                JOptionPane.showMessageDialog(this, "タイトル、著者、総冊数を正しく入力してください。", "入力エラー", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Book newBook = new Book(title, author, totalCopies, isBorrowable);
            library.createBook(newBook);

            JOptionPane.showMessageDialog(this, "本を追加しました: " + title);

            clearForm();
            refreshBookList();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "総冊数は数値を入力してください。", "入力エラー", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "エラー", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        titleField.setText("");
        authorField.setText("");
        totalCopiesField.setText("");
        isBorrowableCheckBox.setSelected(false);
        updatePreview(); 
    }

    private void refreshBookList() {
    
        List<Book> allBooks = library.getAllBooks(); 
        StringBuilder sb = new StringBuilder();
        sb.append("--- 現在の蔵書 ---\n");
        if (allBooks.isEmpty()) {
            sb.append("現在、登録されている本はありません。\n");
        } else {
            for (Book book : allBooks) {
                sb.append(book.getTitle()).append(":").append(book.getAuthor())
                .append(" (在庫: ").append(book.getAvailableCopies()).append("/").append(book.getTotalCopies()).append(")");
                if (!book.isBorrowable()) {
                    sb.append(" (持ち出し不可)");
                }
            sb.append("\n");
        }
    }
    resultArea.setText(sb.toString());
}

    public static void main(String[] args) {
      
        SwingUtilities.invokeLater(() -> {
            AddBookGUI gui = new AddBookGUI();
            gui.setVisible(true);
        });
    }

   
    class BookPreviewComponent extends JComponent {
        private String bookTitle = "タイトル入力待ち";
        private String authorName = "著者入力待ち";

        public void setBookTitle(String title) {
  
            this.bookTitle = title.isEmpty() ? "タイトル入力待ち" : title;
            repaint(); 
        }

        public void setAuthorName(String author) {
            this.authorName = author.isEmpty() ? "著者入力待ち" : author;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2d = (Graphics2D) g;
        
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();

            g2d.setColor(getBackground());
            g2d.fillRect(0, 0, width, height);

            g2d.setColor(new Color(173, 216, 230, 180)); 
            g2d.fillRoundRect(15, 15, width - 30, height - 30, 20, 20); 

            g2d.setColor(Color.BLUE);
            g2d.setFont(new Font("SansSerif", Font.BOLD, 16));
            FontMetrics fmTitle = g2d.getFontMetrics();
            int titleWidth = fmTitle.stringWidth(bookTitle);
            int titleX = (width - titleWidth) / 2;
            int titleY = height / 2 - fmTitle.getHeight() / 2 - 10; 

            g2d.drawString(bookTitle, titleX, titleY);

            g2d.setColor(Color.GRAY);
            g2d.setFont(new Font("SansSerif", Font.PLAIN, 12));
            FontMetrics fmAuthor = g2d.getFontMetrics();
            int authorWidth = fmAuthor.stringWidth(authorName);
            int authorX = (width - authorWidth) / 2;
            int authorY = titleY + fmTitle.getHeight() + 5;

            g2d.drawString(authorName, authorX, authorY);
        }
    }
}
