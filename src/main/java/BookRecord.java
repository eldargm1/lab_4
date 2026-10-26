import java.util.Objects;

public class BookRecord {
    private String genre;
    private String author;
    private String title;
    private int year;

    public BookRecord(String genre, String author, String title, int year) {
        this.genre = genre;
        this.author = author;
        this.title = title;
        this.year = year;
    }

    public String getGenre() {
        return genre;
    }

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public int getYear() {
        return year;
    }

    // Равенство задается только по автору и названию
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookRecord that = (BookRecord) o;
        return Objects.equals(author, that.author) && Objects.equals(title, that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(author, title);
    }

    @Override
    public String toString() {
        return author + " — " + title;
    }
}