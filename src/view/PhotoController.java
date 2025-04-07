package view;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Optional;

public class PhotoController {

    @FXML
    private ListView<Photo> photoListView;

    @FXML
    private TextField searchField;

    private Album album;
    private String username;

    public void start(Album album, String username) {
        this.album = album;
        this.username = username;
        photoListView.getItems().addAll(album.getPhotos());
    }

    @FXML
    private void handleAddPhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Photo");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png", "*.gif")
        );
        File selectedFile = fileChooser.showOpenDialog(photoListView.getScene().getWindow());

        if (selectedFile != null) {
            Photo photo = new Photo(selectedFile.getAbsolutePath());
            album.addPhoto(photo);
            photoListView.getItems().add(photo);
            saveAlbumList();
        }
    }

    @FXML
    private void handleDeletePhoto() {
        Photo selectedPhoto = photoListView.getSelectionModel().getSelectedItem();
        if (selectedPhoto != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Photo");
            alert.setContentText("Are you sure you want to delete this photo?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                album.removePhoto(selectedPhoto);
                photoListView.getItems().remove(selectedPhoto);
                saveAlbumList();
            }
        }
    }

    @FXML
    private void handleEditDescription() {
        Photo selectedPhoto = photoListView.getSelectionModel().getSelectedItem();
        if (selectedPhoto != null) {
            TextInputDialog dialog = new TextInputDialog(selectedPhoto.getDescription());
            dialog.setTitle("Edit Description");
            dialog.setHeaderText("Edit Description for Photo");
            dialog.setContentText("Enter new description:");
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(desc -> {
                selectedPhoto.setDescription(desc.trim());
                photoListView.refresh();
                saveAlbumList();
            });
        }
    }

    @FXML
    private void handleAddTag() {
        Photo selectedPhoto = photoListView.getSelectionModel().getSelectedItem();
        if (selectedPhoto != null) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Add Tag");
            dialog.setHeaderText("Add a tag to this photo");
            dialog.setContentText("Enter tag:");
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(tag -> {
                selectedPhoto.addTag(tag.trim());
                photoListView.refresh();
                saveAlbumList();
            });
        }
    }

    @FXML
    private void handleRemoveTag() {
        Photo selectedPhoto = photoListView.getSelectionModel().getSelectedItem();
        if (selectedPhoto != null && !selectedPhoto.getTags().isEmpty()) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Remove Tag");
            dialog.setHeaderText("Remove a tag from this photo");
            dialog.setContentText("Enter tag to remove:");
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(tag -> {
                selectedPhoto.removeTag(tag.trim());
                photoListView.refresh();
                saveAlbumList();
            });
        }
    }

    @FXML
    private void handleSearch() {
        String searchTag = searchField.getText().trim();
        if (searchTag.isEmpty()) {
            return;
        }

        photoListView.getItems().clear();
        for (Photo photo : album.getPhotos()) {
            if (photo.getTags().contains(searchTag)) {
                photoListView.getItems().add(photo);
            }
        }
    }

    @FXML
    private void handleClearSearch() {
        searchField.clear();
        photoListView.getItems().clear();
        photoListView.getItems().addAll(album.getPhotos());
    }

    private void saveAlbumList() {
        AlbumList albumList = AlbumListHelper.load(username);
        for (Album alb : albumList.getAlbums()) {
            if (alb.getName().equals(album.getName())) {
                alb.getPhotos().clear();
                alb.getPhotos().addAll(album.getPhotos());
                break;
            }
        }
        AlbumListHelper.save(username, albumList);
    }
}
