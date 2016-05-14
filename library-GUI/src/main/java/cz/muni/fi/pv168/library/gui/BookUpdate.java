package cz.muni.fi.pv168.library.gui;

import cz.muni.fi.pv168.common.ValidationException;
import cz.muni.fi.pv168.common.Validator;
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

            try {
                Validator.validateBook(bookToUpdate);
                dialog.dispose();
            } catch (ValidationException | IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Warning", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());
    }

    public Book getData() {
        return book;
    }

    public void display() {
        dialog = new JDialog(parent, "Update Book", true);
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

    private void createUIComponents() {
        yearSpinner = new JSpinner();
        yearSpinner.setEditor(new JSpinner.NumberEditor(yearSpinner, "#"));
        pagesSpinner = new JSpinner();
        pagesSpinner.setEditor(new JSpinner.NumberEditor(pagesSpinner, "#"));
    }
}
