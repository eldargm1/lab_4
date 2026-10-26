import java.util.*;

public class Main {
    /*
     * Обоснование выбора коллекций:
     * 1. В качестве реализации List выбран ArrayList, так как он обеспечивает быструю последовательную
     *    загрузку исходных данных и эффективную сортировку по индексам.
     * 2. В качестве реализации Set выбран LinkedHashSet, поскольку он отбрасывает дубликаты на основе
     *    переопределенных методов equals() и hashCode(), сохраняя при этом порядок первого появления элементов.
     * 3. В качестве реализации Map выбран TreeMap, так как он автоматически поддерживает ключи
     *    (жанры и авторов) в отсортированном по алфавиту порядке, что требуется по заданию.
     */

    // Метод, возвращающий исходный список из 10 записей (используется и в main, и в тестах)
    public static List<BookRecord> getInitialJournal() {
        List<BookRecord> list = new ArrayList<>();
        list.add(new BookRecord("Фантастика", "Лем", "Солярис", 1961));
        list.add(new BookRecord("Проза", "Булгаков", "Мастер и Маргарита", 1967));
        list.add(new BookRecord("Фантастика", "Стругацкие", "Пикник на обочине", 1972));
        list.add(new BookRecord("Детектив", "Кристи", "Убийство в Восточном экспрессе", 1934));
        list.add(new BookRecord("Фантастика", "Лем", "Солярис", 1961)); // повтор
        list.add(new BookRecord("Проза", "Толстой", "Анна Каренина", 1877));
        list.add(new BookRecord("Детектив", "Конан Дойл", "Собака Баскервилей", 1902));
        list.add(new BookRecord("Фантастика", "Брэдбери", "451 градус по Фаренгейту", 1953));
        list.add(new BookRecord("Проза", "Булгаков", "Мастер и Маргарита", 1967)); // повтор
        list.add(new BookRecord("Детектив", "Кристи", "Загадка Эндхауза", 1934));
        return list;
    }

    public static void main(String[] args) {
        // Первая строка: Фамилия и группа латиницей
        System.out.println("Aliev E.N., group 3821B1PR1");

        List<BookRecord> journal = getInitialJournal();

        // 1) Всего записей в журнале
        System.out.println("1) Записей в журнале: " + journal.size());

        // 2) Уникальных книг (LinkedHashSet сохраняет порядок первого появления)
        Set<BookRecord> uniqueBooksSet = new LinkedHashSet<>(journal);
        int duplicatesCount = journal.size() - uniqueBooksSet.size();
        System.out.println("2) Уникальных книг: " + uniqueBooksSet.size() + "; повторов отброшено: " + duplicatesCount);

        // 3) Группировка по жанрам в TreeMap (ключи упорядочены по алфавиту)
        Map<String, List<BookRecord>> booksByGenre = new TreeMap<>();
        for (BookRecord book : uniqueBooksSet) {
            booksByGenre.computeIfAbsent(book.getGenre(), k -> new ArrayList<>()).add(book);
        }

        System.out.println("3) Жанров: " + booksByGenre.size());
        for (Map.Entry<String, List<BookRecord>> entry : booksByGenre.entrySet()) {
            String genre = entry.getKey();
            List<BookRecord> books = entry.getValue();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < books.size(); i++) {
                sb.append(books.get(i).getTitle());
                if (i < books.size() - 1) {
                    sb.append(", ");
                }
            }
            System.out.println("   " + genre + " (" + books.size() + "): " + sb.toString());
        }

        // 4) Хронология: сортировка списка составным компаратором (год -> автор -> название)
        List<BookRecord> sortedBooks = new ArrayList<>(uniqueBooksSet);
        sortedBooks.sort(
                Comparator.comparingInt(BookRecord::getYear)
                        .thenComparing(BookRecord::getAuthor)
                        .thenComparing(BookRecord::getTitle)
        );

        System.out.println("4) Хронология:");
        for (BookRecord book : sortedBooks) {
            System.out.println("   " + book.getYear() + "  " + book.getAuthor() + " — " + book.getTitle());
        }

        // 5) Самая старая и самая новая книги через Collections.min и Collections.max
        BookRecord oldest = Collections.min(sortedBooks, Comparator.comparingInt(BookRecord::getYear));
        BookRecord newest = Collections.max(sortedBooks, Comparator.comparingInt(BookRecord::getYear));
        System.out.println("5) Самая старая: " + oldest.getAuthor() + " — " + oldest.getTitle() + " (" + oldest.getYear() + ")");
        System.out.println("   Самая новая:  " + newest.getAuthor() + " — " + newest.getTitle() + " (" + newest.getYear() + ")");

        // 6) Подсчёт повторов автора «Кристи» через Collections.frequency
        List<String> authorsList = new ArrayList<>();
        for (BookRecord book : sortedBooks) {
            authorsList.add(book.getAuthor());
        }
        int christieCount = Collections.frequency(authorsList, "Кристи");
        System.out.println("6) Книг Кристи в зале: " + christieCount);

        // 7) Защита от изменения через Collections.unmodifiableList и перехват исключения
        List<BookRecord> unmodifiableList = Collections.unmodifiableList(sortedBooks);
        try {
            unmodifiableList.add(new BookRecord("Тест", "Тест", "Тест", 2020));
        } catch (UnsupportedOperationException e) {
            System.out.println("7) Попытка добавить в защищённый список: " + e.getClass().getSimpleName());
        }

        // 8) Удаление по условию (издано до 1950 года) через removeIf
        List<BookRecord> mutableList = new ArrayList<>(sortedBooks);
        int sizeBefore = mutableList.size();
        mutableList.removeIf(book -> book.getYear() < 1950);
        int removedCount = sizeBefore - mutableList.size();
        System.out.println("8) Издано до 1950 года — убрано: " + removedCount + "; осталось: " + mutableList.size());

        // 9) Подсчёт оставшихся книг по авторам в Map с упорядоченными ключами через merge
        Map<String, Integer> authorCounts = new TreeMap<>();
        for (BookRecord book : mutableList) {
            authorCounts.merge(book.getAuthor(), 1, Integer::sum);
        }
        System.out.println("9) Осталось по авторам: " + authorCounts);
    }
}