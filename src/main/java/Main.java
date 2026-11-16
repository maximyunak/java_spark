import book.Application;
import book.controller.BookController;
import book.repository.InMemoryRepository;
import book.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.flywaydb.core.Flyway;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;


public class Main {
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        Config config = ConfigFactory.load();

        Flyway flyway = Flyway
                .configure()
                .locations("classpath:db/migrations")
                .dataSource(config.getString("app.database.url"), config.getString("app.database.user"), config.getString("app.database.password"))
                .load();

        flyway.migrate();

        Jdbi jdbi = Jdbi.create("jdbc:postgresql://localhost:5432/books", "user", "password");
        jdbi.useTransaction((Handle handle) -> {
            handle
                    .createUpdate("INSERT INTO users (firstName, lastName) VALUES (:firstName, :lastName)")
                    .bind("firstName", "Ananas")
                    .bind("lastName", "Bobito")
                    .execute();
        });


        Service service = Service.ignite().port(8080);
        enableCors(service, "http://localhost:8080");
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JsonNullableModule());

        Application application = new Application(
                List.of(new BookController(
                        service,
                        objectMapper,
                        new BookService(
                                new InMemoryRepository(
                                        jdbi
                                )
                        )
                ))
        );

        application.start();
        LOG.info("app started");
    }

    public static void enableCors(Service service, String origin) {
        service.options("/*", (request, response) -> {
            String reqHeaders = request.headers("Access-Control-Request-Headers");
            if (reqHeaders != null) {
                response.header("Access-Control-Allow-Headers", reqHeaders);
            }
            String reqMethod = request.headers("Access-Control-Request-Method");
            if (reqMethod != null) {
                response.header("Access-Control-Allow-Methods", reqMethod);
            }
            return "OK";
        });

        service.before((request, response) -> {
            response.header("Access-Control-Allow-Origin", origin);
            response.header("Access-Control-Allow-Methods", "GET, POST, PATCH, PUT, DELETE, OPTIONS");
            response.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
        });
    }
}
