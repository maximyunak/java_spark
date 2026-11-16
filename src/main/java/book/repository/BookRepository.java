package book.repository;

import book.entity.Book;

import java.util.List;

public interface BookRepository {
    long generateId();

    List<Book> findAll();

    Book findById(long id);

    long create(Book book);

    void update(Book book);

    void delete(long id);
}
