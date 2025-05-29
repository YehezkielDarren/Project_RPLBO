package project.todolist.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import project.todolist.data.DataStore;
import project.todolist.model.ToDoItem;

import java.time.LocalDate;
import javafx.util.StringConverter;

public class TaskFormController {
    private DataStore dataStore;

    @FXML private TextField titleField;
    @FXML private DatePicker datePicker;
    @FXML private Spinner<Integer> hourSpinner;
    @FXML private Spinner<Integer> minuteSpinner;
    @FXML private TextArea catatanArea;
    @FXML private ComboBox<String> kategoriCombo;
    @FXML private TextField kategoriBaruField;

    private String currentUser;
    private ToDoItem editingItem;
    private Runnable onSaveCallback;

    public TaskFormController(){
        this.dataStore=new DataStore();
    }

    @FXML
    private void initialize() {
        SpinnerValueFactory.IntegerSpinnerValueFactory hourFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 0);
        hourFactory.setWrapAround(true);
        hourFactory.setConverter(new StringConverter<Integer>() {
            @Override public String toString(Integer value) {
                return String.format("%02d", (value != null ? value : 0));
            }
            @Override public Integer fromString(String string) {
                try { return Integer.parseInt(string); }
                catch (NumberFormatException e) { return 0; }
            }
        });
        hourSpinner.setValueFactory(hourFactory);
        hourSpinner.getEditor().setText(hourFactory.getConverter().toString(hourFactory.getValue()));

        SpinnerValueFactory.IntegerSpinnerValueFactory minuteFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0);
        minuteFactory.setWrapAround(true);
        minuteFactory.setConverter(new StringConverter<Integer>() {
            @Override public String toString(Integer value) {
                return String.format("%02d", (value != null ? value : 0));
            }
            @Override public Integer fromString(String string) {
                try { return Integer.parseInt(string); }
                catch (NumberFormatException e) { return 0; }
            }
        });
        minuteSpinner.setValueFactory(minuteFactory);
        minuteSpinner.getEditor().setText(minuteFactory.getConverter().toString(minuteFactory.getValue()));
    }

    public void initData(String user, ToDoItem item, Runnable onSave) {
        this.currentUser = user;
        this.editingItem = item;
        this.onSaveCallback = onSave;

        kategoriCombo.getItems().addAll(
                dataStore.getTodosForUser(user).stream()
                        .map(ToDoItem::getKategori)
                        .distinct()
                        .toList()
        );

        if (item != null) {
            titleField.setText(item.getTitle());
            datePicker.setValue(LocalDate.parse(item.getTanggal()));
            String[] waktuParts = item.getWaktu().split(":");
            hourSpinner.getValueFactory().setValue(Integer.parseInt(waktuParts[0]));
            minuteSpinner.getValueFactory().setValue(Integer.parseInt(waktuParts[1]));
            catatanArea.setText(item.getCatatan());
            kategoriCombo.setValue(item.getKategori());
        }
    }

    @FXML
    private void handleAddCategory() {
        String newCategory = kategoriBaruField.getText().trim();
        if (newCategory.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Kategori belum diisi!");
            alert.showAndWait();
            return;
        } else if (kategoriCombo.getItems().contains(newCategory)) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Kategori sudah ada!");
            alert.showAndWait();
            return;
        } else {
            kategoriCombo.getItems().add(newCategory);
            kategoriCombo.setValue(newCategory);
            kategoriBaruField.setText("");
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Kategori berhasil ditambah!");
            alert.showAndWait();
            return;
        }
    }

    @FXML
    private void handleSaveTask() {
        String title = titleField.getText().trim();
        String tanggal = datePicker.getValue() != null ? datePicker.getValue().toString() : "";
        String waktu = String.format("%02d:%02d", hourSpinner.getValue(), minuteSpinner.getValue());
        String catatan = catatanArea.getText().trim();
        String kategori = kategoriCombo.getValue();

        if (title.isEmpty() || tanggal.isEmpty() || waktu.isEmpty() || catatan.isEmpty() || kategori == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Semua field harus diisi!");
            alert.showAndWait();
            return;
        }

        ToDoItem newItem = new ToDoItem(tanggal, waktu, title, catatan, kategori);
        newItem.updateStatus();

        if (editingItem == null) {
            dataStore.addTodoForUser(currentUser, newItem);
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION, "Tugas berhasil ditambahkan!");
            successAlert.showAndWait();
        } else {
            dataStore.editTodoForUser(currentUser, newItem, editingItem.getId());
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION, "Tugas berhasil diperbarui!");
            successAlert.showAndWait();
        }

        if (onSaveCallback != null) onSaveCallback.run();

        ((Stage) datePicker.getScene().getWindow()).close();
    }
}
