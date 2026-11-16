package book.repository;

import book.entity.Book;
import book.exceptions.BookNotFound;
import org.jdbi.v3.core.Jdbi;

import java.util.List;

public class InMemoryRepository implements BookRepository {
    private final Jdbi jdbi;

    public InMemoryRepository(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    @Override
    public long generateId() {
        return jdbi.withHandle(h ->
                h.createQuery("SELECT nextval(pg_get_serial_sequence('book', 'id'))")
                        .mapTo(Long.class)
                        .one());
    }

    @Override
    public List<Book> findAll() {
        return jdbi.withHandle(h ->
                h.createQuery("SELECT id, name, author FROM book")
                        .map((rs, ctx) -> new Book(
                                rs.getLong("id"),
                                rs.getString("name"),
                                rs.getString("author")))
                        .list());
    }

    @Override
    public Book findById(long id) {
        return jdbi.withHandle(h ->
                h.createQuery("SELECT id, name, author FROM book WHERE id = :id")
                        .bind("id", id)
                        .map((rs, ctx) -> new Book(
                                rs.getLong("id"),
                                rs.getString("name"),
                                rs.getString("author")))
                        .findOne()
                        .orElseThrow(() -> new BookNotFound(id)));
    }

    @Override
    public long create(Book book) {
        return jdbi.withHandle(h ->
                h.createUpdate("INSERT INTO book (id, name, author) VALUES (:id, :name, :author)")
                        .bind("id", book.getId())
                        .bind("name", book.getName())
                        .bind("author", book.getAuthor())
                        .executeAndReturnGeneratedKeys("id")
                        .mapTo(Long.class)
                        .one()
        );
    }

    @Override
    public void update(Book book) {
        jdbi.useHandle(h ->
                h.createUpdate("UPDATE book SET name = :name, author = :author WHERE id = :id")
                        .bind("id", book.getId())
                        .bind("name", book.getName())
                        .bind("author", book.getAuthor())
                        .execute());
    }

    @Override
    public void delete(long id) {
        jdbi.useHandle(h ->
                h.createUpdate("DELETE FROM book WHERE id = :id")
                        .bind("id", id)
                        .execute());
    }
}