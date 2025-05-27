package project.todolist.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import project.todolist.data.DataStore;
import project.todolist.model.User;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

    @FXML
    private void handleLogin() {
        User user = DataStore.findUser(usernameField.getText(), passwordField.getText());
        Alert alert;
        if (user != null) {
            try {
                alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setHeaderText("Login Berhasil!");
                alert.setContentText("Selamat Datang " + user.getUsername() + "!");
                alert.showAndWait();

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/project/todolist/todo_list_view.fxml"));
                Stage stage = (Stage) usernameField.getScene().getWindow();
                Scene scene = new Scene(loader.load());
                ToDoListController controller = loader.getController();
                controller.setCurrentUser(user.getUsername());
                stage.setScene(scene);
                stage.centerOnScreen();
                stage.setMaximized(true);
            } catch (Exception e) { e.printStackTrace(); }
        } else {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Login Gagal!");
            alert.setContentText("Username atau Password salah! Silahkan coba lagi!");
            alert.showAndWait();
        }
    }

    @FXML
    private void switchToRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/project/todolist/register_view.fxml"));
            Stage stage = (Stage) usernameField.getScene().getWindow();
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/project/todolist/css/register.css").toExternalForm());
            stage.setScene(scene);
        } catch (Exception e) { e.printStackTrace(); }
    }
}
