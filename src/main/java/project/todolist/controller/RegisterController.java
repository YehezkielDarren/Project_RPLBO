package project.todolist.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import project.todolist.data.DataStore;
import project.todolist.model.User;

public class RegisterController {
    private DataStore dataStore;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

    public RegisterController(){
        this.dataStore=new DataStore();
    }

    @FXML
    private void handleRegister() {
        boolean success = dataStore.registerUser(new User(usernameField.getText(), passwordField.getText()));
        if (success) {
            messageLabel.setText("Registration successful. Go login.");
        } else {
            messageLabel.setText("Username already exists.");
        }
    }

    @FXML
    private void switchToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/project/todolist/login-view.fxml"));
            Stage stage = (Stage) usernameField.getScene().getWindow();
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/project/todolist/css/login.css").toExternalForm());
            stage.setScene(scene);
        } catch (Exception e) { e.printStackTrace(); }
    }
}

