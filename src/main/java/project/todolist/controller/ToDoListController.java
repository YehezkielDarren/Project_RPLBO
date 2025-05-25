package project.todolist.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import project.todolist.data.DataStore;
import project.todolist.model.ToDoItem;

import java.io.IOException;
import java.util.List;

public class ToDoListController {
    public Button logoutButton;
    @FXML private ListView<ToDoItem> todoListView;
    @FXML private ComboBox<String> filterCombo;
    private String currentUser;
    private ObservableList<ToDoItem> todoList = FXCollections.observableArrayList();

    public void setCurrentUser(String username) {
        this.currentUser = username;
        loadTodos();
        loadCategories();
    }

    private void loadTodos() {
        List<ToDoItem> todos = DataStore.getTodos(currentUser);
        todos.removeIf(todo -> todo == null);
        todos.forEach(ToDoItem::updateStatus);

        todoList.setAll(todos);
        todoListView.setItems(todoList);

        int minHeight = 350;
        int itemHeight = 150;

        int calculatedHeight = todoList.size() * itemHeight;
        todoListView.setPrefHeight(Math.max(calculatedHeight, minHeight));

        todoListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(ToDoItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setPrefHeight(0);
                } else {
                    VBox card = new VBox(4);
                    card.setStyle("""
                -fx-border-color: #1976D2;
                -fx-border-radius: 8;
                -fx-background-radius: 8;
                -fx-background-color: #E3F2FD;
                -fx-padding: 10;
            """);

                    // Create a title label with bold formatting
                    Label title = new Label(item.getTitle());
                    title.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
                    title.setWrapText(true);

                    Label tanggal = new Label("Tanggal: " + item.getTanggal());
                    Label waktu = new Label("Waktu: " + item.getWaktu());
                    Label catatan = new Label("Catatan: " + item.getCatatan());
                    Label status = new Label("Status: " + item.getStatus());
                    Label kategori = new Label("Kategori: " + item.getKategori());

                    for (Label label : new Label[]{tanggal, waktu, catatan, status, kategori}) {
                        label.setWrapText(true);
                    }

                    tanggal.setStyle("-fx-font-weight: bold;");

                    switch (item.getStatus().toLowerCase()) {
                        case "pending" -> status.setStyle("-fx-text-fill: orange;");
                        case "done" -> status.setStyle("-fx-text-fill: green;");
                        default -> status.setStyle("-fx-text-fill: red;");
                    }

                    // Add the title as the first element in the card
                    card.getChildren().addAll(title, tanggal, waktu, catatan, status, kategori);
                    VBox wrapper = new VBox(card);
                    VBox.setMargin(card, new Insets(0, 0, 0, 0));

                    setGraphic(wrapper);
                    setPrefHeight(Control.USE_COMPUTED_SIZE);
                }
            }
        });
    }

    private void loadCategories() {
        filterCombo.getItems().clear();
        filterCombo.getItems().add("Select Kategori");

        // Add special date-based filter options
        filterCombo.getItems().add("Terlambat");
        filterCombo.getItems().add("Mendatang");

        // Add existing category-based filters
        DataStore.getTodos(currentUser).stream()
                .map(ToDoItem::getKategori)
                .distinct()
                .forEach(filterCombo.getItems()::add);

        filterCombo.getSelectionModel().selectFirst();
    }

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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void deleteTodo() {
        ToDoItem selected = todoListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Apakah Ingin Menghapus Tugas Ini?");
            alert.setContentText("Klik OK untuk melanjutkan");
            alert.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    DataStore.removeTodo(currentUser, selected);
                    refreshData();
                }
            });
        }
    }

    @FXML
    private void editTodo() {
        ToDoItem selected = todoListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Apakah Ingin Edit Tugas Ini?");
            alert.setContentText("Klik OK untuk melanjutkan");
            alert.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/project/todolist/task_form.fxml"));
                        Stage stage = new Stage();
                        stage.setScene(new Scene(loader.load()));
                        stage.setTitle("Edit Tugas");

                        TaskFormController controller = loader.getController();
                        controller.initData(currentUser, selected, this::refreshData);

                        stage.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @FXML
    private void filterTodos() {
        String filter = filterCombo.getValue();
        if (filter == null || filter.equals("Select Kategori")) {
            loadTodos();
        } else if (filter.equals("Terlambat") || filter.equals("Mendatang")) {
            List<ToDoItem> todos = DataStore.getTodos(currentUser);
            todos.removeIf(todo -> todo == null);
            todos.forEach(ToDoItem::updateStatus);

            List<ToDoItem> filtered;
            if (filter.equals("Terlambat")) {
                filtered = todos.stream()
                        .filter(item -> item.getStatus().startsWith("Terlambat"))
                        .sorted((item1, item2) -> {
                            // Sort by days late (highest first)
                            try {
                                int days1 = extractDaysLate(item1.getStatus());
                                int days2 = extractDaysLate(item2.getStatus());
                                return Integer.compare(days2, days1); // Reverse order - most late first
                            } catch (Exception e) {
                                return 0;
                            }
                        })
                        .toList();
            } else { // "Mendatang"
                filtered = todos.stream()
                        .filter(item -> item.getStatus().startsWith("Tersisa") || item.getStatus().equals("Hari Ini"))
                        .sorted((item1, item2) -> {
                            // Extract the days left as integers for comparison
                            try {
                                int days1 = item1.getStatus().equals("Hari Ini") ? 0 : extractDaysLeft(item1.getStatus());
                                int days2 = item2.getStatus().equals("Hari Ini") ? 0 : extractDaysLeft(item2.getStatus());
                                // Sort ascending by days left (closest first)
                                return Integer.compare(days1, days2);
                            } catch (Exception e) {
                                return 0; // Keep the original order if parsing fails
                            }
                        })
                        .toList();
            }

            todoList.setAll(filtered);
            todoListView.setPrefHeight(Math.max(filtered.size() * 120, 350));
        } else {
            // Original category filter logic
            List<ToDoItem> filtered = DataStore.getTodos(currentUser).stream()
                    .filter(i -> i.getKategori().equals(filter))
                    .toList();
            todoList.setAll(filtered);
            todoListView.setPrefHeight(Math.max(filtered.size() * 120, 350));
        }
    }

    // Helper method to extract days left from status string
    private int extractDaysLeft(String status) {
        try {
            // Format is "Tersisa X hari"
            String[] parts = status.split(" ");
            if (parts.length >= 2) {
                return Integer.parseInt(parts[1]);
            }
        } catch (Exception e) {
            // Do nothing, will return default
        }
        return Integer.MAX_VALUE; // Default to highest value if parsing fails
    }

    // Helper method to extract days late from status string
    private int extractDaysLate(String status) {
        try {
            // Format is "Terlambat X hari"
            String[] parts = status.split(" ");
            if (parts.length >= 2) {
                return Integer.parseInt(parts[1]);
            }
        } catch (Exception e) {
            // Do nothing, will return default
        }
        return 0; // Default to 0 if parsing fails
    }

    @FXML
    private void logout() {
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Logout");
            alert.setHeaderText("Apakah Kamu Mau Logout?");
            alert.setContentText("Klik OK untuk Exit");
            alert.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/project/todolist/login-view.fxml"));
                        Stage stage = (Stage) todoListView.getScene().getWindow();
                        Scene scene = new Scene(loader.load());
                        scene.getStylesheets().add(getClass().getResource("/project/todolist/css/login.css").toExternalForm());
                        stage.setScene(scene);
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
        filterCombo.setOnAction(e -> filterTodos());
    }
}

