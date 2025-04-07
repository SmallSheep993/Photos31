package view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.scene.Parent;
import view.UserList;
import view.UserListHelper;

/**
 * Controller for the login screen. Handles user login logic.
 */
public class LoginController {
    @FXML
    private TextField usernameField;

    /**
     * Handles the Login button action. Determines if user is admin or a normal user and navigates accordingly.
     */
    @FXML
    private void handleLogin() {
        try {
            String input = usernameField.getText().trim();
            if (input.isEmpty()) {
                // Show error if no username entered
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Login Error");
                alert.setHeaderText(null);
                alert.setContentText("Username cannot be empty!");
                alert.showAndWait();
                return;
            }

            Stage stage = (Stage) usernameField.getScene().getWindow();
            Parent root;
            if (input.equalsIgnoreCase("admin")) {
                // If admin user, go to admin interface
                root = FXMLLoader.load(getClass().getResource("/view/admin.fxml"));
                Scene scene = new Scene(root, 600, 400);
                stage.setScene(scene);
                stage.show();
            } else {
                // Normal user login
                String username = input.toLowerCase();
                // Check if user exists in the system
                UserList users = UserListHelper.load();
                if (!users.userExists(username)) {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Login Error");
                    alert.setHeaderText(null);
                    alert.setContentText("User \"" + input + "\" does not exist. Please contact admin to create the user.");
                    alert.showAndWait();
                    return;
                }
                // Load album view for the user
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/album.fxml"));
                root = loader.load();
                AlbumController controller = loader.getController();
                controller.start(username);
                Scene scene = new Scene(root, 600, 400);
                stage.setScene(scene);
                stage.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
