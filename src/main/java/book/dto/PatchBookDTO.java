package book.dto;

import org.openapitools.jackson.nullable.JsonNullable;

import java.util.Optional;

public class PatchBookDTO {
    public JsonNullable<String> name = JsonNullable.undefined();
    public JsonNullable<String> author = JsonNullable.undefined();

    public void setName(JsonNullable<String> name) { this.name = name; }

    public JsonNullable<String> getName() {
        return name;
    }

    public JsonNullable<String> getAuthor() {
        return author;
    }

    public void setAuthor(JsonNullable<String> author) { this.author = author; }
}
