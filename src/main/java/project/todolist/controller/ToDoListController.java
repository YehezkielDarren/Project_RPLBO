package project.todolist.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import project.todolist.data.DataStore;
import project.todolist.model.ToDoItem;

import java.io.IOException;

public class ToDoListController {
    @FXML private TableView<ToDoItem> todoTable;
    @FXML private TableColumn<ToDoItem, String> tanggalCol, waktuCol, catatanCol, statusCol, kategoriCol;
    @FXML private ComboBox<String> filterCombo;
    private String currentUser;

    public void setCurrentUser(String username) {
        this.currentUser = username;
        loadTodos();
        loadCategories();
    }

    private void loadTodos() {
        var todos = DataStore.getTodos(currentUser);
        todos.forEach(ToDoItem::updateStatus);
        todoTable.setItems(FXCollections.observableArrayList(todos));
//        todoTable.setItems(FXCollections.observableArrayList(DataStore.getTodos(currentUser)));
    }

    private void loadCategories() {
        filterCombo.getItems().clear();
        filterCombo.getItems().add("All");
        DataStore.getTodos(currentUser).stream().map(ToDoItem::getKategori).distinct().forEach(filterCombo.getItems()::add);
    }

    // ✅ Tambahkan method refreshData
    public void refreshData() {
        loadTodos();
        loadCategories();
    }

    @FXML
    private void addTodo() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/project/todolist/task_form.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Tambah Tugas");

            TaskFormController controller = loader.getController();
            controller.initData(currentUser, null, this::refreshData);

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void deleteTodo() {
        ToDoItem selected = todoTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            DataStore.removeTodo(currentUser, selected);
            loadTodos();
            loadCategories();
        }
    }

    @FXML
    private void editTodo() {
        ToDoItem selected = todoTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/project/todolist/task_form.fxml"));
                Stage stage = new Stage();
                stage.setScene(new Scene(loader.load()));
                stage.setTitle("Edit Tugas");

                TaskFormController controller = loader.getController();
                controller.initData(currentUser, selected, this::refreshData);

                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void filterTodos() {
        String filter = filterCombo.getValue();
        if (filter == null || filter.equals("All")) {
            loadTodos();
        } else {
            todoTable.setItems(FXCollections.observableArrayList(
                    DataStore.getTodos(currentUser).stream().filter(i -> i.getKategori().equals(filter)).toList()
            ));
        }
    }

    @FXML
    private void logout() {
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Logout");
            alert.setHeaderText("Do You Want To Logout?");
            alert.setContentText("Click OK to Exit");
            alert.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/project/todolist/login-view.fxml"));
                    Stage stage = (Stage) todoTable.getScene().getWindow();
                    try {
                        stage.setScene(new Scene(loader.load()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void initialize() {
        tanggalCol.setCellValueFactory(data -> data.getValue().tanggalProperty());
        waktuCol.setCellValueFactory(data -> data.getValue().waktuProperty());
        catatanCol.setCellValueFactory(data -> data.getValue().catatanProperty());
        statusCol.setCellValueFactory(data -> data.getValue().statusProperty());
        kategoriCol.setCellValueFactory(data -> data.getValue().kategoriProperty());
    }
}
