package project.todolist.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class ToDoItem {
    private StringProperty tanggal;
    private StringProperty waktu;
    private StringProperty catatan;
    private StringProperty kategori;
    private StringProperty status;

    public ToDoItem(String tanggal, String waktu, String catatan, String kategori, String status) {
        this.tanggal = new SimpleStringProperty(tanggal);
        this.waktu = new SimpleStringProperty(waktu);
        this.catatan = new SimpleStringProperty(catatan);
        this.kategori = new SimpleStringProperty(kategori);
        this.status = new SimpleStringProperty(status);
    }

    public ToDoItem(String tanggal, String waktu, String catatan, String kategori) {
        this(tanggal, waktu, catatan, kategori, ""); // default status kosong, akan di-set lewat updateStatus()
    }


    public void updateStatus() {
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDate dueDate = LocalDate.parse(getTanggal());
            LocalTime dueTime = LocalTime.parse(getWaktu());
            LocalDateTime dueDateTime = LocalDateTime.of(dueDate, dueTime);

            if (dueDateTime.isBefore(now)) {
                long daysLate = java.time.temporal.ChronoUnit.DAYS.between(dueDate, now.toLocalDate());
                setStatus("Terlambat " + daysLate + " hari");
            } else if (dueDate.isEqual(now.toLocalDate())) {
                setStatus("Hari Ini");
            } else {
                long daysLeft = java.time.temporal.ChronoUnit.DAYS.between(now.toLocalDate(), dueDate);
                setStatus("Tersisa " + daysLeft + " hari");
            }
        } catch (Exception e) {
            setStatus("Tanggal tidak valid");
        }
    }




    public String getTanggal() { return tanggal.get(); }
    public String getWaktu() { return waktu.get(); }
    public String getCatatan() { return catatan.get(); }
    public String getKategori() { return kategori.get(); }
    public String getStatus() { return status.get(); }

    public void setTanggal(String value) { tanggal.set(value); }
    public void setWaktu(String value) { waktu.set(value); }
    public void setCatatan(String value) { catatan.set(value); }
    public void setKategori(String value) { kategori.set(value); }
    public void setStatus(String value) { status.set(value); }

    public StringProperty tanggalProperty() { return tanggal; }
    public StringProperty waktuProperty() { return waktu; }
    public StringProperty catatanProperty() { return catatan; }
    public StringProperty kategoriProperty() { return kategori; }
    public StringProperty statusProperty() { return status; }
}


