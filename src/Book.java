
public class Book {
    private String title;
    private Author author;

    @Override
    public String toString() {
        return "Book {" +
                "title='" + title + '\'' +
                (author == null ? "" : ", author='" + author.getName() + " " + author.getLastName() + '\'') +
                "}\n";
    }

    public Book(String title, Author author) {
        this.title = title;
        this.author = author;
    }

    public Book(String title) {
        this.title = title;
    }
}