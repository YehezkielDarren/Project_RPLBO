package project.todolist.dao;

import project.todolist.model.ToDoItem;
import java.util.List;
import java.util.Optional;

public interface ToDoItemDao {
    boolean addTodoItem(ToDoItem item, int userId);
    boolean updateToDoItem(ToDoItem oldItem, ToDoItem newItem, int userId); // Mungkin perlu ID item jika oldItem tidak cukup
    boolean updateToDoItemById(ToDoItem item, int itemId, int userId);
    boolean removeToDoItem(int itemId, int userId); // Lebih aman menghapus berdasarkan ID item dan ID pengguna
    Optional<ToDoItem> getToDoItemById(int itemId, int userId);
    List<ToDoItem> getTodosByUserId(int userId);
}