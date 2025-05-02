package project.todolist.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ToDoItem {
    private StringProperty tanggal;
    private StringProperty waktu;
    private StringProperty catatan;
    private StringProperty kategori;

    public ToDoItem(String tanggal, String waktu, String catatan, String kategori) {
        this.tanggal = new SimpleStringProperty(tanggal);
        this.waktu = new SimpleStringProperty(waktu);
        this.catatan = new SimpleStringProperty(catatan);
        this.kategori = new SimpleStringProperty(kategori);
    }

    public String getTanggal() { return tanggal.get(); }
    public String getWaktu() { return waktu.get(); }
    public String getCatatan() { return catatan.get(); }
    public String getKategori() { return kategori.get(); }

    public void setTanggal(String value) { tanggal.set(value); }
    public void setWaktu(String value) { waktu.set(value); }
    public void setCatatan(String value) { catatan.set(value); }
    public void setKategori(String value) { kategori.set(value); }

    public StringProperty tanggalProperty() { return tanggal; }
    public StringProperty waktuProperty() { return waktu; }
    public StringProperty catatanProperty() { return catatan; }
    public StringProperty kategoriProperty() { return kategori; }
}


