package view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Parent;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private void handleLogin() {
        try {
            String username = usernameField.getText().trim();
            if (username.isEmpty()) {
                System.out.println("Username cannot be empty!");
                return;
            }

            Stage stage = (Stage) usernameField.getScene().getWindow();
            Parent root;

            if (username.equals("admin")) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/admin.fxml"));
                root = loader.load();
                Scene scene = new Scene(root, 600, 400);
                scene.getStylesheets().add(getClass().getResource("/view/style.css").toExternalForm());
                stage.setScene(scene);
                stage.show();
            } else {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/album.fxml"));
                root = loader.load();
                AlbumController controller = loader.getController();
                controller.start(username);
                Scene scene = new Scene(root, 600, 400);
                scene.getStylesheets().add(getClass().getResource("/view/style.css").toExternalForm());
                stage.setScene(scene);
                stage.show();
                
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
