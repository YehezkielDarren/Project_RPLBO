module project.todolist {
    requires javafx.controls;
    requires javafx.fxml;

    exports project.todolist;
    opens project.todolist to javafx.fxml;

    exports project.todolist.model;
    opens project.todolist.model to javafx.fxml;

    exports project.todolist.data;
    opens project.todolist.data to javafx.fxml;

    exports project.todolist.controller;
    opens project.todolist.controller to javafx.fxml;
}
