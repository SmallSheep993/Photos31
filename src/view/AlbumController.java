package view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;

import java.util.Optional;

public class AlbumController {

    @FXML
    private ListView<Album> albumListView;

    private AlbumList albumList;
    private String username;

    @FXML
    private void initialize() {
        albumListView.setOnMouseClicked((MouseEvent click) -> {
            if (click.getClickCount() == 2) {
                Album selectedAlbum = albumListView.getSelectionModel().getSelectedItem();
                if (selectedAlbum != null) {
                    openPhotoView(selectedAlbum);
                }
            }
        });
    }

    public void start(String username) {
        this.username = username;
        albumList = AlbumListHelper.load(username);
        albumListView.getItems().addAll(albumList.getAlbums());
    }

    @FXML
    private void handleCreateAlbum() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create Album");
        dialog.setHeaderText("Create New Album");
        dialog.setContentText("Enter album name:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            name = name.trim();
            if (name.isEmpty()) {
                showAlert("Album name cannot be empty!");
            } else if (albumList.albumExists(name)) {
                showAlert("Album already exists!");
            } else {
                Album album = new Album(name);
                albumList.addAlbum(album);
                albumListView.getItems().add(album);
                AlbumListHelper.save(username, albumList);
            }
        });
    }

    @FXML
    private void handleDeleteAlbum() {
        Album selectedAlbum = albumListView.getSelectionModel().getSelectedItem();
        if (selectedAlbum != null) {
            albumList.removeAlbum(selectedAlbum);
            albumListView.getItems().remove(selectedAlbum);
            AlbumListHelper.save(username, albumList);
        }
    }

    private void openPhotoView(Album album) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/photo.fxml"));
            Parent root = loader.load();

            PhotoController controller = loader.getController();
            controller.start(album, username);

            Stage stage = (Stage) albumListView.getScene().getWindow();
            Scene scene = new Scene(root, 800, 600);
            scene.getStylesheets().add(getClass().getResource("/view/style.css").toExternalForm());
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
