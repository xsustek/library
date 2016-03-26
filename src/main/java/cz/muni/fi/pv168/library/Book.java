package cz.muni.fi.pv168.library;

import java.util.Date;
import java.util.Objects;

/**
 * Created by Milan on 26.02.2016.
 */
public class Book {
    private Long id;
    private String title;
    private int pages;
    private Date releaseYear;
    private String author;

    public Book() {
    }

    public String getAuthor(){
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public Date getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(Date releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getTitle() {

        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Book{"
                + "id=" + id
                + ", title=" + title
                + ", author=" + author
                + ", releaseYear=" + releaseYear
                + ", pages=" + pages;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (this != o && this.id == null) {
            return false;
        }

        final Book book = (Book) o;

        return Objects.equals(this.id, book.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.id);
    }
}
