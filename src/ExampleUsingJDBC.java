import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExampleUsingJDBC {
    static final String DATABASE_URL = "jdbc:mysql://localhost/bookstore";
    static final String USER = "root";
    static final String PASSWORD = "ponchik";

    public static void main(String[] args) {

        System.out.println("All books with author");
        System.out.println("================================================================");
        System.out.println(getBooksAndAuthors());
        System.out.println();

        System.out.println("Books without author");
        System.out.println("================================================================");
        System.out.println(getBooksNoHaveAuthor());
        System.out.println();

        System.out.println("Number of books by one author in the store");
        System.out.println("================================================================");
        System.out.println(getAuthorCountBooks());
        System.out.println();

        System.out.println("The number of books by one author in the store with the limit");
        System.out.println("================================================================");
        System.out.println(getAuthorCountBooks(2));
    }

    private static Map<String, Integer> getAuthorCountBooks(int limit) {
        Map<String, Integer> booksPerAuthors = new HashMap<>();
        String sql;
        sql = "SELECT Name AS AuthorName, LastName AS AuthorLastName, COUNT(*) AS BookCount " +
                "FROM authors " +
                "    JOIN books ON authors.id = books.AuthorId " +
                "    GROUP BY 1, 2 HAVING BookCount > ? ";
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setInt(1, limit);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String name = resultSet.getString("AuthorName");
                String lastName = resultSet.getString("AuthorLastName");
                Integer count = resultSet.getInt("BookCount");
                booksPerAuthors.put(name + " " + lastName, count);
            }
        } catch (SQLException se) {
            System.out.println("SQLException");
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return booksPerAuthors;
    }

    private static Map<String, Integer> getAuthorCountBooks() {
        return getAuthorCountBooks(0);
    }

    public static List<Book> getBooksNoHaveAuthor() {
        List<Book> books = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER, PASSWORD);
             Statement statement = connection.createStatement()
        ) {
            String sql;
            sql = "SELECT Title AS BookTitle FROM books WHERE AuthorId IS NULL";

            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                String title = resultSet.getString("BookTitle");
                books.add(new Book(title));
            }
        } catch (SQLException se) {
            System.out.println("SQLException");
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return books;
    }

    public static List<Book> getBooksAndAuthors() {
        List<Book> books = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER, PASSWORD);
             Statement statement = connection.createStatement()
        ) {
            String sql;
            sql = "SELECT Title AS BookTitle, Name AS AuthorName, LastName AS AuthorLastName " +
                    "FROM books LEFT JOIN authors ON authors.id = books.AuthorId ORDER BY Name, LastName;";

            ResultSet resultSet = statement.executeQuery(sql);

            String prevAuthorName = null;
            String prevAuthorLastName = null;
            Author author = null;

            while (resultSet.next()) {
                String title = resultSet.getString("BookTitle");
                String name = resultSet.getString("AuthorName");
                String lastName = resultSet.getString("AuthorLastName");

                if (name == null) {
                    books.add(new Book(title));
                } else {
                    if (!name.equals(prevAuthorName) && !lastName.equals(prevAuthorLastName)) {
                        author = new Author(name, lastName);
                        prevAuthorName = name;
                        prevAuthorLastName = lastName;
                    }
                    books.add(new Book(title, author));
                }
            }
        } catch (SQLException se) {
            System.out.println("SQLException");
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return books;
    }
}