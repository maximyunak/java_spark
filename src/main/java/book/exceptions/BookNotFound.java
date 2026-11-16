package book.exceptions;

public class BookNotFound extends RuntimeException {
    public BookNotFound(long id) {
        super(String.format("Book with id=%d not found", id));
    }
}
