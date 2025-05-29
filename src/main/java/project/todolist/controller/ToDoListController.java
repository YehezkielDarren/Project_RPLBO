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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ToDoListController {
    private DataStore dataStore;
    public Button logoutButton;
    @FXML
    private ListView<ToDoItem> todoListView;
    @FXML
    private ComboBox<String> filterCombo;
    @FXML
    private ComboBox<String> sortCombo;
    @FXML
    private TextField searchField;
    @FXML
    private Label appTitleLabel;
    private String currentUser;
    private ObservableList<ToDoItem> todoList = FXCollections.observableArrayList();
    private ObservableList<ToDoItem> originalTodoList = FXCollections.observableArrayList();

    public ToDoListController(){
        this.dataStore=new DataStore();
    }

    @FXML
    private void initialize() {
        filterCombo.setOnAction(e -> filterTodos());
        sortCombo.setOnAction(e -> sortTodos());
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            performSearch(newValue);
        });
        sortCombo.getItems().addAll("Judul A-Z", "Judul Z-A", "Tenggat Terdekat", "Tenggat Terjauh");
        sortCombo.getSelectionModel().selectFirst();

    }

    public void setCurrentUser(String username) {
        this.currentUser = username;
        setSalam(username);
        loadTodos();
        loadCategories();
    }

    private void setSalam(String username) {
        if (appTitleLabel == null) return;
        String greeting;
        int hour = java.time.LocalTime.now().getHour();
        if (hour >= 5 && hour < 11) {
            greeting = "Selamat pagi";
        } else if (hour >= 11 && hour < 15) {
            greeting = "Selamat siang";
        } else if (hour >= 15 && hour < 18) {
            greeting = "Selamat sore";
        } else {
            greeting = "Selamat malam";
        }
        appTitleLabel.setText(greeting + ", " + username);
    }

    private void loadTodos() {
        List<ToDoItem> todos = dataStore.getTodosForUser(currentUser);
        todos.removeIf(todo -> todo == null);
        todos.forEach(ToDoItem::updateStatus);

        todoList.setAll(todos);
        originalTodoList.setAll(todos);
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

        filterCombo.getItems().add("Terlambat");
        filterCombo.getItems().add("Mendatang");

        dataStore.getTodosForUser(currentUser).stream().map(ToDoItem::getKategori).distinct().forEach(filterCombo.getItems()::add);

        filterCombo.getSelectionModel().selectFirst();
    }

    @FXML
    private void sortTodos() {
        String sortOption = sortCombo.getValue();
        if (sortOption == null) return;

        List<ToDoItem> sortedList = new ArrayList<>(todoList);

        switch (sortOption) {
            case "Judul A-Z" -> sortedList.sort(Comparator.comparing(ToDoItem::getTitle, String.CASE_INSENSITIVE_ORDER));
            case "Judul Z-A" -> sortedList.sort(Comparator.comparing(ToDoItem::getTitle, String.CASE_INSENSITIVE_ORDER).reversed());
            case "Tenggat Terdekat" -> sortedList.sort(Comparator.comparing(ToDoItem::getTanggal)); // assuming getTanggal returns LocalDate
            case "Tenggat Terjauh" -> sortedList.sort(Comparator.comparing(ToDoItem::getTanggal).reversed());
            default -> {}
        }

        todoList.setAll(sortedList);
        todoListView.setItems(todoList);
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
                    dataStore.removeTodoForUser(currentUser, selected.getId());
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

        String searchText = searchField.getText();
        boolean hasSearchText = searchText != null && !searchText.trim().isEmpty();

        List<ToDoItem> todos = dataStore.getTodosForUser(currentUser);
        todos.removeIf(todo -> todo == null);
        todos.forEach(ToDoItem::updateStatus);

        if (filter == null || filter.equals("Select Kategori")) {
            if (hasSearchText) {
                return;
            } else {
                todoList.setAll(todos);
            }
        } else if (filter.equals("Terlambat") || filter.equals("Mendatang")) {
            List<ToDoItem> filtered;
            if (filter.equals("Terlambat")) {
                filtered = todos.stream().filter(item -> item.getStatus().startsWith("Terlambat")).sorted((item1, item2) -> {
                    try {
                        int days1 = extractDaysLate(item1.getStatus());
                        int days2 = extractDaysLate(item2.getStatus());
                        return Integer.compare(days2, days1);
                    } catch (Exception e) {
                        return 0;
                    }
                }).toList();
            } else {
                filtered = todos.stream().filter(item -> item.getStatus().startsWith("Tersisa") || item.getStatus().equals("Hari Ini")).sorted((item1, item2) -> {
                    try {
                        int days1 = item1.getStatus().equals("Hari Ini") ? 0 : extractDaysLeft(item1.getStatus());
                        int days2 = item2.getStatus().equals("Hari Ini") ? 0 : extractDaysLeft(item2.getStatus());
                        return Integer.compare(days1, days2);
                    } catch (Exception e) {
                        return 0;
                    }
                }).toList();
            }

            if (hasSearchText) {
                String searchLower = searchText.toLowerCase().trim();
                filtered = filtered.stream().filter(item -> item.getTitle().toLowerCase().contains(searchLower) || item.getKategori().toLowerCase().contains(searchLower) || item.getCatatan().toLowerCase().contains(searchLower)).toList();
            }

            todoList.setAll(filtered);
            todoListView.setPrefHeight(Math.max(filtered.size() * 120, 350));
        } else {
            List<ToDoItem> filtered = todos.stream().filter(i -> i.getKategori().equals(filter)).toList();

            if (hasSearchText) {
                String searchLower = searchText.toLowerCase().trim();
                filtered = filtered.stream().filter(item -> item.getTitle().toLowerCase().contains(searchLower) || item.getKategori().toLowerCase().contains(searchLower) || item.getCatatan().toLowerCase().contains(searchLower)).toList();
            }

            todoList.setAll(filtered);
            todoListView.setPrefHeight(Math.max(filtered.size() * 120, 350));
        }
    }

    private int extractDaysLeft(String status) {
        try {
            String[] parts = status.split(" ");
            if (parts.length >= 2) {
                return Integer.parseInt(parts[1]);
            }
        } catch (Exception e) {
        }
        return Integer.MAX_VALUE;
    }

    private int extractDaysLate(String status) {
        try {
            String[] parts = status.split(" ");
            if (parts.length >= 2) {
                return Integer.parseInt(parts[1]);
            }
        } catch (Exception e) {
        }
        return 0;
    }

    private void performSearch(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            if (filterCombo.getValue() != null && !filterCombo.getValue().equals("Select Kategori")) {
                filterTodos();
            } else {
                todoList.setAll(originalTodoList);
            }
            return;
        }

        String searchLower = searchText.toLowerCase().trim();

        List<ToDoItem> searchResults = originalTodoList.stream().filter(item -> item.getTitle().toLowerCase().contains(searchLower) || item.getKategori().toLowerCase().contains(searchLower) || item.getCatatan().toLowerCase().contains(searchLower)).toList();

        todoList.setAll(searchResults);

        int minHeight = 350;
        int itemHeight = 150;
        int calculatedHeight = todoList.size() * itemHeight;
        todoListView.setPrefHeight(Math.max(calculatedHeight, minHeight));
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
                        stage.centerOnScreen();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}

