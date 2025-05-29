package project.todolist.data;

import project.todolist.model.ToDoItem;
import project.todolist.model.User;
import project.todolist.dao.ToDoItemDao;
import project.todolist.dao.UserDao;
import project.todolist.dao.ToDoItemSqlite;
import project.todolist.dao.UserSqlite;

import java.util.List;
import java.util.Optional;

import java.util.*;

public class DataStore {
//    private static List<User> users = new ArrayList<>();
//    private static Map<String, List<ToDoItem>> userTodos = new HashMap<>();

    private UserDao userDao;
    private ToDoItemDao toDoItemDao;

    public DataStore() {
        this.userDao = new UserSqlite();
        this.toDoItemDao = new ToDoItemSqlite();
    }

    public boolean registerUser(User user) {
        if (userDao.findUserByUsername(user.getUsername()).isPresent()) {
            return false; // Username sudah ada
        }
        return userDao.addUser(user);
    }

    public Optional<User> loginUser(String username, String password) {
        return userDao.findUser(username, password);
    }

    public int getUserId(String username) {
        return userDao.getUserId(username);
    }

    public List<ToDoItem> getTodosForUser(String username) {
        int userId = getUserId(username);
        if (userId != -1) {
            return toDoItemDao.getTodosByUserId(userId);
        }
        return List.of(); // Atau throw exception
    }

    public boolean addTodoForUser(String username, ToDoItem item) {
        int userId = getUserId(username);
        if (userId != -1) {
            item.updateStatus(); // Hitung status sebelum menyimpan
            return toDoItemDao.addTodoItem(item, userId);
        }
        return false;
    }

    public boolean removeTodoForUser(String username, int itemId) { // Modifikasi untuk menggunakan itemId
        int userId = getUserId(username);
        if (userId != -1) {
            return toDoItemDao.removeToDoItem(itemId, userId);
        }
        return false;
    }

    // Untuk edit, Anda akan butuh ID item yang akan diedit.
    public boolean editTodoForUser(String username, ToDoItem newItem, int itemIdToEdit) {
        int userId = getUserId(username);
        if (userId != -1) {
            newItem.updateStatus(); // Hitung status baru sebelum menyimpan
            return toDoItemDao.updateToDoItemById(newItem, itemIdToEdit, userId);
        }
        return false;
    }

//    public static boolean addUser(User user) {
//        if (users.stream().anyMatch(u -> u.getUsername().equals(user.getUsername()))) return false;
//        users.add(user);
//        userTodos.put(user.getUsername(), new ArrayList<>());
//        return true;
//    }
//
//    public static User findUser(String username, String password) {
//        return users.stream().filter(u -> u.getUsername().equals(username) && u.getPassword().equals(password))
//                .findFirst().orElse(null);
//    }
//
//    public static List<ToDoItem> getTodos(String username) {
//        return userTodos.get(username);
//    }
//
//    public static void addTodo(String username, ToDoItem item) {
//        userTodos.get(username).add(item);
//    }
//
//    public static void removeTodo(String username, ToDoItem item) {
//        userTodos.get(username).remove(item);
//    }
//
//    public static void editTodo(String username, ToDoItem oldItem, ToDoItem newItem) {
//        int idx = userTodos.get(username).indexOf(oldItem);
//        if (idx >= 0) userTodos.get(username).set(idx, newItem);
//    }
}


