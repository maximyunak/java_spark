package book.entity;

public class Book {
    private final long id;
    private final String name;
    private final String author;

    public Book(long id, String name, String author) {
        this.id = id;
        this.name = name;
        this.author = author;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public Book withName(String newName) {
        return new Book(this.id, newName, this.author);
    }

    public Book withAuthor(String newAuthor) {
        return new Book(this.id, this.name, newAuthor);
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", author='" + author + '\'' +
                '}';
    }
}
