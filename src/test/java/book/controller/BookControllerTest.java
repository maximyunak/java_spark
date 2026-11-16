package book.controller;

import book.Application;
import book.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import spark.Service;
import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;


public class BookControllerTest {
    private Service service;

    @BeforeEach
    void beforeEach () {
        service = Service.ignite().port(5050);
    }

    @AfterEach
    void afterEach() {
        service.stop();
        service.awaitStop();
    }

    @Test
    void should201IfSuccessfullyCreatedBook() throws Exception {
        BookService bookService = Mockito.mock(BookService.class);
        ObjectMapper objectMapper= new ObjectMapper();
        Application application = new Application(
                List.of(
                        new BookController(
                                service,
                                objectMapper,
                                bookService
                        )
                )
        );

        Mockito.when(bookService.create("book", "jack")).thenReturn(45L);
        application.start();
        service.awaitInitialization();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:%d/api/books".formatted(service.port())))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("""
                { "name": "book", "author": "jack" }
                """))
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        assertThat(response.statusCode()).isEqualTo(201);
        Mockito.verify(bookService).create("book", "jack");
    }
}
