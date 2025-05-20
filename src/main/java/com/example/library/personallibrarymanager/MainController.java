package com.example.library.personallibrarymanager;

import com.example.library.personallibrarymanager.database.DatabaseConnection;
import com.example.library.personallibrarymanager.model.Book;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.sql.*;

public class MainController {

    @FXML private TextField titleField;
    @FXML private TextField authorField;
    @FXML private TextField genreField;
    @FXML private CheckBox readCheckBox;
    @FXML private TableView<Book> bookTable;
    @FXML private TableColumn<Book, String> titleColumn;
    @FXML private TableColumn<Book, String> authorColumn;
    @FXML private TableColumn<Book, String> genreColumn;
    @FXML private TableColumn<Book, Boolean> readColumn;
    @FXML private ChoiceBox<String> filterChoiceBox;
    @FXML private TextField filterField;

    private final ObservableList<Book> books = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        titleColumn.setCellValueFactory(data -> data.getValue().titleProperty());
        authorColumn.setCellValueFactory(data -> data.getValue().authorProperty());
        genreColumn.setCellValueFactory(data -> data.getValue().genreProperty());
        readColumn.setCellValueFactory(data -> data.getValue().readProperty());

        filterChoiceBox.setItems(FXCollections.observableArrayList("Author", "Genre"));
        filterChoiceBox.setValue("Author");

        loadBooks();
    }

    private void loadBooks() {
        books.clear();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM books")) {

            while (rs.next()) {
                books.add(new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("genre"),
                        rs.getBoolean("is_read")
                ));
            }
            bookTable.setItems(books);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void addBook() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO books (title, author, genre, is_read) VALUES (?, ?, ?, ?)")) {
            ps.setString(1, titleField.getText());
            ps.setString(2, authorField.getText());
            ps.setString(3, genreField.getText());
            ps.setBoolean(4, readCheckBox.isSelected());
            ps.executeUpdate();
            loadBooks();
            clearFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void deleteBook() {
        Book selected = bookTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM books WHERE id = ?")) {
            ps.setInt(1, selected.getId());
            ps.executeUpdate();
            loadBooks();
            clearFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void updateBook() {
        Book selected = bookTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE books SET title = ?, author = ?, genre = ?, is_read = ? WHERE id = ?")) {

            ps.setString(1, titleField.getText());
            ps.setString(2, authorField.getText());
            ps.setString(3, genreField.getText());
            ps.setBoolean(4, readCheckBox.isSelected());
            ps.setInt(5, selected.getId());

            ps.executeUpdate();
            loadBooks();
            clearFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void filterBooks() {
        String filterType = filterChoiceBox.getValue();
        String filterValue = filterField.getText().trim();

        if (filterValue.isEmpty()) {
            loadBooks();
            return;
        }

        books.clear();
        String query = "SELECT * FROM books WHERE " + (filterType.equals("Author") ? "author" : "genre") + " LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, "%" + filterValue + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                books.add(new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("genre"),
                        rs.getBoolean("is_read")
                ));
            }
            bookTable.setItems(books);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void clearFilter() {
        filterField.clear();
        loadBooks();
    }

    private void clearFields() {
        titleField.clear();
        authorField.clear();
        genreField.clear();
        readCheckBox.setSelected(false);
    }

    @FXML
    private void onBookClick(MouseEvent event) {
        Book selected = bookTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        titleField.setText(selected.getTitle());
        authorField.setText(selected.getAuthor());
        genreField.setText(selected.getGenre());
        readCheckBox.setSelected(selected.isRead());
    }
}
