package project.todolist.data;

import project.todolist.model.ToDoItem;
import project.todolist.model.User;

import java.util.*;

public class DataStore {
    private static List<User> users = new ArrayList<>();
    private static Map<String, List<ToDoItem>> userTodos = new HashMap<>();

    public static boolean addUser(User user) {
        if (users.stream().anyMatch(u -> u.getUsername().equals(user.getUsername()))) return false;
        users.add(user);
        userTodos.put(user.getUsername(), new ArrayList<>());
        return true;
    }

    public static User findUser(String username, String password) {
        return users.stream().filter(u -> u.getUsername().equals(username) && u.getPassword().equals(password))
                .findFirst().orElse(null);
    }

    public static List<ToDoItem> getTodos(String username) {
        return userTodos.get(username);
    }

    public static void addTodo(String username, ToDoItem item) {
        userTodos.get(username).add(item);
    }

    public static void removeTodo(String username, ToDoItem item) {
        userTodos.get(username).remove(item);
    }

    public static void editTodo(String username, ToDoItem oldItem, ToDoItem newItem) {
        int idx = userTodos.get(username).indexOf(oldItem);
        if (idx >= 0) userTodos.get(username).set(idx, newItem);
    }
}


