package cz.muni.fi.pv168.library.gui;

import cz.muni.fi.pv168.library.Book;

import javax.swing.*;
import java.text.ParseException;

/**
 * Created by Milan on 25.04.2016.
 */
public class BookUpdate {
    private JButton updateButton;
    private JButton cancelButton;
    private JTextField tfTitle;
    private JTextField tfAuthor;
    private JSpinner yearSpinner;
    private JSpinner pagesSpinner;
    private JPanel main;
    private Book book;
    private JFrame parent;
    private JDialog dialog;

    public BookUpdate(JFrame parent, Book bookToUpdate) {
        book = bookToUpdate;
        this.parent = parent;

        updateButton.addActionListener(e -> {
            try {
                yearSpinner.commitEdit();
                pagesSpinner.commitEdit();
            } catch (ParseException e1) {
                e1.printStackTrace();
            }

            book.setTitle(tfTitle.getText());
            book.setAuthor(tfAuthor.getText());
            book.setReleaseYear((Integer) yearSpinner.getValue());
            book.setPages(Integer.parseInt(pagesSpinner.getValue().toString()));

            dialog.dispose();
        });
    }

    public Book getData() {
        return book;
    }

    public void display() {
        dialog = new JDialog(parent, true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setContentPane(main);

        setFields();

        dialog.pack();
        dialog.setVisible(true);
    }

    private void setFields() {
        tfTitle.setText(book.getTitle());
        tfAuthor.setText(book.getAuthor());
        yearSpinner.setValue(book.getReleaseYear());
        pagesSpinner.setValue(book.getPages());
    }

}
