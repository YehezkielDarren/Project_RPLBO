package project.todolist.dao;

import project.todolist.dao.ToDoItemDao;
import project.todolist.data.DatabaseManager;
import project.todolist.model.ToDoItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ToDoItemSqlite implements ToDoItemDao {

    @Override
    public boolean addTodoItem(ToDoItem item, int userId) {
        String sql = "INSERT INTO todo_items(user_id, title, tanggal, waktu, catatan, kategori, status) VALUES(?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, item.getTitle());
            pstmt.setString(3, item.getTanggal());
            pstmt.setString(4, item.getWaktu());
            pstmt.setString(5, item.getCatatan());
            pstmt.setString(6, item.getKategori());
            pstmt.setString(7, item.getStatus());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error adding ToDoItem: " + e.getMessage());
            return false;
        }
    }

    // Metode updateToDoItem akan membutuhkan ID item untuk WHERE clause
    // Atau Anda harus memastikan kombinasi field lain unik untuk item lama
    // Lebih baik menggunakan ID item.

    @Override
    public boolean updateToDoItemById(ToDoItem item, int itemId, int userId) {
        String sql = "UPDATE todo_items SET title = ?, tanggal = ?, waktu = ?, catatan = ?, kategori = ?, status = ? "
                + "WHERE id = ? AND user_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, item.getTitle());
            pstmt.setString(2, item.getTanggal());
            pstmt.setString(3, item.getWaktu());
            pstmt.setString(4, item.getCatatan());
            pstmt.setString(5, item.getKategori());
            pstmt.setString(6, item.getStatus());
            pstmt.setInt(7, itemId);
            pstmt.setInt(8, userId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating ToDoItem: " + e.getMessage());
            return false;
        }
    }

    // Implementasi updateToDoItem(ToDoItem oldItem, ToDoItem newItem, int userId)
    // akan lebih kompleks jika tidak ada ID unik. Sebaiknya dapatkan ID item lama terlebih dahulu.
    @Override
    public boolean updateToDoItem(ToDoItem oldItem, ToDoItem newItem, int userId) {
        // Implementasi ini bergantung pada bagaimana Anda mengidentifikasi oldItem di database.
        // Jika oldItem memiliki ID yang valid, Anda bisa mengambil ID tersebut dan memanggil updateToDoItemById.
        // Jika tidak, Anda perlu query berdasarkan field lain, yang kurang ideal.
        // Contoh (dengan asumsi Anda bisa mendapatkan ID dari oldItem atau mencarinya):
        // int oldItemId = findItemId(oldItem, userId); // Metode pembantu untuk mencari ID
        // if (oldItemId != -1) {
        //     return updateToDoItemById(newItem, oldItemId, userId);
        // }
        System.err.println("updateToDoItem with oldItem object is not fully implemented without a clear ID strategy for oldItem.");
        return false;
    }


    @Override
    public boolean removeToDoItem(int itemId, int userId) {
        String sql = "DELETE FROM todo_items WHERE id = ? AND user_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, itemId);
            pstmt.setInt(2, userId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error removing ToDoItem: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Optional<ToDoItem> getToDoItemById(int itemId, int userId) {
        String sql = "SELECT id, title, tanggal, waktu, catatan, kategori, status FROM todo_items WHERE id = ? AND user_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, itemId);
            pstmt.setInt(2, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                ToDoItem item = new ToDoItem(
                        rs.getString("tanggal"),
                        rs.getString("waktu"),
                        rs.getString("title"),
                        rs.getString("catatan"),
                        rs.getString("kategori"),
                        rs.getString("status")
                );
                // menambahkan setter untuk ID di ToDoItem
                item.setId(rs.getInt("id"));
                return Optional.of(item);
            }
        } catch (SQLException e) {
            System.err.println("Error getting ToDoItem by ID: " + e.getMessage());
        }
        return Optional.empty();
    }


    @Override
    public List<ToDoItem> getTodosByUserId(int userId) {
        List<ToDoItem> todoList = new ArrayList<>();
        String sql = "SELECT id, title, tanggal, waktu, catatan, kategori, status FROM todo_items WHERE user_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                ToDoItem item = new ToDoItem(
                        rs.getString("tanggal"),
                        rs.getString("waktu"),
                        rs.getString("title"),
                        rs.getString("catatan"),
                        rs.getString("kategori"),
                        rs.getString("status")
                );
                // menyimpan ID item di objek ToDoItem
                item.setId(rs.getInt("id"));
                todoList.add(item);
            }
        } catch (SQLException e) {
            System.err.println("Error getting ToDoItems for user: " + e.getMessage());
        }
        return todoList;
    }
}