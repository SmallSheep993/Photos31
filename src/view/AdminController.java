package view;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.util.Optional;

public class AdminController {

    @FXML
    private ListView<String> userListView;

    private UserList userList;

    @FXML
    private void initialize() {
        userList = UserListHelper.load();
        refreshUserList();
    }
    

    @FXML
    private void handleCreateUser() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create User");
        dialog.setHeaderText("Create a New User");
        dialog.setContentText("Enter username:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            name = name.trim();
            if (name.isEmpty()) {
                showAlert("Username cannot be empty!");
            } else if (userList.contains(name)) {
                showAlert("User already exists!");
            } else {
                userList.addUser(name);
                UserListHelper.save(userList);
                refreshUserList();
            }
        });
    }

    @FXML
    private void handleDeleteUser() {
        String selectedUser = userListView.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert("Please select a user to delete.");
            return;
        }

        userList.removeUser(selectedUser);
        UserListHelper.save(userList);
        refreshUserList();
    }

    private void refreshUserList() {
        userListView.getItems().clear();
        userListView.getItems().addAll(userList.getUsers()); // 加载所有用户名
    }

    private void showAlert(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
