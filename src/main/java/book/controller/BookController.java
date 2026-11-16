package book.controller;

import book.dto.CreateBookDTO;
import book.dto.CreateBookResponse;
import book.dto.ErrorResponse;
import book.dto.PatchBookDTO;
import book.entity.Book;
import book.exceptions.BookCreateException;
import book.exceptions.BookNotFound;
import book.repository.BookRepository;
import book.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Service;

import java.util.List;

public class BookController implements Controller{
    private final Service service;
    private final ObjectMapper objectMapper;
    private final BookService bookService;
    private static final Logger LOG = LoggerFactory.getLogger(BookController.class);


    public BookController(Service service, ObjectMapper objectMapper, BookService bookService) {
        this.service = service;
        this.objectMapper = objectMapper;
        this.bookService = bookService;
    }

    @Override
    public void initializeEndpoints() {
        createBook();
        getBooks();
        getBook();
        editBook();
    }

    public void createBook() {
        service.post("/api/books", (Request request, Response response) -> {
            response.type("application/json");
            String body = request.body();
            CreateBookDTO bookCreateRequest = objectMapper.readValue(body, CreateBookDTO.class);
            try {
                long bookId = bookService.create(bookCreateRequest.name(), bookCreateRequest.author());
                response.status(201);
                return objectMapper.writeValueAsString(new CreateBookResponse(bookId));
            } catch (BookCreateException e) {
                response.status(400);
                return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
            }
        });
    }

    public void getBooks() {
        service.get("/api/books", (Request request, Response response) -> {
           response.type("application/json");
           List<Book> bookList = bookService.getBooks();
           return objectMapper.writeValueAsString(bookList);
        });
    }

    public void getBook() {
        service.get("/api/books/:bookId", (Request request, Response response) -> {
            response.type("application/json");
            long bookId = Long.parseLong(request.params("bookId"));
            try {
                Book book = bookService.getBook(bookId);
                return objectMapper.writeValueAsString(book);
            } catch (BookNotFound e) {
                return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
            }
        });
    }

    public void editBook() {
        service.patch("/api/books/:bookId", (Request request, Response response) -> {
            response.type("application/json");
            long bookId = Long.parseLong(request.params("bookId"));
            String body = request.body();
            PatchBookDTO dto = objectMapper.readValue(body, PatchBookDTO.class);
            try {
                Book existing = bookService.getBook(bookId);
                
                String name = dto.getName().isPresent() ? dto.getName().get() : existing.getName();
                String author = dto.getAuthor().isPresent() ? dto.getAuthor().get() : existing.getAuthor();

                Book updated = bookService.update(bookId, name, author);
                return objectMapper.writeValueAsString(updated);
            } catch (BookNotFound e) {
                response.status(404);
                LOG.error("Patch has failed:", e);
                return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
            }
        });
    }
}
