import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class BookRecordTest {

    @Test
    @DisplayName("Тест равенства книг по автору и названию")
    void testEqualsAndHashCode() {
        BookRecord book1 = new BookRecord("Фантастика", "Лем", "Солярис", 1961);
        BookRecord book2 = new BookRecord("Детектив", "Лем", "Солярис", 2000);

        assertEquals(book1, book2);
        assertEquals(book1.hashCode(), book2.hashCode());
    }

    @Test
    @DisplayName("Тест фильтрации дубликатов с сохранением порядка через LinkedHashSet")
    void testUniqueSet() {
        List<BookRecord> journal = Main.getInitialJournal();
        Set<BookRecord> uniqueSet = new LinkedHashSet<>(journal);

        assertEquals(8, uniqueSet.size());
        assertEquals(2, journal.size() - uniqueSet.size());
    }

    @Test
    @DisplayName("Тест группировки по жанрам в TreeMap")
    void testGroupingByGenre() {
        Set<BookRecord> uniqueBooks = new LinkedHashSet<>(Main.getInitialJournal());
        Map<String, List<BookRecord>> booksByGenre = new TreeMap<>();
        for (BookRecord book : uniqueBooks) {
            booksByGenre.computeIfAbsent(book.getGenre(), k -> new ArrayList<>()).add(book);
        }

        assertEquals(3, booksByGenre.size());
        assertTrue(booksByGenre.containsKey("Детектив"));
        assertTrue(booksByGenre.containsKey("Проза"));
        assertTrue(booksByGenre.containsKey("Фантастика"));
    }

    @Test
    @DisplayName("Тест сортировки составным компаратором (год -> автор -> название)")
    void testSorting() {
        List<BookRecord> books = new ArrayList<>(new LinkedHashSet<>(Main.getInitialJournal()));
        books.sort(
                Comparator.comparingInt(BookRecord::getYear)
                        .thenComparing(BookRecord::getAuthor)
                        .thenComparing(BookRecord::getTitle)
        );

        assertEquals(1877, books.get(0).getYear());
        assertEquals("Толстой", books.get(0).getAuthor());
        assertEquals(1972, books.get(books.size() - 1).getYear());
    }

    @Test
    @DisplayName("Тест поиска крайних значений через Collections.min и Collections.max")
    void testMinMax() {
        List<BookRecord> books = new ArrayList<>(new LinkedHashSet<>(Main.getInitialJournal()));
        BookRecord oldest = Collections.min(books, Comparator.comparingInt(BookRecord::getYear));
        BookRecord newest = Collections.max(books, Comparator.comparingInt(BookRecord::getYear));

        assertEquals(1877, oldest.getYear());
        assertEquals("Анна Каренина", oldest.getTitle());
        assertEquals(1972, newest.getYear());
        assertEquals("Пикник на обочине", newest.getTitle());
    }

    @Test
    @DisplayName("Тест подсчета элементов через Collections.frequency")
    void testFrequency() {
        List<BookRecord> books = new ArrayList<>(new LinkedHashSet<>(Main.getInitialJournal()));
        List<String> authors = new ArrayList<>();
        for (BookRecord book : books) {
            authors.add(book.getAuthor());
        }

        assertEquals(2, Collections.frequency(authors, "Кристи"));
        assertEquals(1, Collections.frequency(authors, "Толстой"));
    }

    @Test
    @DisplayName("Тест защиты от изменения через Collections.unmodifiableList")
    void testUnmodifiableList() {
        List<BookRecord> books = new ArrayList<>(new LinkedHashSet<>(Main.getInitialJournal()));
        List<BookRecord> protectedList = Collections.unmodifiableList(books);

        assertThrows(UnsupportedOperationException.class, () -> {
            protectedList.add(new BookRecord("Тест", "Тест", "Тест", 2024));
        });
    }

    @Test
    @DisplayName("Тест удаления старых книг через removeIf и подсчета через merge")
    void testRemoveIfAndMerge() {
        List<BookRecord> books = new ArrayList<>(new LinkedHashSet<>(Main.getInitialJournal()));
        books.removeIf(book -> book.getYear() < 1950);

        assertEquals(4, books.size());

        Map<String, Integer> counts = new TreeMap<>();
        for (BookRecord book : books) {
            counts.merge(book.getAuthor(), 1, Integer::sum);
        }

        assertEquals(1, counts.get("Брэдбери"));
        assertEquals(1, counts.get("Булгаков"));
        assertEquals(1, counts.get("Лем"));
        assertEquals(1, counts.get("Стругацкие"));
        assertNull(counts.get("Кристи"));
    }
}