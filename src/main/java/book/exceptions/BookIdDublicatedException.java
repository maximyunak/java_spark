package book.exceptions;

public class BookIdDublicatedException extends RuntimeException {
    public BookIdDublicatedException(long id) {
        super(String.format("Book with id: %d already exists", id));
    }
}
