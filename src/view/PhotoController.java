package view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;
import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controller for a photo album view.
 * Allows adding/removing photos, editing photo details, tag management, copying/moving photos, searching within an album, and viewing photos.
 */
public class PhotoController {
    @FXML
    private ListView<Photo> photoListView;
    @FXML
    private TextField searchField;
    @FXML
    private Button addPhotoButton;
    @FXML
    private Button deletePhotoButton;
    @FXML
    private Button movePhotoButton;
    @FXML
    private Button searchButton;
    @FXML
    private Button clearSearchButton;

    /** The album currently being viewed. */
    private Album album;
    /** The username of the current user. */
    private String username;
    /** The AlbumList of the current user (all albums). */
    private AlbumList albumList;

    /**
     * Initializes the photo list controller. Sets up double-click handler for viewing photos and cell factory for thumbnails.
     */
    @FXML
    private void initialize() {
        // Double-click a photo to open in viewer
        photoListView.setOnMouseClicked((MouseEvent click) -> {
            if (click.getClickCount() == 2) {
                Photo selected = photoListView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    openPhotoViewer(selected);
                }
            }
        });
        // Set custom cell factory to show photo thumbnails and description
        photoListView.setCellFactory(list -> new javafx.scene.control.ListCell<Photo>() {
            @Override
            protected void updateItem(Photo photo, boolean empty) {
                super.updateItem(photo, empty);
                if (empty || photo == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(photo.toString());
                    File file = new File(photo.getFilePath());
                    if (file.exists()) {
                        javafx.scene.image.Image img = new javafx.scene.image.Image(file.toURI().toString(), 80, 80, true, true);
                        javafx.scene.image.ImageView thumb = new javafx.scene.image.ImageView(img);
                        setGraphic(thumb);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });
    }

    /**
     * Starts the photo view for a given album and user.
     * @param album the Album to display
     * @param username the username of the album owner
     * @param albumList the AlbumList of the user (for persistence and cross-album operations)
     */
    public void start(Album album, String username, AlbumList albumList) {
        this.album = album;
        this.username = username;
        this.albumList = albumList;
        // Populate photo list
        photoListView.getItems().setAll(album.getPhotos());
        // Check if this album view is showing search results (not an actual album in user's list)
        boolean searchMode = !albumList.albumExists(album.getName());
        if (searchMode) {
            addPhotoButton.setDisable(true);
            deletePhotoButton.setDisable(true);
            movePhotoButton.setDisable(true);
            searchField.setDisable(true);
            searchButton.setDisable(true);
            clearSearchButton.setDisable(true);
        } else {
            // Ensure buttons enabled for normal album
            addPhotoButton.setDisable(false);
            deletePhotoButton.setDisable(false);
            movePhotoButton.setDisable(false);
            searchField.setDisable(false);
            searchButton.setDisable(false);
            clearSearchButton.setDisable(false);
        }
    }

    /**
     * Handles adding a new photo to the album.
     * Opens a file chooser to select an image and adds it to the album if not already present.
     */
    @FXML
    private void handleAddPhoto() {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Select Photo");
        fileChooser.getExtensionFilters().addAll(
            new javafx.stage.FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png", "*.gif")
        );
        File selectedFile = fileChooser.showOpenDialog(photoListView.getScene().getWindow());
        if (selectedFile != null) {
            // Check for duplicates in album
            String path = selectedFile.getAbsolutePath();
            for (Photo p : album.getPhotos()) {
                if (p.getFilePath().equals(path)) {
                    showAlert("Photo already exists in this album.");
                    return;
                }
            }
            Photo photo = new Photo(path);
            album.addPhoto(photo);
            photoListView.getItems().add(photo);
            saveAlbumList();
        }
    }

    /**
     * Handles deleting the selected photo from the album.
     * Prompts for confirmation before removal.
     */
    @FXML
    private void handleDeletePhoto() {
        Photo selectedPhoto = photoListView.getSelectionModel().getSelectedItem();
        if (selectedPhoto != null) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Delete Photo");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to delete this photo from the album?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                album.removePhoto(selectedPhoto);
                photoListView.getItems().remove(selectedPhoto);
                saveAlbumList();
            }
        }
    }

    /**
     * Handles editing the description (caption) of the selected photo.
     * Prompts the user for a new description and updates it.
     */
    @FXML
    private void handleEditDescription() {
        Photo selectedPhoto = photoListView.getSelectionModel().getSelectedItem();
        if (selectedPhoto != null) {
            TextInputDialog dialog = new TextInputDialog(selectedPhoto.getDescription());
            dialog.setTitle("Edit Description");
            dialog.setHeaderText("Edit Photo Caption");
            dialog.setContentText("Enter new description:");
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(desc -> {
                selectedPhoto.setDescription(desc.trim());
                photoListView.refresh();
                saveAlbumList();
            });
        }
    }

    /**
     * Handles adding a new tag to the selected photo.
     * Prompts the user for a tag (name=value) and adds it if valid.
     */
    @FXML
    private void handleAddTag() {
        Photo selectedPhoto = photoListView.getSelectionModel().getSelectedItem();
        if (selectedPhoto != null) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Add Tag");
            dialog.setHeaderText("Add a tag to this photo");
            dialog.setContentText("Enter tag (name=value):");
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(tagInput -> {
                String tagStr = tagInput.trim();
                if (!tagStr.contains("=")) {
                    showAlert("Tag must be in the format name=value.");
                    return;
                }
                String[] kv = tagStr.split("=", 2);
                String tagName = kv[0].trim();
                String tagValue = kv.length > 1 ? kv[1].trim() : "";
                if (tagName.isEmpty() || tagValue.isEmpty()) {
                    showAlert("Tag name and value cannot be empty.");
                } else {
                    boolean success = selectedPhoto.addTag(tagName, tagValue);
                    if (!success) {
                        showAlert("Unable to add tag. It may already exist or be invalid for this photo.");
                    }
                    photoListView.refresh();
                    saveAlbumList();
                }
            });
        }
    }

    /**
     * Handles removing a tag from the selected photo.
     * Prompts the user for the tag (name=value) to remove and deletes it if present.
     */
    @FXML
    private void handleRemoveTag() {
        Photo selectedPhoto = photoListView.getSelectionModel().getSelectedItem();
        if (selectedPhoto != null && !selectedPhoto.getTags().isEmpty()) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Remove Tag");
            dialog.setHeaderText("Remove a tag from this photo");
            dialog.setContentText("Enter tag to remove (name=value):");
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(tagInput -> {
                String removeStr = tagInput.trim();
                if (!removeStr.contains("=")) {
                    showAlert("Please enter the tag in name=value format.");
                } else {
                    String[] kv = removeStr.split("=", 2);
                    String tagName = kv[0].trim();
                    String tagValue = kv.length > 1 ? kv[1].trim() : "";
                    if (tagName.isEmpty() || tagValue.isEmpty()) {
                        showAlert("Tag name and value cannot be empty.");
                    } else {
                        selectedPhoto.removeTag(tagName, tagValue);
                        photoListView.refresh();
                        saveAlbumList();
                    }
                }
            });
        }
    }

    /**
     * Handles searching within the current album by a tag keyword.
     * Filters the displayed photos to those whose tags contain the search text.
     */
    @FXML
    private void handleSearch() {
        String query = searchField.getText().trim().toLowerCase();
        if (query.isEmpty()) {
            return;
        }
        photoListView.getItems().clear();
        for (Photo photo : album.getPhotos()) {
            boolean match = false;
            for (Tag tag : photo.getTags()) {
                if (tag.getName().toLowerCase().contains(query) || tag.getValue().toLowerCase().contains(query)) {
                    match = true;
                    break;
                }
            }
            if (match) {
                photoListView.getItems().add(photo);
            }
        }
    }

    /**
     * Clears the search filter and displays all photos in the album.
     */
    @FXML
    private void handleClearSearch() {
        searchField.clear();
        photoListView.getItems().clear();
        photoListView.getItems().addAll(album.getPhotos());
    }

    /**
     * Handles copying the selected photo to another album.
     * Prompts the user to select a target album and then copies the photo reference to that album.
     */
    @FXML
    private void handleCopyPhoto() {
        Photo selectedPhoto = photoListView.getSelectionModel().getSelectedItem();
        if (selectedPhoto == null) {
            return;
        }
        // List of other albums to copy into
        List<String> otherAlbums = albumList.getAlbums().stream()
                .map(Album::getName)
                .filter(name -> !name.equals(album.getName()))
                .collect(Collectors.toList());
        if (otherAlbums.isEmpty()) {
            showAlert("No other album available to copy into.");
            return;
        }
        ChoiceDialog<String> dialog = new ChoiceDialog<>(otherAlbums.get(0), otherAlbums);
        dialog.setTitle("Copy Photo");
        dialog.setHeaderText("Copy Photo to Another Album");
        dialog.setContentText("Select target album:");
        Optional<String> choice = dialog.showAndWait();
        if (choice.isPresent()) {
            String targetAlbumName = choice.get();
            Album targetAlbum = null;
            for (Album alb : albumList.getAlbums()) {
                if (alb.getName().equals(targetAlbumName)) {
                    targetAlbum = alb;
                    break;
                }
            }
            if (targetAlbum == null) {
                return;
            }
            // Check if photo already in target album
            for (Photo p : targetAlbum.getPhotos()) {
                if (p.getFilePath().equals(selectedPhoto.getFilePath())) {
                    showAlert("Photo already exists in the target album.");
                    return;
                }
            }
            targetAlbum.addPhoto(selectedPhoto);
            AlbumListHelper.save(username, albumList);
        }
    }

    /**
     * Handles moving the selected photo to another album.
     * Prompts the user to select a target album and moves the photo (removing it from the current album).
     */
    @FXML
    private void handleMovePhoto() {
        Photo selectedPhoto = photoListView.getSelectionModel().getSelectedItem();
        if (selectedPhoto == null) {
            return;
        }
        List<String> otherAlbums = albumList.getAlbums().stream()
                .map(Album::getName)
                .filter(name -> !name.equals(album.getName()))
                .collect(Collectors.toList());
        if (otherAlbums.isEmpty()) {
            showAlert("No other album available to move into.");
            return;
        }
        ChoiceDialog<String> dialog = new ChoiceDialog<>(otherAlbums.get(0), otherAlbums);
        dialog.setTitle("Move Photo");
        dialog.setHeaderText("Move Photo to Another Album");
        dialog.setContentText("Select target album:");
        Optional<String> choice = dialog.showAndWait();
        if (choice.isPresent()) {
            String targetAlbumName = choice.get();
            Album targetAlbum = null;
            for (Album alb : albumList.getAlbums()) {
                if (alb.getName().equals(targetAlbumName)) {
                    targetAlbum = alb;
                    break;
                }
            }
            if (targetAlbum == null) {
                return;
            }
            // Check duplicate in target
            for (Photo p : targetAlbum.getPhotos()) {
                if (p.getFilePath().equals(selectedPhoto.getFilePath())) {
                    showAlert("Photo already exists in the target album.");
                    return;
                }
            }
            Alert confirm = new Alert(AlertType.CONFIRMATION);
            confirm.setTitle("Move Photo");
            confirm.setHeaderText(null);
            confirm.setContentText("Move photo to album \"" + targetAlbumName + "\"? It will be removed from the current album.");
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                targetAlbum.addPhoto(selectedPhoto);
                album.removePhoto(selectedPhoto);
                photoListView.getItems().remove(selectedPhoto);
                AlbumListHelper.save(username, albumList);
            }
        }
    }

    /**
     * Handles creating a new album from the current search results.
     * Copies all currently displayed photos into a new album in the user's collection.
     */
    @FXML
    private void handleCreateAlbumFromSearch() {
        // Only enabled or applicable when a search result is being displayed
        if (!albumList.albumExists(album.getName()) || !searchField.getText().trim().isEmpty()) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("New Album from Search Results");
            dialog.setHeaderText("Create Album from Search Results");
            dialog.setContentText("Enter new album name:");
            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                String newAlbumName = result.get().trim();
                if (newAlbumName.isEmpty()) {
                    showAlert("Album name cannot be empty.");
                    return;
                }
                if (albumList.albumExists(newAlbumName)) {
                    showAlert("Album \"" + newAlbumName + "\" already exists.");
                    return;
                }
                Album newAlbum = new Album(newAlbumName);
                // Copy all photos currently displayed
                for (Photo p : photoListView.getItems()) {
                    newAlbum.addPhoto(p);
                }
                albumList.addAlbum(newAlbum);
                AlbumListHelper.save(username, albumList);
                Alert info = new Alert(AlertType.INFORMATION);
                info.setTitle("Album Created");
                info.setHeaderText(null);
                info.setContentText("Album \"" + newAlbumName + "\" has been created from search results.");
                info.showAndWait();
            }
        } else {
            // If no search results to save (shouldn't happen if button is properly enabled/disabled)
            showAlert("No search results to create an album from.");
        }
    }

    /**
     * Handles returning to the album list view (logout from album view).
     * Saves any changes and loads the album list screen for the user.
     */
    @FXML
    private void handleBackToAlbums() {
        try {
            Stage stage = (Stage) photoListView.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/album.fxml"));
            Parent root = loader.load();
            AlbumController controller = loader.getController();
            controller.start(username);
            stage.setScene(new Scene(root, 600, 400));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens a separate window to display the selected photo in a larger view with details.
     * @param photo the Photo to display
     */
    private void openPhotoViewer(Photo photo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/photoView.fxml"));
            Parent root = loader.load();
            PhotoViewController controller = loader.getController();
            // Determine index of photo in its album
            int index = album.getPhotos().indexOf(photo);
            controller.start(album, index);
            Stage viewerStage = new Stage();
            viewerStage.setTitle("Photo View");
            viewerStage.setScene(new Scene(root, 600, 500));
            viewerStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves the current state of the user's album list to storage.
     * This ensures any changes to photo captions/tags or album contents are persisted.
     */
    private void saveAlbumList() {
        AlbumList list = AlbumListHelper.load(username);
        for (Album alb : list.getAlbums()) {
            if (alb.getName().equals(album.getName())) {
                alb.getPhotos().clear();
                alb.getPhotos().addAll(album.getPhotos());
                break;
            }
        }
        AlbumListHelper.save(username, list);
    }

    /**
     * Displays an error alert with the given message.
     * @param message the message to display
     */
    private void showAlert(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
