package view;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import java.io.File;
import java.util.Optional;
import view.UserList;
import view.UserListHelper;
import view.AlbumList;
import view.AlbumListHelper;

/**
 * Controller for the admin management screen.
 * Allows listing all users and creating or deleting users.
 */
public class AdminController {
    @FXML
    private ListView<String> userListView;

    /** Holds the list of users for admin operations. */
    private UserList userList;

    /**
     * Initializes the admin controller by loading the current user list into the ListView.
     */
    @FXML
    private void initialize() {
        userList = UserListHelper.load();
        userListView.getItems().setAll(userList.getUsers());
    }

    /**
     * Handles creation of a new user. Pops up a dialog to enter username, then creates the user if valid.
     */
    @FXML
    private void handleCreateUser() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create User");
        dialog.setHeaderText("Create New User");
        dialog.setContentText("Enter new username:");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String newUser = result.get().trim().toLowerCase();
            if (newUser.isEmpty()) {
                showAlert("Username cannot be empty.");
            } else if (newUser.equals("admin") || newUser.equals("stock")) {
                showAlert("Cannot use reserved username \"" + newUser + "\".");
            } else if (userList.userExists(newUser)) {
                showAlert("User \"" + newUser + "\" already exists.");
            } else {
                // Add user and save
                userList.addUser(newUser);
                UserListHelper.save(userList);
                // Create an empty album list file for the new user
                AlbumListHelper.save(newUser, new AlbumList());
                // Update the UI list
                userListView.getItems().add(newUser);
            }
        }
    }

    /**
     * Handles deletion of a selected user. Prompts for confirmation before deleting the user.
     */
    @FXML
    private void handleDeleteUser() {
        String selectedUser = userListView.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert("No user selected.");
            return;
        }
        if (selectedUser.equals("stock") || selectedUser.equals("admin")) {
            showAlert("Cannot delete user \"" + selectedUser + "\".");
            return;
        }
        // Confirm deletion
        Alert confirm = new Alert(AlertType.CONFIRMATION);
        confirm.setTitle("Delete User");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to delete user \"" + selectedUser + "\"?");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Remove user and save
            userList.removeUser(selectedUser);
            UserListHelper.save(userList);
            // Delete the user's album data file
            File dataFile = new File(selectedUser + "_albums.dat");
            if (dataFile.exists()) {
                dataFile.delete();
            }
            // Update UI list
            userListView.getItems().remove(selectedUser);
        }
    }

    /**
     * Handles logout action from admin screen. Returns to the login screen.
     */
    @FXML
    private void handleLogout() {
        try {
            Stage stage = (Stage) userListView.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
            stage.setScene(new Scene(root, 400, 300));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Utility method to show an error alert with the given message.
     * @param message the message to display in the alert
     */
    private void showAlert(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Admin Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
