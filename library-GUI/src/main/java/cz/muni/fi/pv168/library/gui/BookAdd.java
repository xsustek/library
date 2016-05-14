package cz.muni.fi.pv168.library.gui;

import cz.muni.fi.pv168.common.ValidationException;
import cz.muni.fi.pv168.common.Validator;
import cz.muni.fi.pv168.library.Book;

import javax.swing.*;
import java.text.ParseException;
import java.time.LocalDate;

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

            try {
                Validator.validateBook(book);
                dialog.dispose();
            } catch (ValidationException | IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Warning", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> {
            dialog.dispose();
        });
    }


    public void display() {
        dialog = new JDialog(parent, "Add Book", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setContentPane(main);
        dialog.pack();
        dialog.setVisible(true);
    }

    public Book getData() {
        return book;
    }

    private void createUIComponents() {
        yearSpinner = new JSpinner(new SpinnerNumberModel(LocalDate.now().getYear(),
                0, LocalDate.now().getYear() + 20, 1));
        yearSpinner.setEditor(new JSpinner.NumberEditor(yearSpinner, "#"));
        pagesSpinner = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
        pagesSpinner.setEditor(new JSpinner.NumberEditor(pagesSpinner, "#"));
    }
}
