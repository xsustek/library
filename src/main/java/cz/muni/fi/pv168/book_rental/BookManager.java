package cz.muni.fi.pv168.book_rental;

import java.util.List;

/**
 * Created by Milan on 26.02.2016.
 */
public interface BookManager {
    void createBook(Book book);

    Book getBookById(Long id);

    List<Book> findAllBooks();

    void updateBook(Book book);

    void deleteBook(Book book);

    List<Book> findBookByAuthor();

    List<Book> findBookByTitle();

}
