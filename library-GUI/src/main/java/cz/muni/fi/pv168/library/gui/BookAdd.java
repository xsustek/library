package cz.muni.fi.pv168.library.gui;

import cz.muni.fi.pv168.library.Book;

import javax.swing.*;
import java.text.ParseException;

/**
 * Created by robert on 25.4.2016.
 */
public class BookAdd {
    private JButton cancelButton;
    private JTextField tfAuthor;
    private JTextField tfTitle;
    private JButton addButton;
    private JSpinner pagesSpinner;
    private JSpinner yearSpinner;
    private JPanel main;
    private JDialog dialog;
    private JFrame parent;
    private Book book;

    public BookAdd(JFrame parent) {
        this.parent = parent;
    }


    public void display() {
        dialog = new JDialog(parent, true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setContentPane(main);
        addButton.addActionListener(e -> {
            try {
                yearSpinner.commitEdit();
                pagesSpinner.commitEdit();
            } catch (ParseException e1) {
                e1.printStackTrace();
            }

            book = new Book();
            book.setTitle(tfTitle.getText());
            book.setAuthor(tfAuthor.getText());
            book.setReleaseYear((Integer) yearSpinner.getValue());
            book.setPages(Integer.parseInt(pagesSpinner.getValue().toString()));

            dialog.dispose();
        });
        dialog.pack();
        dialog.setVisible(true);
    }

    public Book getData() {
        return book;
    }
}
