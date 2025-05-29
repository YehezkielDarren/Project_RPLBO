package project.todolist.dao;

import project.todolist.model.User;
import java.util.Optional;

public interface UserDao {
    boolean addUser(User user);
    Optional<User> findUser(String username, String password);
    Optional<User> findUserByUsername(String username); // Berguna untuk validasi
    int getUserId(String username); // Untuk mendapatkan ID pengguna
}