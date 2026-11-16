package book.service;

import book.dto.PatchBookDTO;
import book.entity.Book;
import book.exceptions.BookCreateException;
import book.exceptions.BookIdDublicatedException;
import book.exceptions.BookNotFound;
import book.repository.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class BookService {
    private final BookRepository bookRepository;
    private static final Logger LOG = LoggerFactory.getLogger(BookService.class);

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public long create(String name, String author) {
        long bookId = bookRepository.generateId();
        Book book = new Book(bookId, name, author);
        try {
            bookRepository.create(book);
            LOG.debug("create book request");
        } catch (BookIdDublicatedException e) {
            throw new BookCreateException("Cannot create a book", e);
        }
        LOG.info("Book created: {}", book);
        return bookId;
    }

    public List<Book> getBooks() {
        return bookRepository.findAll();
    }

    public Book getBook(long id) {
        return bookRepository.findById(id);
    }

    public Book update(long id, String name, String author) {
        Book book = new Book(
                id, name, author
        );
        bookRepository.update(book);
        return book;
    }
}
