package cz.muni.fi.pv168.library.gui;

import cz.muni.fi.pv168.library.Book;

import javax.swing.*;

/**
 * Created by Milan on 25.04.2016.
 */
public class BookUpdate {
    private JButton updateButton;
    private JButton cancelButton;
    private JTextField textField1;
    private JTextField textField2;
    private JSpinner yearSpinner;
    private JSpinner pagesSpinner;
    private Book book;
    private JFrame parent;

    public BookUpdate(Book bookToUpdate, JFrame parent) {
        book = bookToUpdate;
        this.parent = parent;
    }

}
