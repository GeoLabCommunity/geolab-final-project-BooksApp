package ge.geolab.bookswap.models;

/**
 * Created by dalkh on 28-Dec-15.
 */
public class Book {
    private String title,author,description;
    public Book(){}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Book(String title, String author, String description){
        this.author=author;
        this.title=title;
        this.description=description;

    }
}
