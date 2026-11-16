package book.repository;

import book.entity.Book;
import org.flywaydb.core.Flyway;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;


public class BookRepositoryTest {
    private static Jdbi jdbi;

    @BeforeAll
    static void beforeAll() {
        String h2Url = "jdbc:h2:~/test";
        Flyway flyway = Flyway
                .configure()
                .locations("classpath:db/migrations")
                .dataSource(h2Url, "sa", "")
                .load();
        flyway.migrate();
        jdbi = Jdbi.create(h2Url, "sa", "");
    }

    @Test
    void shouldCreateNewBook() {
        BookRepository bookRepository = new InMemoryRepository(jdbi);

        Book newBook = new Book(0L, "BERSERK", "MAXIM");
        long id = bookRepository.create(newBook);

        Book book = bookRepository.findById(id);

        assertThat(book.getName()).isEqualTo("BERSERK");
        assertThat(book.getId()).isEqualTo(id);
    }
}
